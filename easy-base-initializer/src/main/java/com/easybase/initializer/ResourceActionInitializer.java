/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.initializer;

import com.easybase.core.auth.entity.ResourceAction;
import com.easybase.core.auth.entity.RolePermission;
import com.easybase.core.auth.helper.PermissionHelper;
import com.easybase.core.auth.repository.ResourceActionRepository;
import com.easybase.core.auth.repository.RolePermissionRepository;
import com.easybase.infrastructure.auth.annotation.ActionDefinition;
import com.easybase.infrastructure.auth.annotation.ActionRoles;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializer for discovering and registering actions from annotated classes.
 *
 * @author Akhash R
 */
@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class ResourceActionInitializer implements ApplicationRunner {

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		log.info("=== Step 3: Action Discovery ===");

		_discoverAndRegisterActions();
	}

	private int _calculateNextBitValue(
		Iterable<ResourceAction> existingActions) {

		int maxBitValue = 0;

		for (ResourceAction action : existingActions) {
			if (action.getBitValue() > maxBitValue) {
				maxBitValue = action.getBitValue();
			}
		}

		if (maxBitValue == 0) {
			return 1;
		}

		return maxBitValue << 1;
	}

	private void _discoverAndRegisterActions() {
		log.info("Starting action discovery process...");

		try {
			ClassPathScanningCandidateComponentProvider scanner =
				new ClassPathScanningCandidateComponentProvider(false);

			scanner.addIncludeFilter(
				new AnnotationTypeFilter(ActionDefinition.class));

			Set<String> packagesToScan = new HashSet<>();

			packagesToScan.add("com.easybase.api");
			packagesToScan.add("com.easybase.core");

			int totalActionsProcessed = 0;
			int totalRoleMappingsCreated = 0;

			for (String packageToScan : packagesToScan) {
				var beanDefinitions = scanner.findCandidateComponents(
					packageToScan);

				for (var beanDefinition : beanDefinitions) {
					try {
						Class<?> actionClass = Class.forName(
							beanDefinition.getBeanClassName());

						ActionDefinition actionDef = actionClass.getAnnotation(
							ActionDefinition.class);

						if (actionDef != null) {
							int actionsProcessed = _processActionClass(
								actionClass, actionDef);

							totalActionsProcessed += actionsProcessed;

							int roleMappings = _processRoleMappings(
								actionClass, actionDef);

							totalRoleMappingsCreated += roleMappings;
						}
					}
					catch (ClassNotFoundException classNotFoundException) {
						log.error(
							"Failed to load class: {}",
							beanDefinition.getBeanClassName(),
							classNotFoundException);
					}
				}
			}

			log.info(
				"Action discovery completed. Processed {} actions and created/updated {} role mappings.",
				totalActionsProcessed, totalRoleMappingsCreated);
		}
		catch (Exception exception) {
			log.error("Error during action discovery", exception);
		}
	}

	private int _processActionClass(
		Class<?> actionClass, ActionDefinition actionDef) {

		String resourceType = actionDef.resourceType();

		log.debug(
			"Processing action class: {} for resource: {}",
			actionClass.getSimpleName(), resourceType);

		Map<String, ResourceAction> existingActions = new HashMap<>();

		List<ResourceAction> actions =
			_resourceActionRepository.findByResourceType(resourceType);

		for (ResourceAction action : actions) {
			existingActions.put(action.getActionKey(), action);
		}

		int nextBitValue = _calculateNextBitValue(existingActions.values());

		List<ResourceAction> resourceActionsList = new ArrayList<>();
		int processedCount = 0;

		for (Field field : actionClass.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) &&
				Modifier.isFinal(field.getModifiers()) &&
				(field.getType() == String.class)) {

				try {
					field.setAccessible(true);

					String actionKey = (String)field.get(null);

					if ((actionKey != null) && actionKey.contains(":")) {
						String actionName = actionKey.split(":")[1];

						ResourceAction action = existingActions.get(actionKey);

						if (action == null) {
							action = new ResourceAction();

							action.setActionKey(actionKey);
							action.setResourceType(resourceType);
							action.setActionName(actionName);
							action.setBitValue(nextBitValue);
							action.setActive(true);

							log.debug(
								"Creating new action: {} with bit value: {}",
								actionKey, nextBitValue);

							nextBitValue = nextBitValue << 1;
						}
						else {
							if (!action.isActive()) {
								action.setActive(true);
							}

							if (!actionName.equals(action.getActionName())) {
								action.setActionName(actionName);
							}
						}

						resourceActionsList.add(action);
						processedCount++;
					}
				}
				catch (IllegalAccessException illegalAccessException) {
					log.error(
						"Failed to access field: {}", field.getName(),
						illegalAccessException);
				}
			}
		}

		if (!resourceActionsList.isEmpty()) {
			_resourceActionRepository.saveAll(resourceActionsList);
		}

		return processedCount;
	}

	private int _processRoleMappings(
		Class<?> actionClass, ActionDefinition actionDef) {

		String resourceType = actionDef.resourceType();

		int roleMapping = 0;

		Map<String, Set<String>> rolePermissionsMap = new HashMap<>();

		for (Field field : actionClass.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) &&
				Modifier.isFinal(field.getModifiers()) &&
				(field.getType() == String.class)) {

				ActionRoles actionRoles = field.getAnnotation(
					ActionRoles.class);

				if (actionRoles != null) {
					try {
						field.setAccessible(true);

						String actionKey = (String)field.get(null);

						for (String roleName : actionRoles.value()) {
							Set<String> actionSet =
								rolePermissionsMap.computeIfAbsent(
									roleName, k -> new HashSet<>());

							actionSet.add(actionKey);
						}
					}
					catch (IllegalAccessException illegalAccessException) {
						log.error(
							"Failed to access field: {}", field.getName(),
							illegalAccessException);
					}
				}
			}
		}

		for (Map.Entry<String, Set<String>> entry :
				rolePermissionsMap.entrySet()) {

			String roleName = entry.getKey();

			UUID roleId;

			try {
				TypedQuery<UUID> query = _entityManager.createQuery(
					"SELECT r.id FROM Role r WHERE r.name = :name AND r.system = true",
					UUID.class);

				query.setParameter("name", roleName);

				roleId = query.getSingleResult();
			}
			catch (NoResultException noResultException) {
				log.warn(
					"Role '{}' not found in database. Skipping permission assignment.",
					roleName);

				continue;
			}

			Set<String> actionKeys = entry.getValue();

			long permissionsMask = _permissionHelper.calculatePermissionMask(
				resourceType, new ArrayList<>(actionKeys));

			Optional<RolePermission> rolePermissionOptional =
				_rolePermissionRepository.findByRoleIdAndResourceType(
					roleId, resourceType);

			RolePermission rolePermission;

			if (rolePermissionOptional.isPresent()) {
				rolePermission = rolePermissionOptional.get();

				if (rolePermission.getPermissionsMask() != permissionsMask) {
					rolePermission.setPermissionsMask(permissionsMask);

					_rolePermissionRepository.save(rolePermission);

					log.debug(
						"Updated permissions for role '{}' on resource '{}'",
						roleName, resourceType);

					roleMapping++;
				}
			}
			else {
				rolePermission = new RolePermission(
					roleId, resourceType, permissionsMask);

				_rolePermissionRepository.save(rolePermission);

				log.debug(
					"Created permissions for role '{}' on resource '{}'",
					roleName, resourceType);

				roleMapping++;
			}
		}

		return roleMapping;
	}

	private final EntityManager _entityManager;
	private final PermissionHelper _permissionHelper;
	private final ResourceActionRepository _resourceActionRepository;
	private final RolePermissionRepository _rolePermissionRepository;

}