/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.data.engine.domain.type;

import com.easybase.core.data.engine.domain.enums.AttributeType;
import com.easybase.core.data.engine.domain.type.impl.IntegerTypeDefinition;
import com.easybase.core.data.engine.domain.type.impl.StringTypeDefinition;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Akhash R
 */
class AttributeTypeDefinitionRegistryTest {

	@Test
	void testGetDescriptorReturnsCorrectDefinition() {
		StringTypeDefinition stringDef = new StringTypeDefinition();
		IntegerTypeDefinition integerDef = new IntegerTypeDefinition();

		AttributeTypeDefinitionRegistry registry =
			new AttributeTypeDefinitionRegistry(List.of(stringDef, integerDef));

		Assertions.assertSame(
			stringDef, registry.getDescriptor(AttributeType.STRING));
		Assertions.assertSame(
			integerDef, registry.getDescriptor(AttributeType.INTEGER));
	}

	@Test
	void testGetDescriptorThrowsForUnregisteredType() {
		AttributeTypeDefinitionRegistry registry =
			new AttributeTypeDefinitionRegistry(Collections.emptyList());

		Assertions.assertThrows(
			IllegalStateException.class,
			() -> registry.getDescriptor(AttributeType.BOOLEAN));
	}

}