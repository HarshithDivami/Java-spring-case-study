package com.harshith.assigment.domain.notification.scheduler;

import com.harshith.assigment.domain.league.repository.LeagueSeasonRepository;
import com.harshith.assigment.domain.match.repository.MatchRepository;
import com.harshith.assigment.domain.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final MatchRepository matchRepository;
    private final LeagueSeasonRepository seasonRepository;
    private final EmailService emailService;

    /**
     * Runs every 15 minutes. Sends match prediction reminders for matches whose
     * prediction window closes within the next 30 minutes.
     */
    @Scheduled(fixedDelayString = "${app.scheduler.match-reminder-delay-ms:900000}")
    public void sendMatchPredictionReminders() {
        Instant now = Instant.now();
        Instant windowEnd = now.plus(30, ChronoUnit.MINUTES);
        matchRepository.findMatchesWithLockBetween(now, windowEnd).forEach(match -> {
            log.info("Sending match prediction reminder for match {}", match.getId());
            emailService.sendMatchPredictionReminder(match);
        });
    }

    /**
     * Runs every hour. Sends league prediction reminders for seasons whose
     * league prediction window closes within the next 2 hours.
     */
    @Scheduled(fixedDelayString = "${app.scheduler.league-reminder-delay-ms:3600000}")
    public void sendLeaguePredictionReminders() {
        Instant now = Instant.now();
        Instant windowEnd = now.plus(2, ChronoUnit.HOURS);
        seasonRepository.findSeasonsWithLeagueLockBetween(now, windowEnd).forEach(season -> {
            log.info("Sending league prediction reminder for season {}", season.getId());
            emailService.sendLeaguePredictionReminder(season.getId());
        });
    }

    /**
     * Retries failed emails once per hour.
     */
    @Scheduled(fixedDelayString = "${app.scheduler.email-retry-delay-ms:3600000}")
    public void retryFailedEmails() {
        log.debug("Retrying failed emails");
        emailService.retryFailedEmails();
    }
}
