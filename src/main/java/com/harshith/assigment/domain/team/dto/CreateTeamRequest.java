package com.harshith.assigment.domain.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTeamRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 10)
    private String shortName;

    @Size(max = 500)
    private String logoUrl;

    @Size(max = 200)
    private String homeGround;

    @Size(max = 100)
    private String country;
}
