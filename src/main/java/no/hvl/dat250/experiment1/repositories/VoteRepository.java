package no.hvl.dat250.experiment1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.hvl.dat250.experiment1.domain.Poll;
import no.hvl.dat250.experiment1.domain.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{
    long countByVoteOptionId(Long optionId);
    List<Vote> findByPollId(Poll poll);
}
