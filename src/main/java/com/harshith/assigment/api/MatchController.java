package com.harshith.assigment.api;

import com.harshith.assigment.common.dto.ApiResponse;
import com.harshith.assigment.common.dto.PagedResponse;
import com.harshith.assigment.domain.match.dto.*;
import com.harshith.assigment.domain.match.service.MatchService;
import com.harshith.assigment.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seasons/{seasonId}/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MatchDto>> createMatch(
            @PathVariable UUID seasonId,
            @Valid @RequestBody CreateMatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(matchService.createMatch(seasonId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<MatchDto>>> listMatches(
            @PathVariable UUID seasonId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                new PagedResponse<>(matchService.listMatches(seasonId, pageable))));
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<ApiResponse<MatchDto>> getMatch(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ApiResponse.success(matchService.getMatch(matchId)));
    }

    @PutMapping("/{matchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MatchDto>> updateMatch(
            @PathVariable UUID matchId,
            @Valid @RequestBody CreateMatchRequest request) {
        return ResponseEntity.ok(ApiResponse.success(matchService.updateMatch(matchId, request)));
    }

    @PostMapping("/{matchId}/result")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MatchResultDto>> publishResult(
            @PathVariable UUID matchId,
            @Valid @RequestBody PublishResultRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        matchService.publishResult(matchId, request, principal.getId())));
    }

    @GetMapping("/{matchId}/result")
    public ResponseEntity<ApiResponse<MatchResultDto>> getResult(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ApiResponse.success(matchService.getResult(matchId)));
    }

    @PostMapping("/{matchId}/result/notify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> notifyMatchResult(@PathVariable UUID matchId) {
        matchService.notifyMatchResult(matchId);
        return ResponseEntity.ok(ApiResponse.<Void>success("Result notification sent to all users", null));
    }
}
