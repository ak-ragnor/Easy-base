-- =====================================================
-- Easy Base Enterprise - User Management Schema
-- Database: PostgreSQL (Production)
-- Version: 1.0
-- Description: Creates users table with enterprise standards
-- =====================================================

-- ===== EXTENSIONS =====

-- Enable UUID extension for future use
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable timezone extension for comprehensive timezone support
CREATE EXTENSION IF NOT EXISTS "pg_timezone";

-- ===== SEQUENCES =====

-- User ID sequence with proper increment and caching for high performance
CREATE SEQUENCE user_sequence
    START WITH 1000
    INCREMENT BY 1
    CACHE 50
    OWNED BY NONE;

-- ===== TABLES =====

-- Users table with comprehensive enterprise features
CREATE TABLE users (
    -- Primary Key
                       id                  BIGINT NOT NULL DEFAULT nextval('user_sequence'),

    -- Core User Fields
                       name                VARCHAR(100) NOT NULL,
                       email               VARCHAR(255) NOT NULL,
                       role                VARCHAR(20) NOT NULL DEFAULT 'USER',
                       status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       user_timezone       VARCHAR(50) DEFAULT 'UTC',

    -- Audit Fields (Enterprise Standard)
                       created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       last_modified       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       version             BIGINT NOT NULL DEFAULT 0,

    -- Primary Key Constraint
                       CONSTRAINT pk_users PRIMARY KEY (id)
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
    CHECK (LENGTH(TRIM(name)) >= 2 AND LENGTH(TRIM(name)) <= 100);

ALTER TABLE users ADD CONSTRAINT chk_users_email_format
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE users ADD CONSTRAINT chk_users_version
    CHECK (version >= 0);

ALTER TABLE users ADD CONSTRAINT chk_users_timezone_valid
    CHECK (user_timezone IS NULL OR LENGTH(TRIM(user_timezone)) > 0);

-- ===== INDEXES FOR PERFORMANCE =====

-- Primary indexes for common queries with PostgreSQL-specific optimizations
CREATE INDEX CONCURRENTLY idx_users_email ON users USING btree(email);
CREATE INDEX CONCURRENTLY idx_users_role ON users USING btree(role);
CREATE INDEX CONCURRENTLY idx_users_status ON users USING btree(status);
CREATE INDEX CONCURRENTLY idx_users_timezone ON users USING btree(user_timezone);
CREATE INDEX CONCURRENTLY idx_users_created_at ON users USING btree(created_at);

-- Composite indexes for complex queries
CREATE INDEX CONCURRENTLY idx_users_role_status ON users USING btree(role, status);
CREATE INDEX CONCURRENTLY idx_users_status_created ON users USING btree(status, created_at DESC);
CREATE INDEX CONCURRENTLY idx_users_role_created ON users USING btree(role, created_at DESC);

-- Partial indexes for specific use cases (PostgreSQL feature)
CREATE INDEX CONCURRENTLY idx_users_active ON users USING btree(name, email)
    WHERE status = 'ACTIVE';

CREATE INDEX CONCURRENTLY idx_users_inactive ON users USING btree(created_at DESC)
    WHERE status IN ('INACTIVE', 'LOCKED', 'SUSPENDED', 'DISABLED');

-- Text search index for user search functionality
CREATE INDEX CONCURRENTLY idx_users_search ON users USING gin(
    to_tsvector('english', name || ' ' || email)
    );

-- Covering index for user listings (includes commonly accessed fields)
CREATE INDEX CONCURRENTLY idx_users_listing ON users
    USING btree(status, role) INCLUDE (name, email, user_timezone, created_at);

-- ===== TRIGGERS FOR AUDIT =====

-- Function to automatically update last_modified timestamp
CREATE OR REPLACE FUNCTION update_last_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified = NOW();
RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to call the function on every update
CREATE TRIGGER tr_users_update_last_modified
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified_column();

-- ===== ROW LEVEL SECURITY (Enterprise Security) =====

-- Enable row level security for fine-grained access control
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

-- Policy for users to see only their own data (will be refined with actual authentication)
CREATE POLICY users_self_policy ON users
    FOR ALL
    TO PUBLIC
    USING (true);  -- Temporarily allow all access, will be refined later

-- ===== INITIAL SEED DATA =====

-- Insert enterprise-standard test users with proper timezone distribution
INSERT INTO users (
    name, email, role, status, user_timezone
) VALUES
-- System Administrator
(
    'System Administrator',
    'admin@easybase.com',
    'ADMIN',
    'ACTIVE',
    'UTC'
),

-- Regional Managers with different timezones
(
    'John Smith',
    'john.smith@easybase.com',
    'MANAGER',
    'ACTIVE',
    'America/New_York'
),

(
    'Sarah Johnson',
    'sarah.johnson@easybase.com',
    'MANAGER',
    'ACTIVE',
    'Europe/London'
),

(
    'Hiroshi Tanaka',
    'hiroshi.tanaka@easybase.com',
    'MANAGER',
    'ACTIVE',
    'Asia/Tokyo'
),

-- Regular Users across different timezones and statuses
(
    'Alice Brown',
    'alice.brown@easybase.com',
    'USER',
    'ACTIVE',
    'America/Los_Angeles'
),

(
    'Bob Wilson',
    'bob.wilson@easybase.com',
    'USER',
    'ACTIVE',
    'America/Chicago'
),

(
    'Emma Davis',
    'emma.davis@easybase.com',
    'USER',
    'ACTIVE',
    'Europe/Paris'
),

(
    'Chen Wei',
    'chen.wei@easybase.com',
    'USER',
    'ACTIVE',
    'Asia/Shanghai'
),

(
    'Maria Garcia',
    'maria.garcia@easybase.com',
    'USER',
    'ACTIVE',
    'Europe/Madrid'
),

-- Test users with different statuses for testing scenarios
(
    'Test Inactive User',
    'inactive.user@easybase.com',
    'USER',
    'INACTIVE',
    'UTC'
),

(
    'Test Locked User',
    'locked.user@easybase.com',
    'USER',
    'LOCKED',
    'UTC'
),

(
    'Test Suspended User',
    'suspended.user@easybase.com',
    'USER',
    'SUSPENDED',
    'UTC'
),

-- Additional users for pagination and search testing
(
    'Alex Thompson',
    'alex.thompson@easybase.com',
    'USER',
    'ACTIVE',
    'Australia/Sydney'
),

(
    'Lisa Anderson',
    'lisa.anderson@easybase.com',
    'USER',
    'ACTIVE',
    'America/Toronto'
),

(
    'David Lee',
    'david.lee@easybase.com',
    'USER',
    'ACTIVE',
    'Asia/Seoul'
);

-- ===== STATISTICS AND MAINTENANCE =====

-- Update table statistics for optimal query performance
ANALYZE users;

-- ===== VERIFICATION QUERIES =====

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

-- Display timezone distribution
SELECT
    user_timezone,
    COUNT(*) as user_count,
    ROUND((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM users)), 2) as percentage
FROM users
GROUP BY user_timezone
ORDER BY user_count DESC;

-- ===== COMMENTS FOR DOCUMENTATION =====

COMMENT ON TABLE users IS 'Enterprise user management table with comprehensive audit trail and timezone support';
COMMENT ON COLUMN users.id IS 'Primary key using sequence for scalable ID generation';
COMMENT ON COLUMN users.name IS 'User full name (2-100 characters)';
COMMENT ON COLUMN users.email IS 'Unique user email address for authentication';
COMMENT ON COLUMN users.role IS 'User role: ADMIN, MANAGER, or USER';
COMMENT ON COLUMN users.status IS 'User account status: ACTIVE, INACTIVE, LOCKED, SUSPENDED, or DISABLED';
COMMENT ON COLUMN users.user_timezone IS 'User preferred timezone for date/time display';
COMMENT ON COLUMN users.created_at IS 'Timestamp when user was created (UTC)';
COMMENT ON COLUMN users.last_modified IS 'Timestamp when user was last modified (UTC)';
COMMENT ON COLUMN users.version IS 'Optimistic locking version number';

-- ===== COMPLETION MESSAGE =====
DO $$
DECLARE
user_count INTEGER;
BEGIN
SELECT COUNT(*) INTO user_count FROM users;
RAISE NOTICE 'Users table created successfully with % initial records', user_count;
END $$;