package com.harshith.assigment.domain.notification.dto;

import com.harshith.assigment.common.enums.EmailType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BulkEmailRequest {

    /** If empty, sends to all active users */
    private List<UUID> userIds;

    @NotNull
    private EmailType emailType;

    @NotBlank
    @Size(max = 500)
    private String subject;

    @NotBlank
    private String body;
}
