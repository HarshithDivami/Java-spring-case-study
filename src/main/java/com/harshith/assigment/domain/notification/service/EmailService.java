package com.harshith.assigment.domain.notification.service;

import com.harshith.assigment.domain.match.entity.MatchResult;
import com.harshith.assigment.domain.match.entity.Match;
import com.harshith.assigment.domain.notification.dto.BulkEmailRequest;
import com.harshith.assigment.domain.notification.entity.EmailLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/** Sends transactional and bulk emails and maintains an audit log of every send attempt. */
public interface EmailService {

    /** Notifies the admin and each predictor when a match result is published. */
    void sendResultPublishedNotification(MatchResult result);

    /**
     * Sends a personalised match result email (including prediction scorecard and leaderboard)
     * to every active user for the given match.
     */
    void sendMatchResultToAllUsers(UUID matchId);

    /** Reminds users who have not yet submitted a match prediction before the lock deadline. */
    void sendMatchPredictionReminder(Match match);

    /** Reminds all active users to submit their league-standings prediction before the season lock. */
    void sendLeaguePredictionReminder(UUID seasonId);

    /** Sends a custom subject/body email to a specific set of users, or to all active users if none specified. */
    void sendBulkEmail(BulkEmailRequest request);

    /** Retries up to {@code MAX_RETRIES} times for all emails currently in FAILED status. */
    void retryFailedEmails();

    /** Returns a paginated audit log of all email send attempts. */
    Page<EmailLog> getLogs(Pageable pageable);

    /** Returns a paginated audit log of email send attempts for a specific recipient user. */
    Page<EmailLog> getLogsByUser(UUID userId, Pageable pageable);
}
