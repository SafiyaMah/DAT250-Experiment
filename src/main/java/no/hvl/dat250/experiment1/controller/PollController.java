package no.hvl.dat250.experiment1.controller;

import no.hvl.dat250.experiment1.domain.Poll;
import no.hvl.dat250.experiment1.domain.VoteOption;
import no.hvl.dat250.experiment1.manager.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/polls")
@CrossOrigin(origins = {
        "http://localhost:8080"
})
public class PollController {

    @Autowired
    private PollManager pollManager;

    @PostMapping
    public Poll createPoll(@RequestParam String username, @RequestBody Poll body) {
        return pollManager.createPoll(username, body.getQuestion(), body.isaPublic(), body.getValidUntil());
    }

    @GetMapping
    public Map<Long, Poll> getAllPolls() {
        return pollManager.getPolls();
    }

    @GetMapping("/{pollId}")
    public Poll getPoll(@PathVariable Long pollId) {
        return pollManager.getPolls().get(pollId);
    }

    @PostMapping("/{pollId}/options")
    public VoteOption createVoteOption(@PathVariable Long pollId, @RequestBody VoteOption option) {
        return pollManager.createVoteOption(pollId, option.getCaption(), option.getPresentationOrder());
    }

    @DeleteMapping("/{pollId}")
    public void deletePoll(@PathVariable Long pollId) {
        pollManager.deletePoll(pollId);
    }
}
