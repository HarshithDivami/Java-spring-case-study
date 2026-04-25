package com.harshith.assigment.domain.leaderboard.repository;

import com.harshith.assigment.domain.leaderboard.entity.UserSeasonPoints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSeasonPointsRepository extends JpaRepository<UserSeasonPoints, UUID> {

    Optional<UserSeasonPoints> findByUserIdAndLeagueSeasonId(UUID userId, UUID seasonId);

    @Query("SELECT usp FROM UserSeasonPoints usp " +
           "WHERE usp.leagueSeason.id = :seasonId AND usp.deleted = false " +
           "ORDER BY usp.totalPoints DESC")
    Page<UserSeasonPoints> findLeaderboard(@Param("seasonId") UUID seasonId, Pageable pageable);

    @Query("SELECT usp FROM UserSeasonPoints usp " +
           "WHERE usp.leagueSeason.id = :seasonId AND usp.deleted = false " +
           "ORDER BY usp.totalPoints DESC")
    List<UserSeasonPoints> findAllForSeasonOrdered(@Param("seasonId") UUID seasonId);
}
