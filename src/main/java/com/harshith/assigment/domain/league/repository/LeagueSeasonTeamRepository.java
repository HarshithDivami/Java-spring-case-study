package com.harshith.assigment.domain.league.repository;

import com.harshith.assigment.domain.league.entity.LeagueSeasonTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeagueSeasonTeamRepository extends JpaRepository<LeagueSeasonTeam, UUID> {

    List<LeagueSeasonTeam> findByLeagueSeasonIdAndDeletedFalse(UUID leagueSeasonId);

    Optional<LeagueSeasonTeam> findByLeagueSeasonIdAndTeamIdAndDeletedFalse(
            UUID leagueSeasonId, UUID teamId);

    boolean existsByLeagueSeasonIdAndTeamId(UUID leagueSeasonId, UUID teamId);

    int countByLeagueSeasonIdAndDeletedFalse(UUID leagueSeasonId);
}
