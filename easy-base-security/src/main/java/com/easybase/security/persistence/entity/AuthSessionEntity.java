package com.easybase.security.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

import com.easybase.common.data.entity.base.BaseEntity;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "eb_auth_sessions", indexes = {
		@Index(name = "idx_session_token", columnList = "session_token"),
		@Index(name = "idx_user_tenant", columnList = "user_id, tenant_id"),
		@Index(name = "idx_expires_at", columnList = "expires_at")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthSessionEntity extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "tenant_id", nullable = false)
	private UUID tenantId;

	@Column(name = "session_token", nullable = false, unique = true, length = 500)
	private String sessionToken;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "user_agent", length = 1000)
	private String userAgent;

	@Column(name = "ip_address", length = 45)
	private String ipAddress;

	@Column(name = "revoked", nullable = false)
	private boolean revoked;
}
