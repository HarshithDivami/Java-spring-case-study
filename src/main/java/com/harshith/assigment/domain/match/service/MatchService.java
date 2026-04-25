package com.harshith.assigment.domain.match.service;

import com.harshith.assigment.domain.match.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/** Manages match scheduling, result publishing, and result notifications within a season. */
public interface MatchService {

    /** Schedules a new match within the given season. */
    MatchDto createMatch(UUID seasonId, CreateMatchRequest request);

    /** Returns a paginated list of matches for the given season. */
    Page<MatchDto> listMatches(UUID seasonId, Pageable pageable);

    /** Returns a match by its ID. */
    MatchDto getMatch(UUID matchId);

    /** Updates scheduling details of an unplayed match. */
    MatchDto updateMatch(UUID matchId, CreateMatchRequest request);

    /**
     * Records the official result for a match, triggers point calculation,
     * and marks the match as COMPLETED.
     *
     * @param adminUserId the ID of the admin user publishing the result
     */
    MatchResultDto publishResult(UUID matchId, PublishResultRequest request, UUID adminUserId);

    /** Returns the recorded result for a completed match. */
    MatchResultDto getResult(UUID matchId);

    /** Sends the match result notification email to all active users asynchronously. */
    void notifyMatchResult(UUID matchId);
}
