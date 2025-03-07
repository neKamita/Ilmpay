-- Create translations table for storing translations of static text
CREATE TABLE translations (
    id BIGSERIAL PRIMARY KEY,
    translation_key VARCHAR(255) NOT NULL,
    language_code VARCHAR(10) NOT NULL,
    translated_text TEXT NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    CONSTRAINT uk_translation_key_language UNIQUE (translation_key, language_code)
);

-- Add index for faster lookups by key
CREATE INDEX idx_translations_key ON translations (translation_key);

-- Add index for faster lookups by language
CREATE INDEX idx_translations_language ON translations (language_code);

-- Add comment to table
COMMENT ON TABLE translations IS 'Stores translations for static text in the application';
