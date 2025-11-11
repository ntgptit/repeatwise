-- V10: Insert sample refresh tokens
-- Purpose: Seed demo refresh tokens linked to sample users

INSERT INTO refresh_tokens (
    id,
    token,
    user_id,
    device_id,
    device_info,
    ip_address,
    expires_at,
    created_at,
    updated_at,
    is_revoked
)
SELECT
    '31111111-aaaa-4aaa-8aaa-aaaaaaaaaaaa'::uuid,
    'sample-refresh-token-alice-1',
    u.id,
    'alice-laptop',
    'Chrome on Windows',
    '192.168.1.10',
    CURRENT_TIMESTAMP + INTERVAL '7 days',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    FALSE
FROM users u
WHERE u.username = 'alice'
UNION ALL
SELECT
    '32222222-bbbb-4bbb-8bbb-bbbbbbbbbbbb'::uuid,
    'sample-refresh-token-bao-1',
    u.id,
    'bao-iphone',
    'Safari on iOS',
    '192.168.1.20',
    CURRENT_TIMESTAMP + INTERVAL '6 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    FALSE
FROM users u
WHERE u.username = 'bao'
ON CONFLICT (id) DO UPDATE
SET
    token = EXCLUDED.token,
    user_id = EXCLUDED.user_id,
    device_id = EXCLUDED.device_id,
    device_info = EXCLUDED.device_info,
    ip_address = EXCLUDED.ip_address,
    expires_at = EXCLUDED.expires_at,
    updated_at = EXCLUDED.updated_at,
    is_revoked = EXCLUDED.is_revoked;

