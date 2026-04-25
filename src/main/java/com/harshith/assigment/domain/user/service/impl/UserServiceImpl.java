package com.harshith.assigment.domain.user.service.impl;

import com.harshith.assigment.common.enums.RoleName;
import com.harshith.assigment.common.exception.ConflictException;
import com.harshith.assigment.common.exception.ResourceNotFoundException;
import com.harshith.assigment.domain.user.dto.*;
import com.harshith.assigment.domain.user.entity.User;
import com.harshith.assigment.domain.user.repository.RoleRepository;
import com.harshith.assigment.domain.user.repository.UserRepository;
import com.harshith.assigment.domain.user.service.UserService;
import com.harshith.assigment.security.JwtTokenProvider;
import com.harshith.assigment.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(), request.getPassword()));
        String token = tokenProvider.generateToken(auth);
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Set<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(principal.getId())
                .username(principal.getUsername())
                .email(principal.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered: " + request.getEmail());
        }
        var userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName() != null
                        ? request.getDisplayName() : request.getUsername())
                .build();
        user.getRoles().add(userRole);
        return UserDto.from(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UUID userId) {
        return UserDto.from(findUserById(userId));
    }

    @Override
    @Transactional
    public UserDto updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = findUserById(userId);
        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        return UserDto.from(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> listUsers(String query, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return userRepository.searchUsers(query, pageable).map(UserDto::from);
        }
        return userRepository.findByDeletedFalse(pageable).map(UserDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(UUID id) {
        return UserDto.from(findUserById(id));
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {
        User user = findUserById(id);
        user.setActive(false);
        userRepository.save(user);
        log.info("User deactivated: {}", id);
    }

    @Override
    @Transactional
    public void activateUser(UUID id) {
        User user = findUserById(id);
        user.setActive(true);
        userRepository.save(user);
        log.info("User activated: {}", id);
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
