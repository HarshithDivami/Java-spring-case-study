package com.harshith.assigment.api;

import com.harshith.assigment.common.dto.ApiResponse;
import com.harshith.assigment.domain.prediction.dto.*;
import com.harshith.assigment.domain.prediction.service.PredictionService;
import com.harshith.assigment.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    // --- Match predictions ---

    @PutMapping("/api/v1/matches/{matchId}/prediction")
    public ResponseEntity<ApiResponse<MatchPredictionDto>> submitMatchPrediction(
            @PathVariable UUID matchId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SubmitMatchPredictionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                predictionService.submitMatchPrediction(matchId, principal.getId(), request)));
    }

    @GetMapping("/api/v1/matches/{matchId}/prediction")
    public ResponseEntity<ApiResponse<MatchPredictionDto>> getMyMatchPrediction(
            @PathVariable UUID matchId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                predictionService.getMyMatchPrediction(matchId, principal.getId())));
    }

    @GetMapping("/api/v1/matches/{matchId}/predictions")
    public ResponseEntity<ApiResponse<List<MatchPredictionDto>>> getAllMatchPredictions(
            @PathVariable UUID matchId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                predictionService.getAllMatchPredictions(matchId, principal.getId())));
    }

    // --- League predictions ---

    @PutMapping("/api/v1/seasons/{seasonId}/prediction")
    public ResponseEntity<ApiResponse<LeaguePredictionDto>> submitLeaguePrediction(
            @PathVariable UUID seasonId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SubmitLeaguePredictionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                predictionService.submitLeaguePrediction(seasonId, principal.getId(), request)));
    }

    @GetMapping("/api/v1/seasons/{seasonId}/prediction")
    public ResponseEntity<ApiResponse<LeaguePredictionDto>> getMyLeaguePrediction(
            @PathVariable UUID seasonId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                predictionService.getMyLeaguePrediction(seasonId, principal.getId())));
    }

    @GetMapping("/api/v1/seasons/{seasonId}/predictions")
    public ResponseEntity<ApiResponse<List<LeaguePredictionDto>>> getAllLeaguePredictions(
            @PathVariable UUID seasonId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                predictionService.getAllLeaguePredictions(seasonId, principal.getId())));
    }
}
