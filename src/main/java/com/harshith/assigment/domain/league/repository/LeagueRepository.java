package com.harshith.assigment.domain.league.repository;

import com.harshith.assigment.domain.league.entity.League;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface LeagueRepository extends JpaRepository<League, UUID> {

    Optional<League> findByIdAndDeletedFalse(UUID id);

    Page<League> findByDeletedFalse(Pageable pageable);

    @Query("SELECT l FROM League l WHERE l.deleted = false AND " +
           "LOWER(l.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<League> searchByName(@Param("q") String query, Pageable pageable);
}
