package no.hvl.dat250.experiment1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.hvl.dat250.experiment1.domain.Poll;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long>{
    List<Poll> findByCreatorId(Long creatorId);
}