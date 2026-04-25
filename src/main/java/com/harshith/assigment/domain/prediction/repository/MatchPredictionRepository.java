package com.harshith.assigment.domain.prediction.repository;

import com.harshith.assigment.domain.prediction.entity.MatchPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchPredictionRepository extends JpaRepository<MatchPrediction, UUID> {

    Optional<MatchPrediction> findByMatchIdAndUserId(UUID matchId, UUID userId);

    List<MatchPrediction> findByMatchIdAndDeletedFalse(UUID matchId);

    List<MatchPrediction> findByUserIdAndMatchLeagueSeasonIdAndDeletedFalse(
            UUID userId, UUID seasonId);

    /** Users who have NOT predicted for a given match (for reminder emails) */
    @Query("""
            SELECT u.id FROM User u
            JOIN LeagueSeasonTeam lst ON lst.leagueSeason.id = :seasonId
            WHERE u.deleted = false AND u.active = true
              AND NOT EXISTS (
                  SELECT 1 FROM MatchPrediction mp
                  WHERE mp.match.id = :matchId AND mp.user.id = u.id AND mp.deleted = false
              )
            """)
    List<UUID> findUserIdsWithoutPrediction(@Param("matchId") UUID matchId,
                                             @Param("seasonId") UUID seasonId);

    @Query("SELECT SUM(mp.totalPoints) FROM MatchPrediction mp " +
           "WHERE mp.user.id = :userId AND mp.match.leagueSeason.id = :seasonId " +
           "AND mp.deleted = false")
    Integer sumTotalPointsByUserAndSeason(@Param("userId") UUID userId,
                                          @Param("seasonId") UUID seasonId);
}
