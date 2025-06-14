-- =====================================================
-- Easy Base - Site Management Schemas
-- Database: PostgreSQL (Production)
-- Version: 1.0
-- Description: Creates Site tables
-- =====================================================

-- Create Site sequence
CREATE SEQUENCE site_sequence
    START WITH 2000
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

-- Create Sites table
CREATE TABLE sites (
    -- Primary key
                       id BIGINT NOT NULL DEFAULT nextval('site_sequence'),

    -- Core site fields
                       code VARCHAR(50) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       description VARCHAR(500),
                       status VARCHAR(20) NOT NULL,
                       time_zone VARCHAR(50) NOT NULL,
                       language_code VARCHAR(10) NOT NULL,

    -- Audit fields (inherited from AuditableEntity)
                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       last_modified TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       version BIGINT NOT NULL DEFAULT 0,

    -- Primary key constraint
                       CONSTRAINT pk_sites PRIMARY KEY (id),

    -- Unique constraints
                       CONSTRAINT uk_sites_code UNIQUE (code),

    -- Check constraints
                       CONSTRAINT ck_sites_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'MAINTENANCE', 'ARCHIVED', 'SETUP')),
                       CONSTRAINT ck_sites_code_format CHECK (code = UPPER(code) AND code ~ '^[A-Z0-9_]+$'),
    CONSTRAINT ck_sites_language_format CHECK (language_code ~ '^[a-z]{2}(-[A-Z]{2})?$'),
    CONSTRAINT ck_sites_name_length CHECK (LENGTH(TRIM(name)) >= 2),
    CONSTRAINT ck_sites_code_length CHECK (LENGTH(TRIM(code)) >= 2)
);

-- Create indexes on sites table with specific PostgreSQL optimizations
CREATE INDEX idx_sites_code ON sites USING btree (code);
CREATE INDEX idx_sites_status ON sites USING btree (status);
CREATE INDEX idx_sites_timezone ON sites USING btree (time_zone);
CREATE INDEX idx_sites_language ON sites USING btree (language_code);
CREATE INDEX idx_sites_created_at ON sites USING btree (created_at);
CREATE INDEX idx_sites_status_code ON sites USING btree (status, code);
CREATE INDEX idx_sites_name_text ON sites USING gin (to_tsvector('english', name));
CREATE INDEX idx_sites_description_text ON sites USING gin (to_tsvector('english', description));

-- Create UserSites mapping table
CREATE TABLE user_sites (
    -- Composite primary key fields
                            user_id BIGINT NOT NULL,
                            site_id BIGINT NOT NULL,

    -- Relationship fields
                            site_role VARCHAR(20),
                            is_active BOOLEAN NOT NULL DEFAULT TRUE,
                            access_granted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            access_revoked_at TIMESTAMPTZ,
                            granted_by_user_id BIGINT,
                            revoked_by_user_id BIGINT,
                            notes VARCHAR(500),

    -- Audit fields (inherited from AuditableEntity)
                            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            last_modified TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            version BIGINT NOT NULL DEFAULT 0,

    -- Primary key constraint
                            CONSTRAINT pk_user_sites PRIMARY KEY (user_id, site_id),

    -- Foreign key constraints with proper cascading
                            CONSTRAINT fk_user_sites_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users (id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
                            CONSTRAINT fk_user_sites_site
                                FOREIGN KEY (site_id)
                                    REFERENCES sites (id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
                            CONSTRAINT fk_user_sites_granted_by
                                FOREIGN KEY (granted_by_user_id)
                                    REFERENCES users (id)
                                    ON DELETE SET NULL
                                    ON UPDATE CASCADE,
                            CONSTRAINT fk_user_sites_revoked_by
                                FOREIGN KEY (revoked_by_user_id)
                                    REFERENCES users (id)
                                    ON DELETE SET NULL
                                    ON UPDATE CASCADE,

    -- Check constraints
                            CONSTRAINT ck_user_sites_role CHECK (site_role IN ('USER', 'MODERATOR', 'ADMIN', 'SUPER_ADMIN')),
                            CONSTRAINT ck_user_sites_access_dates CHECK (
                                access_revoked_at IS NULL OR access_revoked_at > access_granted_at
                                ),
                            CONSTRAINT ck_user_sites_granted_by_logic CHECK (
                                granted_by_user_id IS NULL OR granted_by_user_id != user_id
),
    CONSTRAINT ck_user_sites_revoked_by_logic CHECK (
        revoked_by_user_id IS NULL OR revoked_by_user_id != user_id
    )
);

-- Create indexes on user_sites table
CREATE INDEX idx_user_sites_user ON user_sites USING btree (user_id);
CREATE INDEX idx_user_sites_site ON user_sites USING btree (site_id);
CREATE INDEX idx_user_sites_role ON user_sites USING btree (site_role);
CREATE INDEX idx_user_sites_active ON user_sites USING btree (is_active);
CREATE INDEX idx_user_sites_access_granted ON user_sites USING btree (access_granted_at);
CREATE INDEX idx_user_sites_user_active ON user_sites USING btree (user_id, is_active);
CREATE INDEX idx_user_sites_site_active ON user_sites USING btree (site_id, is_active);
CREATE INDEX idx_user_sites_site_role ON user_sites USING btree (site_id, site_role);
CREATE INDEX idx_user_sites_active_granted ON user_sites USING btree (is_active, access_granted_at);

-- Create partial indexes for performance optimization
CREATE INDEX idx_user_sites_active_only ON user_sites USING btree (user_id, site_id) WHERE is_active = true;
CREATE INDEX idx_user_sites_admin_roles ON user_sites USING btree (site_id, user_id) WHERE site_role IN ('ADMIN', 'SUPER_ADMIN');

-- Create triggers for automatic timestamp updates
CREATE OR REPLACE FUNCTION update_last_modified()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_modified = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_sites_last_modified
    BEFORE UPDATE ON sites
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified();

CREATE TRIGGER tr_user_sites_last_modified
    BEFORE UPDATE ON user_sites
    FOR EACH ROW
    EXECUTE FUNCTION update_last_modified();

-- Create function for site access validation
CREATE OR REPLACE FUNCTION validate_site_access(
    p_user_id BIGINT,
    p_site_id BIGINT
) RETURNS BOOLEAN AS $$
DECLARE
v_is_accessible BOOLEAN := FALSE;
BEGIN
SELECT
    (us.is_active = TRUE AND
     s.status IN ('ACTIVE', 'MAINTENANCE') AND
     us.access_granted_at <= CURRENT_TIMESTAMP AND
     (us.access_revoked_at IS NULL OR us.access_revoked_at > CURRENT_TIMESTAMP))
INTO v_is_accessible
FROM user_sites us
         JOIN sites s ON s.id = us.site_id
WHERE us.user_id = p_user_id
  AND us.site_id = p_site_id;

RETURN COALESCE(v_is_accessible, FALSE);
END;
$$ LANGUAGE plpgsql;

-- Create function to get effective user role in site
CREATE OR REPLACE FUNCTION get_effective_site_role(
    p_user_id BIGINT,
    p_site_id BIGINT
) RETURNS VARCHAR(20) AS $$
DECLARE
v_site_role VARCHAR(20);
    v_global_role VARCHAR(20);
BEGIN
SELECT us.site_role, u.role
INTO v_site_role, v_global_role
FROM user_sites us
         JOIN users u ON u.id = us.user_id
WHERE us.user_id = p_user_id
  AND us.site_id = p_site_id
  AND us.is_active = TRUE;

RETURN COALESCE(v_site_role, v_global_role);
END;
$$ LANGUAGE plpgsql;

-- Create materialized view for site statistics (useful for dashboards)
CREATE MATERIALIZED VIEW site_statistics AS
SELECT
    s.id,
    s.code,
    s.name,
    s.status,
    s.time_zone,
    s.language_code,
    COUNT(us.user_id) FILTER (WHERE us.is_active = TRUE) as active_users_count,
        COUNT(us.user_id) FILTER (WHERE us.is_active = TRUE AND us.site_role IN ('ADMIN', 'SUPER_ADMIN')) as admin_users_count,
        COUNT(us.user_id) as total_users_count,
    MIN(us.access_granted_at) as first_user_granted,
    MAX(us.access_granted_at) as last_user_granted,
    s.created_at,
    s.last_modified
FROM sites s
         LEFT JOIN user_sites us ON s.id = us.site_id
GROUP BY s.id, s.code, s.name, s.status, s.time_zone, s.language_code, s.created_at, s.last_modified;

-- Create unique index on materialized view
CREATE UNIQUE INDEX idx_site_statistics_id ON site_statistics (id);
CREATE INDEX idx_site_statistics_status ON site_statistics (status);
CREATE INDEX idx_site_statistics_user_count ON site_statistics (active_users_count);

-- Insert sample data for production setup
INSERT INTO sites (code, name, description, status, time_zone, language_code) VALUES
                                                                                  ('MAIN', 'Main Production Site', 'Primary production site for all users', 'ACTIVE', 'UTC', 'en'),
                                                                                  ('ADMIN', 'Administration Site', 'Administrative interface and tools', 'ACTIVE', 'UTC', 'en');

-- Insert initial admin user access (assuming admin user exists with ID 1000)
-- This will be handled by application logic in most cases
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM users WHERE role = 'SUPER_ADMIN' LIMIT 1) THEN
        INSERT INTO user_sites (user_id, site_id, site_role, is_active, notes)
SELECT
    u.id,
    s.id,
    'SUPER_ADMIN',
    TRUE,
    'Initial production setup - super admin access'
FROM users u, sites s
WHERE u.role = 'SUPER_ADMIN'
  AND s.code IN ('MAIN', 'ADMIN')
  AND NOT EXISTS (
    SELECT 1 FROM user_sites us2
    WHERE us2.user_id = u.id AND us2.site_id = s.id
);
END IF;
END $$;

-- Set up row-level security (RLS) for tenant isolation
ALTER TABLE sites ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_sites ENABLE ROW LEVEL SECURITY;

-- Create RLS policies for site access control
CREATE POLICY site_access_policy ON sites
    FOR ALL
    TO public
    USING (
        id IN (
            SELECT DISTINCT us.site_id
            FROM user_sites us
            WHERE us.user_id = current_setting('app.current_user_id', true)::BIGINT
              AND us.is_active = TRUE
        )
        OR current_setting('app.user_role', true) IN ('SUPER_ADMIN')
    );

CREATE POLICY user_sites_access_policy ON user_sites
    FOR ALL
    TO public
    USING (
        user_id = current_setting('app.current_user_id', true)::BIGINT
        OR site_id IN (
            SELECT DISTINCT us.site_id
            FROM user_sites us
            WHERE us.user_id = current_setting('app.current_user_id', true)::BIGINT
              AND us.is_active = TRUE
              AND get_effective_site_role(us.user_id, us.site_id) IN ('ADMIN', 'SUPER_ADMIN')
        )
        OR current_setting('app.user_role', true) IN ('SUPER_ADMIN')
    );

-- Add table comments for documentation
COMMENT ON TABLE sites IS 'Sites/tenants in the multi-tenant system with comprehensive audit and access control';
COMMENT ON COLUMN sites.code IS 'Unique site identifier code (uppercase, alphanumeric + underscore only)';
COMMENT ON COLUMN sites.name IS 'Human-readable site name for display purposes';
COMMENT ON COLUMN sites.description IS 'Detailed site description for administrative purposes';
COMMENT ON COLUMN sites.status IS 'Current operational status controlling site accessibility';
COMMENT ON COLUMN sites.time_zone IS 'Site timezone for date/time operations and scheduling';
COMMENT ON COLUMN sites.language_code IS 'Primary language code following ISO 639-1 format';

COMMENT ON TABLE user_sites IS 'Many-to-many mapping between users and sites with comprehensive relationship management';
COMMENT ON COLUMN user_sites.user_id IS 'Reference to user in the relationship';
COMMENT ON COLUMN user_sites.site_id IS 'Reference to site in the relationship';
COMMENT ON COLUMN user_sites.site_role IS 'User role specific to this site (overrides global role if set)';
COMMENT ON COLUMN user_sites.is_active IS 'Whether this user-site relationship is currently active';
COMMENT ON COLUMN user_sites.access_granted_at IS 'Timestamp when access to this site was granted';
COMMENT ON COLUMN user_sites.access_revoked_at IS 'Timestamp when access was revoked (null if still active)';
COMMENT ON COLUMN user_sites.granted_by_user_id IS 'User who granted this site access (for audit trail)';
COMMENT ON COLUMN user_sites.revoked_by_user_id IS 'User who revoked this site access (for audit trail)';
COMMENT ON COLUMN user_sites.notes IS 'Additional notes about this relationship for administrative reference';

-- Create function to refresh materialized view
CREATE OR REPLACE FUNCTION refresh_site_statistics()
RETURNS VOID AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY site_statistics;
END;
$$ LANGUAGE plpgsql;

-- Schedule materialized view refresh (requires pg_cron extension)
-- SELECT cron.schedule('refresh-site-stats', '*/15 * * * *', 'SELECT refresh_site_statistics();');

-- Verify the migration
SELECT
    'Site system migration completed successfully' as status,
    (SELECT COUNT(*) FROM sites) as sites_created,
    (SELECT COUNT(*) FROM user_sites) as user_site_relationships_created,
    (SELECT COUNT(*) FROM site_statistics) as statistics_records_created;