package com.harshith.assigment.domain.match.repository;

import com.harshith.assigment.common.enums.MatchStatus;
import com.harshith.assigment.domain.match.entity.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {

    Optional<Match> findByIdAndDeletedFalse(UUID id);

    Page<Match> findByLeagueSeasonIdAndDeletedFalse(UUID seasonId, Pageable pageable);

    List<Match> findByLeagueSeasonIdAndStatusAndDeletedFalse(UUID seasonId, MatchStatus status);

    /** Matches whose prediction window is closing soon (for reminder scheduler) */
    @Query("SELECT m FROM Match m WHERE m.deleted = false " +
           "AND m.status = 'SCHEDULED' " +
           "AND m.predictionLockAt BETWEEN :from AND :to")
    List<Match> findMatchesWithLockBetween(@Param("from") Instant from, @Param("to") Instant to);
}
