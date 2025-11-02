import apiClient from '../lib/api-client';
import type {
  LoginRequest,
  TokenResponse,
  RefreshTokenRequest,
  LogoutRequest,
  Session,
} from '../types/auth';

/**
 * Authentication service - handles all auth-related API calls
 */
class AuthService {
  private readonly BASE_PATH = '/easy-base/api/auth';

  /**
   * Login user with credentials
   */
  async login(userName: string, password: string): Promise<TokenResponse> {
    const payload: LoginRequest = {
      userName,
      password,
    };

    const response = await apiClient.post<TokenResponse>(
      `${this.BASE_PATH}/login`,
      payload
    );

    return response.data;
  }

  /**
   * Refresh access token using refresh token
   */
  async refreshToken(refreshToken: string): Promise<TokenResponse> {
    const payload: RefreshTokenRequest = {
      refreshToken,
    };

    const response = await apiClient.post<TokenResponse>(
      `${this.BASE_PATH}/refresh`,
      payload
    );

    return response.data;
  }

  /**
   * Logout user and invalidate session
   */
  async logout(sessionId?: string): Promise<void> {
    const payload: LogoutRequest = sessionId ? { sessionId } : {};

    await apiClient.post(`${this.BASE_PATH}/logout`, payload);
  }

  /**
   * Get all active sessions for current user
   */
  async getSessions(): Promise<Session[]> {
    const response = await apiClient.get<Session[]>(`${this.BASE_PATH}/sessions`);
    return response.data;
  }

  /**
   * Revoke a specific session
   */
  async revokeSession(sessionId: string): Promise<void> {
    await apiClient.delete(`${this.BASE_PATH}/sessions/${sessionId}`);
  }

  /**
   * Revoke all sessions for current user
   */
  async revokeAllSessions(): Promise<void> {
    await apiClient.post(`${this.BASE_PATH}/sessions/revoke-all`);
  }
}

// Export singleton instance
export const authService = new AuthService();
export default authService;
