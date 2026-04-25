package com.harshith.assigment.domain.match.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import com.harshith.assigment.domain.team.entity.Player;
import com.harshith.assigment.domain.team.entity.Team;
import com.harshith.assigment.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "match_results",
        indexes = @Index(name = "idx_match_results_match_id", columnList = "match_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResult extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    private Match match;

    /** Null when match has no result (cancelled, rain, etc.) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winning_team_id")
    private Team winningTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toss_winning_team_id")
    private Team tossWinningTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_of_match_id")
    private Player playerOfMatch;

    @Column(name = "is_tie", nullable = false)
    @Builder.Default
    private boolean tie = false;

    @Column(name = "result_summary", columnDefinition = "TEXT")
    private String resultSummary;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "published_by", nullable = false)
    private User publishedBy;

    @Column(name = "published_at", nullable = false)
    @Builder.Default
    private Instant publishedAt = Instant.now();
}
