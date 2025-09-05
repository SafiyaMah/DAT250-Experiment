package no.hvl.dat250.experiment1.controller;

import no.hvl.dat250.experiment1.domain.Poll;
import no.hvl.dat250.experiment1.domain.Vote;
import no.hvl.dat250.experiment1.manager.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Set;
import java.util.Map;

@RestController
@RequestMapping("/api/polls/{pollId}/votes")
@CrossOrigin(origins = {
        "http://localhost:8080"
})
public class VoteController {

    @Autowired
    private PollManager pollManager;

    @PostMapping
    public Vote castVote(@PathVariable Long pollId,
                         @RequestParam String username,
                         @RequestParam Long voteOptionId,
                         @RequestBody(required = false) Vote vote) {
        Instant ts = (vote != null && vote.getPublishedAt() != null) ? vote.getPublishedAt() : Instant.now();
        return pollManager.castVote(username, pollId, voteOptionId, ts);
    }

    @GetMapping
    public Set<Vote> getVotesForPoll(@PathVariable Long pollId) {
        Poll poll = pollManager.getPolls().get(pollId);
        return poll != null ? poll.getVotes() : Set.of();
    }

    @GetMapping("/count")
    public Map<String, Integer> countVotes(@PathVariable Long pollId) {
      var poll = pollManager.getPolls().get(pollId);
      int count = (poll == null || poll.getVotes() == null) ? 0 : poll.getVotes().size();
      return Map.of("pollId", pollId.intValue(), "count", count);
    }
}
