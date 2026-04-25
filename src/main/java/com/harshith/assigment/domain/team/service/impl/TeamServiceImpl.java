package com.harshith.assigment.domain.team.service.impl;

import com.harshith.assigment.common.exception.AppException;
import com.harshith.assigment.common.exception.ResourceNotFoundException;
import com.harshith.assigment.domain.team.dto.*;
import com.harshith.assigment.domain.team.entity.Player;
import com.harshith.assigment.domain.team.entity.Team;
import com.harshith.assigment.domain.team.entity.TeamPlayer;
import com.harshith.assigment.domain.team.repository.PlayerRepository;
import com.harshith.assigment.domain.team.repository.TeamPlayerRepository;
import com.harshith.assigment.domain.team.repository.TeamRepository;
import com.harshith.assigment.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TeamPlayerRepository teamPlayerRepository;

    @Override
    @Transactional
    public TeamDto createTeam(CreateTeamRequest request) {
        Team team = Team.builder()
                .name(request.getName())
                .shortName(request.getShortName())
                .logoUrl(request.getLogoUrl())
                .homeGround(request.getHomeGround())
                .country(request.getCountry())
                .build();
        return TeamDto.from(teamRepository.save(team));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamDto> listTeams(Pageable pageable) {
        return teamRepository.findByDeletedFalse(pageable).map(TeamDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDto getTeam(UUID teamId) {
        return TeamDto.from(requireTeam(teamId));
    }

    @Override
    @Transactional
    public TeamDto updateTeam(UUID teamId, CreateTeamRequest request) {
        Team team = requireTeam(teamId);
        team.setName(request.getName());
        team.setShortName(request.getShortName());
        team.setLogoUrl(request.getLogoUrl());
        team.setHomeGround(request.getHomeGround());
        team.setCountry(request.getCountry());
        return TeamDto.from(teamRepository.save(team));
    }

    @Override
    @Transactional
    public void deleteTeam(UUID teamId) {
        Team team = requireTeam(teamId);
        team.setDeleted(true);
        team.setActive(false);
        teamRepository.save(team);
    }

    @Override
    @Transactional
    public PlayerDto addPlayer(UUID teamId, CreatePlayerRequest request) {
        Team team = requireTeam(teamId);

        Player player = Player.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .dateOfBirth(request.getDateOfBirth())
                .nationality(request.getNationality())
                .playerRole(request.getPlayerRole())
                .battingStyle(request.getBattingStyle())
                .bowlingStyle(request.getBowlingStyle())
                .build();
        player = playerRepository.save(player);

        TeamPlayer membership = TeamPlayer.builder()
                .team(team)
                .player(player)
                .joinedAt(LocalDate.now())
                .build();
        teamPlayerRepository.save(membership);

        return PlayerDto.from(player);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlayerDto> listPlayers(UUID teamId, Pageable pageable) {
        requireTeam(teamId);
        return playerRepository.findByDeletedFalse(pageable).map(PlayerDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerDto getPlayer(UUID playerId) {
        return PlayerDto.from(requirePlayer(playerId));
    }

    @Override
    @Transactional
    public PlayerDto updatePlayer(UUID playerId, CreatePlayerRequest request) {
        Player player = requirePlayer(playerId);
        player.setName(request.getName());
        player.setDisplayName(request.getDisplayName());
        player.setDateOfBirth(request.getDateOfBirth());
        player.setNationality(request.getNationality());
        player.setPlayerRole(request.getPlayerRole());
        player.setBattingStyle(request.getBattingStyle());
        player.setBowlingStyle(request.getBowlingStyle());
        return PlayerDto.from(playerRepository.save(player));
    }

    @Override
    @Transactional
    public void removePlayer(UUID teamId, UUID playerId) {
        TeamPlayer membership = teamPlayerRepository
                .findByTeamIdAndPlayerIdAndDeletedFalse(teamId, playerId)
                .orElseThrow(() -> new AppException(
                        "Player is not part of this team", HttpStatus.NOT_FOUND));
        membership.setActive(false);
        membership.setLeftAt(LocalDate.now());
        membership.setDeleted(true);
        teamPlayerRepository.save(membership);
    }

    private Team requireTeam(UUID id) {
        return teamRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
    }

    private Player requirePlayer(UUID id) {
        return playerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", "id", id));
    }
}
