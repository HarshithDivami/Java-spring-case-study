package com.harshith.assigment.domain.user.repository;

import com.harshith.assigment.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsernameAndDeletedFalse(String username);

    Optional<User> findByEmailAndDeletedFalse(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           " LOWER(u.displayName) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<User> searchUsers(@Param("q") String query, Pageable pageable);

    Page<User> findByDeletedFalse(Pageable pageable);

    java.util.List<User> findAllByDeletedFalseAndActiveTrue();
}
