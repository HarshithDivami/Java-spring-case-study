package com.harshith.assigment.domain.match.dto;

import com.harshith.assigment.common.enums.MatchStatus;
import com.harshith.assigment.common.enums.MatchType;
import com.harshith.assigment.domain.match.entity.Match;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class MatchDto {
    private UUID id;
    private UUID seasonId;
    private UUID homeTeamId;
    private String homeTeamName;
    private UUID awayTeamId;
    private String awayTeamName;
    private Integer matchNumber;
    private MatchType matchType;
    private String venue;
    private Instant scheduledAt;
    private Instant predictionLockAt;
    private MatchStatus status;
    private boolean predictionWindowOpen;

    public static MatchDto from(Match match) {
        return MatchDto.builder()
                .id(match.getId())
                .seasonId(match.getLeagueSeason().getId())
                .homeTeamId(match.getHomeTeam().getId())
                .homeTeamName(match.getHomeTeam().getName())
                .awayTeamId(match.getAwayTeam().getId())
                .awayTeamName(match.getAwayTeam().getName())
                .matchNumber(match.getMatchNumber())
                .matchType(match.getMatchType())
                .venue(match.getVenue())
                .scheduledAt(match.getScheduledAt())
                .predictionLockAt(match.getPredictionLockAt())
                .status(match.getStatus())
                .predictionWindowOpen(Instant.now().isBefore(match.getPredictionLockAt()))
                .build();
    }
}
