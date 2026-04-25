package com.harshith.assigment.domain.leaderboard.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import com.harshith.assigment.domain.league.entity.LeagueSeason;
import com.harshith.assigment.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_season_points",
        uniqueConstraints = @UniqueConstraint(name = "uq_usp_user_season",
                columnNames = {"user_id", "league_season_id"}),
        indexes = {
                @Index(name = "idx_usp_season_id", columnList = "league_season_id"),
                @Index(name = "idx_usp_total_points", columnList = "league_season_id, total_points DESC")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSeasonPoints extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "league_season_id", nullable = false)
    private LeagueSeason leagueSeason;

    @Column(name = "match_points", nullable = false)
    @Builder.Default
    private Integer matchPoints = 0;

    @Column(name = "league_prediction_points", nullable = false)
    @Builder.Default
    private Integer leaguePredictionPoints = 0;

    @Column(name = "total_points", nullable = false)
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "last_calculated_at")
    private Instant lastCalculatedAt;
}
