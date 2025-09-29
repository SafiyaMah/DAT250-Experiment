package no.hvl.dat250.experiment1.controller;

import no.hvl.dat250.experiment1.domain.Vote;
import no.hvl.dat250.experiment1.manager.PollManager;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" })
@RequiredArgsConstructor
public class VoteController {
    private PollManager pollManager;

    @PostMapping
    public Vote cast(@RequestBody CastVote req) {
        return pollManager.castVote(req.pollId(), req.optionId(), req.voterId());
    }

    public record CastVote(Long pollId, Long optionId, Long voterId) {
    }
}
