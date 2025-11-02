import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import authService from '../services/auth-service';
import {
  getUserFromToken,
  isTokenExpired,
  isTokenExpiringSoon,
} from '../lib/token-storage';
import type { User, AuthError, TokenResponse } from '../types/auth';

interface AuthState {
  // State
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  sessionId: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: AuthError | null;

  // Actions
  login: (userName: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshTokens: () => Promise<void>;
  clearAuth: () => void;
  clearError: () => void;
  checkAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      // Initial state
      user: null,
      accessToken: null,
      refreshToken: null,
      sessionId: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // Login action
      login: async (userName: string, password: string) => {
        set({ isLoading: true, error: null });

        try {
          const response: TokenResponse = await authService.login(userName, password);

          // Extract user info from token
          const user = getUserFromToken(response.accessToken);

          if (!user) {
            throw new Error('Failed to decode user information from token');
          }

          set({
            user,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            sessionId: response.sessionId,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });
        } catch (error) {
          const authError = error as AuthError;
          set({
            user: null,
            accessToken: null,
            refreshToken: null,
            sessionId: null,
            isAuthenticated: false,
            isLoading: false,
            error: authError,
          });
          throw error;
        }
      },

      // Logout action
      logout: async () => {
        const { sessionId } = get();

        try {
          // Call logout API if we have a session
          if (sessionId) {
            await authService.logout(sessionId);
          }
        } catch (error) {
          console.error('Logout error:', error);
          // Continue with logout even if API call fails
        } finally {
          // Clear all auth state
          set({
            user: null,
            accessToken: null,
            refreshToken: null,
            sessionId: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          });
        }
      },

      // Refresh tokens action
      refreshTokens: async () => {
        const { refreshToken } = get();

        if (!refreshToken) {
          get().clearAuth();
          return;
        }

        try {
          const response: TokenResponse = await authService.refreshToken(refreshToken);

          // Extract updated user info from new token
          const user = getUserFromToken(response.accessToken);

          if (!user) {
            throw new Error('Failed to decode user information from token');
          }

          set({
            user,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            sessionId: response.sessionId,
            isAuthenticated: true,
          });
        } catch (error) {
          console.error('Token refresh failed:', error);
          get().clearAuth();
          throw error;
        }
      },

      // Clear authentication state
      clearAuth: () => {
        set({
          user: null,
          accessToken: null,
          refreshToken: null,
          sessionId: null,
          isAuthenticated: false,
          isLoading: false,
          error: null,
        });
      },

      // Clear error
      clearError: () => {
        set({ error: null });
      },

      // Check authentication status
      checkAuth: () => {
        const { accessToken, refreshToken } = get();

        // No tokens means not authenticated
        if (!accessToken || !refreshToken) {
          get().clearAuth();
          return;
        }

        // Check if access token is expired
        if (isTokenExpired(accessToken)) {
          // Try to refresh if refresh token is available
          if (refreshToken && !isTokenExpired(refreshToken)) {
            get().refreshTokens().catch(() => {
              get().clearAuth();
            });
          } else {
            get().clearAuth();
          }
          return;
        }

        // Check if access token is expiring soon
        if (isTokenExpiringSoon(accessToken, 300)) {
          // Refresh in background if expiring within 5 minutes
          if (refreshToken && !isTokenExpired(refreshToken)) {
            get().refreshTokens().catch((error) => {
              console.error('Background token refresh failed:', error);
            });
          }
        }
      },
    }),
    {
      name: 'auth-storage', // localStorage key
      partialize: (state) => ({
        // Only persist these fields
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        sessionId: state.sessionId,
        user: state.user,
      }),
      onRehydrateStorage: () => (state) => {
        // After rehydrating from localStorage, set isAuthenticated based on tokens
        if (state && state.accessToken && state.refreshToken) {
          state.isAuthenticated = true;
        }
      },
    }
  )
);

// Auto-check auth status on mount and periodically
if (typeof window !== 'undefined') {
  // Check auth on initial load
  useAuthStore.getState().checkAuth();

  // Check auth every 5 minutes
  setInterval(() => {
    useAuthStore.getState().checkAuth();
  }, 5 * 60 * 1000);
}
