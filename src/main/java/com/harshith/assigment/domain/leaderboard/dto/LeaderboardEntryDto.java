package com.harshith.assigment.domain.leaderboard.dto;

import com.harshith.assigment.domain.leaderboard.entity.UserSeasonPoints;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class LeaderboardEntryDto {
    private Integer rank;
    private UUID userId;
    private String username;
    private String displayName;
    private Integer matchPoints;
    private Integer leaguePredictionPoints;
    private Integer totalPoints;
    private Instant lastCalculatedAt;

    public static LeaderboardEntryDto from(UserSeasonPoints usp) {
        return LeaderboardEntryDto.builder()
                .rank(usp.getRank())
                .userId(usp.getUser().getId())
                .username(usp.getUser().getUsername())
                .displayName(usp.getUser().getDisplayName())
                .matchPoints(usp.getMatchPoints())
                .leaguePredictionPoints(usp.getLeaguePredictionPoints())
                .totalPoints(usp.getTotalPoints())
                .lastCalculatedAt(usp.getLastCalculatedAt())
                .build();
    }
}
