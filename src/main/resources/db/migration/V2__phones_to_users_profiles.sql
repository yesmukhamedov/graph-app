CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id UUID PRIMARY KEY,
    node_id BIGINT NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NULL,
    CONSTRAINT uk_users_node_id UNIQUE (node_id)
);

CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    phone_digits VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NULL,
    expired_at TIMESTAMPTZ NULL,
    created_by VARCHAR(100) NULL,
    CONSTRAINT chk_profiles_digits_only CHECK (phone_digits ~ '^[0-9]+$'),
    CONSTRAINT ux_profiles_phone_digits UNIQUE (phone_digits)
);

CREATE INDEX idx_profiles_user_id ON profiles(user_id);
CREATE INDEX idx_profiles_user_created ON profiles (user_id, created_at);

INSERT INTO users (id, node_id, created_at)
SELECT gen_random_uuid(), p.node_id, now()
FROM phones p;

INSERT INTO profiles (user_id, phone_digits, created_at, expired_at, created_by)
SELECT u.id, pv.value, pv.created_at, pv.expired_at, pv.created_by
FROM phone_values pv
JOIN phones p ON p.id = pv.phone_id
JOIN users u ON u.node_id = p.node_id;

DROP TABLE IF EXISTS phone_values;
DROP TABLE IF EXISTS phone_patterns;
DROP TABLE IF EXISTS phones;
