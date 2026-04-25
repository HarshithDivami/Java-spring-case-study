package com.harshith.assigment.domain.team.repository;

import com.harshith.assigment.domain.team.entity.TeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, UUID> {

    List<TeamPlayer> findByTeamIdAndDeletedFalse(UUID teamId);

    List<TeamPlayer> findByPlayerIdAndDeletedFalse(UUID playerId);

    Optional<TeamPlayer> findByTeamIdAndPlayerIdAndDeletedFalse(UUID teamId, UUID playerId);

    boolean existsByTeamIdAndPlayerIdAndActiveTrue(UUID teamId, UUID playerId);
}
