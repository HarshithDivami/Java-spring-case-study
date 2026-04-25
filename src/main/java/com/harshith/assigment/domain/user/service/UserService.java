package com.harshith.assigment.domain.user.service;

import com.harshith.assigment.domain.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    LoginResponse login(LoginRequest request);
    UserDto register(RegisterRequest request);
    UserDto getCurrentUser(UUID userId);
    UserDto updateProfile(UUID userId, UpdateProfileRequest request);
    Page<UserDto> listUsers(String query, Pageable pageable);
    UserDto getUser(UUID id);
    void deactivateUser(UUID id);
    void activateUser(UUID id);
}
