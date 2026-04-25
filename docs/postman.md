# Postman Collection Guide

The file `FamilyLeague_IPL2026.postman_collection.json` at the root of the repo contains the complete API flow.

---

## Import the Collection

1. Open Postman
2. Click **Import** (top left)
3. Select `FamilyLeague_IPL2026.postman_collection.json`
4. The collection **"Family League – IPL 2026"** will appear in your sidebar

---

## Collection Variables

The collection uses variables that auto-populate via Scripts. No manual environment setup needed.

| Variable | Set By | Description |
|----------|--------|-------------|
| `base_url` | Pre-set | `http://localhost:8080` |
| `token` | Login scripts | Current JWT token |
| `league_id` | Create League | League UUID |
| `season_id` | Create Season | Season UUID |
| `match_id` | Create Match | Match 1 UUID |
| `match_id_2` | Create Match | Match 2 UUID |
| `user_token` | User Login | Regular user JWT |

---

## Scripts Tab (Important)

Each Login request has a **Post-response** script that saves the token:

```javascript
var res = pm.response.json();
pm.collectionVariables.set('token', res.data.accessToken);
```

If the token is not updating, check that:
- The Scripts tab → Post-response has this code
- You are using `pm.collectionVariables.set` (not `pm.environment.set`)
- The response field is `accessToken` (not `token`)

---

## Step-by-Step Flow

### Step 1 — Admin Login
Folder: **07 – PUBLISH RESULT (Admin)**
→ Run **"Login as Admin (refresh token)"**
→ This sets `{{token}}` to the admin JWT

### Step 2 — Create League
Folder: **Season Setup**
→ Run **"Create League"**
→ Auto-saves `{{league_id}}`

### Step 3 — Create Season
→ Run **"Create Season"**
→ Auto-saves `{{season_id}}`

### Step 4 — Add Teams to Season
→ Run each **"Add [Team] to Season"** request (10 teams)

### Step 5 — Create Matches
Folder: **Create Matches**
→ Run **"Create Match 1"**, **"Create Match 2"**, etc.
→ Auto-saves `{{match_id}}`, `{{match_id_2}}`

### Step 6 — User Login
Folder: **04 – USER AUTH**
→ Run **"Login"**
→ Saves `{{user_token}}`

### Step 7 — Submit Predictions
Folder: **06 – SUBMIT PREDICTIONS (User)**
→ Swap token to `{{user_token}}`
→ Run **"Submit Match 1 Prediction"**

```json
{
  "predictedWinnerName": "CSK",
  "predictedTossWinnerName": "MI",
  "predictedPlayerOfMatchName": "MS Dhoni"
}
```

### Step 8 — Publish Result (Admin)
Folder: **07 – PUBLISH RESULT (Admin)**
→ First run **"Login as Admin (refresh token)"**
→ Then run **"Publish Match 1 Result"**

```json
{
  "winningTeamName": "CSK",
  "tossWinningTeamName": "MI",
  "playerOfMatchName": "MS Dhoni",
  "tie": false,
  "resultSummary": "CSK won by 5 wickets"
}
```

### Step 9 — Email Result to All Users
Folder: **09 – ADMIN MANAGEMENT**
→ `POST /api/v1/seasons/{{season_id}}/matches/{{match_id}}/result/notify`
→ Sends a personalised email to every active user with match result, their predictions, points, and leaderboard

### Step 10 — Leaderboard
Folder: **08 – LEADERBOARD**
→ Run **"Get Season Leaderboard"**
→ Run **"Get My Rank"**

---

## Common Issues

| Problem | Fix |
|---------|-----|
| 403 Forbidden | Re-run the Admin Login request to refresh `{{token}}` |
| 401 Unauthorized | Token expired — re-login |
| 400 "predictedWinnerName is null" | Check field names use **lowercase** first letter, e.g. `predictedWinnerName` not `PredictedWinnerName` |
| "Prediction window is closed" | Run the SQL to extend lock time: `UPDATE matches SET prediction_lock_at = now() + interval '7 days'` |
| Email not received | Check user email in DB is a real address: `UPDATE users SET email = 'real@email.com' WHERE username = 'harshith'` |
