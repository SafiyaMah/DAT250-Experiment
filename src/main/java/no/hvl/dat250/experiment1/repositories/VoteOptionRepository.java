package no.hvl.dat250.experiment1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.hvl.dat250.experiment1.domain.VoteOption;

@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    List<VoteOption> findByPollId(Long pollId);
}
