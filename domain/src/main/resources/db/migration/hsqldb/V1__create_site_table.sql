-- =====================================================
-- Easy Base - Site Management Schemas
-- Database: HSQLDB (Development & Testing)
-- Version: 1.0
-- Description: Creates Site tables
-- =====================================================

-- Create Site sequence
CREATE SEQUENCE site_sequence AS BIGINT START WITH 2000 INCREMENT BY 1;

-- Create Sites table
CREATE TABLE sites (
    -- Primary key
                       id BIGINT NOT NULL DEFAULT NEXT VALUE FOR site_sequence,

    -- Core site fields
                       code VARCHAR(50) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       description VARCHAR(500),
                       status VARCHAR(20) NOT NULL,
                       time_zone VARCHAR(50) NOT NULL,
                       language_code VARCHAR(10) NOT NULL,

    -- Audit fields (inherited from AuditableEntity)
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_modified TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       version BIGINT NOT NULL DEFAULT 0,

    -- Primary key constraint
                       CONSTRAINT pk_sites PRIMARY KEY (id),

    -- Unique constraints
                       CONSTRAINT uk_sites_code UNIQUE (code),

    -- Check constraints
                       CONSTRAINT ck_sites_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'MAINTENANCE', 'ARCHIVED', 'SETUP')),
                       CONSTRAINT ck_sites_code_format CHECK (code = UPPER(code) AND code REGEXP '^[A-Z0-9_]+$'),
    CONSTRAINT ck_sites_language_format CHECK (language_code REGEXP '^[a-z]{2}(-[A-Z]{2})?$')
);

-- Create indexes on sites table
CREATE INDEX idx_sites_code ON sites (code);
CREATE INDEX idx_sites_status ON sites (status);
CREATE INDEX idx_sites_timezone ON sites (time_zone);
CREATE INDEX idx_sites_language ON sites (language_code);
CREATE INDEX idx_sites_created_at ON sites (created_at);
CREATE INDEX idx_sites_status_code ON sites (status, code);

-- Create UserSites mapping table
CREATE TABLE user_sites (
    -- Composite primary key fields
                            user_id BIGINT NOT NULL,
                            site_id BIGINT NOT NULL,

    -- Relationship fields
                            site_role VARCHAR(20),
                            is_active BOOLEAN NOT NULL DEFAULT TRUE,
                            access_granted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            access_revoked_at TIMESTAMP WITH TIME ZONE,
                            granted_by_user_id BIGINT,
                            revoked_by_user_id BIGINT,
                            notes VARCHAR(500),

    -- Audit fields (inherited from AuditableEntity)
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            last_modified TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            version BIGINT NOT NULL DEFAULT 0,

    -- Primary key constraint
                            CONSTRAINT pk_user_sites PRIMARY KEY (user_id, site_id),

    -- Foreign key constraints
                            CONSTRAINT fk_user_sites_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                            CONSTRAINT fk_user_sites_site FOREIGN KEY (site_id) REFERENCES sites (id) ON DELETE CASCADE,
                            CONSTRAINT fk_user_sites_granted_by FOREIGN KEY (granted_by_user_id) REFERENCES users (id) ON DELETE SET NULL,
                            CONSTRAINT fk_user_sites_revoked_by FOREIGN KEY (revoked_by_user_id) REFERENCES users (id) ON DELETE SET NULL,

    -- Check constraints
                            CONSTRAINT ck_user_sites_role CHECK (site_role IN ('USER', 'MODERATOR', 'ADMIN', 'SUPER_ADMIN')),
                            CONSTRAINT ck_user_sites_access_dates CHECK (
                                access_revoked_at IS NULL OR access_revoked_at > access_granted_at
                                )
);

-- Create indexes on user_sites table
CREATE INDEX idx_user_sites_user ON user_sites (user_id);
CREATE INDEX idx_user_sites_site ON user_sites (site_id);
CREATE INDEX idx_user_sites_role ON user_sites (site_role);
CREATE INDEX idx_user_sites_active ON user_sites (is_active);
CREATE INDEX idx_user_sites_access_granted ON user_sites (access_granted_at);
CREATE INDEX idx_user_sites_user_active ON user_sites (user_id, is_active);
CREATE INDEX idx_user_sites_site_active ON user_sites (site_id, is_active);
CREATE INDEX idx_user_sites_site_role ON user_sites (site_id, site_role);

-- Insert sample data for development/testing
INSERT INTO sites (code, name, description, status, time_zone, language_code) VALUES
                                                                                  ('MAIN', 'Main Site', 'Primary corporate site', 'ACTIVE', 'UTC', 'en'),
                                                                                  ('DEMO', 'Demo Site', 'Demonstration and testing site', 'ACTIVE', 'America/New_York', 'en'),
                                                                                  ('DEV', 'Development Site', 'Development and testing environment', 'SETUP', 'America/Los_Angeles', 'en'),
                                                                                  ('EU_SITE', 'European Site', 'European regional site', 'ACTIVE', 'Europe/London', 'en'),
                                                                                  ('ASIA_SITE', 'Asian Site', 'Asian regional site', 'MAINTENANCE', 'Asia/Tokyo', 'en'),
                                                                                  ('TEST_INACTIVE', 'Inactive Test Site', 'Site for testing inactive status', 'INACTIVE', 'UTC', 'en');

-- Insert sample user-site relationships (assuming some users exist)
-- Note: This assumes user IDs 1000-1005 exist from previous user data
INSERT INTO user_sites (user_id, site_id, site_role, is_active, granted_by_user_id, notes)
SELECT
    u.id as user_id,
    s.id as site_id,
    CASE
        WHEN u.role = 'SUPER_ADMIN' THEN 'SUPER_ADMIN'
        WHEN u.role = 'ADMIN' THEN 'ADMIN'
        ELSE NULL
        END as site_role,
    TRUE as is_active,
    NULL as granted_by_user_id,
    'Initial setup - automatic assignment' as notes
FROM users u, sites s
WHERE u.id IN (
    SELECT id FROM users ORDER BY id LIMIT 5
    ) AND s.code IN ('MAIN', 'DEMO', 'DEV');

-- Add comments to tables and columns for documentation
COMMENT ON TABLE sites IS 'Sites/tenants in the multi-tenant system';
COMMENT ON COLUMN sites.code IS 'Unique site identifier code (uppercase, alphanumeric + underscore)';
COMMENT ON COLUMN sites.name IS 'Human-readable site name';
COMMENT ON COLUMN sites.description IS 'Detailed site description';
COMMENT ON COLUMN sites.status IS 'Current operational status of the site';
COMMENT ON COLUMN sites.time_zone IS 'Site timezone for date/time operations';
COMMENT ON COLUMN sites.language_code IS 'Primary language code (ISO 639-1 format)';

COMMENT ON TABLE user_sites IS 'Many-to-many mapping between users and sites';
COMMENT ON COLUMN user_sites.user_id IS 'Reference to user in the relationship';
COMMENT ON COLUMN user_sites.site_id IS 'Reference to site in the relationship';
COMMENT ON COLUMN user_sites.site_role IS 'User role specific to this site (overrides global role)';
COMMENT ON COLUMN user_sites.is_active IS 'Whether this user-site relationship is currently active';
COMMENT ON COLUMN user_sites.access_granted_at IS 'When access to this site was granted';
COMMENT ON COLUMN user_sites.access_revoked_at IS 'When access was revoked (if applicable)';
COMMENT ON COLUMN user_sites.granted_by_user_id IS 'User who granted this site access';
COMMENT ON COLUMN user_sites.revoked_by_user_id IS 'User who revoked this site access';
COMMENT ON COLUMN user_sites.notes IS 'Additional notes about this relationship';

-- Create triggers for audit fields (HSQLDB specific)
CREATE TRIGGER tr_sites_last_modified
    BEFORE UPDATE ON sites
    REFERENCING NEW AS new_row
    FOR EACH ROW
    SET new_row.last_modified = CURRENT_TIMESTAMP;

CREATE TRIGGER tr_user_sites_last_modified
    BEFORE UPDATE ON user_sites
    REFERENCING NEW AS new_row
    FOR EACH ROW
    SET new_row.last_modified = CURRENT_TIMESTAMP;

-- Verify the migration
SELECT 'Site system migration completed successfully' as status,
       (SELECT COUNT(*) FROM sites) as sites_created,
       (SELECT COUNT(*) FROM user_sites) as user_site_relationships_created;