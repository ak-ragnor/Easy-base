/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import type { AxiosError, InternalAxiosRequestConfig } from 'axios';
import axios from 'axios';

import type { AuthError } from '../types/auth';

import { isValidTokenFormat } from './token-storage';

// Create axios instance with base configuration
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL as string,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds
});

// Request interceptor - attach access token to all requests
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Get token from localStorage (Zustand persists it there)
    const authStorage = localStorage.getItem('auth-storage');

    if (authStorage) {
      try {
        const parsed = JSON.parse(authStorage) as { state?: { accessToken?: string } };
        const accessToken = parsed.state?.accessToken;

        if (accessToken && isValidTokenFormat(accessToken) && config.headers) {
          config.headers.Authorization = `Bearer ${accessToken}`;
        }
      } catch (error) {
        console.error('Error parsing auth storage:', error);
      }
    }

    return config;
  },
  error => {
    console.error('[API] Request error:', error);
    return Promise.reject(new Error(String(error)));
  }
);

// Response interceptor - handle errors globally
apiClient.interceptors.response.use(
  response => response,
  (error: AxiosError<AuthError>) => {
    // Handle 401 Unauthorized - will be handled by Zustand store
    if (error.response?.status === 401) {
      // Don't auto-logout here - let the store handle it
      // This allows the store to attempt token refresh first
      console.warn('[API] 401 Unauthorized - auth required');
    }

    // Handle network errors
    if (!error.response) {
      const networkError: AuthError = {
        message: 'Network error - please check your connection',
        code: 'NETWORK_ERROR',
      };
      return Promise.reject(new Error(networkError.message));
    }

    // Handle other errors
    const authError: AuthError = {
      message: error.response.data?.message ?? error.message ?? 'An error occurred',
      code: error.response.data?.code,
      status: error.response.status,
    };

    return Promise.reject(new Error(authError.message));
  }
);

export default apiClient;
