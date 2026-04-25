package com.harshith.assigment.domain.prediction.service.impl;

import com.harshith.assigment.common.exception.AppException;
import com.harshith.assigment.common.exception.PredictionLockedException;
import com.harshith.assigment.common.exception.ResourceNotFoundException;
import com.harshith.assigment.domain.league.entity.LeagueSeason;
import com.harshith.assigment.domain.league.repository.LeagueSeasonRepository;
import com.harshith.assigment.domain.league.repository.LeagueSeasonTeamRepository;
import com.harshith.assigment.domain.leaderboard.entity.UserSeasonPoints;
import com.harshith.assigment.domain.leaderboard.repository.UserSeasonPointsRepository;
import com.harshith.assigment.domain.match.entity.Match;
import com.harshith.assigment.domain.match.entity.MatchResult;
import com.harshith.assigment.domain.match.repository.MatchRepository;
import com.harshith.assigment.domain.prediction.dto.*;
import com.harshith.assigment.domain.prediction.entity.LeaguePrediction;
import com.harshith.assigment.domain.prediction.entity.LeaguePredictionEntry;
import com.harshith.assigment.domain.prediction.entity.MatchPrediction;
import com.harshith.assigment.domain.prediction.repository.LeaguePredictionRepository;
import com.harshith.assigment.domain.prediction.repository.LeaguePredictionEntryRepository;
import com.harshith.assigment.domain.prediction.repository.MatchPredictionRepository;
import com.harshith.assigment.domain.prediction.service.PredictionService;
import com.harshith.assigment.domain.team.entity.Player;
import com.harshith.assigment.domain.team.entity.Team;
import com.harshith.assigment.domain.team.repository.PlayerRepository;
import com.harshith.assigment.domain.team.repository.TeamRepository;
import com.harshith.assigment.domain.user.entity.User;
import com.harshith.assigment.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionServiceImpl implements PredictionService {

    private final MatchPredictionRepository matchPredictionRepo;
    private final LeaguePredictionRepository leaguePredictionRepo;
    private final LeaguePredictionEntryRepository entryRepo;
    private final MatchRepository matchRepository;
    private final LeagueSeasonRepository seasonRepository;
    private final LeagueSeasonTeamRepository seasonTeamRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final UserSeasonPointsRepository pointsRepository;

    @Override
    @Transactional
    public MatchPredictionDto submitMatchPrediction(UUID matchId, UUID userId,
                                                     SubmitMatchPredictionRequest request) {
        Match match = requireMatch(matchId);
        if (Instant.now().isAfter(match.getPredictionLockAt())) {
            throw new PredictionLockedException("Prediction window is closed for this match");
        }
        User user = requireUser(userId);
        MatchPrediction prediction = matchPredictionRepo
                .findByMatchIdAndUserId(matchId, userId)
                .orElse(MatchPrediction.builder().match(match).user(user).build());

        if (request.getPredictedWinnerName() != null) {
            prediction.setPredictedWinner(requireTeamByName(request.getPredictedWinnerName()));
        }
        if (request.getPredictedTossWinnerName() != null) {
            prediction.setPredictedTossWinner(requireTeamByName(request.getPredictedTossWinnerName()));
        }
        if (request.getPredictedPlayerOfMatchName() != null) {
            prediction.setPredictedPlayerOfMatch(
                    playerRepository.findByNameIgnoreCase(request.getPredictedPlayerOfMatchName())
                            .orElseThrow(() -> new ResourceNotFoundException("Player", "name",
                                    request.getPredictedPlayerOfMatchName())));
        }
        return MatchPredictionDto.from(matchPredictionRepo.save(prediction), false);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchPredictionDto getMyMatchPrediction(UUID matchId, UUID userId) {
        return matchPredictionRepo.findByMatchIdAndUserId(matchId, userId)
                .map(p -> MatchPredictionDto.from(p, false))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No prediction found for this match"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchPredictionDto> getAllMatchPredictions(UUID matchId, UUID requestingUserId) {
        Match match = requireMatch(matchId);
        boolean windowOpen = Instant.now().isBefore(match.getPredictionLockAt());
        return matchPredictionRepo.findByMatchIdAndDeletedFalse(matchId).stream()
                .map(p -> {
                    boolean hideUser = windowOpen && !p.getUser().getId().equals(requestingUserId);
                    return MatchPredictionDto.from(p, hideUser);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeaguePredictionDto submitLeaguePrediction(UUID seasonId, UUID userId,
                                                       SubmitLeaguePredictionRequest request) {
        LeagueSeason season = requireSeason(seasonId);
        if (season.getLeaguePredictionLockTime() != null &&
                Instant.now().isAfter(season.getLeaguePredictionLockTime())) {
            throw new PredictionLockedException("League prediction window is closed");
        }
        User user = requireUser(userId);
        int teamCount = seasonTeamRepository.countByLeagueSeasonIdAndDeletedFalse(seasonId);
        if (request.getEntries().size() != teamCount) {
            throw new AppException(
                    "Must predict all " + teamCount + " teams", HttpStatus.BAD_REQUEST);
        }
        LeaguePrediction prediction = leaguePredictionRepo
                .findByLeagueSeasonIdAndUserId(seasonId, userId)
                .orElse(LeaguePrediction.builder().leagueSeason(season).user(user).build());

        prediction.getEntries().clear();
        entryRepo.deleteByLeaguePredictionId(prediction.getId());

        LeaguePrediction saved = leaguePredictionRepo.save(prediction);

        request.getEntries().forEach(e -> {
            Team team = requireTeam(e.getTeamId());
            entryRepo.save(LeaguePredictionEntry.builder()
                    .leaguePrediction(saved)
                    .position(e.getPosition())
                    .team(team)
                    .build());
        });

        return LeaguePredictionDto.from(
                leaguePredictionRepo.findById(saved.getId()).get(), false);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaguePredictionDto getMyLeaguePrediction(UUID seasonId, UUID userId) {
        return leaguePredictionRepo.findByLeagueSeasonIdAndUserId(seasonId, userId)
                .map(p -> LeaguePredictionDto.from(p, false))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No league prediction found for this season"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaguePredictionDto> getAllLeaguePredictions(UUID seasonId, UUID requestingUserId) {
        LeagueSeason season = requireSeason(seasonId);
        boolean windowOpen = season.getLeaguePredictionLockTime() == null ||
                Instant.now().isBefore(season.getLeaguePredictionLockTime());
        return leaguePredictionRepo.findByLeagueSeasonIdAndDeletedFalse(seasonId).stream()
                .map(p -> {
                    boolean hideUser = windowOpen && !p.getUser().getId().equals(requestingUserId);
                    return LeaguePredictionDto.from(p, hideUser);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void calculateMatchPoints(MatchResult result) {
        UUID matchId = result.getMatch().getId();
        UUID seasonId = result.getMatch().getLeagueSeason().getId();

        matchPredictionRepo.findByMatchIdAndDeletedFalse(matchId).forEach(pred -> {
            int winnerPts = 0, tossPts = 0, potmPts = 0;

            // Winner: tie means either team wins
            if (result.isTie()) {
                winnerPts = (pred.getPredictedWinner() != null) ? 1 : 0;
            } else if (result.getWinningTeam() != null && pred.getPredictedWinner() != null) {
                winnerPts = result.getWinningTeam().getId()
                        .equals(pred.getPredictedWinner().getId()) ? 1 : 0;
            }
            if (result.getTossWinningTeam() != null && pred.getPredictedTossWinner() != null) {
                tossPts = result.getTossWinningTeam().getId()
                        .equals(pred.getPredictedTossWinner().getId()) ? 1 : 0;
            }
            if (result.getPlayerOfMatch() != null && pred.getPredictedPlayerOfMatch() != null) {
                potmPts = result.getPlayerOfMatch().getId()
                        .equals(pred.getPredictedPlayerOfMatch().getId()) ? 1 : 0;
            }

            pred.setWinnerPoints(winnerPts);
            pred.setTossPoints(tossPts);
            pred.setPotmPoints(potmPts);
            pred.setTotalPoints(winnerPts + tossPts + potmPts);
            pred.setLocked(true);
            pred.setLockedAt(Instant.now());
            matchPredictionRepo.save(pred);

            updateUserSeasonPoints(pred.getUser().getId(), seasonId,
                    winnerPts + tossPts + potmPts, 0);
        });

        recalculateRanks(seasonId);
        log.info("Match points calculated for match {}", matchId);
    }

    @Override
    @Transactional
    public void calculateLeaguePoints(UUID seasonId) {
        LeagueSeason season = requireSeason(seasonId);
        var seasonTeams = seasonTeamRepository.findByLeagueSeasonIdAndDeletedFalse(seasonId);

        leaguePredictionRepo.findByLeagueSeasonIdAndDeletedFalse(seasonId).forEach(pred -> {
            int total = 0;
            for (LeaguePredictionEntry entry : pred.getEntries()) {
                int actualPosition = seasonTeams.stream()
                        .filter(st -> st.getTeam().getId().equals(entry.getTeam().getId()))
                        .findFirst()
                        .map(st -> st.getFinalPosition() != null ? st.getFinalPosition() : -1)
                        .orElse(-1);
                int pts = (actualPosition > 0 && actualPosition == entry.getPosition()) ? 1 : 0;
                entry.setPointsAwarded(pts);
                entryRepo.save(entry);
                total += pts;
            }
            pred.setTotalPoints(total);
            pred.setLocked(true);
            pred.setLockedAt(Instant.now());
            leaguePredictionRepo.save(pred);

            updateUserSeasonPoints(pred.getUser().getId(), seasonId, 0, total);
        });

        recalculateRanks(seasonId);
        log.info("League prediction points calculated for season {}", seasonId);
    }

    private void updateUserSeasonPoints(UUID userId, UUID seasonId,
                                         int matchPointsDelta, int leaguePtsDelta) {
        User user = requireUser(userId);
        LeagueSeason season = requireSeason(seasonId);
        UserSeasonPoints usp = pointsRepository
                .findByUserIdAndLeagueSeasonId(userId, seasonId)
                .orElse(UserSeasonPoints.builder()
                        .user(user).leagueSeason(season).build());
        usp.setMatchPoints(usp.getMatchPoints() + matchPointsDelta);
        usp.setLeaguePredictionPoints(usp.getLeaguePredictionPoints() + leaguePtsDelta);
        usp.setTotalPoints(usp.getMatchPoints() + usp.getLeaguePredictionPoints());
        usp.setLastCalculatedAt(Instant.now());
        pointsRepository.save(usp);
    }

    private void recalculateRanks(UUID seasonId) {
        var ordered = pointsRepository.findAllForSeasonOrdered(seasonId);
        for (int i = 0; i < ordered.size(); i++) {
            ordered.get(i).setRank(i + 1);
            pointsRepository.save(ordered.get(i));
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

    private User requireUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
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
