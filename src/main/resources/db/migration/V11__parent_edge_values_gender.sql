-- Update demo family parent edge values to gendered labels

WITH parent_pairs (parent_name, child_name, new_value) AS (
    VALUES
        ('Serik', 'Ayan', 'FATHER'),
        ('Ayan', 'Dias', 'FATHER'),
        ('Ayan', 'Madina', 'FATHER'),
        ('Gulnara', 'Ayan', 'MOTHER'),
        ('Aigerim', 'Dias', 'MOTHER'),
        ('Aigerim', 'Madina', 'MOTHER')
),
parent_edges AS (
    SELECT e.id, parent_pairs.new_value
    FROM parent_pairs
    JOIN node_values parent_values
        ON parent_values.value = parent_pairs.parent_name
        AND parent_values.expired_at IS NULL
    JOIN node_values child_values
        ON child_values.value = parent_pairs.child_name
        AND child_values.expired_at IS NULL
    JOIN edges e
        ON e.from_id = parent_values.node_id
        AND e.to_id = child_values.node_id
)
UPDATE edge_values ev
SET value = parent_edges.new_value
FROM parent_edges
WHERE ev.edge_id = parent_edges.id
  AND ev.value = 'Parent'
  AND ev.expired_at IS NULL;
