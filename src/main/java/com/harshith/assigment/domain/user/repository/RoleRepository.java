package com.harshith.assigment.domain.user.repository;

import com.harshith.assigment.common.enums.RoleName;
import com.harshith.assigment.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);
}
