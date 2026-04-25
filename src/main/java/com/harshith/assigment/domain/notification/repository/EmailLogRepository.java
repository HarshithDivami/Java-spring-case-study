package com.harshith.assigment.domain.notification.repository;

import com.harshith.assigment.common.enums.EmailStatus;
import com.harshith.assigment.common.enums.EmailType;
import com.harshith.assigment.domain.notification.entity.EmailLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {

    Page<EmailLog> findByDeletedFalse(Pageable pageable);

    Page<EmailLog> findByRecipientUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    Page<EmailLog> findByEmailTypeAndDeletedFalse(EmailType type, Pageable pageable);

    List<EmailLog> findByStatusAndRetryCountLessThan(EmailStatus status, int maxRetries);
}
