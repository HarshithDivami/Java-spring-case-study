package com.harshith.assigment.domain.leaderboard.service;

import com.harshith.assigment.domain.leaderboard.dto.LeaderboardEntryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LeaderboardService {
    Page<LeaderboardEntryDto> getLeaderboard(UUID seasonId, Pageable pageable);
    LeaderboardEntryDto getMyRank(UUID seasonId, UUID userId);
}
