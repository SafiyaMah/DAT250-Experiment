package no.hvl.dat250.experiment1.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.ToString;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Getter
@Setter
@Table(name = "polls")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private Instant publishedAt = Instant.now();
    private LocalDate validUntil; 
    @JsonProperty("aPublic")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean aPublic; 

    // Many polls share one creator
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    private User creator;

    // One poll can have many vote options
    @JsonManagedReference
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("presentationOrder ASC")  
    @ToString.Exclude
    private List<VoteOption> voteOptions = new ArrayList<>();
    
    // One poll can have many votes
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Vote> votes = new ArrayList<>();

    public Poll(String question, LocalDate validUntil, User creator, boolean aPublic) {
        this.question = question; 
        this.validUntil = validUntil;
        this.creator = creator; 
        this.aPublic = aPublic;
    }

    // Helpers
    public VoteOption addVoteOption(String caption) {
        VoteOption voteOption = new VoteOption();
        voteOption.setCaption(caption);
        voteOption.setPoll(this);
        voteOption.setPresentationOrder(this.getVoteOptions().size());
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

    @JsonProperty("aPublic")
    public boolean isAPublic() { return aPublic; }
    @JsonProperty("aPublic")
    public void setAPublic(boolean aPublic) { this.aPublic = aPublic; }
}