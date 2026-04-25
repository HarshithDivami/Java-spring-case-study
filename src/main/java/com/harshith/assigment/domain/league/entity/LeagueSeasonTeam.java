package com.harshith.assigment.domain.league.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import com.harshith.assigment.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "league_season_teams",
        uniqueConstraints = @UniqueConstraint(name = "uq_season_team",
                columnNames = {"league_season_id", "team_id"}),
        indexes = @Index(name = "idx_lst_season_id", columnList = "league_season_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeagueSeasonTeam extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "league_season_id", nullable = false)
    private LeagueSeason leagueSeason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /** Final league standing position — set when season completes */
    @Column(name = "final_position")
    private Integer finalPosition;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
