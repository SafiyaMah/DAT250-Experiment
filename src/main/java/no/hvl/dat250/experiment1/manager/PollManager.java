package no.hvl.dat250.experiment1.manager;

import no.hvl.dat250.experiment1.domain.Poll;
import no.hvl.dat250.experiment1.domain.User;
import no.hvl.dat250.experiment1.domain.Vote;
import no.hvl.dat250.experiment1.domain.VoteOption;
import no.hvl.dat250.experiment1.repositories.PollRepository;
import no.hvl.dat250.experiment1.repositories.UserRepository;
import no.hvl.dat250.experiment1.repositories.VoteOptionRepository;
import no.hvl.dat250.experiment1.repositories.VoteRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import no.hvl.dat250.experiment1.infra.PollEvent;
import no.hvl.dat250.experiment1.infra.RedisCache;
import no.hvl.dat250.experiment1.infra.RedisPublisher;

@Service
@RequiredArgsConstructor
public class PollManager {
    private final PollRepository polls;
    private final UserRepository users;
    private final VoteOptionRepository options;
    private final VoteRepository votes;
    private final Optional<RedisCache> redis;
    private final Optional<RedisPublisher> publisher;

    private void publish(String channel, PollEvent ev) {
        publisher.ifPresent(p -> p.publish(channel, ev));
    }

    private static String countsKey(long pollId) {
        return "poll:" + pollId + ":counts";
    }

    @Transactional
    public User createUser(String username, String email){
        return users.save(new User(username, email));
    }

    public List<User> listUsers(){
        return users.findAll();
    }

    public User getUserByUsername(String username) {
        return users.findByUsername(username).orElseThrow(() -> notFound("User", username));
    }

    public User getUser(Long id) {
        return users.findById(id).orElseThrow(() -> notFound("User", id));
    }

    @Transactional
    public void deleteUser(Long id){
        if (!users.existsById(id)) throw notFound("User", id);
        users.deleteById(id);
    }

    @Transactional
    public Poll createPoll(Long creatorId, String question, LocalDate validUntil, boolean aPublic, List<String> opts){
        if (opts == null || opts.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide at least two options");
        }
        User creator = users.findById(creatorId).orElseThrow(() -> notFound("User", creatorId));
        // build new entity
        Poll createNewPoll = new Poll(question, validUntil, creator, aPublic);
        opts.forEach(createNewPoll::addVoteOption);
        // save it
        Poll created = polls.save(createNewPoll);
        // clear/init cache enrtry fo his poll
        redis.ifPresent(r -> r.del(countsKey(created.getId())));
        // publish a poll-created event on redis pub/sub
        // per poll channel
        publish(RedisPublisher.channelFor(created.getId()), new PollEvent("poll-created", created.getId(), null, null));
        // for new creation poll channel
        publish("polls:new", new PollEvent("poll-created", created.getId(), null, null));
        return created;                          
    }

    public List<Poll> listPolls(){
        return polls.findAll();
    }

    public Poll getPoll(Long pollId){
        return polls.findById(pollId).orElseThrow(() -> notFound("Poll", pollId));
    }

    @Transactional
    public void deletePoll(Long pollId){
        if (!polls.existsById(pollId)) throw notFound("Poll", pollId);
        polls.deleteById(pollId);

        // drop cache and announce deletion
        redis.ifPresent(r -> r.del(countsKey(pollId)));
        publish(RedisPublisher.channelFor(pollId), new PollEvent("poll-deleted", pollId, null, null));
    }

    @Transactional
    public VoteOption addOption(Long pollId, String caption){
        Poll poll = getPoll(pollId);
        VoteOption option = poll.addVoteOption(caption);
        polls.save(poll);
        
        // new option means previous counts are incomplete so we nuke cache
        redis.ifPresent(r -> r.del(countsKey(pollId)));

        // announce option added
        publish(RedisPublisher.channelFor(pollId), new PollEvent("option-added", pollId, option.getId(), null));

        return option;
    }

    @Transactional
    public Vote castVote(Long pollId, Long optionId, Long voterId){
        Poll poll = getPoll(pollId);
        VoteOption option = options.findById(optionId).orElseThrow(() -> notFound("Option", optionId));
        User voter = users.findById(voterId).orElseThrow(() -> notFound("User", voterId));
        
        if (!option.getPoll().getId().equals(poll.getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Option does not belong to poll");
        }
        if (votes.existsByPoll_IdAndVoter_Id(pollId, voterId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already voted on this poll");
        }
        
        //Vote vote = new Vote(voter, poll, option);
        Vote saved = votes.save(new Vote(voter, poll, option));

        // increment cached count, if cache present
        redis.ifPresent(r -> r.hincrBy(countsKey(pollId), String.valueOf(optionId), 1L));

        // publish vote event
        publish(RedisPublisher.channelFor(pollId), new PollEvent("vote", pollId, optionId, voterId));

        return saved;
    }

    public List<Vote> listVotesForPoll(Long pollId){
        return getPoll(pollId).getVotes().stream().toList();
    }

    public List<VoteOption> listOptionsForPoll(Long pollId){
        return getPoll(pollId).getVoteOptions().stream().toList();
    }

    public List<ResultDto> results(Long pollId){
        Poll poll = getPoll(pollId);

        String key = countsKey(pollId);
        // read cache safely via optional redisache
        Map<String, String> cached = redis.map(r -> r.hgetAll(key)).orElseGet(Map::of);

        if (!cached.isEmpty()) {
            // build from cache + voteoption already ordered
            return poll.getVoteOptions().stream().map(opt -> {
                long c = 0L; 
                String v = cached.get(String.valueOf(opt.getId()));
                if (v != null) {
                    try { c = Long.parseLong(v); } catch (NumberFormatException ignored) {}
                }
                return new ResultDto(opt.getId(), opt.getCaption(), c);
            }).toList();
        }

        Map<String, String> map = new LinkedHashMap<>();
        List<ResultDto> result = poll.getVoteOptions().stream().map(opt -> {
            long count = votes.countByVoteOptionId(opt.getId());
            map.put(String.valueOf(opt.getId()), String.valueOf(count));
            return new ResultDto(opt.getId(), opt.getCaption(), count);
        }).toList();

        // write to cache
        redis.ifPresent(r -> r.hsetAndExpire(key, map));
        return result;
    }

    private static ResponseStatusException notFound(String what, Object id){
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " " + id + " not found");
    }

    public record ResultDto(Long optionId, String caption, long votes) {}
}