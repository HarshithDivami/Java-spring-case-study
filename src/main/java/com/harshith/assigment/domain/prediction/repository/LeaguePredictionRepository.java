package com.harshith.assigment.domain.prediction.repository;

import com.harshith.assigment.domain.prediction.entity.LeaguePrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaguePredictionRepository extends JpaRepository<LeaguePrediction, UUID> {

    Optional<LeaguePrediction> findByLeagueSeasonIdAndUserId(UUID seasonId, UUID userId);

    List<LeaguePrediction> findByLeagueSeasonIdAndDeletedFalse(UUID seasonId);

    /** Users who have NOT submitted a league prediction yet */
    @Query("""
            SELECT u.id FROM User u
            WHERE u.deleted = false AND u.active = true
              AND NOT EXISTS (
                  SELECT 1 FROM LeaguePrediction lp
                  WHERE lp.leagueSeason.id = :seasonId AND lp.user.id = u.id AND lp.deleted = false
              )
            """)
    List<UUID> findUserIdsWithoutLeaguePrediction(@Param("seasonId") UUID seasonId);
}
