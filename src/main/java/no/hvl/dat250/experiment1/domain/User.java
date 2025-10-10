package no.hvl.dat250.experiment1.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Getter
@Setter
@Table(name = "users") 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    private String username;
    private String email;

    // One user can create many polls
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<Poll> createdPolls = new ArrayList<>();

    // One user can cast many votes
    @OneToMany(mappedBy = "voter", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<Vote> votes = new ArrayList<>();

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Helpers 
    public void addCreatedPoll(Poll poll){
        createdPolls.add(poll);
        poll.setCreator(this);
    }

    public void removeCreatedPoll(Poll poll){
        createdPolls.remove(poll);
        poll.setCreator(this);
    }

    public void addVote(Vote vote){
        votes.add(vote);
        vote.setVoter(this);
    }

    public void removeVote(Vote vote){
        votes.remove(vote);
        vote.setVoter(null);
    }

    // Helpers for testing
    public Poll createPoll(String question) {
    // validuntil is null for the test 
    Poll p = new Poll(question, null, this, true);
    this.addCreatedPoll(p); // keep both sides in sync
    p.setCreator(this);
    return p;
}

    public Vote voteFor(VoteOption option) {
        Poll poll = option.getPoll();
        Vote v = new Vote(this, poll, option);
        this.addVote(v);
        poll.addVote(v);
        option.addVote(v);
        return v;
    }
}