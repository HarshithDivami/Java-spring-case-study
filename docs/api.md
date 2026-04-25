# API Reference

Base URL: `http://localhost:8080`

All protected endpoints require:
```
Authorization: Bearer <jwt_token>
```

All responses follow the wrapper:
```json
{
  "success": true,
  "message": "optional message",
  "data": { ... },
  "timestamp": "2026-04-25T09:00:00Z"
}
```

---

## Authentication

### Register User
`POST /api/v1/auth/register`
```json
{
  "username": "harshith",
  "email": "harshith@example.com",
  "password": "User@1234",
  "displayName": "Harshith"
}
```

### Login
`POST /api/v1/auth/login`
```json
{
  "usernameOrEmail": "admin",
  "password": "Admin@1234"
}
```
Response includes `accessToken` — use it as `Bearer <token>`.

---

## Users (Admin)

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/users` | List all users |
| `GET` | `/api/v1/users/{userId}` | Get user by ID |
| `POST` | `/api/v1/users/{userId}/deactivate` | Deactivate user |
| `POST` | `/api/v1/users/{userId}/activate` | Activate user |

### My Profile
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/users/me` | Get my profile |
| `PUT` | `/api/v1/users/me` | Update my profile |

---

## Leagues

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/v1/leagues` | Admin | Create league |
| `GET` | `/api/v1/leagues` | User | List leagues |
| `GET` | `/api/v1/leagues/{leagueId}` | User | Get league |
| `PUT` | `/api/v1/leagues/{leagueId}` | Admin | Update league |
| `DELETE` | `/api/v1/leagues/{leagueId}` | Admin | Delete league |

### Create League
```json
{
  "name": "Family League",
  "description": "IPL 2026 Family Prediction League",
  "sportType": "CRICKET"
}
```

---

## Seasons

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/v1/leagues/{leagueId}/seasons` | Admin | Create season |
| `GET` | `/api/v1/leagues/{leagueId}/seasons` | User | List seasons |
| `GET` | `/api/v1/leagues/{leagueId}/seasons/{seasonId}` | User | Get season |
| `POST` | `/api/v1/leagues/{leagueId}/seasons/{seasonId}/start` | Admin | Start season |
| `POST` | `/api/v1/leagues/{leagueId}/seasons/{seasonId}/close` | Admin | Close season |
| `POST` | `/api/v1/leagues/{leagueId}/seasons/{seasonId}/teams/{teamId}` | Admin | Add team |
| `DELETE` | `/api/v1/leagues/{leagueId}/seasons/{seasonId}/teams/{teamId}` | Admin | Remove team |

### Create Season
```json
{
  "seasonName": "IPL 2026",
  "seasonNumber": 1,
  "leaguePredictionLockHours": 4,
  "matchPredictionLockHours": 1
}
```

---

## Teams & Players

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/v1/teams` | Admin | Create team |
| `GET` | `/api/v1/teams` | User | List teams |
| `GET` | `/api/v1/teams/{teamId}` | User | Get team |
| `PUT` | `/api/v1/teams/{teamId}` | Admin | Update team |
| `DELETE` | `/api/v1/teams/{teamId}` | Admin | Delete team |
| `POST` | `/api/v1/teams/{teamId}/players` | Admin | Add player |
| `GET` | `/api/v1/teams/{teamId}/players` | User | List team players |
| `GET` | `/api/v1/teams/players/{playerId}` | User | Get player |

### Create Team
```json
{
  "name": "Chennai Super Kings",
  "shortName": "CSK",
  "homeGround": "MA Chidambaram Stadium",
  "country": "India"
}
```

---

## Matches

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/v1/seasons/{seasonId}/matches` | Admin | Create match |
| `GET` | `/api/v1/seasons/{seasonId}/matches` | User | List matches |
| `GET` | `/api/v1/seasons/{seasonId}/matches/{matchId}` | User | Get match |
| `PUT` | `/api/v1/seasons/{seasonId}/matches/{matchId}` | Admin | Update match |
| `POST` | `/api/v1/seasons/{seasonId}/matches/{matchId}/result` | Admin | Publish result |
| `GET` | `/api/v1/seasons/{seasonId}/matches/{matchId}/result` | User | Get result |
| `POST` | `/api/v1/seasons/{seasonId}/matches/{matchId}/result/notify` | Admin | Email result to all users |

### Create Match
```json
{
  "homeTeamId": "<team-uuid>",
  "awayTeamId": "<team-uuid>",
  "matchNumber": 1,
  "matchType": "LEAGUE",
  "venue": "Wankhede Stadium",
  "scheduledAt": "2026-05-01T14:00:00Z"
}
```

### Publish Result
```json
{
  "winningTeamName": "CSK",
  "tossWinningTeamName": "MI",
  "playerOfMatchName": "MS Dhoni",
  "tie": false,
  "resultSummary": "CSK won by 5 wickets"
}
```
> Accepts team short name (e.g. `"CSK"`) or full name (e.g. `"Chennai Super Kings"`).
> Accepts player full name (e.g. `"MS Dhoni"`).

---

## Predictions

### Match Predictions

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `PUT` | `/api/v1/matches/{matchId}/prediction` | User | Submit / update prediction |
| `GET` | `/api/v1/matches/{matchId}/prediction` | User | Get my prediction |
| `GET` | `/api/v1/matches/{matchId}/predictions` | User | Get all predictions |

```json
{
  "predictedWinnerName": "CSK",
  "predictedTossWinnerName": "MI",
  "predictedPlayerOfMatchName": "MS Dhoni"
}
```

> Predictions are locked `matchPredictionLockHours` before the match starts.
> Other users' predictions are hidden until the window closes.

### League Predictions

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `PUT` | `/api/v1/seasons/{seasonId}/prediction` | User | Submit / update league prediction |
| `GET` | `/api/v1/seasons/{seasonId}/prediction` | User | Get my league prediction |
| `GET` | `/api/v1/seasons/{seasonId}/predictions` | User | Get all league predictions |

```json
{
  "entries": [
    { "teamId": "<uuid>", "position": 1 },
    { "teamId": "<uuid>", "position": 2 },
    ...
  ]
}
```
> Must include all teams registered in the season.

---

## Leaderboard

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/api/v1/seasons/{seasonId}/leaderboard` | User | Full leaderboard (paginated) |
| `GET` | `/api/v1/seasons/{seasonId}/leaderboard/me` | User | My rank and points |

---

## Notifications (Admin)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/notifications/bulk` | Send bulk email |
| `GET` | `/api/v1/notifications/logs` | View all email logs |
| `GET` | `/api/v1/notifications/logs/me` | View my email logs |

### Send Bulk Email
```json
{
  "emailType": "BULK_NOTIFICATION",
  "subject": "Important Update",
  "body": "Your message here",
  "userIds": []
}
```
> Leave `userIds` empty to send to all active users.

Valid `emailType` values: `BULK_NOTIFICATION`, `RESULT_PUBLISHED`, `MATCH_PREDICTION_REMINDER`, `LEAGUE_PREDICTION_REMINDER`, `SEASON_STARTED`, `SEASON_CLOSED`, `LEADERBOARD_UPDATED`, `WELCOME`
