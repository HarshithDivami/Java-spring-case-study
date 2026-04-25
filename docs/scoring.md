# Scoring Rules

## Match Predictions

Each match has three prediction fields. Each correct prediction earns **1 point**.

| Prediction | Correct | Points |
|------------|---------|--------|
| Match Winner | Yes | +1 |
| Match Winner | No | 0 |
| Toss Winner | Yes | +1 |
| Toss Winner | No | 0 |
| Player of the Match | Yes | +1 |
| Player of the Match | No | 0 |

**Maximum per match: 3 points**

### Tie Rule
If the match ends in a tie, any user who predicted *either* team as winner receives 1 point.

---

## League Predictions

Before the season starts, users predict the final standings of all teams in order.

| Prediction | Correct | Points |
|------------|---------|--------|
| Team at correct final position | Yes | +1 per team |
| Team at incorrect position | No | 0 |

**Maximum: 1 point per team** (e.g. 10 teams = max 10 points)

---

## Total Season Points

```
Total Points = Match Points + League Prediction Points
```

---

## Prediction Windows

| Type | Lock Time |
|------|-----------|
| Match prediction | `matchPredictionLockHours` before match start (default: 1 hour) |
| League prediction | `leaguePredictionLockHours` before the first match of the season (default: 4 hours) |

Once the window closes, predictions cannot be submitted or changed.

---

## Leaderboard & Ranking

- Rankings are recalculated automatically after each match result is published.
- Users are ranked by `totalPoints` descending.
- In case of a tie, users share the same rank.

---

## Points Calculation Flow

```
Admin publishes match result
        ↓
System calculates match predictions asynchronously
        ↓
Winner / Toss / POTM points assigned to each prediction
        ↓
UserSeasonPoints updated for each user
        ↓
All ranks recalculated
        ↓
Admin triggers result/notify → email sent to all users
```
