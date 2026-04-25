package com.harshith.assigment.domain.team.service;

import com.harshith.assigment.domain.team.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/** Manages cricket teams and their player rosters. */
public interface TeamService {

    /** Creates a new team. */
    TeamDto createTeam(CreateTeamRequest request);

    /** Returns a paginated list of all teams. */
    Page<TeamDto> listTeams(Pageable pageable);

    /** Returns a team by its ID. */
    TeamDto getTeam(UUID teamId);

    /** Updates the team's details. */
    TeamDto updateTeam(UUID teamId, CreateTeamRequest request);

    /** Soft-deletes a team. */
    void deleteTeam(UUID teamId);

    /** Adds a player to the given team's roster. */
    PlayerDto addPlayer(UUID teamId, CreatePlayerRequest request);

    /** Returns a paginated list of players belonging to the given team. */
    Page<PlayerDto> listPlayers(UUID teamId, Pageable pageable);

    /** Returns a player by their ID regardless of which team they belong to. */
    PlayerDto getPlayer(UUID playerId);

    /** Updates a player's details. */
    PlayerDto updatePlayer(UUID playerId, CreatePlayerRequest request);

    /** Removes a player from the given team's roster. */
    void removePlayer(UUID teamId, UUID playerId);
}
