CREATE TABLE nodes (
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE edges (
    id BIGSERIAL PRIMARY KEY,
    from_id BIGINT NULL,
    to_id BIGINT NULL,
    created_at TIMESTAMPTZ NULL,
    expired_at TIMESTAMPTZ NULL,
    CONSTRAINT fk_edges_from_node FOREIGN KEY (from_id) REFERENCES nodes (id) ON DELETE CASCADE,
    CONSTRAINT fk_edges_to_node FOREIGN KEY (to_id) REFERENCES nodes (id) ON DELETE CASCADE,
    CONSTRAINT chk_edges_not_both_null CHECK (NOT (from_id IS NULL AND to_id IS NULL)),
    CONSTRAINT chk_edges_no_self_loop CHECK (
        from_id IS NULL
        OR to_id IS NULL
        OR from_id <> to_id
    ),
    CONSTRAINT uq_edges_from_to UNIQUE (from_id, to_id)
);

CREATE INDEX idx_edges_from_id ON edges (from_id);
CREATE INDEX idx_edges_to_id ON edges (to_id);
CREATE INDEX idx_edges_public_to ON edges (to_id)
    WHERE from_id IS NULL AND to_id IS NOT NULL;
CREATE INDEX idx_edges_private_from ON edges (from_id)
    WHERE to_id IS NULL AND from_id IS NOT NULL;
CREATE INDEX idx_edges_created_at ON edges (created_at);
CREATE INDEX idx_edges_expired_at ON edges (expired_at);

CREATE TABLE node_values (
    id BIGSERIAL PRIMARY KEY,
    node_id BIGINT NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    value VARCHAR(200) NOT NULL,
    body TEXT NULL,
    color VARCHAR(32) NULL,
    created_at TIMESTAMPTZ NULL,
    expired_at TIMESTAMPTZ NULL,
    created_by VARCHAR(100) NULL
);

CREATE INDEX idx_node_values_node_id ON node_values(node_id);
CREATE INDEX idx_node_values_node_created ON node_values (node_id, created_at);

CREATE TABLE edge_values (
    id BIGSERIAL PRIMARY KEY,
    edge_id BIGINT NOT NULL REFERENCES edges(id) ON DELETE CASCADE,
    value VARCHAR(200) NOT NULL,
    body TEXT NULL,
    color VARCHAR(32) NULL,
    created_at TIMESTAMPTZ NULL,
    expired_at TIMESTAMPTZ NULL,
    created_by VARCHAR(100) NULL
);

CREATE INDEX idx_edge_values_edge_id ON edge_values(edge_id);
CREATE INDEX idx_edge_values_edge_created ON edge_values (edge_id, created_at);

CREATE TABLE phone_patterns (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    value VARCHAR(64) NOT NULL
);

CREATE TABLE phones (
    id BIGSERIAL PRIMARY KEY,
    node_id BIGINT NOT NULL REFERENCES nodes(id) ON DELETE CASCADE,
    CONSTRAINT uk_phones_node_id UNIQUE (node_id)
);

CREATE TABLE phone_values (
    id BIGSERIAL PRIMARY KEY,
    phone_id BIGINT NOT NULL REFERENCES phones(id) ON DELETE CASCADE,
    pattern_id BIGINT NOT NULL REFERENCES phone_patterns(id) ON DELETE RESTRICT,
    value VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NULL,
    expired_at TIMESTAMPTZ NULL,
    created_by VARCHAR(100) NULL,
    CONSTRAINT chk_phone_values_digits_only CHECK (value ~ '^[0-9]+$')
);

CREATE INDEX idx_phone_values_phone_id ON phone_values(phone_id);
CREATE UNIQUE INDEX ux_phone_values_value ON phone_values(value);
CREATE INDEX idx_phone_values_phone_created ON phone_values (phone_id, created_at);

INSERT INTO phone_patterns (code, value)
VALUES
    ('KZ', '+7 ( _ _ _ ) - _ _ _ - _ _ - _ _'),
    ('TR', '+90 ( _ _ _ ) - _ _ _ - _ _ - _ _'),
    ('RU', '+7 ( _ _ _ ) - _ _ _ - _ _ - _ _'),
    ('USA', '+1 ( _ _ _ ) - _ _ _ - _ _ - _ _'),
    ('UZ', '+998 ( _ _ ) - _ _ _ - _ _ - _ _'),
    ('KG', '+996 ( _ _ _ ) - _ _ - _ _ - _ _'),
    ('AE', '+971 ( _ ) - _ _ _ - _ _ _ - _ _'),
    ('CN', '+86 ( _ _ _ ) - _ _ _ - _ _ _ - _ _'),
    ('AZ', '+994 ( _ _ ) - _ _ _ - _ _ - _ _'),
    ('MY', '+60 ( _ _ ) - _ _ _ _ - _ _ _ _'),
    ('TJ', '+992 ( _ _ ) - _ _ _ - _ _ - _ _')
ON CONFLICT (code) DO NOTHING;

WITH system_node AS (
    INSERT INTO nodes DEFAULT VALUES
    RETURNING id
),
vocab(value, color, ord) AS (
    VALUES
        ('MALE', 'blue', 1),
        ('FEMALE', 'red', 2),
        ('ALIVE', 'green', 3),
        ('DEAD', 'green', 4)
),
inserted_node_value AS (
    INSERT INTO node_values (node_id, value, body, color, created_at, expired_at)
    SELECT id, 'SYSTEM_VOCAB', NULL, NULL, now(), NULL
    FROM system_node
    RETURNING node_id
),
inserted_edges AS (
    INSERT INTO edges (from_id, to_id, created_at, expired_at)
    SELECT NULL, node_id, now(), NULL
    FROM inserted_node_value
    JOIN vocab ON true
    ORDER BY vocab.ord
    RETURNING id
),
ordered_edges AS (
    SELECT id, row_number() OVER (ORDER BY id) AS ord
    FROM inserted_edges
)
INSERT INTO edge_values (edge_id, value, body, color, created_at, expired_at)
SELECT ordered_edges.id, vocab.value, NULL, vocab.color, now(), NULL
FROM ordered_edges
JOIN vocab ON vocab.ord = ordered_edges.ord;
