-- Add session_duration column to visitors table
ALTER TABLE visitors ADD COLUMN session_duration BIGINT;

-- Add index on visit_time for better query performance
CREATE INDEX idx_visit_time ON visitors(visit_time);

-- Add index on bounced and is_active for stats queries
CREATE INDEX idx_visitor_stats ON visitors(bounced, is_active);

-- Add comment to explain the session_duration field
COMMENT ON COLUMN visitors.session_duration IS 'Duration of user session in seconds';
