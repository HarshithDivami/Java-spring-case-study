package com.harshith.assigment.domain.league.dto;

import com.harshith.assigment.common.enums.LeagueSeasonStatus;
import com.harshith.assigment.domain.league.entity.LeagueSeason;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class LeagueSeasonDto {
    private UUID id;
    private UUID leagueId;
    private String leagueName;
    private String seasonName;
    private Integer seasonNumber;
    private LeagueSeasonStatus status;
    private Integer leaguePredictionLockHours;
    private Integer matchPredictionLockHours;
    private Instant firstMatchTime;
    private Instant leaguePredictionLockTime;
    private int teamCount;
    private Instant createdAt;

    public static LeagueSeasonDto from(LeagueSeason season) {
        return LeagueSeasonDto.builder()
                .id(season.getId())
                .leagueId(season.getLeague().getId())
                .leagueName(season.getLeague().getName())
                .seasonName(season.getSeasonName())
                .seasonNumber(season.getSeasonNumber())
                .status(season.getStatus())
                .leaguePredictionLockHours(season.getLeaguePredictionLockHours())
                .matchPredictionLockHours(season.getMatchPredictionLockHours())
                .firstMatchTime(season.getFirstMatchTime())
                .leaguePredictionLockTime(season.getLeaguePredictionLockTime())
                .teamCount(season.getSeasonTeams().size())
                .createdAt(season.getCreatedAt())
                .build();
    }
}
