package no.hvl.dat250.experiment1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.hvl.dat250.experiment1.domain.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{
    long countByVoteOptionId(Long optionId);
    boolean existsByPoll_IdAndVoter_Id(Long pollId, Long voterId);
}
