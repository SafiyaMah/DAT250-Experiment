package no.hvl.dat250.experiment1.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Poll {
    private Long id;
    private String question;
    private Instant publishedAt;
    private LocalDate validUntil;
    private boolean aPublic; 

    private User creator;
    private Set<VoteOption> voteOptions = new HashSet<>();
    private Set<Vote> votes = new HashSet<>();

    public Poll() {}

    public Poll(String question, LocalDate validUntil, User creator, boolean aPublic) {
        this.question = question;
        this.validUntil = validUntil;
        this.creator = creator;
        this.aPublic = aPublic;
        this.publishedAt = Instant.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public boolean isaPublic() { return aPublic; }
    public void setaPublic(boolean aPublic) { this.aPublic = aPublic; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public Set<VoteOption> getVoteOptions() { return voteOptions; }
    public void setVoteOptions(Set<VoteOption> voteOptions) { this.voteOptions = voteOptions; }

    public Set<Vote> getVotes() { return votes; }
    public void setVotes(Set<Vote> votes) { this.votes = votes; }
}
