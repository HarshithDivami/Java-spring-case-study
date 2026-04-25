# Setup Guide

## Prerequisites

| Tool | Version |
|------|---------|
| Java (Temurin) | 25+ |
| Gradle | 9+ (wrapper included) |
| PostgreSQL | 14+ |
| Git | any |

---

## 1. Clone the Repository

```bash
git clone <repo-url>
cd assigment
```

---

## 2. Create the Database

```bash
psql -U postgres -c "CREATE DATABASE family_league;"
```

> Flyway will automatically create all tables and seed the admin user on first startup.

---

## 3. Configure Environment Variables (Optional)

The app runs with sensible defaults but you should override the mail credentials:

| Variable | Default | Description |
|----------|---------|-------------|
| `MAIL_HOST` | `smtp.gmail.com` | SMTP host |
| `MAIL_PORT` | `587` | SMTP port |
| `MAIL_USERNAME` | _(none)_ | Gmail address |
| `MAIL_PASSWORD` | _(none)_ | Gmail App Password |
| `MAIL_FROM` | _(same as username)_ | From address |
| `JWT_SECRET` | built-in default | Override in production |
| `ADMIN_ALERT_EMAIL` | `admin@familyleague.local` | Admin alert recipient |

### Gmail App Password Setup

1. Enable 2-Step Verification on your Google account
2. Go to **Google Account → Security → App passwords**
3. Generate a password for "Mail"
4. Use that 16-character password as `MAIL_PASSWORD`

### Setting Variables (macOS/Linux)

```bash
export MAIL_USERNAME=yourname@gmail.com
export MAIL_PASSWORD=abcdabcdabcdabcd
export MAIL_FROM=yourname@gmail.com
```

---

## 4. Start the Application

```bash
./gradlew bootRun
```

The app starts on **http://localhost:8080**.

First startup runs all Flyway migrations automatically, including:
- `V1__init_schema.sql` — creates all tables
- `V2__seed_data.sql` — creates roles and the default admin user

---

## 5. Default Admin Credentials

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `Admin@1234` |

**Change this password immediately in a production environment.**

---

## 6. API Documentation (Swagger)

Once running, open:

```
http://localhost:8080/swagger-ui.html
```

---

## 7. Health Check

```
http://localhost:8080/actuator/health
```

---

## Next Steps

- [API Reference](api.md) — all endpoints with request/response examples
- [Postman Collection](postman.md) — import and run the full flow
- [Scoring Rules](scoring.md) — how points are calculated
