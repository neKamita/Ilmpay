-- Function to check if a column exists
CREATE OR REPLACE FUNCTION column_exists(tbl text, col text) RETURNS boolean AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = tbl AND column_name = col
    );
END;
$$ LANGUAGE plpgsql;

-- Add session_id if it doesn't exist
DO $$ 
BEGIN 
    IF NOT column_exists('visitors', 'session_id') THEN
        ALTER TABLE visitors ADD COLUMN session_id VARCHAR(255) NOT NULL DEFAULT 'legacy';
    END IF;
END $$;

-- Add timestamps if they don't exist
DO $$ 
BEGIN 
    IF NOT column_exists('visitors', 'first_visit_time') THEN
        ALTER TABLE visitors ADD COLUMN first_visit_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
    END IF;
    
    IF NOT column_exists('visitors', 'last_active_time') THEN
        ALTER TABLE visitors ADD COLUMN last_active_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;

-- Add page tracking columns if they don't exist
DO $$ 
BEGIN 
    IF NOT column_exists('visitors', 'last_page_visited') THEN
        ALTER TABLE visitors ADD COLUMN last_page_visited VARCHAR(255);
    END IF;
    
    IF NOT column_exists('visitors', 'page_visit_count') THEN
        ALTER TABLE visitors ADD COLUMN page_visit_count INTEGER NOT NULL DEFAULT 1;
    END IF;
END $$;

-- Create indexes if they don't exist
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_session_id') THEN
        CREATE INDEX idx_session_id ON visitors (session_id);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_last_active') THEN
        CREATE INDEX idx_last_active ON visitors (last_active_time);
    END IF;
END $$;

-- Update data from old columns if they exist
DO $$ 
BEGIN 
    -- If visit_time exists and first_visit_time exists, copy data
    IF column_exists('visitors', 'visit_time') AND column_exists('visitors', 'first_visit_time') THEN
        UPDATE visitors 
        SET first_visit_time = visit_time,
            last_active_time = visit_time
        WHERE first_visit_time = CURRENT_TIMESTAMP;
        
        ALTER TABLE visitors DROP COLUMN visit_time;
    END IF;
    
    -- If page_visited exists and last_page_visited exists, copy data
    IF column_exists('visitors', 'page_visited') AND column_exists('visitors', 'last_page_visited') THEN
        UPDATE visitors 
        SET last_page_visited = page_visited
        WHERE last_page_visited IS NULL;
        
        ALTER TABLE visitors DROP COLUMN page_visited;
    END IF;
END $$;

-- Clean up duplicate legacy sessions and set proper session IDs
WITH ranked_visitors AS (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY session_id ORDER BY last_active_time DESC) as rn
    FROM visitors
    WHERE session_id = 'legacy'
)
DELETE FROM visitors 
WHERE id IN (
    SELECT id 
    FROM ranked_visitors 
    WHERE rn > 1
);

UPDATE visitors 
SET session_id = CONCAT('legacy_', id) 
WHERE session_id = 'legacy';

-- Drop the helper function
DROP FUNCTION IF EXISTS column_exists;
