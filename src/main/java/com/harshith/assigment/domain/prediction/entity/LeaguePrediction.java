package com.harshith.assigment.domain.prediction.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import com.harshith.assigment.domain.league.entity.LeagueSeason;
import com.harshith.assigment.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "league_predictions",
        uniqueConstraints = @UniqueConstraint(name = "uq_league_pred_user_season",
                columnNames = {"league_season_id", "user_id"}),
        indexes = {
                @Index(name = "idx_lp_season_id", columnList = "league_season_id"),
                @Index(name = "idx_lp_user_id", columnList = "user_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaguePrediction extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "league_season_id", nullable = false)
    private LeagueSeason leagueSeason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private boolean locked = false;

    @Column(name = "locked_at")
    private Instant lockedAt;

    /** Server-calculated total points for this prediction */
    @Column(name = "total_points", nullable = false)
    @Builder.Default
    private Integer totalPoints = 0;

    @OneToMany(mappedBy = "leaguePrediction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LeaguePredictionEntry> entries = new ArrayList<>();
}
