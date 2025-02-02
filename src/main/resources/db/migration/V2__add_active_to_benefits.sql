-- Add active column to benefits table with default value true
ALTER TABLE benefits ADD COLUMN active BOOLEAN NOT NULL DEFAULT true;
