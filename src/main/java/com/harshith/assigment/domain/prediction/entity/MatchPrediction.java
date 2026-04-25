package com.harshith.assigment.domain.prediction.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import com.harshith.assigment.domain.match.entity.Match;
import com.harshith.assigment.domain.team.entity.Player;
import com.harshith.assigment.domain.team.entity.Team;
import com.harshith.assigment.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "match_predictions",
        uniqueConstraints = @UniqueConstraint(name = "uq_match_pred_user_match",
                columnNames = {"match_id", "user_id"}),
        indexes = {
                @Index(name = "idx_mp_match_id", columnList = "match_id"),
                @Index(name = "idx_mp_user_id", columnList = "user_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchPrediction extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predicted_winner_team_id")
    private Team predictedWinner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predicted_toss_winner_team_id")
    private Team predictedTossWinner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predicted_player_of_match_id")
    private Player predictedPlayerOfMatch;

    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private boolean locked = false;

    @Column(name = "locked_at")
    private Instant lockedAt;

    // Points are server-calculated only — never accepted via API
    @Column(name = "winner_points", nullable = false)
    @Builder.Default
    private Integer winnerPoints = 0;

    @Column(name = "toss_points", nullable = false)
    @Builder.Default
    private Integer tossPoints = 0;

    @Column(name = "potm_points", nullable = false)
    @Builder.Default
    private Integer potmPoints = 0;

    @Column(name = "total_points", nullable = false)
    @Builder.Default
    private Integer totalPoints = 0;
}
