package com.harshith.assigment.domain.league.service;

import com.harshith.assigment.domain.league.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LeagueService {
    LeagueDto createLeague(CreateLeagueRequest request);
    Page<LeagueDto> listLeagues(String query, Pageable pageable);
    LeagueDto getLeague(UUID id);
    LeagueDto updateLeague(UUID id, CreateLeagueRequest request);
    void deleteLeague(UUID id);

    LeagueSeasonDto createSeason(UUID leagueId, CreateSeasonRequest request);
    Page<LeagueSeasonDto> listSeasons(UUID leagueId, Pageable pageable);
    LeagueSeasonDto getSeason(UUID seasonId);
    LeagueSeasonDto startSeason(UUID seasonId);
    LeagueSeasonDto closeSeason(UUID seasonId);
    LeagueSeasonDto addTeamToSeason(UUID seasonId, UUID teamId);
    void removeTeamFromSeason(UUID seasonId, UUID teamId);
}
