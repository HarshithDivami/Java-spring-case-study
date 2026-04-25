package com.harshith.assigment.domain.match.service.impl;

import com.harshith.assigment.common.enums.MatchStatus;
import com.harshith.assigment.common.exception.AppException;
import com.harshith.assigment.common.exception.ConflictException;
import com.harshith.assigment.common.exception.ResourceNotFoundException;
import com.harshith.assigment.domain.league.entity.LeagueSeason;
import com.harshith.assigment.domain.league.repository.LeagueSeasonRepository;
import com.harshith.assigment.domain.match.dto.*;
import com.harshith.assigment.domain.match.entity.Match;
import com.harshith.assigment.domain.match.entity.MatchResult;
import com.harshith.assigment.domain.match.repository.MatchRepository;
import com.harshith.assigment.domain.match.repository.MatchResultRepository;
import com.harshith.assigment.domain.match.service.MatchService;
import com.harshith.assigment.domain.notification.service.EmailService;
import com.harshith.assigment.domain.prediction.service.PredictionService;
import com.harshith.assigment.domain.team.entity.Player;
import com.harshith.assigment.domain.team.entity.Team;
import com.harshith.assigment.domain.team.repository.PlayerRepository;
import com.harshith.assigment.domain.team.repository.TeamRepository;
import com.harshith.assigment.domain.user.entity.User;
import com.harshith.assigment.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final MatchResultRepository resultRepository;
    private final LeagueSeasonRepository seasonRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final PredictionService predictionService;
    private final EmailService emailService;

    @Override
    @Transactional
    public MatchDto createMatch(UUID seasonId, CreateMatchRequest request) {
        LeagueSeason season = requireSeason(seasonId);
        Team home = requireTeam(request.getHomeTeamId());
        Team away = requireTeam(request.getAwayTeamId());

        Instant lockAt = request.getScheduledAt()
                .minus(season.getMatchPredictionLockHours(), ChronoUnit.HOURS);

        Match match = Match.builder()
                .leagueSeason(season)
                .homeTeam(home)
                .awayTeam(away)
                .matchNumber(request.getMatchNumber())
                .matchType(request.getMatchType())
                .venue(request.getVenue())
                .scheduledAt(request.getScheduledAt())
                .predictionLockAt(lockAt)
                .build();

        // Update season's first match time and prediction lock
        if (season.getFirstMatchTime() == null ||
                request.getScheduledAt().isBefore(season.getFirstMatchTime())) {
            season.setFirstMatchTime(request.getScheduledAt());
            season.setLeaguePredictionLockTime(
                    request.getScheduledAt()
                            .minus(season.getLeaguePredictionLockHours(), ChronoUnit.HOURS));
            seasonRepository.save(season);
        }

        return MatchDto.from(matchRepository.save(match));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatchDto> listMatches(UUID seasonId, Pageable pageable) {
        return matchRepository.findByLeagueSeasonIdAndDeletedFalse(seasonId, pageable)
                .map(MatchDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchDto getMatch(UUID matchId) {
        return MatchDto.from(requireMatch(matchId));
    }

    @Override
    @Transactional
    public MatchDto updateMatch(UUID matchId, CreateMatchRequest request) {
        Match match = requireMatch(matchId);
        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new AppException("Cannot update a completed match", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        match.setVenue(request.getVenue());
        match.setMatchType(request.getMatchType());
        if (request.getScheduledAt() != null) {
            match.setScheduledAt(request.getScheduledAt());
            match.setPredictionLockAt(request.getScheduledAt()
                    .minus(match.getLeagueSeason().getMatchPredictionLockHours(), ChronoUnit.HOURS));
        }
        return MatchDto.from(matchRepository.save(match));
    }

    @Override
    @Transactional
    public MatchResultDto publishResult(UUID matchId, PublishResultRequest request, UUID adminUserId) {
        Match match = requireMatch(matchId);
        if (resultRepository.existsByMatchId(matchId)) {
            throw new ConflictException("Result already published for this match");
        }
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", adminUserId));

        Team winner = request.getWinningTeamName() != null
                ? requireTeamByName(request.getWinningTeamName()) : null;
        Team tossWinner = request.getTossWinningTeamName() != null
                ? requireTeamByName(request.getTossWinningTeamName()) : null;
        Player potm = request.getPlayerOfMatchName() != null
                ? playerRepository.findByNameIgnoreCase(request.getPlayerOfMatchName())
                    .orElseThrow(() -> new ResourceNotFoundException("Player", "name",
                            request.getPlayerOfMatchName()))
                : null;

        MatchResult result = MatchResult.builder()
                .match(match)
                .winningTeam(winner)
                .tossWinningTeam(tossWinner)
                .playerOfMatch(potm)
                .tie(request.isTie())
                .resultSummary(request.getResultSummary())
                .publishedBy(admin)
                .publishedAt(Instant.now())
                .build();
        resultRepository.save(result);

        match.setStatus(MatchStatus.COMPLETED);
        matchRepository.save(match);

        // Async: calculate points and notify
        processResultAsync(result);

        return MatchResultDto.from(result);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchResultDto getResult(UUID matchId) {
        return MatchResultDto.from(resultRepository.findByMatchId(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Result not found for match: " + matchId)));
    }

    @Override
    public void notifyMatchResult(UUID matchId) {
        if (!resultRepository.existsByMatchId(matchId)) {
            throw new ResourceNotFoundException("Result not published yet for match: " + matchId);
        }
        emailService.sendMatchResultToAllUsers(matchId);
    }

    @Async("taskExecutor")
    protected void processResultAsync(MatchResult result) {
        try {
            predictionService.calculateMatchPoints(result);
            emailService.sendResultPublishedNotification(result);
            log.info("Result processed for match {}", result.getMatch().getId());
        } catch (Exception ex) {
            log.error("Error processing result for match {}", result.getMatch().getId(), ex);
        }
    }

    private Match requireMatch(UUID id) {
        return matchRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", "id", id));
    }

    private LeagueSeason requireSeason(UUID id) {
        return seasonRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeagueSeason", "id", id));
    }

    private Team requireTeam(UUID id) {
        return teamRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
    }

    private Team requireTeamByName(String name) {
        return teamRepository.findByShortNameOrNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "name", name));
    }
}
