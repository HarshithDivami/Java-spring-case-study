package com.harshith.assigment.api;

import com.harshith.assigment.common.dto.ApiResponse;
import com.harshith.assigment.common.dto.PagedResponse;
import com.harshith.assigment.domain.team.dto.*;
import com.harshith.assigment.domain.team.service.TeamService;
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
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TeamDto>> createTeam(
            @Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(teamService.createTeam(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TeamDto>>> listTeams(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                new PagedResponse<>(teamService.listTeams(pageable))));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamDto>> getTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(ApiResponse.success(teamService.getTeam(teamId)));
    }

    @PutMapping("/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TeamDto>> updateTeam(
            @PathVariable UUID teamId,
            @Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.ok(ApiResponse.success(teamService.updateTeam(teamId, request)));
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(@PathVariable UUID teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // --- Players ---

    @PostMapping("/{teamId}/players")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlayerDto>> addPlayer(
            @PathVariable UUID teamId,
            @Valid @RequestBody CreatePlayerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(teamService.addPlayer(teamId, request)));
    }

    @GetMapping("/{teamId}/players")
    public ResponseEntity<ApiResponse<PagedResponse<PlayerDto>>> listPlayers(
            @PathVariable UUID teamId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                new PagedResponse<>(teamService.listPlayers(teamId, pageable))));
    }

    @GetMapping("/players/{playerId}")
    public ResponseEntity<ApiResponse<PlayerDto>> getPlayer(@PathVariable UUID playerId) {
        return ResponseEntity.ok(ApiResponse.success(teamService.getPlayer(playerId)));
    }

    @PutMapping("/players/{playerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlayerDto>> updatePlayer(
            @PathVariable UUID playerId,
            @Valid @RequestBody CreatePlayerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(teamService.updatePlayer(playerId, request)));
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removePlayer(
            @PathVariable UUID teamId,
            @PathVariable UUID playerId) {
        teamService.removePlayer(teamId, playerId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
