package com.harshith.assigment.domain.league.service;

import com.harshith.assigment.domain.league.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/** Manages leagues, their seasons, and season-team associations. */
public interface LeagueService {

    /** Creates a new league. */
    LeagueDto createLeague(CreateLeagueRequest request);

    /** Returns a paginated list of leagues, optionally filtered by a search query. */
    Page<LeagueDto> listLeagues(String query, Pageable pageable);

    /** Returns a league by its ID. */
    LeagueDto getLeague(UUID id);

    /** Updates the league's details. */
    LeagueDto updateLeague(UUID id, CreateLeagueRequest request);

    /** Soft-deletes a league. */
    void deleteLeague(UUID id);

    /** Creates a new season for the given league. */
    LeagueSeasonDto createSeason(UUID leagueId, CreateSeasonRequest request);

    /** Returns a paginated list of seasons for the given league. */
    Page<LeagueSeasonDto> listSeasons(UUID leagueId, Pageable pageable);

    /** Returns a season by its ID. */
    LeagueSeasonDto getSeason(UUID seasonId);

    /** Transitions the season from PENDING to ACTIVE. */
    LeagueSeasonDto startSeason(UUID seasonId);

    /** Transitions the season from ACTIVE to CLOSED. */
    LeagueSeasonDto closeSeason(UUID seasonId);

    /** Registers a team to participate in the given season. */
    LeagueSeasonDto addTeamToSeason(UUID seasonId, UUID teamId);

    /** Removes a team from the given season's participant list. */
    void removeTeamFromSeason(UUID seasonId, UUID teamId);
}
