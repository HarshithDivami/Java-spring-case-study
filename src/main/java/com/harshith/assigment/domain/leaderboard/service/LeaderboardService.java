package com.harshith.assigment.domain.leaderboard.service;

import com.harshith.assigment.domain.leaderboard.dto.LeaderboardEntryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/** Provides ranked standings for a league season. */
public interface LeaderboardService {

    /** Returns the full paginated leaderboard for the given season, ordered by total points descending. */
    Page<LeaderboardEntryDto> getLeaderboard(UUID seasonId, Pageable pageable);

    /** Returns the calling user's rank and points for the given season. */
    LeaderboardEntryDto getMyRank(UUID seasonId, UUID userId);
}
