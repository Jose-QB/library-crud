-- =====================================================
-- LIBRARY CRUD - SECURITY
-- =====================================================

SET search_path TO library;

-- =====================================================
-- USERS
-- =====================================================

CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,

    username VARCHAR(100) NOT NULL UNIQUE,

    password VARCHAR(255) NOT NULL,

    role VARCHAR(20) NOT NULL,

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    must_change_password BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_app_user_role
        CHECK (role IN ('USER', 'ADMIN'))
);

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_app_user_username
ON app_user(username);

CREATE INDEX idx_app_user_role
ON app_user(role);