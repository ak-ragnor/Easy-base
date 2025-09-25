/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.security.core.service;

import com.easybase.context.api.domain.ServiceContext;
import com.easybase.security.api.dto.AuthenticatedPrincipalData;

/**
 * @author Akhash
 */
public interface ServiceContextBinding {

	public void bind(AuthenticatedPrincipalData principal);

	public void clear();

	public AuthenticatedPrincipalData fromCurrentContext();

	public ServiceContext fromPrincipal(AuthenticatedPrincipalData principal);

	public ServiceContext getCurrentServiceContext();

}