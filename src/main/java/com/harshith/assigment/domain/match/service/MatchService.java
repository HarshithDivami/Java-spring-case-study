package com.harshith.assigment.domain.match.service;

import com.harshith.assigment.domain.match.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MatchService {
    MatchDto createMatch(UUID seasonId, CreateMatchRequest request);
    Page<MatchDto> listMatches(UUID seasonId, Pageable pageable);
    MatchDto getMatch(UUID matchId);
    MatchDto updateMatch(UUID matchId, CreateMatchRequest request);
    MatchResultDto publishResult(UUID matchId, PublishResultRequest request, UUID adminUserId);
    MatchResultDto getResult(UUID matchId);
    void notifyMatchResult(UUID matchId);
}
