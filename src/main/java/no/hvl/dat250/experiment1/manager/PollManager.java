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
import java.util.List;

@Service
@RequiredArgsConstructor
public class PollManager {
    private final PollRepository polls;
    private final UserRepository users;
    private final VoteOptionRepository options;
    private final VoteRepository votes;

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
        Poll createNewPoll = new Poll(question, validUntil, creator, aPublic);
        opts.forEach(createNewPoll::addVoteOption);       
        return polls.save(createNewPoll);                           
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
    }

    @Transactional
    public VoteOption addOption(Long pollId, String caption){
        Poll poll = getPoll(pollId);
        VoteOption option = poll.addVoteOption(caption);
        polls.save(poll);
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
        Vote vote = new Vote(voter, poll, option);
        return votes.save(vote);
    }

    public List<Vote> listVotesForPoll(Long pollId){
        return getPoll(pollId).getVotes().stream().toList();
    }

    public List<VoteOption> listOptionsForPoll(Long pollId){
        return getPoll(pollId).getVoteOptions().stream().toList();
    }

    public List<ResultDto> results(Long pollId){
        Poll poll = getPoll(pollId);
        return poll.getVoteOptions().stream().map(o -> new ResultDto(o.getId(), o.getCaption(), votes.countByVoteOptionId(o.getId()))).toList();
    }

    private static ResponseStatusException notFound(String what, Object id){
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " " + id + " not found");
    }

    public record ResultDto(Long optionId, String caption, long votes) {}
}