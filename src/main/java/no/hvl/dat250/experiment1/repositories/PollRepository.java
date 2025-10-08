package no.hvl.dat250.experiment1.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import no.hvl.dat250.experiment1.domain.Poll;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long>{
    // creator render "Creator: ..."
    @Override
    @EntityGraph(attributePaths = {"creator", "voteOptions"})
    @NonNull
    List<Poll> findAll();

    // render creator + voteoptions
    @EntityGraph(attributePaths = {"creator", "voteOptions"})
    @NonNull
    Optional<Poll> findById(@NonNull Long id);
}