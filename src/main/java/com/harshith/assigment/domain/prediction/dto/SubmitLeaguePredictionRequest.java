package com.harshith.assigment.domain.prediction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SubmitLeaguePredictionRequest {

    @NotEmpty
    private List<Entry> entries;

    @Getter
    @Setter
    public static class Entry {
        @NotNull
        @Min(1)
        private Integer position;

        @NotNull
        private UUID teamId;
    }
}
