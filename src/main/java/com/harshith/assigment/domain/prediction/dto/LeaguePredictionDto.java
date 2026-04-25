package com.harshith.assigment.domain.prediction.dto;

import com.harshith.assigment.domain.prediction.entity.LeaguePrediction;
import com.harshith.assigment.domain.prediction.entity.LeaguePredictionEntry;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class LeaguePredictionDto {
    private UUID id;
    private UUID seasonId;
    private UUID userId;
    private String username;
    private boolean locked;
    private Instant lockedAt;
    private Integer totalPoints;
    private List<EntryDto> entries;

    @Getter
    @Builder
    public static class EntryDto {
        private Integer position;
        private UUID teamId;
        private String teamName;
        private Integer pointsAwarded;
    }

    public static LeaguePredictionDto from(LeaguePrediction lp, boolean hideUser) {
        return LeaguePredictionDto.builder()
                .id(lp.getId())
                .seasonId(lp.getLeagueSeason().getId())
                .userId(hideUser ? null : lp.getUser().getId())
                .username(hideUser ? null : lp.getUser().getUsername())
                .locked(lp.isLocked())
                .lockedAt(lp.getLockedAt())
                .totalPoints(lp.getTotalPoints())
                .entries(lp.getEntries().stream()
                        .map(e -> EntryDto.builder()
                                .position(e.getPosition())
                                .teamId(e.getTeam().getId())
                                .teamName(e.getTeam().getName())
                                .pointsAwarded(e.getPointsAwarded())
                                .build())
                        .sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
                        .collect(Collectors.toList()))
                .build();
    }
}
