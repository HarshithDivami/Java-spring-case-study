package com.harshith.assigment.api;

import com.harshith.assigment.common.dto.ApiResponse;
import com.harshith.assigment.common.dto.PagedResponse;
import com.harshith.assigment.domain.notification.dto.BulkEmailRequest;
import com.harshith.assigment.domain.notification.entity.EmailLog;
import com.harshith.assigment.domain.notification.service.EmailService;
import com.harshith.assigment.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> sendBulkEmail(
            @Valid @RequestBody BulkEmailRequest request) {
        emailService.sendBulkEmail(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<EmailLog>>> getLogs(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                new PagedResponse<>(emailService.getLogs(pageable))));
    }

    @GetMapping("/logs/me")
    public ResponseEntity<ApiResponse<PagedResponse<EmailLog>>> getMyLogs(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                new PagedResponse<>(emailService.getLogsByUser(principal.getId(), pageable))));
    }
}
