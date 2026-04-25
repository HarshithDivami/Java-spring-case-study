package com.harshith.assigment.domain.notification.service.impl;

import com.harshith.assigment.common.enums.EmailStatus;
import com.harshith.assigment.common.enums.EmailType;
import com.harshith.assigment.common.exception.ResourceNotFoundException;
import com.harshith.assigment.config.AppProperties;
import com.harshith.assigment.domain.leaderboard.entity.UserSeasonPoints;
import com.harshith.assigment.domain.leaderboard.repository.UserSeasonPointsRepository;
import com.harshith.assigment.domain.match.entity.Match;
import com.harshith.assigment.domain.match.entity.MatchResult;
import com.harshith.assigment.domain.match.repository.MatchResultRepository;
import com.harshith.assigment.domain.notification.dto.BulkEmailRequest;
import com.harshith.assigment.domain.notification.entity.EmailLog;
import com.harshith.assigment.domain.notification.repository.EmailLogRepository;
import com.harshith.assigment.domain.notification.service.EmailService;
import com.harshith.assigment.domain.prediction.entity.MatchPrediction;
import com.harshith.assigment.domain.prediction.repository.MatchPredictionRepository;
import com.harshith.assigment.domain.user.entity.User;
import com.harshith.assigment.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private static final int MAX_RETRIES = 3;

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;
    private final UserRepository userRepository;
    private final MatchPredictionRepository matchPredictionRepository;
    private final MatchResultRepository matchResultRepository;
    private final UserSeasonPointsRepository userSeasonPointsRepository;
    private final AppProperties appProperties;

    @Override
    @Async("taskExecutor")
    @Transactional
    public void sendResultPublishedNotification(MatchResult result) {
        Match match = result.getMatch();
        String subject = "Result Published: " + match.getHomeTeam().getName()
                + " vs " + match.getAwayTeam().getName();
        String body = buildResultBody(result);

        sendAndLog(null, appProperties.getAdmin().getAlertEmail(),
                EmailType.RESULT_PUBLISHED, subject, body,
                match.getId(), "Match");

        matchPredictionRepository.findByMatchIdAndDeletedFalse(match.getId())
                .forEach(pred -> sendAndLog(
                        pred.getUser().getId(), pred.getUser().getEmail(),
                        EmailType.RESULT_PUBLISHED, subject, body,
                        match.getId(), "Match"));
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public void sendMatchResultToAllUsers(UUID matchId) {
        MatchResult result = matchResultRepository.findByMatchId(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("MatchResult", "matchId", matchId));
        Match match = result.getMatch();
        UUID seasonId = match.getLeagueSeason().getId();

        String subject = "IPL 2026 | Match #" + match.getMatchNumber() + " Result: "
                + match.getHomeTeam().getShortName() + " vs " + match.getAwayTeam().getShortName();

        List<UserSeasonPoints> leaderboard = userSeasonPointsRepository.findAllForSeasonOrdered(seasonId);

        userRepository.findAllByDeletedFalseAndActiveTrue().forEach(user -> {
            Optional<MatchPrediction> predOpt = matchPredictionRepository
                    .findByMatchIdAndUserId(matchId, user.getId());
            Optional<UserSeasonPoints> pointsOpt = userSeasonPointsRepository
                    .findByUserIdAndLeagueSeasonId(user.getId(), seasonId);

            String body = buildRichBody(result, user, predOpt.orElse(null),
                    pointsOpt.orElse(null), leaderboard);

            sendAndLog(user.getId(), user.getEmail(),
                    EmailType.RESULT_PUBLISHED, subject, body,
                    match.getId(), "Match");
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public void sendMatchPredictionReminder(Match match) {
        List<UUID> userIds = matchPredictionRepository.findUserIdsWithoutPrediction(
                match.getId(), match.getLeagueSeason().getId());

        String subject = "Reminder: Predict before lock — "
                + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName();
        String body = "The prediction window closes soon for match #" + match.getMatchNumber()
                + ". Submit your predictions now!";

        userIds.forEach(userId -> userRepository.findById(userId).ifPresent(user ->
                sendAndLog(userId, user.getEmail(),
                        EmailType.MATCH_PREDICTION_REMINDER, subject, body,
                        match.getId(), "Match")));
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public void sendLeaguePredictionReminder(UUID seasonId) {
        List<User> activeUsers = userRepository.findAllByDeletedFalseAndActiveTrue();

        String subject = "Reminder: League standings prediction window is closing soon!";
        String body = "Don't forget to submit your league standings prediction before the window closes.";

        activeUsers.forEach(user ->
                sendAndLog(user.getId(), user.getEmail(),
                        EmailType.LEAGUE_PREDICTION_REMINDER, subject, body,
                        seasonId, "LeagueSeason"));
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public void sendBulkEmail(BulkEmailRequest request) {
        List<User> recipients = request.getUserIds() != null && !request.getUserIds().isEmpty()
                ? userRepository.findAllById(request.getUserIds())
                : userRepository.findAllByDeletedFalseAndActiveTrue();

        recipients.forEach(user ->
                sendAndLog(user.getId(), user.getEmail(),
                        request.getEmailType(), request.getSubject(), request.getBody(),
                        null, null));
    }

    @Override
    @Transactional
    public void retryFailedEmails() {
        List<EmailLog> failed = emailLogRepository
                .findByStatusAndRetryCountLessThan(EmailStatus.FAILED, MAX_RETRIES);
        failed.forEach(log -> {
            try {
                dispatch(log.getRecipientEmail(), log.getSubject(), log.getBody());
                log.setStatus(EmailStatus.SENT);
                log.setSentAt(Instant.now());
            } catch (MailException ex) {
                log.setRetryCount(log.getRetryCount() + 1);
                log.setErrorMessage(ex.getMessage());
                if (log.getRetryCount() >= MAX_RETRIES) {
                    log.setStatus(EmailStatus.FAILED);
                }
            }
            emailLogRepository.save(log);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailLog> getLogs(Pageable pageable) {
        return emailLogRepository.findByDeletedFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailLog> getLogsByUser(UUID userId, Pageable pageable) {
        return emailLogRepository.findByRecipientUserIdAndDeletedFalse(userId, pageable);
    }

    private String buildRichBody(MatchResult result, User user, MatchPrediction pred,
                                  UserSeasonPoints points, List<UserSeasonPoints> leaderboard) {
        Match match = result.getMatch();
        StringBuilder sb = new StringBuilder();

        sb.append("Hi ").append(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername()).append(",\n\n");

        // ── Match Result ──────────────────────────────────────────────────────
        sb.append("═══════════════════════════════════\n");
        sb.append("  MATCH #").append(match.getMatchNumber()).append(" RESULT\n");
        sb.append("═══════════════════════════════════\n");
        sb.append(match.getHomeTeam().getName()).append(" vs ").append(match.getAwayTeam().getName()).append("\n");
        if (match.getVenue() != null) {
            sb.append("Venue : ").append(match.getVenue()).append("\n");
        }
        sb.append("\n");
        if (result.isTie()) {
            sb.append("Result : TIE\n");
        } else if (result.getWinningTeam() != null) {
            sb.append("Winner : ").append(result.getWinningTeam().getName()).append("\n");
        }
        if (result.getTossWinningTeam() != null) {
            sb.append("Toss   : ").append(result.getTossWinningTeam().getName()).append("\n");
        }
        if (result.getPlayerOfMatch() != null) {
            sb.append("POTM   : ").append(result.getPlayerOfMatch().getName()).append("\n");
        }
        if (result.getResultSummary() != null) {
            sb.append("Summary: ").append(result.getResultSummary()).append("\n");
        }

        // ── Your Predictions ─────────────────────────────────────────────────
        sb.append("\n───────────────────────────────────\n");
        sb.append("  YOUR PREDICTIONS\n");
        sb.append("───────────────────────────────────\n");
        if (pred == null) {
            sb.append("You did not submit a prediction for this match.\n");
        } else {
            String winnerPred = pred.getPredictedWinner() != null ? pred.getPredictedWinner().getName() : "—";
            String tossPred   = pred.getPredictedTossWinner() != null ? pred.getPredictedTossWinner().getName() : "—";
            String potmPred   = pred.getPredictedPlayerOfMatch() != null ? pred.getPredictedPlayerOfMatch().getName() : "—";

            sb.append(String.format("%-8s %-22s %s\n", "Winner", winnerPred, pred.getWinnerPoints() > 0 ? "✓ +1 pt" : "✗  0 pts"));
            sb.append(String.format("%-8s %-22s %s\n", "Toss",   tossPred,   pred.getTossPoints()   > 0 ? "✓ +1 pt" : "✗  0 pts"));
            sb.append(String.format("%-8s %-22s %s\n", "POTM",   potmPred,   pred.getPotmPoints()   > 0 ? "✓ +1 pt" : "✗  0 pts"));
            sb.append("\nPoints earned this match: ").append(pred.getTotalPoints()).append(" / 3\n");
        }

        // ── Your Season Standing ─────────────────────────────────────────────
        sb.append("\n───────────────────────────────────\n");
        sb.append("  YOUR SEASON STANDING\n");
        sb.append("───────────────────────────────────\n");
        if (points == null) {
            sb.append("No season points recorded yet.\n");
        } else {
            sb.append("Rank              : #").append(points.getRank() != null ? points.getRank() : "—").append("\n");
            sb.append("Total Points      : ").append(points.getTotalPoints()).append("\n");
            sb.append("Match Points      : ").append(points.getMatchPoints()).append("\n");
            sb.append("League Pred Points: ").append(points.getLeaguePredictionPoints()).append("\n");
        }

        // ── Leaderboard ───────────────────────────────────────────────────────
        sb.append("\n───────────────────────────────────\n");
        sb.append("  SEASON LEADERBOARD\n");
        sb.append("───────────────────────────────────\n");
        sb.append(String.format("%-4s %-16s %s\n", "Rank", "Player", "Points"));
        sb.append("────────────────────────────────\n");
        for (int i = 0; i < leaderboard.size(); i++) {
            UserSeasonPoints entry = leaderboard.get(i);
            String name = entry.getUser().getDisplayName() != null
                    ? entry.getUser().getDisplayName() : entry.getUser().getUsername();
            String marker = entry.getUser().getId().equals(user.getId()) ? " ← you" : "";
            sb.append(String.format("%-4s %-16s %d%s\n", "#" + (i + 1), name, entry.getTotalPoints(), marker));
        }

        sb.append("\n\nGood luck for the next match!\n— Family League");
        return sb.toString();
    }

    private String buildResultBody(MatchResult result) {
        Match match = result.getMatch();
        StringBuilder sb = new StringBuilder();
        sb.append("Match #").append(match.getMatchNumber()).append(" Result\n\n");
        sb.append(match.getHomeTeam().getName()).append(" vs ").append(match.getAwayTeam().getName()).append("\n");
        if (result.isTie()) {
            sb.append("Result: TIE\n");
        } else if (result.getWinningTeam() != null) {
            sb.append("Winner: ").append(result.getWinningTeam().getName()).append("\n");
        }
        if (result.getTossWinningTeam() != null) {
            sb.append("Toss: ").append(result.getTossWinningTeam().getName()).append("\n");
        }
        if (result.getPlayerOfMatch() != null) {
            sb.append("Player of the Match: ").append(result.getPlayerOfMatch().getName()).append("\n");
        }
        if (result.getResultSummary() != null) {
            sb.append("\n").append(result.getResultSummary());
        }
        return sb.toString();
    }

    private void sendAndLog(UUID recipientUserId, String recipientEmail,
                             EmailType type, String subject, String body,
                             UUID referenceId, String referenceType) {
        EmailLog entry = EmailLog.builder()
                .recipientUserId(recipientUserId)
                .recipientEmail(recipientEmail)
                .emailType(type)
                .subject(subject)
                .body(body)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .build();

        try {
            dispatch(recipientEmail, subject, body);
            entry.setStatus(EmailStatus.SENT);
            entry.setSentAt(Instant.now());
        } catch (MailException ex) {
            entry.setStatus(EmailStatus.FAILED);
            entry.setErrorMessage(ex.getMessage());
            entry.setRetryCount(1);
            log.error("Failed to send {} email to {}: {}", type, recipientEmail, ex.getMessage());
        }
        emailLogRepository.save(entry);
    }

    private void dispatch(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(appProperties.getMail().getFrom());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
