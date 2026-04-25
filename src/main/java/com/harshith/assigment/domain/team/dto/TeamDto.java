package com.harshith.assigment.domain.team.dto;

import com.harshith.assigment.domain.team.entity.Team;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class TeamDto {
    private UUID id;
    private String name;
    private String shortName;
    private String logoUrl;
    private String homeGround;
    private String country;
    private boolean active;
    private Instant createdAt;

    public static TeamDto from(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .shortName(team.getShortName())
                .logoUrl(team.getLogoUrl())
                .homeGround(team.getHomeGround())
                .country(team.getCountry())
                .active(team.isActive())
                .createdAt(team.getCreatedAt())
                .build();
    }
}
