/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.common.api.context;

import java.util.function.Supplier;

/**
 * Thread-safe lazy value holder that computes its value only once when first accessed.
 * Uses double-checked locking pattern to ensure thread safety with minimal performance overhead.
 *
 * @param <T> the type of the lazily computed value
 * @author Akhash R
 */
public final class LazyValue<T> {

	/**
	 * Creates a new lazy value with the given supplier.
	 *
	 * @param supplier the supplier to compute the value when first accessed
	 * @throws IllegalArgumentException if supplier is null
	 */
	public LazyValue(Supplier<T> supplier) {
		if (supplier == null) {
			throw new IllegalArgumentException("Supplier cannot be null");
		}

		_supplier = supplier;
	}

	/**
	 * Gets the value, computing it on first access if necessary.
	 * Subsequent calls return the same computed value without re-computation.
	 *
	 * @return the computed value
	 */
	public T get() {
		T result = _value;

		if (result == null) {
			synchronized (this) {
				result = _value;

				if (result == null) {
					result = _supplier.get();
					_value = result;
				}
			}
		}

		return result;
	}

	/**
	 * Checks whether the value has been computed yet.
	 *
	 * @return true if value has been computed, false otherwise
	 */
	public boolean isComputed() {
		if (_value != null) {
			return true;
		}

		return false;
	}

	private final Supplier<T> _supplier;
	private volatile T _value;

}