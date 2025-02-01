-- Add order column to support_logos table
ALTER TABLE support_logos ADD COLUMN display_order INTEGER;

-- Update existing rows to have a default order based on creation time
UPDATE support_logos SET display_order = id WHERE display_order IS NULL;
