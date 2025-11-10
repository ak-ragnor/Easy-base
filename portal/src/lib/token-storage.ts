/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { jwtDecode } from 'jwt-decode';

import type { DecodedToken } from '../types/auth';

/**
 * Decode JWT token to extract user information
 */
export const decodeToken = (token: string): DecodedToken | null => {
  try {
    return jwtDecode<DecodedToken>(token);
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
};

/**
 * Check if token is expired
 */
export const isTokenExpired = (token: string): boolean => {
  const decoded = decodeToken(token);
  if (!decoded) {
    return true;
  }

  const currentTime = Date.now() / 1000; // Convert to seconds
  return decoded.exp < currentTime;
};

/**
 * Get token expiry time in seconds
 */
export const getTokenExpiry = (token: string): number | null => {
  const decoded = decodeToken(token);
  return decoded?.exp ?? null;
};

/**
 * Check if token will expire soon (within given seconds)
 */
export const isTokenExpiringSoon = (token: string, bufferSeconds = 300): boolean => {
  const decoded = decodeToken(token);
  if (!decoded) {
    return true;
  }

  const currentTime = Date.now() / 1000;
  const timeUntilExpiry = decoded.exp - currentTime;

  return timeUntilExpiry < bufferSeconds; // Refresh if less than 5 minutes remaining
};

/**
 * Extract user information from access token
 */
export const getUserFromToken = (token: string) => {
  const decoded = decodeToken(token);
  if (!decoded) {
    return null;
  }

  return {
    userId: decoded.sub,
    email: decoded.email ?? '',
    userName: decoded.userName ?? decoded.email ?? '',
    tenantId: decoded.tenantId,
    authorities: decoded.authorities ?? [],
  };
};

/**
 * Validate token format (basic check)
 */
export const isValidTokenFormat = (token: string): boolean => {
  if (!token || typeof token !== 'string') {
    return false;
  }

  // JWT tokens have 3 parts separated by dots
  const parts = token.split('.');
  return parts.length === 3;
};
