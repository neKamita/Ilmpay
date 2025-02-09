-- Create visitors table for tracking website traffic
CREATE TABLE IF NOT EXISTS visitors (
    id BIGSERIAL PRIMARY KEY,
    ip_address VARCHAR(255),
    user_agent TEXT,
    visit_time TIMESTAMP,
    page_visited VARCHAR(255),
    is_downloaded BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    bounced BOOLEAN DEFAULT false
);
