package com.harshith.assigment.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private UUID userId;
    private String username;
    private String email;
    private Set<String> roles;
}
