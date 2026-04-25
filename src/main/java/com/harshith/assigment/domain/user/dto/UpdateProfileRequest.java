package com.harshith.assigment.domain.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(max = 100)
    private String displayName;

    @Size(max = 500)
    private String avatarUrl;
}
