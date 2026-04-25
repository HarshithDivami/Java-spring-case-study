package com.harshith.assigment.domain.prediction.service;

import com.harshith.assigment.domain.match.entity.MatchResult;
import com.harshith.assigment.domain.prediction.dto.*;

import java.util.List;
import java.util.UUID;

/** Handles submission, retrieval, and scoring of match and league predictions. */
public interface PredictionService {

    /**
     * Submits or updates the user's prediction for a match.
     * Throws {@link com.harshith.assigment.common.exception.PredictionLockedException}
     * if the match prediction window has closed.
     */
    MatchPredictionDto submitMatchPrediction(UUID matchId, UUID userId, SubmitMatchPredictionRequest request);

    /** Returns the calling user's prediction for the given match, if one exists. */
    MatchPredictionDto getMyMatchPrediction(UUID matchId, UUID userId);

    /** Returns all predictions for a match; admins see everyone's, regular users see only their own. */
    List<MatchPredictionDto> getAllMatchPredictions(UUID matchId, UUID requestingUserId);

    /**
     * Submits or updates the user's league-standings prediction for the given season.
     * Throws {@link com.harshith.assigment.common.exception.PredictionLockedException}
     * if the season prediction window has closed.
     */
    LeaguePredictionDto submitLeaguePrediction(UUID seasonId, UUID userId, SubmitLeaguePredictionRequest request);

    /** Returns the calling user's league prediction for the given season, if one exists. */
    LeaguePredictionDto getMyLeaguePrediction(UUID seasonId, UUID userId);

    /** Returns all league predictions for a season; visibility rules mirror {@link #getAllMatchPredictions}. */
    List<LeaguePredictionDto> getAllLeaguePredictions(UUID seasonId, UUID requestingUserId);

    /** Scores all match predictions against the published result and updates each user's season points. */
    void calculateMatchPoints(MatchResult result);

    /** Scores all league predictions for the season once final standings are known. */
    void calculateLeaguePoints(UUID seasonId);
}
