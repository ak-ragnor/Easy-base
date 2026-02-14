/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { create } from 'zustand';
import { persist } from 'zustand/middleware';

import { getUserFromToken, isTokenExpired, isTokenExpiringSoon } from '@/lib/token-storage';
import authService from '@/pages/auth/services/auth-service.ts';
import type { AuthError, TokenResponse, User } from '@/types/auth';

interface AuthState {
  // State
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  sessionId: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: AuthError | null;
  sessionWarning: boolean;

  // Actions
  login: (userName: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshTokens: () => Promise<void>;
  clearAuth: () => void;
  clearError: () => void;
  checkAuth: () => void;
  initializeAuth: () => void;
}

// Module-level state for refresh deduplication and interval management
let refreshPromise: Promise<void> | null = null;
let authCheckIntervalId: ReturnType<typeof setInterval> | null = null;

// Cross-tab auth synchronization via BroadcastChannel
type AuthBroadcastMessage =
  | { type: 'TOKEN_REFRESHED'; accessToken: string; refreshToken: string; sessionId: string }
  | { type: 'LOGOUT' };

let authChannel: BroadcastChannel | null = null;

function getAuthChannel(): BroadcastChannel | null {
  if (typeof window === 'undefined' || typeof BroadcastChannel === 'undefined') {
    return null;
  }
  if (!authChannel) {
    authChannel = new BroadcastChannel('easybase-auth');
    authChannel.onmessage = (event: MessageEvent<AuthBroadcastMessage>) => {
      const msg = event.data;
      if (msg.type === 'LOGOUT') {
        stopAuthCheckInterval();
        useAuthStore.setState({
          user: null,
          accessToken: null,
          refreshToken: null,
          sessionId: null,
          isAuthenticated: false,
          isLoading: false,
          error: null,
          sessionWarning: false,
        });
      } else if (msg.type === 'TOKEN_REFRESHED') {
        const user = getUserFromToken(msg.accessToken);
        if (user) {
          useAuthStore.setState({
            user,
            accessToken: msg.accessToken,
            refreshToken: msg.refreshToken,
            sessionId: msg.sessionId,
            isAuthenticated: true,
            sessionWarning: false,
          });
        }
      }
    };
  }
  return authChannel;
}

function broadcastAuth(msg: AuthBroadcastMessage) {
  getAuthChannel()?.postMessage(msg);
}

function startAuthCheckInterval() {
  stopAuthCheckInterval();
  authCheckIntervalId = setInterval(
    () => {
      useAuthStore.getState().checkAuth();
    },
    5 * 60 * 1000
  );
}

function stopAuthCheckInterval() {
  if (authCheckIntervalId !== null) {
    clearInterval(authCheckIntervalId);
    authCheckIntervalId = null;
  }
}

function toAuthError(error: unknown): AuthError {
  if (error instanceof Error) {
    return { message: error.message };
  }
  return { message: 'An unexpected error occurred' };
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
      sessionWarning: false,

      /**
       * Authenticate a user with their credentials.
       * On success, stores tokens, decodes user info, and starts background auth checks.
       */
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
            sessionWarning: false,
          });

          broadcastAuth({
            type: 'TOKEN_REFRESHED',
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            sessionId: response.sessionId,
          });

          startAuthCheckInterval();
        } catch (error) {
          set({
            user: null,
            accessToken: null,
            refreshToken: null,
            sessionId: null,
            isAuthenticated: false,
            isLoading: false,
            error: toAuthError(error),
          });
          throw error;
        }
      },

      /**
       * End the current session. Calls the logout API and clears all auth state
       * regardless of whether the API call succeeds.
       */
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
          stopAuthCheckInterval();
          broadcastAuth({ type: 'LOGOUT' });
          // Clear all auth state
          set({
            user: null,
            accessToken: null,
            refreshToken: null,
            sessionId: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
            sessionWarning: false,
          });
        }
      },

      /**
       * Refresh the access token using the stored refresh token.
       * Concurrent calls are deduplicated — only one refresh request is in-flight at a time.
       */
      refreshTokens: async () => {
        const { refreshToken } = get();

        if (!refreshToken) {
          get().clearAuth();
          return;
        }

        // Deduplicate concurrent refresh calls
        if (refreshPromise) {
          return refreshPromise;
        }

        refreshPromise = (async () => {
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
              sessionWarning: false,
            });

            broadcastAuth({
              type: 'TOKEN_REFRESHED',
              accessToken: response.accessToken,
              refreshToken: response.refreshToken,
              sessionId: response.sessionId,
            });
          } catch (error) {
            console.error('Token refresh failed:', error);
            get().clearAuth();
            throw error;
          } finally {
            refreshPromise = null;
          }
        })();

        return refreshPromise;
      },

      // Clear authentication state
      clearAuth: () => {
        stopAuthCheckInterval();
        set({
          user: null,
          accessToken: null,
          refreshToken: null,
          sessionId: null,
          isAuthenticated: false,
          isLoading: false,
          error: null,
          sessionWarning: false,
        });
      },

      // Clear error
      clearError: () => {
        set({ error: null });
      },

      /**
       * Check the current auth status. Triggers a token refresh if the access token
       * is expired or expiring soon. Sets `sessionWarning` when the token is
       * within 2 minutes of expiry.
       */
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
            get()
              .refreshTokens()
              .catch(() => {
                get().clearAuth();
              });
          } else {
            get().clearAuth();
          }
          return;
        }

        // Session expiry warning (within 2 minutes)
        if (isTokenExpiringSoon(accessToken, 120)) {
          set({ sessionWarning: true });
        } else {
          set({ sessionWarning: false });
        }

        // Check if access token is expiring soon (within 5 minutes)
        if (isTokenExpiringSoon(accessToken, 300)) {
          // Refresh in background if expiring within 5 minutes
          if (refreshToken && !isTokenExpired(refreshToken)) {
            get()
              .refreshTokens()
              .catch(error => {
                console.error('Background token refresh failed:', error);
              });
          }
        }
      },

      /**
       * Initialize auth state on app load. Checks stored tokens and starts
       * the background auth check interval if authenticated.
       */
      initializeAuth: () => {
        get().checkAuth();

        const { isAuthenticated } = get();
        if (isAuthenticated) {
          startAuthCheckInterval();
        }
      },
    }),
    {
      name: 'auth-storage', // localStorage key
      partialize: state => ({
        // Only persist these fields
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        sessionId: state.sessionId,
        user: state.user,
      }),
      onRehydrateStorage: () => state => {
        // After rehydrating from localStorage, set isAuthenticated based on tokens
        if (state?.accessToken && state.refreshToken) {
          state.isAuthenticated = true;
        }
      },
    }
  )
);

// Auto-initialize auth on load and set up cross-tab sync
if (typeof window !== 'undefined') {
  getAuthChannel();
  useAuthStore.getState().initializeAuth();
}
