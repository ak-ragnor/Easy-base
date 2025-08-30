package com.easybase.core.user.entity;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.*;

@Entity
@Table(name = "eb_user_credentials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCredential {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotBlank
	@Size(max = 50)
	@Column(name = "password_type", nullable = false)
	private String passwordType;

	@Column(name = "password_hash", length = 255)
	private String passwordHash;

	@Size(max = 32)
	@Column(name = "password_algo", length = 32)
	private String passwordAlgo;

	@Column(name = "password_changed_at")
	private LocalDateTime passwordChangedAt;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "credential_data", columnDefinition = "jsonb")
	private Map<String, Object> credentialData;
}
