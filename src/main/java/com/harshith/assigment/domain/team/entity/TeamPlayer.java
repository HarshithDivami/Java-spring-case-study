package com.harshith.assigment.domain.team.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "team_players",
        indexes = {
                @Index(name = "idx_team_players_team_id", columnList = "team_id"),
                @Index(name = "idx_team_players_player_id", columnList = "player_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamPlayer extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    @Column(name = "joined_at")
    private LocalDate joinedAt;

    @Column(name = "left_at")
    private LocalDate leftAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
