package com.harshith.assigment.domain.league.service.impl;

import com.harshith.assigment.common.enums.LeagueSeasonStatus;
import com.harshith.assigment.common.exception.AppException;
import com.harshith.assigment.common.exception.ConflictException;
import com.harshith.assigment.common.exception.ResourceNotFoundException;
import com.harshith.assigment.domain.league.dto.*;
import com.harshith.assigment.domain.league.entity.League;
import com.harshith.assigment.domain.league.entity.LeagueSeason;
import com.harshith.assigment.domain.league.entity.LeagueSeasonTeam;
import com.harshith.assigment.domain.league.repository.LeagueRepository;
import com.harshith.assigment.domain.league.repository.LeagueSeasonRepository;
import com.harshith.assigment.domain.league.repository.LeagueSeasonTeamRepository;
import com.harshith.assigment.domain.league.service.LeagueService;
import com.harshith.assigment.domain.team.entity.Team;
import com.harshith.assigment.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeagueServiceImpl implements LeagueService {

    private final LeagueRepository leagueRepository;
    private final LeagueSeasonRepository seasonRepository;
    private final LeagueSeasonTeamRepository seasonTeamRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public LeagueDto createLeague(CreateLeagueRequest request) {
        League league = League.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sportType(request.getSportType())
                .build();
        return LeagueDto.from(leagueRepository.save(league));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeagueDto> listLeagues(String query, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return leagueRepository.searchByName(query, pageable).map(LeagueDto::from);
        }
        return leagueRepository.findByDeletedFalse(pageable).map(LeagueDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public LeagueDto getLeague(UUID id) {
        return LeagueDto.from(requireLeague(id));
    }

    @Override
    @Transactional
    public LeagueDto updateLeague(UUID id, CreateLeagueRequest request) {
        League league = requireLeague(id);
        league.setName(request.getName());
        if (request.getDescription() != null) league.setDescription(request.getDescription());
        if (request.getSportType() != null) league.setSportType(request.getSportType());
        return LeagueDto.from(leagueRepository.save(league));
    }

    @Override
    @Transactional
    public void deleteLeague(UUID id) {
        League league = requireLeague(id);
        league.setDeleted(true);
        leagueRepository.save(league);
    }

    @Override
    @Transactional
    public LeagueSeasonDto createSeason(UUID leagueId, CreateSeasonRequest request) {
        League league = requireLeague(leagueId);
        if (seasonRepository.existsByLeagueIdAndSeasonNumber(leagueId, request.getSeasonNumber())) {
            throw new ConflictException("Season " + request.getSeasonNumber() + " already exists for this league");
        }
        LeagueSeason season = LeagueSeason.builder()
                .league(league)
                .seasonName(request.getSeasonName())
                .seasonNumber(request.getSeasonNumber())
                .leaguePredictionLockHours(request.getLeaguePredictionLockHours())
                .matchPredictionLockHours(request.getMatchPredictionLockHours())
                .build();
        return LeagueSeasonDto.from(seasonRepository.save(season));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeagueSeasonDto> listSeasons(UUID leagueId, Pageable pageable) {
        requireLeague(leagueId);
        return seasonRepository.findByLeagueIdAndDeletedFalse(leagueId, pageable)
                .map(LeagueSeasonDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public LeagueSeasonDto getSeason(UUID seasonId) {
        return LeagueSeasonDto.from(requireSeason(seasonId));
    }

    @Override
    @Transactional
    public LeagueSeasonDto startSeason(UUID seasonId) {
        LeagueSeason season = requireSeason(seasonId);
        if (season.getStatus() != LeagueSeasonStatus.DRAFT &&
                season.getStatus() != LeagueSeasonStatus.REGISTRATION) {
            throw new AppException("Season can only be started from DRAFT or REGISTRATION status",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        season.setStatus(LeagueSeasonStatus.ACTIVE);
        return LeagueSeasonDto.from(seasonRepository.save(season));
    }

    @Override
    @Transactional
    public LeagueSeasonDto closeSeason(UUID seasonId) {
        LeagueSeason season = requireSeason(seasonId);
        if (season.getStatus() == LeagueSeasonStatus.CLOSED) {
            throw new AppException("Season is already closed", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        season.setStatus(LeagueSeasonStatus.CLOSED);
        return LeagueSeasonDto.from(seasonRepository.save(season));
    }

    @Override
    @Transactional
    public LeagueSeasonDto addTeamToSeason(UUID seasonId, UUID teamId) {
        LeagueSeason season = requireSeason(seasonId);
        Team team = teamRepository.findByIdAndDeletedFalse(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        if (seasonTeamRepository.existsByLeagueSeasonIdAndTeamId(seasonId, teamId)) {
            throw new ConflictException("Team is already in this season");
        }
        seasonTeamRepository.save(LeagueSeasonTeam.builder()
                .leagueSeason(season).team(team).build());
        return LeagueSeasonDto.from(seasonRepository.findById(seasonId).get());
    }

    @Override
    @Transactional
    public void removeTeamFromSeason(UUID seasonId, UUID teamId) {
        LeagueSeasonTeam lst = seasonTeamRepository
                .findByLeagueSeasonIdAndTeamIdAndDeletedFalse(seasonId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Team not found in this season"));
        lst.setDeleted(true);
        seasonTeamRepository.save(lst);
    }

    private League requireLeague(UUID id) {
        return leagueRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", id));
    }

    private LeagueSeason requireSeason(UUID id) {
        return seasonRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeagueSeason", "id", id));
    }
}
