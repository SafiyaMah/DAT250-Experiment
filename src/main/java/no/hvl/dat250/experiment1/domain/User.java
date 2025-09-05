package no.hvl.dat250.experiment1.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.HashSet;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "username")
public class User {
    private String username;
    private String email;

    private Set<Poll> createdPolls = new HashSet<>();
    private Set<Vote> votes = new HashSet<>();

    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters & Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<Poll> getCreatedPolls() { return createdPolls; }
    public void setCreatedPolls(Set<Poll> createdPolls) { this.createdPolls = createdPolls; }

    public Set<Vote> getVotes() { return votes; }
    public void setVotes(Set<Vote> votes) { this.votes = votes; }

    @Override
    public String toString() {
        return "User{username='" + username + "', email='" + email + "'}";
    }
}
