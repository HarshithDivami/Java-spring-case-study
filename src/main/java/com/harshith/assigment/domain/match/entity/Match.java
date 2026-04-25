package com.harshith.assigment.domain.match.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import com.harshith.assigment.common.enums.MatchStatus;
import com.harshith.assigment.common.enums.MatchType;
import com.harshith.assigment.domain.league.entity.LeagueSeason;
import com.harshith.assigment.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "matches",
        uniqueConstraints = @UniqueConstraint(name = "uq_match_season_number",
                columnNames = {"league_season_id", "match_number"}),
        indexes = {
                @Index(name = "idx_matches_season_id", columnList = "league_season_id"),
                @Index(name = "idx_matches_scheduled_at", columnList = "scheduled_at"),
                @Index(name = "idx_matches_prediction_lock_at", columnList = "prediction_lock_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "league_season_id", nullable = false)
    private LeagueSeason leagueSeason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column(name = "match_number", nullable = false)
    private Integer matchNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false, length = 50)
    @Builder.Default
    private MatchType matchType = MatchType.LEAGUE;

    @Column(name = "venue", length = 200)
    private String venue;

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    /** Stored so prediction-lock queries avoid runtime arithmetic */
    @Column(name = "prediction_lock_at", nullable = false)
    private Instant predictionLockAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private MatchStatus status = MatchStatus.SCHEDULED;

    /** Free-form extensible metadata (e.g. weather, day/night flag) */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;
}
