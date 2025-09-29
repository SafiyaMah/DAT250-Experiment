package no.hvl.dat250.experiment1.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.ToString;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private Instant publishedAt = Instant.now();
    private LocalDate validUntil;
    private boolean aPublic; 

    // Many polls share one creator
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    private User creator;

    // One poll can have many vote options
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<VoteOption> voteOptions = new HashSet<>();
    
    // One poll can have many votes
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Vote> votes = new HashSet<>();

    public Poll(String question, LocalDate validUntil, User creator, boolean aPublic) {
        this.question = question; 
        this.validUntil = validUntil;
        this.creator = creator; 
        this.aPublic = aPublic;
    }

    // Helpers
    public VoteOption addVoteOption(String caption) {
        VoteOption voteOption = new VoteOption(this, caption, voteOptions.size());
        voteOptions.add(voteOption);
        return voteOption;
    }

    public void removeVoteOption(VoteOption opt){
        voteOptions.remove(opt);
        opt.setPoll(null);
    }

    public void addVote(Vote vote){
        votes.add(vote);
        vote.setPoll(this);
    }

    public void removeVote(Vote vote){
        votes.remove(vote);
        vote.setPoll(null);
    }
}