package com.harshith.assigment.domain.league.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSeasonRequest {

    @NotBlank
    @Size(max = 200)
    private String seasonName;

    @NotNull
    @Min(1)
    private Integer seasonNumber;

    @Min(1)
    private Integer leaguePredictionLockHours = 4;

    @Min(1)
    private Integer matchPredictionLockHours = 1;
}
