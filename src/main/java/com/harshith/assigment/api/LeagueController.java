package com.harshith.assigment.api;

import com.harshith.assigment.common.dto.ApiResponse;
import com.harshith.assigment.common.dto.PagedResponse;
import com.harshith.assigment.domain.league.dto.*;
import com.harshith.assigment.domain.league.service.LeagueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leagues")
@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeagueDto>> createLeague(
            @Valid @RequestBody CreateLeagueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(leagueService.createLeague(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<LeagueDto>>> listLeagues(
            @RequestParam(required = false, defaultValue = "") String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                new PagedResponse<>(leagueService.listLeagues(q, pageable))));
    }

    @GetMapping("/{leagueId}")
    public ResponseEntity<ApiResponse<LeagueDto>> getLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(ApiResponse.success(leagueService.getLeague(leagueId)));
    }

    @PutMapping("/{leagueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeagueDto>> updateLeague(
            @PathVariable UUID leagueId,
            @Valid @RequestBody CreateLeagueRequest request) {
        return ResponseEntity.ok(ApiResponse.success(leagueService.updateLeague(leagueId, request)));
    }

    @DeleteMapping("/{leagueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLeague(@PathVariable UUID leagueId) {
        leagueService.deleteLeague(leagueId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // --- Seasons ---

    @PostMapping("/{leagueId}/seasons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeagueSeasonDto>> createSeason(
            @PathVariable UUID leagueId,
            @Valid @RequestBody CreateSeasonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(leagueService.createSeason(leagueId, request)));
    }

    @GetMapping("/{leagueId}/seasons")
    public ResponseEntity<ApiResponse<PagedResponse<LeagueSeasonDto>>> listSeasons(
            @PathVariable UUID leagueId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                new PagedResponse<>(leagueService.listSeasons(leagueId, pageable))));
    }

    @GetMapping("/{leagueId}/seasons/{seasonId}")
    public ResponseEntity<ApiResponse<LeagueSeasonDto>> getSeason(
            @PathVariable UUID leagueId,
            @PathVariable UUID seasonId) {
        return ResponseEntity.ok(ApiResponse.success(leagueService.getSeason(seasonId)));
    }

    @PostMapping("/{leagueId}/seasons/{seasonId}/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeagueSeasonDto>> startSeason(
            @PathVariable UUID leagueId,
            @PathVariable UUID seasonId) {
        return ResponseEntity.ok(ApiResponse.success(leagueService.startSeason(seasonId)));
    }

    @PostMapping("/{leagueId}/seasons/{seasonId}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeagueSeasonDto>> closeSeason(
            @PathVariable UUID leagueId,
            @PathVariable UUID seasonId) {
        return ResponseEntity.ok(ApiResponse.success(leagueService.closeSeason(seasonId)));
    }

    @PostMapping("/{leagueId}/seasons/{seasonId}/teams/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeagueSeasonDto>> addTeamToSeason(
            @PathVariable UUID leagueId,
            @PathVariable UUID seasonId,
            @PathVariable UUID teamId) {
        return ResponseEntity.ok(ApiResponse.success(
                leagueService.addTeamToSeason(seasonId, teamId)));
    }

    @DeleteMapping("/{leagueId}/seasons/{seasonId}/teams/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeTeamFromSeason(
            @PathVariable UUID leagueId,
            @PathVariable UUID seasonId,
            @PathVariable UUID teamId) {
        leagueService.removeTeamFromSeason(seasonId, teamId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
