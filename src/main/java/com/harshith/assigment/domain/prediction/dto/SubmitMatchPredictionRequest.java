package com.harshith.assigment.domain.prediction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitMatchPredictionRequest {

    private String predictedWinnerName;

    private String predictedTossWinnerName;

    private String predictedPlayerOfMatchName;
}
