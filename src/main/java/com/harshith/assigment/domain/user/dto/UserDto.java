package com.harshith.assigment.domain.user.dto;

import com.harshith.assigment.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
    private boolean active;
    private Set<String> roles;
    private Instant createdAt;

    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .active(user.isActive())
                .roles(user.getRoles().stream()
                        .map(r -> r.getName().name())
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
