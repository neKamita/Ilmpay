-- First, remove duplicate session IDs, keeping only the most recent record for each session
DELETE FROM visitors
WHERE id IN (
    SELECT id
    FROM (
        SELECT id,
               ROW_NUMBER() OVER (PARTITION BY session_id ORDER BY last_active_time DESC) as rn
        FROM visitors
    ) duplicates
    WHERE duplicates.rn > 1
);

-- Now add the unique constraint to prevent future duplicates
ALTER TABLE visitors ADD CONSTRAINT uk_visitor_session_id UNIQUE (session_id);

-- Add comment explaining the migration
COMMENT ON CONSTRAINT uk_visitor_session_id ON visitors IS 'Ensures each session ID is unique to prevent duplicate visitor records';
