/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.core.context;

import com.easybase.context.api.domain.ServiceContext;
import com.easybase.context.api.port.ServiceContextProvider;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;
import com.easybase.security.core.service.ServiceContextBinding;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * Implementation of {@link ServiceContextBinding} that manages authentication
 * principal data in thread-local storage and provides service context creation.
 *
 * <p>This binding implementation ensures thread-safe storage of authentication
 * context and provides utilities for creating service contexts from principal data.</p>
 *
 * @author Akhash
 */
@Component
@RequiredArgsConstructor
public class ServiceContextBindingImpl
	extends AbstractContextBinding implements ServiceContextBinding {

	@Override
	public ServiceContext fromPrincipal(AuthenticatedPrincipalData principal) {
		return _serviceContextProvider.build(principal);
	}

	@Override
	public ServiceContext getCurrentServiceContext() {
		AuthenticatedPrincipalData authenticatedPrincipalData =
			fromCurrentContext();

		if (authenticatedPrincipalData == null) {
			return null;
		}

		return fromPrincipal(authenticatedPrincipalData);
	}

	private final ServiceContextProvider _serviceContextProvider;

}