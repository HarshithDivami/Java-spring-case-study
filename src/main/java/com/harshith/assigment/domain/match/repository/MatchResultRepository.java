package com.harshith.assigment.domain.match.repository;

import com.harshith.assigment.domain.match.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MatchResultRepository extends JpaRepository<MatchResult, UUID> {

    Optional<MatchResult> findByMatchId(UUID matchId);

    boolean existsByMatchId(UUID matchId);
}
