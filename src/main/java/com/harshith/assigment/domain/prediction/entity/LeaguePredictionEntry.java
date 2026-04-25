package com.harshith.assigment.domain.prediction.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import com.harshith.assigment.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "league_prediction_entries",
        uniqueConstraints = @UniqueConstraint(name = "uq_lpe_prediction_position",
                columnNames = {"league_prediction_id", "position"}),
        indexes = @Index(name = "idx_lpe_prediction_id", columnList = "league_prediction_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaguePredictionEntry extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "league_prediction_id", nullable = false)
    private LeaguePrediction leaguePrediction;

    /** 1 = predicted winner, 2 = runner-up, … n = last */
    @Column(name = "position", nullable = false)
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /** 1 if actual final position matches predicted position, else 0 */
    @Column(name = "points_awarded", nullable = false)
    @Builder.Default
    private Integer pointsAwarded = 0;
}
