package com.easybase.security.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Entity
@Table(name = "eb_refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotNull @Column(name = "user_id", nullable = false)
	private UUID userId;

	@NotNull @Column(name = "tenant_id", nullable = false)
	private UUID tenantId;

	@NotNull @Column(name = "session_id", nullable = false)
	private UUID sessionId;

	@NotNull @Column(name = "issued_at", nullable = false)
	private Instant issuedAt;

	@NotNull @Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Builder.Default
	@Column(name = "revoked", nullable = false)
	private boolean revoked = false;

	@Column(name = "rotation_parent_id", length = 36)
	private String rotationParentId;
}
