package com.harshith.assigment.domain.match.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublishResultRequest {

    // Accept team short name (e.g. "CSK", "MI") or full name
    private String winningTeamName;

    private String tossWinningTeamName;

    // Accept player name (e.g. "MS Dhoni")
    private String playerOfMatchName;

    private boolean tie;

    @Size(max = 1000)
    private String resultSummary;
}
