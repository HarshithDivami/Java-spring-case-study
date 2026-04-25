package com.harshith.assigment.domain.team.service;

import com.harshith.assigment.domain.team.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TeamService {
    TeamDto createTeam(CreateTeamRequest request);
    Page<TeamDto> listTeams(Pageable pageable);
    TeamDto getTeam(UUID teamId);
    TeamDto updateTeam(UUID teamId, CreateTeamRequest request);
    void deleteTeam(UUID teamId);

    PlayerDto addPlayer(UUID teamId, CreatePlayerRequest request);
    Page<PlayerDto> listPlayers(UUID teamId, Pageable pageable);
    PlayerDto getPlayer(UUID playerId);
    PlayerDto updatePlayer(UUID playerId, CreatePlayerRequest request);
    void removePlayer(UUID teamId, UUID playerId);
}
