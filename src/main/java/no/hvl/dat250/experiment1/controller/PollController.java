package no.hvl.dat250.experiment1.controller;

import no.hvl.dat250.experiment1.domain.Poll;
import no.hvl.dat250.experiment1.domain.VoteOption;
import no.hvl.dat250.experiment1.manager.PollManager;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/polls")
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" })
@RequiredArgsConstructor
public class PollController {
    private final PollManager pollManager;

    @PostMapping
    public Poll create(@RequestParam Long creatorId, @RequestBody CreatePoll req) {
        return pollManager.createPoll(creatorId, req.question(), req.validUntil(), req.aPublic(), req.options());
    }

    @GetMapping
    public List<Poll> all() {
        return pollManager.listPolls();
    }

    @GetMapping("/{id}")
    public Poll one(@PathVariable Long id) {
        return pollManager.getPoll(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pollManager.deletePoll(id);
    }

    @PostMapping("/{id}/options")
    public VoteOption addOption(@PathVariable Long id, @RequestBody CreateOption req) {
        return pollManager.addOption(id, req.caption());
    }

    @GetMapping("/{id}/results")
    public List<PollManager.ResultDto> results(@PathVariable Long id){
        return pollManager.results(id);
    }

    public record CreatePoll(String question, LocalDate validUntil, boolean aPublic, List<String> options) {}
    public record CreateOption(String caption) {}
}
