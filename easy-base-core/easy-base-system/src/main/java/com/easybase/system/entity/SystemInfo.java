package com.easybase.system.entity;

import jakarta.persistence.*;

import com.easybase.common.data.entity.base.BaseEntity;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "eb_system_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemInfo extends BaseEntity {

	@Column(name = "app_version", nullable = false)
	private String appVersion;

	@Column(name = "db_version", nullable = false)
	private String dbVersion;

	@Column(name = "status", nullable = false)
	private String status;

}
