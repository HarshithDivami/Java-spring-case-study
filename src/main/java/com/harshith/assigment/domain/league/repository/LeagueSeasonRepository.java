package com.harshith.assigment.domain.league.repository;

import com.harshith.assigment.common.enums.LeagueSeasonStatus;
import com.harshith.assigment.domain.league.entity.LeagueSeason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeagueSeasonRepository extends JpaRepository<LeagueSeason, UUID> {

    Optional<LeagueSeason> findByIdAndDeletedFalse(UUID id);

    Page<LeagueSeason> findByLeagueIdAndDeletedFalse(UUID leagueId, Pageable pageable);

    List<LeagueSeason> findByStatusAndDeletedFalse(LeagueSeasonStatus status);

    boolean existsByLeagueIdAndSeasonNumber(UUID leagueId, Integer seasonNumber);

    @Query("SELECT ls FROM LeagueSeason ls WHERE ls.league.id = :leagueId " +
           "AND ls.deleted = false ORDER BY ls.seasonNumber DESC")
    Optional<LeagueSeason> findLatestSeasonForLeague(@Param("leagueId") UUID leagueId);

    @Query("SELECT ls FROM LeagueSeason ls WHERE ls.deleted = false " +
           "AND ls.leaguePredictionLockTime BETWEEN :from AND :to")
    List<LeagueSeason> findSeasonsWithLeagueLockBetween(@Param("from") Instant from,
                                                        @Param("to") Instant to);
}
