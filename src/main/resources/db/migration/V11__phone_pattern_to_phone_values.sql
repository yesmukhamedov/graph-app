ALTER TABLE phone_values ADD COLUMN pattern_id BIGINT;

UPDATE phone_values pv
SET pattern_id = p.pattern_id
FROM phones p
WHERE pv.phone_id = p.id
  AND pv.pattern_id IS NULL;

ALTER TABLE phone_values ALTER COLUMN pattern_id SET NOT NULL;

ALTER TABLE phone_values
    ADD CONSTRAINT fk_phone_values_pattern
    FOREIGN KEY (pattern_id) REFERENCES phone_patterns(id) ON DELETE RESTRICT;

ALTER TABLE phones DROP CONSTRAINT IF EXISTS phones_pattern_id_fkey;
ALTER TABLE phones DROP COLUMN pattern_id;

ALTER TABLE node_values ADD COLUMN body TEXT;
ALTER TABLE node_values ADD COLUMN color VARCHAR(32);

ALTER TABLE edge_values ADD COLUMN body TEXT;
ALTER TABLE edge_values ADD COLUMN color VARCHAR(32);
