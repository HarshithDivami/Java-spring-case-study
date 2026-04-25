package com.harshith.assigment.domain.prediction.dto;

import com.harshith.assigment.domain.prediction.entity.MatchPrediction;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class MatchPredictionDto {
    private UUID id;
    private UUID matchId;
    private UUID userId;
    private String username;
    private UUID predictedWinnerId;
    private String predictedWinnerName;
    private UUID predictedTossWinnerId;
    private String predictedTossWinnerName;
    private UUID predictedPlayerOfMatchId;
    private String predictedPlayerOfMatchName;
    private boolean locked;
    private Instant lockedAt;
    private Integer totalPoints;

    public static MatchPredictionDto from(MatchPrediction mp, boolean hideUser) {
        return MatchPredictionDto.builder()
                .id(mp.getId())
                .matchId(mp.getMatch().getId())
                .userId(hideUser ? null : mp.getUser().getId())
                .username(hideUser ? null : mp.getUser().getUsername())
                .predictedWinnerId(mp.getPredictedWinner() != null ? mp.getPredictedWinner().getId() : null)
                .predictedWinnerName(mp.getPredictedWinner() != null ? mp.getPredictedWinner().getName() : null)
                .predictedTossWinnerId(mp.getPredictedTossWinner() != null ? mp.getPredictedTossWinner().getId() : null)
                .predictedTossWinnerName(mp.getPredictedTossWinner() != null ? mp.getPredictedTossWinner().getName() : null)
                .predictedPlayerOfMatchId(mp.getPredictedPlayerOfMatch() != null ? mp.getPredictedPlayerOfMatch().getId() : null)
                .predictedPlayerOfMatchName(mp.getPredictedPlayerOfMatch() != null ? mp.getPredictedPlayerOfMatch().getName() : null)
                .locked(mp.isLocked())
                .lockedAt(mp.getLockedAt())
                .totalPoints(mp.getTotalPoints())
                .build();
    }
}
