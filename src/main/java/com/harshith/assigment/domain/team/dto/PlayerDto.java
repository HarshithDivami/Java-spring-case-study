package com.harshith.assigment.domain.team.dto;

import com.harshith.assigment.common.enums.PlayerRole;
import com.harshith.assigment.domain.team.entity.Player;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class PlayerDto {
    private UUID id;
    private String name;
    private String displayName;
    private LocalDate dateOfBirth;
    private String nationality;
    private PlayerRole playerRole;
    private String battingStyle;
    private String bowlingStyle;
    private boolean active;

    public static PlayerDto from(Player player) {
        return PlayerDto.builder()
                .id(player.getId())
                .name(player.getName())
                .displayName(player.getDisplayName())
                .dateOfBirth(player.getDateOfBirth())
                .nationality(player.getNationality())
                .playerRole(player.getPlayerRole())
                .battingStyle(player.getBattingStyle())
                .bowlingStyle(player.getBowlingStyle())
                .active(player.isActive())
                .build();
    }
}
