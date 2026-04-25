package com.harshith.assigment.domain.team.dto;

import com.harshith.assigment.common.enums.PlayerRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatePlayerRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 200)
    private String displayName;

    private LocalDate dateOfBirth;

    @Size(max = 100)
    private String nationality;

    private PlayerRole playerRole;

    @Size(max = 50)
    private String battingStyle;

    @Size(max = 50)
    private String bowlingStyle;
}
