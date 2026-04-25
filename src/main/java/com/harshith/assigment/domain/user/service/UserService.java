package com.harshith.assigment.domain.user.service;

import com.harshith.assigment.domain.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/** Manages user registration, authentication, profile updates, and account lifecycle. */
public interface UserService {

    /** Authenticates credentials and returns a JWT alongside the caller's profile summary. */
    LoginResponse login(LoginRequest request);

    /** Creates a new user account with the default {@code ROLE_USER} role. */
    UserDto register(RegisterRequest request);

    /** Returns the profile of the currently authenticated user. */
    UserDto getCurrentUser(UUID userId);

    /** Updates mutable profile fields (display name, avatar URL) for the given user. */
    UserDto updateProfile(UUID userId, UpdateProfileRequest request);

    /** Returns a paginated list of non-deleted users, optionally filtered by a search query. */
    Page<UserDto> listUsers(String query, Pageable pageable);

    /** Returns the profile of any non-deleted user by ID. */
    UserDto getUser(UUID id);

    /** Marks the account inactive; the user can no longer authenticate. */
    void deactivateUser(UUID id);

    /** Restores an inactive account, allowing the user to authenticate again. */
    void activateUser(UUID id);
}
