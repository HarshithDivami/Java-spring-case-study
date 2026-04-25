package com.harshith.assigment.domain.notification.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import com.harshith.assigment.common.enums.EmailStatus;
import com.harshith.assigment.common.enums.EmailType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_logs",
        indexes = {
                @Index(name = "idx_email_logs_recipient", columnList = "recipient_user_id"),
                @Index(name = "idx_email_logs_status", columnList = "status"),
                @Index(name = "idx_email_logs_type", columnList = "email_type"),
                @Index(name = "idx_email_logs_ref", columnList = "reference_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLog extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Null for external recipients (bulk admin messaging) */
    @Column(name = "recipient_user_id")
    private UUID recipientUserId;

    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_type", nullable = false, length = 100)
    private EmailType emailType;

    @Column(name = "subject", nullable = false, length = 500)
    private String subject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private EmailStatus status = EmailStatus.PENDING;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /** ID of the related entity (match, season, etc.) */
    @Column(name = "reference_id")
    private UUID referenceId;

    /** Type of the related entity for context */
    @Column(name = "reference_type", length = 100)
    private String referenceType;
}
