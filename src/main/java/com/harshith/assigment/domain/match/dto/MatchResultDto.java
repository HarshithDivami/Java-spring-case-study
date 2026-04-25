package com.harshith.assigment.domain.match.dto;

import com.harshith.assigment.domain.match.entity.MatchResult;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class MatchResultDto {
    private UUID id;
    private UUID matchId;
    private UUID winningTeamId;
    private String winningTeamName;
    private UUID tossWinningTeamId;
    private String tossWinningTeamName;
    private UUID playerOfMatchId;
    private String playerOfMatchName;
    private boolean tie;
    private String resultSummary;
    private Instant publishedAt;

    public static MatchResultDto from(MatchResult r) {
        return MatchResultDto.builder()
                .id(r.getId())
                .matchId(r.getMatch().getId())
                .winningTeamId(r.getWinningTeam() != null ? r.getWinningTeam().getId() : null)
                .winningTeamName(r.getWinningTeam() != null ? r.getWinningTeam().getName() : null)
                .tossWinningTeamId(r.getTossWinningTeam() != null ? r.getTossWinningTeam().getId() : null)
                .tossWinningTeamName(r.getTossWinningTeam() != null ? r.getTossWinningTeam().getName() : null)
                .playerOfMatchId(r.getPlayerOfMatch() != null ? r.getPlayerOfMatch().getId() : null)
                .playerOfMatchName(r.getPlayerOfMatch() != null ? r.getPlayerOfMatch().getName() : null)
                .tie(r.isTie())
                .resultSummary(r.getResultSummary())
                .publishedAt(r.getPublishedAt())
                .build();
    }
}
