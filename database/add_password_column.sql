-- Add password column to users table if it doesn't exist
-- Run this if you get errors about missing password column

USE meeting_planner;

-- Check if column exists and add it if not
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS password VARCHAR(255) NOT NULL DEFAULT 'password123';

-- For MySQL versions that don't support IF NOT EXISTS, use this instead:
-- ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT 'password123';

-- Update existing users to have a default password
UPDATE users SET password = 'password123' WHERE password IS NULL OR password = '';
