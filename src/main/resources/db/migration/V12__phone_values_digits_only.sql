ALTER TABLE phone_values ALTER COLUMN value TYPE VARCHAR(32);

UPDATE phone_values
SET value = regexp_replace(value, '\\D', '', 'g');

ALTER TABLE phone_values
    ADD CONSTRAINT chk_phone_values_digits_only
    CHECK (value ~ '^[0-9]+$');
