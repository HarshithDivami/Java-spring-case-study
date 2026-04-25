-- ─────────────────────────────────────────────────────────────────────────────
-- V2 : Seed roles and admin user
-- Default admin password : Admin@1234
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO users (username, email, password_hash, display_name, is_active)
VALUES (
    'admin',
    'admin@familyleague.local',
    crypt('Admin@1234', gen_salt('bf', 12)),
    'Administrator',
    true
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM   users u, roles r
WHERE  u.username = 'admin'
AND    r.name     = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;
