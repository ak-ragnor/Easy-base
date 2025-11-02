// Authentication request/response types

export interface LoginRequest {
  userName: string;
  password: string;
  tenantId?: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number; // seconds
  sessionId: string;
  tokenType: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface LogoutRequest {
  sessionId?: string;
}

export interface User {
  userId: string;
  email: string;
  userName: string;
  tenantId?: string;
  authorities: string[];
  metadata?: Record<string, unknown>;
}

export interface Session {
  sessionId: string;
  userId: string;
  createdAt: string;
  lastAccessedAt: string;
  expiresAt: string;
  ipAddress?: string;
  userAgent?: string;
  active: boolean;
}

export interface AuthError {
  message: string;
  code?: string;
  status?: number;
}

export interface DecodedToken {
  sub: string; // userId
  email?: string;
  userName?: string;
  tenantId?: string;
  authorities?: string[];
  exp: number;
  iat: number;
}
