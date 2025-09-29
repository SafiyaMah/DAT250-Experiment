package no.hvl.dat250.experiment1.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    private String username;
    private String email;

    // One user can create many polls
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Poll> createdPolls = new HashSet<>();

    // One user can cast many votes
    @OneToMany(mappedBy = "voter", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Vote> votes = new HashSet<>();

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
}