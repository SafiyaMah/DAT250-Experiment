package no.hvl.dat250.experiment1.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String caption;
    private int presentationOrder;

    // Many options belong to one poll
    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    @ToString.Exclude
    private Poll poll;

    // One option can have many votes
    @OneToMany(mappedBy = "voteOption", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Vote> votes = new ArrayList<>();

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

    @Override public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof VoteOption other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override public int hashCode(){ return 31; }
}