-- V9: Insert sample users
-- Purpose: Seed demo accounts for local development

INSERT INTO users (
    id,
    email,
    username,
    password_hash,
    name,
    timezone,
    language,
    theme,
    created_at,
    updated_at
)
VALUES
    (
        '11111111-1111-1111-1111-111111111111'::uuid,
        'alice.nguyen@example.com',
        'alice',
        '$2a$12$eImiTXuWVxfM37uY4JANj.QMsjFQ36gm6AAdcV1lY8w6j7w7K4CWy',
        'Alice Nguyen',
        'Asia/Ho_Chi_Minh',
        'VI',
        'DARK',
        CURRENT_TIMESTAMP - INTERVAL '45 days',
        CURRENT_TIMESTAMP - INTERVAL '1 day'
    ),
    (
        '22222222-2222-2222-2222-222222222222'::uuid,
        'bao.tran@example.com',
        'bao',
        '$2a$12$eImiTXuWVxfM37uY4JANj.QMsjFQ36gm6AAdcV1lY8w6j7w7K4CWy',
        'Bao Tran',
        'Asia/Ho_Chi_Minh',
        'EN',
        'LIGHT',
        CURRENT_TIMESTAMP - INTERVAL '30 days',
        CURRENT_TIMESTAMP - INTERVAL '2 days'
    )
ON CONFLICT (id) DO UPDATE
SET
    email = EXCLUDED.email,
    username = EXCLUDED.username,
    password_hash = EXCLUDED.password_hash,
    name = EXCLUDED.name,
    timezone = EXCLUDED.timezone,
    language = EXCLUDED.language,
    theme = EXCLUDED.theme,
    updated_at = EXCLUDED.updated_at;

