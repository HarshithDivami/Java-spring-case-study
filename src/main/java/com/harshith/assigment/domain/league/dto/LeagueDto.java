package com.harshith.assigment.domain.league.dto;

import com.harshith.assigment.common.enums.SportType;
import com.harshith.assigment.domain.league.entity.League;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class LeagueDto {
    private UUID id;
    private String name;
    private String description;
    private SportType sportType;
    private boolean active;
    private int seasonCount;
    private Instant createdAt;

    public static LeagueDto from(League league) {
        return LeagueDto.builder()
                .id(league.getId())
                .name(league.getName())
                .description(league.getDescription())
                .sportType(league.getSportType())
                .active(league.isActive())
                .seasonCount(league.getSeasons().size())
                .createdAt(league.getCreatedAt())
                .build();
    }
}
