package no.hvl.dat250.experiment1.domain;

import java.util.HashSet;
import java.util.Set;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String caption;
    private int presentationOrder;

    // Many options belong to one poll
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    @ToString.Exclude
    private Poll poll;

    // One option can have many votes
    @OneToMany(mappedBy = "voteOption", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Vote> votes = new HashSet<>();

    public VoteOption(Poll poll, String caption, int order) {
    this.poll = poll;
    this.caption = caption;
    this.presentationOrder = order;
    }

    // Helpers
    public void addVote(Vote vote){
        votes.add(vote);
        vote.setVoteOption(this);
    }

    public void removeVote(Vote vote){
        votes.remove(vote);
        vote.setVoteOption(null);
    }
}