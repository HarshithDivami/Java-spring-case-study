package com.harshith.assigment.domain.league.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import com.harshith.assigment.common.enums.LeagueSeasonStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "league_seasons",
        uniqueConstraints = @UniqueConstraint(name = "uq_season_league_number",
                columnNames = {"league_id", "season_number"}),
        indexes = @Index(name = "idx_league_seasons_league_id", columnList = "league_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeagueSeason extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    @Column(name = "season_name", nullable = false, length = 200)
    private String seasonName;

    @Column(name = "season_number", nullable = false)
    private Integer seasonNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private LeagueSeasonStatus status = LeagueSeasonStatus.DRAFT;

    /** Hours before first match that league-level predictions are locked */
    @Column(name = "league_prediction_lock_hours", nullable = false)
    @Builder.Default
    private Integer leaguePredictionLockHours = 4;

    /** Hours before each match that match-level predictions are locked */
    @Column(name = "match_prediction_lock_hours", nullable = false)
    @Builder.Default
    private Integer matchPredictionLockHours = 1;

    @Column(name = "first_match_time")
    private Instant firstMatchTime;

    /** Computed from firstMatchTime - leaguePredictionLockHours and stored for quick queries */
    @Column(name = "league_prediction_lock_time")
    private Instant leaguePredictionLockTime;

    /** Extensible configuration (max points, tie-breaking rules, etc.) */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config", columnDefinition = "jsonb")
    private String config;

    @OneToMany(mappedBy = "leagueSeason", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LeagueSeasonTeam> seasonTeams = new ArrayList<>();
}
