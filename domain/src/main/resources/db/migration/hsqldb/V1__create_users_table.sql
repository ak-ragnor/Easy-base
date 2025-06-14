-- =====================================================
-- Easy Base - User Management Schemas
-- Database: HSQLDB (Development & Testing)
-- Version: 1.0
-- Description: Creates users table
-- =====================================================

-- ===== SEQUENCES =====

-- User ID sequence with proper increment and caching
CREATE SEQUENCE user_sequence
    START WITH 1000
    INCREMENT BY 1;

-- ===== TABLES =====

-- Users table with comprehensive enterprise features
CREATE TABLE users (
    -- Primary Key
                       id                  BIGINT NOT NULL PRIMARY KEY,

    -- Core User Fields
                       name                VARCHAR(100) NOT NULL,
                       email               VARCHAR(255) NOT NULL,
                       role                VARCHAR(20) NOT NULL DEFAULT 'USER',
                       status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       user_timezone       VARCHAR(50) DEFAULT 'UTC',

    -- Audit Fields (Enterprise Standard)
                       created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
                       last_modified       TIMESTAMP WITH TIME ZONE NOT NULL,
                       version             BIGINT NOT NULL DEFAULT 0
);

-- ===== CONSTRAINTS =====

-- Unique Constraints
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);

-- Check Constraints for Data Integrity
ALTER TABLE users ADD CONSTRAINT chk_users_role
    CHECK (role IN ('ADMIN', 'MANAGER', 'USER'));

ALTER TABLE users ADD CONSTRAINT chk_users_status
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'SUSPENDED', 'DISABLED'));

ALTER TABLE users ADD CONSTRAINT chk_users_name_length
    CHECK (LENGTH(TRIM(name)) >= 2);

ALTER TABLE users ADD CONSTRAINT chk_users_email_format
    CHECK (email LIKE '%@%' AND LENGTH(TRIM(email)) >= 5);

ALTER TABLE users ADD CONSTRAINT chk_users_version
    CHECK (version >= 0);

-- ===== INDEXES FOR PERFORMANCE =====

-- Primary indexes for common queries
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_timezone ON users(user_timezone);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Composite indexes for complex queries
CREATE INDEX idx_users_role_status ON users(role, status);
CREATE INDEX idx_users_status_created ON users(status, created_at);
CREATE INDEX idx_users_role_created ON users(role, created_at);

-- Covering index for user listings (includes commonly accessed fields)
CREATE INDEX idx_users_listing ON users(status, role, name, email);

-- ===== INITIAL SEED DATA =====

-- Insert enterprise-standard test users with proper timezone distribution
INSERT INTO users (
    id, name, email, role, status, user_timezone,
    created_at, last_modified, version
) VALUES
-- System Administrator
(
    NEXT VALUE FOR user_sequence,
         'System Administrator',
         'admin@easybase.com',
         'ADMIN',
         'ACTIVE',
         'UTC',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

-- Regional Managers with different timezones
(
    NEXT VALUE FOR user_sequence,
         'John Smith',
         'john.smith@easybase.com',
         'MANAGER',
         'ACTIVE',
         'America/New_York',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

(
    NEXT VALUE FOR user_sequence,
         'Sarah Johnson',
         'sarah.johnson@easybase.com',
         'MANAGER',
         'ACTIVE',
         'Europe/London',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

(
    NEXT VALUE FOR user_sequence,
         'Hiroshi Tanaka',
         'hiroshi.tanaka@easybase.com',
         'MANAGER',
         'ACTIVE',
         'Asia/Tokyo',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

-- Regular Users across different timezones and statuses
(
    NEXT VALUE FOR user_sequence,
         'Alice Brown',
         'alice.brown@easybase.com',
         'USER',
         'ACTIVE',
         'America/Los_Angeles',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

(
    NEXT VALUE FOR user_sequence,
         'Bob Wilson',
         'bob.wilson@easybase.com',
         'USER',
         'ACTIVE',
         'America/Chicago',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

(
    NEXT VALUE FOR user_sequence,
         'Emma Davis',
         'emma.davis@easybase.com',
         'USER',
         'ACTIVE',
         'Europe/Paris',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

(
    NEXT VALUE FOR user_sequence,
         'Chen Wei',
         'chen.wei@easybase.com',
         'USER',
         'ACTIVE',
         'Asia/Shanghai',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

(
    NEXT VALUE FOR user_sequence,
         'Maria Garcia',
         'maria.garcia@easybase.com',
         'USER',
         'ACTIVE',
         'Europe/Madrid',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

-- Test users with different statuses for testing scenarios
(
    NEXT VALUE FOR user_sequence,
         'Test Inactive User',
         'inactive.user@easybase.com',
         'USER',
         'INACTIVE',
         'UTC',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

(
    NEXT VALUE FOR user_sequence,
         'Test Locked User',
         'locked.user@easybase.com',
         'USER',
         'LOCKED',
         'UTC',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

(
    NEXT VALUE FOR user_sequence,
         'Test Suspended User',
         'suspended.user@easybase.com',
         'USER',
         'SUSPENDED',
         'UTC',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

-- Additional users for pagination and search testing
(
    NEXT VALUE FOR user_sequence,
         'Alex Thompson',
         'alex.thompson@easybase.com',
         'USER',
         'ACTIVE',
         'Australia/Sydney',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

(
    NEXT VALUE FOR user_sequence,
         'Lisa Anderson',
         'lisa.anderson@easybase.com',
         'USER',
         'ACTIVE',
         'America/Toronto',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
),

(
    NEXT VALUE FOR user_sequence,
         'David Lee',
         'david.lee@easybase.com',
         'USER',
         'ACTIVE',
         'Asia/Seoul',
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP,
         0
);

-- ===== VERIFY DATA INSERTION =====

-- Display summary of inserted data for verification
SELECT
    'User Count by Role' as summary_type,
    role,
    COUNT(*) as count
FROM users
GROUP BY role
UNION ALL
SELECT
    'User Count by Status' as summary_type,
    status,
    COUNT(*) as count
FROM users
GROUP BY status
ORDER BY summary_type, role, status;

-- ===== PERFORMANCE VERIFICATION =====

-- Update table statistics for optimal query performance
UPDATE INFORMATION_SCHEMA.SYSTEM_TABLES SET TABLE_SCHEM = 'PUBLIC' WHERE TABLE_NAME = 'USERS';

-- ===== COMPLETION MESSAGE =====
SELECT 'Users table created successfully with ' || COUNT(*) || ' initial records' as status
FROM users;