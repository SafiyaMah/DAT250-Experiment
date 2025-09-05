package no.hvl.dat250.experiment1.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Vote {
    private Long id;
    private Instant publishedAt;
    private VoteOption voteOption;
    private User voter;
    private Poll poll;

    public Vote() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public VoteOption getVoteOption() { return voteOption; }
    public void setVoteOption(VoteOption voteOption) { this.voteOption = voteOption; }

    public User getVoter() { return voter; }
    public void setVoter(User voter) { this.voter = voter; }

    public Poll getPoll() { return poll; }
    public void setPoll(Poll poll) { this.poll = poll; }
}
