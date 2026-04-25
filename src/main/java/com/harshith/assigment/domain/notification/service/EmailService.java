package com.harshith.assigment.domain.notification.service;

import com.harshith.assigment.domain.match.entity.MatchResult;
import com.harshith.assigment.domain.match.entity.Match;
import com.harshith.assigment.domain.notification.dto.BulkEmailRequest;
import com.harshith.assigment.domain.notification.entity.EmailLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EmailService {
    void sendResultPublishedNotification(MatchResult result);
    void sendMatchResultToAllUsers(UUID matchId);
    void sendMatchPredictionReminder(Match match);
    void sendLeaguePredictionReminder(UUID seasonId);
    void sendBulkEmail(BulkEmailRequest request);
    void retryFailedEmails();
    Page<EmailLog> getLogs(Pageable pageable);
    Page<EmailLog> getLogsByUser(UUID userId, Pageable pageable);
}
