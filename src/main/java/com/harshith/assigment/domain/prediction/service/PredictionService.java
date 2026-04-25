package com.harshith.assigment.domain.prediction.service;

import com.harshith.assigment.domain.match.entity.MatchResult;
import com.harshith.assigment.domain.prediction.dto.*;

import java.util.List;
import java.util.UUID;

public interface PredictionService {
    MatchPredictionDto submitMatchPrediction(UUID matchId, UUID userId, SubmitMatchPredictionRequest request);
    MatchPredictionDto getMyMatchPrediction(UUID matchId, UUID userId);
    List<MatchPredictionDto> getAllMatchPredictions(UUID matchId, UUID requestingUserId);

    LeaguePredictionDto submitLeaguePrediction(UUID seasonId, UUID userId, SubmitLeaguePredictionRequest request);
    LeaguePredictionDto getMyLeaguePrediction(UUID seasonId, UUID userId);
    List<LeaguePredictionDto> getAllLeaguePredictions(UUID seasonId, UUID requestingUserId);

    void calculateMatchPoints(MatchResult result);
    void calculateLeaguePoints(UUID seasonId);
}
