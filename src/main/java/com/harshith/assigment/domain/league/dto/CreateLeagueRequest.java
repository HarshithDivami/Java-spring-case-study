package com.harshith.assigment.domain.league.dto;

import com.harshith.assigment.common.enums.SportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLeagueRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    private String description;

    private SportType sportType = SportType.CRICKET;
}
