package com.harshith.assigment.domain.leaderboard.service.impl;

import com.harshith.assigment.common.exception.ResourceNotFoundException;
import com.harshith.assigment.domain.leaderboard.dto.LeaderboardEntryDto;
import com.harshith.assigment.domain.leaderboard.repository.UserSeasonPointsRepository;
import com.harshith.assigment.domain.leaderboard.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final UserSeasonPointsRepository pointsRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<LeaderboardEntryDto> getLeaderboard(UUID seasonId, Pageable pageable) {
        return pointsRepository.findLeaderboard(seasonId, pageable)
                .map(LeaderboardEntryDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaderboardEntryDto getMyRank(UUID seasonId, UUID userId) {
        return pointsRepository.findByUserIdAndLeagueSeasonId(userId, seasonId)
                .map(LeaderboardEntryDto::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No leaderboard entry found for this user and season"));
    }
}
