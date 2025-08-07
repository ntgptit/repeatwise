-- Enable UUID extension if not present
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==============================================
-- USERS
-- ==============================================
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username      VARCHAR(64) NOT NULL,
    email         VARCHAR(128) NOT NULL UNIQUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at    TIMESTAMPTZ,
    CONSTRAINT uq_users_email UNIQUE (email)
);

COMMENT ON TABLE users IS 'Stores application users.';
COMMENT ON COLUMN users.username IS 'User display name.';
COMMENT ON COLUMN users.email IS 'Unique email for login.';

-- ==============================================
-- SETS
-- ==============================================
CREATE TABLE sets (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name                VARCHAR(128) NOT NULL,
    description         TEXT,
    word_count          INTEGER NOT NULL CHECK (word_count > 0),
    status              VARCHAR(20) NOT NULL DEFAULT 'not_started', -- not_started | learning | reviewing | mastered
    current_cycle       INTEGER NOT NULL DEFAULT 1 CHECK (current_cycle > 0),
    last_cycle_end_date DATE,
    next_cycle_start_date DATE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ
);

CREATE INDEX idx_sets_user ON sets(user_id, deleted_at);

COMMENT ON TABLE sets IS 'User''s learning sets.';

-- ==============================================
-- SET_CYCLES
-- ==============================================
CREATE TABLE set_cycles (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    set_id                  UUID NOT NULL REFERENCES sets(id) ON DELETE CASCADE,
    cycle_no                INTEGER NOT NULL CHECK (cycle_no > 0),
    started_at              DATE NOT NULL,
    finished_at             DATE,
    avg_score               NUMERIC(5,2) CHECK (avg_score >= 0 AND avg_score <= 100),
    next_cycle_delay_days   INTEGER CHECK (next_cycle_delay_days IS NULL OR next_cycle_delay_days >= 0),
    status                  VARCHAR(20) NOT NULL DEFAULT 'active',  -- active | finished
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at              TIMESTAMPTZ
);

CREATE INDEX idx_set_cycles_set ON set_cycles(set_id, deleted_at);

COMMENT ON TABLE set_cycles IS 'Stores learning cycles per set.';

-- ==============================================
-- SET_REVIEWS
-- ==============================================
CREATE TABLE set_reviews (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    set_cycle_id    UUID NOT NULL REFERENCES set_cycles(id) ON DELETE CASCADE,
    review_no       INTEGER NOT NULL CHECK (review_no BETWEEN 1 AND 5),
    reviewed_at     DATE NOT NULL,
    score           INTEGER NOT NULL CHECK (score BETWEEN 0 AND 100),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_set_reviews_cycle ON set_reviews(set_cycle_id, deleted_at);

COMMENT ON TABLE set_reviews IS 'Stores each review within a cycle.';

-- ==============================================
-- REMIND_SCHEDULES
-- ==============================================
CREATE TABLE remind_schedules (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    set_id              UUID NOT NULL REFERENCES sets(id) ON DELETE CASCADE,
    scheduled_date      DATE NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'pending', -- pending | sent | skipped | done | rescheduled | cancelled
    rescheduled_by      UUID REFERENCES users(id),
    rescheduled_at      TIMESTAMPTZ,
    reschedule_reason   TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ
);

CREATE INDEX idx_remind_user_date ON remind_schedules(user_id, scheduled_date, status, deleted_at);

COMMENT ON TABLE remind_schedules IS 'Tracks reminders per user per set.'; 