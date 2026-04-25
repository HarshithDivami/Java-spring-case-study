package com.harshith.assigment.domain.match.dto;

import com.harshith.assigment.common.enums.MatchType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class CreateMatchRequest {

    @NotNull
    private UUID homeTeamId;

    @NotNull
    private UUID awayTeamId;

    @NotNull
    @Min(1)
    private Integer matchNumber;

    private MatchType matchType = MatchType.LEAGUE;

    @Size(max = 200)
    private String venue;

    @NotNull
    private Instant scheduledAt;
}
