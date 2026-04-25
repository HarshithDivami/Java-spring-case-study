package com.harshith.assigment.domain.team.repository;

import com.harshith.assigment.domain.team.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Optional<Player> findByIdAndDeletedFalse(UUID id);

    Page<Player> findByDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM Player p WHERE p.deleted = false AND " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Player> searchByName(@Param("q") String query, Pageable pageable);

    @Query("SELECT p FROM Player p WHERE p.deleted = false AND LOWER(p.name) = LOWER(:name)")
    Optional<Player> findByNameIgnoreCase(@Param("name") String name);
}
