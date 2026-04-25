# Family League – IPL 2026

A Spring Boot REST API for a family cricket prediction league. Users predict match winners, toss winners, and players of the match. Points are calculated automatically after results are published, and a live leaderboard tracks rankings throughout the season.

---

## Quick Start

### Prerequisites
- Java 25+
- PostgreSQL 14+

### 1. Create the database
```bash
psql -U postgres -c "CREATE DATABASE family_league;"
```

### 2. Start the app
```bash
./gradlew bootRun
```

Flyway runs all migrations automatically on startup — no manual SQL needed.

### 3. Login as admin
```
POST http://localhost:8080/api/v1/auth/login
{ "usernameOrEmail": "admin", "password": "Admin@1234" }
```

---

## Documentation

| Doc | Description |
|-----|-------------|
| [Setup Guide](docs/setup.md) | Full setup including environment variables and Gmail SMTP configuration |
| [API Reference](docs/api.md) | All endpoints with request/response examples |
| [Scoring Rules](docs/scoring.md) | How points are calculated for match and league predictions |
| [Postman Guide](docs/postman.md) | Import the collection and run the complete flow step-by-step |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 25 |
| Framework | Spring Boot 4.0.6 |
| Database | PostgreSQL 14+ |
| Migrations | Flyway |
| Auth | JWT (HS512) |
| Email | Spring Mail / Gmail SMTP |
| Docs | OpenAPI 3 / Swagger UI |
| Build | Gradle 9 |

---

## Project Structure

```
src/main/java/com/harshith/assigment/
├── api/                   REST controllers
├── config/                Security, async, JPA, OpenAPI config
├── common/                Shared DTOs, enums, exceptions
├── domain/
│   ├── league/            League & season management
│   ├── match/             Matches & results
│   ├── prediction/        Match & league predictions
│   ├── leaderboard/       Points & rankings
│   ├── team/              Teams & players
│   ├── user/              User accounts
│   └── notification/      Email service & logs
└── security/              JWT filter, UserPrincipal

src/main/resources/
├── application.properties
├── logback-spring.xml
└── db/migration/
    ├── V1__init_schema.sql   Full schema
    └── V2__seed_data.sql     Roles + admin user
```

---

## API Overview

| Area | Base Path |
|------|-----------|
| Auth | `/api/v1/auth` |
| Users | `/api/v1/users` |
| Leagues | `/api/v1/leagues` |
| Seasons | `/api/v1/leagues/{leagueId}/seasons` |
| Teams | `/api/v1/teams` |
| Matches | `/api/v1/seasons/{seasonId}/matches` |
| Predictions | `/api/v1/matches/{matchId}/prediction` |
| Leaderboard | `/api/v1/seasons/{seasonId}/leaderboard` |
| Notifications | `/api/v1/notifications` |

Full details → [docs/api.md](docs/api.md)

Interactive docs (once running) → http://localhost:8080/swagger-ui.html

---

## Key Features

- **Match predictions** — predict winner, toss winner, and player of the match before each game
- **League predictions** — predict the final team standings before the season starts
- **Automatic scoring** — points calculated async immediately after result is published
- **Live leaderboard** — ranked by total points, updated after every match
- **Result emails** — admin triggers personalised emails per user showing their predictions, points earned, and full leaderboard
- **Prediction lock** — predictions close automatically before match start (configurable)
- **Role-based access** — `ROLE_ADMIN` for setup/results, `ROLE_USER` for predictions

---

## Default Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `Admin@1234` |

See [docs/setup.md](docs/setup.md) to register additional users.
