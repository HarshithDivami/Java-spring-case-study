-- ─────────────────────────────────────────────────────────────────────────────
-- V1 : Initial schema
-- ─────────────────────────────────────────────────────────────────────────────

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ── Users & Roles ─────────────────────────────────────────────────────────────
CREATE TABLE roles (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(50)  NOT NULL UNIQUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by    UUID,
    updated_by    UUID,
    is_deleted    BOOLEAN      NOT NULL DEFAULT false
);

CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(100) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name  VARCHAR(200),
    avatar_url    VARCHAR(500),
    is_active     BOOLEAN      NOT NULL DEFAULT true,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by    UUID,
    updated_by    UUID,
    is_deleted    BOOLEAN      NOT NULL DEFAULT false
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id),
    role_id UUID NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- ── Teams & Players ───────────────────────────────────────────────────────────
CREATE TABLE teams (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(200) NOT NULL,
    short_name   VARCHAR(10),
    logo_url     VARCHAR(500),
    home_ground  VARCHAR(200),
    country      VARCHAR(100),
    is_active    BOOLEAN      NOT NULL DEFAULT true,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by   UUID,
    updated_by   UUID,
    is_deleted   BOOLEAN      NOT NULL DEFAULT false
);

CREATE TABLE players (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(200) NOT NULL,
    display_name   VARCHAR(200),
    date_of_birth  DATE,
    nationality    VARCHAR(100),
    player_role    VARCHAR(50),
    batting_style  VARCHAR(50),
    bowling_style  VARCHAR(50),
    is_active      BOOLEAN      NOT NULL DEFAULT true,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by     UUID,
    updated_by     UUID,
    is_deleted     BOOLEAN      NOT NULL DEFAULT false
);

CREATE TABLE team_players (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    team_id        UUID NOT NULL REFERENCES teams(id),
    player_id      UUID NOT NULL REFERENCES players(id),
    jersey_number  INTEGER,
    joined_at      DATE,
    left_at        DATE,
    is_active      BOOLEAN     NOT NULL DEFAULT true,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by     UUID,
    updated_by     UUID,
    is_deleted     BOOLEAN     NOT NULL DEFAULT false
);

-- ── Leagues & Seasons ─────────────────────────────────────────────────────────
CREATE TABLE leagues (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    sport_type  VARCHAR(50)  NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT true,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_deleted  BOOLEAN      NOT NULL DEFAULT false
);

CREATE TABLE league_seasons (
    id                           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    league_id                    UUID        NOT NULL REFERENCES leagues(id),
    season_name                  VARCHAR(200) NOT NULL,
    season_number                INTEGER     NOT NULL,
    status                       VARCHAR(50) NOT NULL DEFAULT 'UPCOMING',
    league_prediction_lock_hours INTEGER     NOT NULL DEFAULT 4,
    match_prediction_lock_hours  INTEGER     NOT NULL DEFAULT 1,
    first_match_time             TIMESTAMPTZ,
    league_prediction_lock_time  TIMESTAMPTZ,
    config                       JSONB,
    created_at                   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                   TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by                   UUID,
    updated_by                   UUID,
    is_deleted                   BOOLEAN     NOT NULL DEFAULT false,
    UNIQUE (league_id, season_number)
);

CREATE TABLE league_season_teams (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    league_season_id UUID    NOT NULL REFERENCES league_seasons(id),
    team_id          UUID    NOT NULL REFERENCES teams(id),
    final_position   INTEGER,
    is_active        BOOLEAN NOT NULL DEFAULT true,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by       UUID,
    updated_by       UUID,
    is_deleted       BOOLEAN NOT NULL DEFAULT false,
    UNIQUE (league_season_id, team_id)
);

-- ── Matches ───────────────────────────────────────────────────────────────────
CREATE TABLE matches (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    league_season_id   UUID        NOT NULL REFERENCES league_seasons(id),
    home_team_id       UUID        NOT NULL REFERENCES teams(id),
    away_team_id       UUID        NOT NULL REFERENCES teams(id),
    match_number       INTEGER     NOT NULL,
    match_type         VARCHAR(50) NOT NULL DEFAULT 'LEAGUE',
    venue              VARCHAR(300),
    scheduled_at       TIMESTAMPTZ NOT NULL,
    prediction_lock_at TIMESTAMPTZ NOT NULL,
    status             VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    metadata           JSONB,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by         UUID,
    updated_by         UUID,
    is_deleted         BOOLEAN     NOT NULL DEFAULT false,
    UNIQUE (league_season_id, match_number)
);

CREATE TABLE match_results (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_id           UUID    NOT NULL UNIQUE REFERENCES matches(id),
    winning_team_id    UUID    REFERENCES teams(id),
    toss_winning_team_id UUID  REFERENCES teams(id),
    player_of_match_id UUID    REFERENCES players(id),
    is_tie             BOOLEAN NOT NULL DEFAULT false,
    result_summary     TEXT,
    published_by       UUID    REFERENCES users(id),
    published_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by         UUID,
    updated_by         UUID,
    is_deleted         BOOLEAN NOT NULL DEFAULT false
);

-- ── Predictions ───────────────────────────────────────────────────────────────
CREATE TABLE match_predictions (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_id                    UUID NOT NULL REFERENCES matches(id),
    user_id                     UUID NOT NULL REFERENCES users(id),
    predicted_winner_team_id    UUID REFERENCES teams(id),
    predicted_toss_winner_team_id UUID REFERENCES teams(id),
    predicted_player_of_match_id  UUID REFERENCES players(id),
    winner_points               INTEGER NOT NULL DEFAULT 0,
    toss_points                 INTEGER NOT NULL DEFAULT 0,
    potm_points                 INTEGER NOT NULL DEFAULT 0,
    total_points                INTEGER NOT NULL DEFAULT 0,
    is_locked                   BOOLEAN NOT NULL DEFAULT false,
    locked_at                   TIMESTAMPTZ,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by                  UUID,
    updated_by                  UUID,
    is_deleted                  BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_mp_toss_winner FOREIGN KEY (predicted_toss_winner_team_id) REFERENCES teams(id),
    UNIQUE (match_id, user_id)
);

CREATE TABLE league_predictions (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    league_season_id UUID    NOT NULL REFERENCES league_seasons(id),
    user_id          UUID    NOT NULL REFERENCES users(id),
    total_points     INTEGER NOT NULL DEFAULT 0,
    is_locked        BOOLEAN NOT NULL DEFAULT false,
    locked_at        TIMESTAMPTZ,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by       UUID,
    updated_by       UUID,
    is_deleted       BOOLEAN NOT NULL DEFAULT false,
    UNIQUE (league_season_id, user_id)
);

CREATE TABLE league_prediction_entries (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    league_prediction_id UUID    NOT NULL REFERENCES league_predictions(id),
    team_id              UUID    NOT NULL REFERENCES teams(id),
    position             INTEGER NOT NULL,
    points_awarded       INTEGER NOT NULL DEFAULT 0,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by           UUID,
    updated_by           UUID,
    is_deleted           BOOLEAN NOT NULL DEFAULT false,
    UNIQUE (league_prediction_id, position),
    UNIQUE (league_prediction_id, team_id)
);

-- ── Leaderboard ───────────────────────────────────────────────────────────────
CREATE TABLE user_season_points (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                  UUID    NOT NULL REFERENCES users(id),
    league_season_id         UUID    NOT NULL REFERENCES league_seasons(id),
    match_points             INTEGER NOT NULL DEFAULT 0,
    league_prediction_points INTEGER NOT NULL DEFAULT 0,
    total_points             INTEGER NOT NULL DEFAULT 0,
    rank                     INTEGER,
    last_calculated_at       TIMESTAMPTZ,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by               UUID,
    updated_by               UUID,
    is_deleted               BOOLEAN NOT NULL DEFAULT false,
    UNIQUE (user_id, league_season_id)
);

-- ── Notifications ─────────────────────────────────────────────────────────────
CREATE TABLE email_logs (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_user_id UUID,
    recipient_email  VARCHAR(255) NOT NULL,
    email_type       VARCHAR(100) NOT NULL,
    subject          VARCHAR(500) NOT NULL,
    body             TEXT,
    status           VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    sent_at          TIMESTAMPTZ,
    retry_count      INTEGER      NOT NULL DEFAULT 0,
    error_message    TEXT,
    reference_id     UUID,
    reference_type   VARCHAR(100),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by       UUID,
    updated_by       UUID,
    is_deleted       BOOLEAN      NOT NULL DEFAULT false
);

-- ── Audit ─────────────────────────────────────────────────────────────────────
CREATE TABLE audit_logs (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_name  VARCHAR(100) NOT NULL,
    entity_id    VARCHAR(255) NOT NULL,
    action       VARCHAR(50)  NOT NULL,
    old_value    JSONB,
    new_value    JSONB,
    performed_by UUID,
    performed_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ── Indexes ───────────────────────────────────────────────────────────────────
CREATE INDEX idx_users_username       ON users(username)  WHERE is_deleted = false;
CREATE INDEX idx_users_email          ON users(email)     WHERE is_deleted = false;
CREATE INDEX idx_users_active         ON users(is_active) WHERE is_deleted = false;
CREATE INDEX idx_teams_name           ON teams(name)      WHERE is_deleted = false;
CREATE INDEX idx_players_name         ON players(name)    WHERE is_deleted = false;
CREATE INDEX idx_leagues_name         ON leagues(name)    WHERE is_deleted = false;
CREATE INDEX idx_league_seasons_league_id ON league_seasons(league_id)   WHERE is_deleted = false;
CREATE INDEX idx_league_seasons_status    ON league_seasons(status)      WHERE is_deleted = false;
CREATE INDEX idx_lst_season_id        ON league_season_teams(league_season_id) WHERE is_deleted = false;
CREATE INDEX idx_matches_season_id    ON matches(league_season_id)       WHERE is_deleted = false;
CREATE INDEX idx_matches_scheduled_at ON matches(scheduled_at)           WHERE is_deleted = false;
CREATE INDEX idx_matches_prediction_lock ON matches(prediction_lock_at, status) WHERE is_deleted = false;
CREATE INDEX idx_match_results_match_id  ON match_results(match_id);
CREATE INDEX idx_match_predictions_match_id ON match_predictions(match_id) WHERE is_deleted = false;
CREATE INDEX idx_match_predictions_user_id  ON match_predictions(user_id)  WHERE is_deleted = false;
CREATE INDEX idx_league_predictions_season_id ON league_predictions(league_season_id) WHERE is_deleted = false;
CREATE INDEX idx_league_predictions_user_id   ON league_predictions(user_id)          WHERE is_deleted = false;
CREATE INDEX idx_lpe_prediction_id    ON league_prediction_entries(league_prediction_id);
CREATE INDEX idx_usp_season_id        ON user_season_points(league_season_id) WHERE is_deleted = false;
CREATE INDEX idx_usp_total_points     ON user_season_points(league_season_id, total_points DESC) WHERE is_deleted = false;
CREATE INDEX idx_team_players_team_id   ON team_players(team_id);
CREATE INDEX idx_team_players_player_id ON team_players(player_id);
CREATE INDEX idx_email_logs_recipient ON email_logs(recipient_user_id);
CREATE INDEX idx_email_logs_status    ON email_logs(status);
CREATE INDEX idx_email_logs_type      ON email_logs(email_type);
CREATE INDEX idx_email_logs_ref       ON email_logs(reference_id);
CREATE INDEX idx_audit_logs_entity    ON audit_logs(entity_name, entity_id);
CREATE INDEX idx_audit_logs_by        ON audit_logs(performed_by);
