package com.easybase.core.user.entity;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.easybase.common.data.entity.base.BaseEntity;
import com.easybase.core.tenant.entity.Tenant;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "eb_users", uniqueConstraints = {
		@UniqueConstraint(columnNames = "email") })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

	@NotBlank
	@Size(max = 255)
	@Email
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Size(max = 100)
	@Column(name = "first_name")
	private String firstName;

	@Size(max = 100)
	@Column(name = "last_name")
	private String lastName;

	@Size(max = 100)
	@Column(name = "display_name")
	private String displayName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id")
	private Tenant tenant;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<UserCredential> credentials;
}
