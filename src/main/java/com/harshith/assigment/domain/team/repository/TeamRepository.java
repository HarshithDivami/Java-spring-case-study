package com.harshith.assigment.domain.team.repository;

import com.harshith.assigment.domain.team.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    Optional<Team> findByIdAndDeletedFalse(UUID id);

    Page<Team> findByDeletedFalse(Pageable pageable);

    @Query("SELECT t FROM Team t WHERE t.deleted = false AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(t.shortName) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Team> searchByName(@Param("q") String query, Pageable pageable);

    @Query("SELECT t FROM Team t WHERE t.deleted = false AND " +
           "(LOWER(t.shortName) = LOWER(:name) OR LOWER(t.name) = LOWER(:name))")
    Optional<Team> findByShortNameOrNameIgnoreCase(@Param("name") String name);
}
