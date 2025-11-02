import axios, { AxiosError } from 'axios';
import type { AuthError } from '../types/auth';

// Create axios instance with base configuration
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds
});

// Request interceptor - attach access token to all requests
apiClient.interceptors.request.use(
  (config: any) => {
    // Get token from localStorage (Zustand persists it there)
    const authStorage = localStorage.getItem('auth-storage');

    if (authStorage) {
      try {
        const { state } = JSON.parse(authStorage);
        const accessToken = state?.accessToken;

        if (accessToken && config.headers) {
          config.headers.Authorization = `Bearer ${accessToken}`;
        }
      } catch (error) {
        console.error('Error parsing auth storage:', error);
      }
    }

    // Log requests in development
    if (import.meta.env.DEV) {
      console.log(`[API] ${config.method?.toUpperCase()} ${config.url}`, config.data);
    }

    return config;
  },
  (error) => {
    console.error('[API] Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor - handle errors globally
apiClient.interceptors.response.use(
  (response) => {
    // Log responses in development
    if (import.meta.env.DEV) {
      console.log(`[API] Response ${response.status}:`, response.data);
    }
    return response;
  },
  (error: AxiosError<AuthError>) => {
    // Log errors in development
    if (import.meta.env.DEV) {
      console.error('[API] Response error:', error.response?.data || error.message);
    }

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
      return Promise.reject(networkError);
    }

    // Handle other errors
    const authError: AuthError = {
      message: error.response.data?.message || error.message || 'An error occurred',
      code: error.response.data?.code,
      status: error.response.status,
    };

    return Promise.reject(authError);
  }
);

export default apiClient;
