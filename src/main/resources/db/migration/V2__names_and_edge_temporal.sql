CREATE TABLE names (
    id BIGSERIAL PRIMARY KEY,
    text VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(100) NULL
);

INSERT INTO names (text)
SELECT DISTINCT name
FROM nodes
WHERE name IS NOT NULL;

ALTER TABLE nodes ADD COLUMN name_id BIGINT;

UPDATE nodes
SET name_id = names.id
FROM names
WHERE nodes.name = names.text;

ALTER TABLE nodes ALTER COLUMN name_id SET NOT NULL;
ALTER TABLE nodes
    ADD CONSTRAINT fk_nodes_name_id FOREIGN KEY (name_id) REFERENCES names (id) ON DELETE RESTRICT;

ALTER TABLE nodes DROP COLUMN name;

ALTER TABLE edges ADD COLUMN name_id BIGINT;
ALTER TABLE edges ADD COLUMN created_at TIMESTAMPTZ NULL;
ALTER TABLE edges ADD COLUMN expired_at TIMESTAMPTZ NULL;

ALTER TABLE edges
    ADD CONSTRAINT fk_edges_name_id FOREIGN KEY (name_id) REFERENCES names (id) ON DELETE SET NULL;

CREATE INDEX idx_edges_name_id ON edges (name_id);
