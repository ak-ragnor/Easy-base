/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

package com.easybase.core.auth.util;

/**
 * Utility class for bitmask operations.
 * Provides pure static methods for common bitwise operations used in permission management.
 *
 * @author Akhash R
 */
public final class BitMaskUtil {

	/**
	 * Add a single bit to a mask.
	 *
	 * @param mask the current mask
	 * @param bitValue the bit value to add
	 * @return the updated mask
	 */
	public static long addBit(long mask, int bitValue) {
		return mask | bitValue;
	}

	/**
	 * Add multiple bits to a mask.
	 *
	 * @param mask the current mask
	 * @param bitValues the bit values to add
	 * @return the updated mask
	 */
	public static long addBits(long mask, int... bitValues) {
		long result = mask;

		for (int bitValue : bitValues) {
			result |= bitValue;
		}

		return result;
	}

	/**
	 * Clear all bits in a mask.
	 *
	 * @return an empty mask (0)
	 */
	public static long clearMask() {
		return 0L;
	}

	/**
	 * Combine multiple bit values into a single mask.
	 *
	 * @param bitValues the bit values to combine
	 * @return the combined mask
	 */
	public static long combineBits(int... bitValues) {
		long result = 0L;

		for (int bitValue : bitValues) {
			result |= bitValue;
		}

		return result;
	}

	/**
	 * Check if a mask has a specific bit set.
	 *
	 * @param mask the mask to check
	 * @param bitValue the bit value to check
	 * @return true if the bit is set
	 */
	public static boolean hasBit(long mask, int bitValue) {
		if ((mask & bitValue) != 0) {
			return true;
		}

		return false;
	}

	/**
	 * Remove a single bit from a mask.
	 *
	 * @param mask the current mask
	 * @param bitValue the bit value to remove
	 * @return the updated mask
	 */
	public static long removeBit(long mask, int bitValue) {
		return mask & ~bitValue;
	}

	/**
	 * Remove multiple bits from a mask.
	 *
	 * @param mask the current mask
	 * @param bitValues the bit values to remove
	 * @return the updated mask
	 */
	public static long removeBits(long mask, int... bitValues) {
		long result = mask;

		for (int bitValue : bitValues) {
			result &= ~bitValue;
		}

		return result;
	}

	private BitMaskUtil() {
		throw new UnsupportedOperationException(
			"BitMaskUtil is a utility class and cannot be instantiated");
	}

}