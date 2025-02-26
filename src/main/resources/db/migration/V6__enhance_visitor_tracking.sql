-- Create visitors table if it doesn't exist
CREATE TABLE IF NOT EXISTS visitors (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    first_visit_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_active_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_page_visited VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    page_visit_count INTEGER DEFAULT 1,
    session_duration BIGINT DEFAULT 0,
    is_downloaded BOOLEAN DEFAULT false,
    bounced BOOLEAN DEFAULT false
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_visitors_session_id ON visitors(session_id);
CREATE INDEX IF NOT EXISTS idx_visitors_first_visit_time ON visitors(first_visit_time);
CREATE INDEX IF NOT EXISTS idx_visitors_is_active ON visitors(is_active);
CREATE INDEX IF NOT EXISTS idx_visitors_last_active ON visitors(last_active_time);

-- Add sample visitor data for testing
INSERT INTO visitors (
    session_id,
    ip_address,
    user_agent,
    first_visit_time,
    last_active_time,
    last_page_visited,
    is_active,
    page_visit_count,
    session_duration,
    is_downloaded,
    bounced
)
SELECT 
    'test-session-' || i,
    '127.0.0.' || (i % 255),
    'Mozilla/5.0 (Sample User Agent) Chrome/Test Safari/Test',
    NOW() - (INTERVAL '1 hour' * (100 - i)), -- More recent sessions have higher i
    NOW() - (INTERVAL '1 hour' * (100 - i)) + (INTERVAL '5 minute'),
    CASE (i % 4)
        WHEN 0 THEN '/'
        WHEN 1 THEN '/features'
        WHEN 2 THEN '/pricing'
        ELSE '/contact'
    END,
    i > 50, -- Make 50% of visitors active
    FLOOR(RANDOM() * 10 + 1)::INT,
    FLOOR(RANDOM() * 3600)::BIGINT,
    i % 5 = 0, -- 20% downloaded the app
    i % 3 = 0  -- 33% bounced
FROM generate_series(1, 100) i
ON CONFLICT DO NOTHING;
