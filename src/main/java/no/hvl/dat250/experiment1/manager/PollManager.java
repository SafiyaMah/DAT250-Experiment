package no.hvl.dat250.experiment1.manager;

import no.hvl.dat250.experiment1.domain.*;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Component
public class PollManager {

    private final Map<String, User> users = new HashMap<>();
    private final Map<Long, Poll> polls = new HashMap<>();

    private long nextPollId = 1L;
    private long nextVoteOptionId = 1L;
    private long nextVoteId = 1L;

    public User createUser(String username, String email) {
        User u = new User(username, email);
        users.put(username, u);
        return u;
    }

    public Map<String, User> getUsers() { return users; } // get all users

    public User getUser(String username) { return users.get(username); } // get one user

    public Map<Long, Poll> getPolls() { return polls; }

    public Poll createPoll(String username, String question, boolean aPublic, LocalDate validUntil) {
        User creator = users.get(username);
        if (creator == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        Poll poll = new Poll(question, validUntil, creator, aPublic);
        poll.setId(nextPollId++);
        creator.getCreatedPolls().add(poll);
        polls.put(poll.getId(), poll);
        return poll;
    }

    public VoteOption createVoteOption(Long pollId, String caption, int presentationOrder) {
        Poll poll = polls.get(pollId);
        if (poll == null) {
            throw new IllegalArgumentException("Poll not found: " + pollId);
        }
        VoteOption opt = new VoteOption(caption, presentationOrder);
        opt.setId(nextVoteOptionId++);
        opt.setPoll(poll);
        poll.getVoteOptions().add(opt);
        return opt;
    }

    public Vote castVote(String username, Long pollId, Long voteOptionId, Instant publishedAt) {
        User voter = users.get(username);
        if (voter == null) throw new IllegalArgumentException("User not found: " + username);

        Poll poll = polls.get(pollId);
        if (poll == null) throw new IllegalArgumentException("Poll not found: " + pollId);

        VoteOption selected = poll.getVoteOptions().stream()
                .filter(o -> Objects.equals(o.getId(), voteOptionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Vote option not found: " + voteOptionId));

        Vote vote = new Vote();
        vote.setId(nextVoteId++);
        vote.setPublishedAt(publishedAt != null ? publishedAt : Instant.now());
        vote.setVoteOption(selected);
        vote.setVoter(voter);
        vote.setPoll(poll);

        poll.getVotes().add(vote);
        voter.getVotes().add(vote);
        return vote;
    }

    public void deletePoll(Long pollId) {
        polls.remove(pollId);
    }
}
