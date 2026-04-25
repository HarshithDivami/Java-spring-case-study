package com.harshith.assigment.api;

import com.harshith.assigment.common.dto.ApiResponse;
import com.harshith.assigment.common.dto.PagedResponse;
import com.harshith.assigment.domain.leaderboard.dto.LeaderboardEntryDto;
import com.harshith.assigment.domain.leaderboard.service.LeaderboardService;
import com.harshith.assigment.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seasons/{seasonId}/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<LeaderboardEntryDto>>> getLeaderboard(
            @PathVariable UUID seasonId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                new PagedResponse<>(leaderboardService.getLeaderboard(seasonId, pageable))));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LeaderboardEntryDto>> getMyRank(
            @PathVariable UUID seasonId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                leaderboardService.getMyRank(seasonId, principal.getId())));
    }
}
