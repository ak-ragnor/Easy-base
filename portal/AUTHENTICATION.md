# Authentication Implementation Guide

## Overview
This application uses a modern authentication system with **Zustand** for state management, **Axios** for API calls, **React Router** for routing, and **Formik + Yup** for form handling.

## Architecture

### Tech Stack
- **State Management:** Zustand with persist middleware
- **HTTP Client:** Axios with interceptors
- **Routing:** React Router v6
- **Form Management:** Formik + Yup validation
- **Auth Strategy:** JWT tokens (access + refresh)

### File Structure
```
portal/src/
├── types/
│ └── auth.ts # TypeScript interfaces
├── lib/
│ ├── api-client.ts # Axios instance + interceptors
│ └── token-storage.ts # Token utilities
├── services/
│ └── auth-service.ts # Auth API calls
├── stores/
│ └── auth-store.ts # Zustand auth store
├── pages/
│ ├── Login.tsx # Login page
│ └── Dashboard.tsx # Protected dashboard
├── components/
│ ├── ProtectedRoute.tsx # Route guard
│ ├── login-form.tsx # Login form
│ └── nav-user.tsx # User dropdown
└── App.tsx # Router setup
```

## How It Works

### 1. Login Flow

1. User enters credentials in login form (`/src/components/login-form.tsx`)

1. Formik validates input (email format, password length)

1. On submit, `useAuthStore().login()` is called

1. Auth service makes POST request to `/auth/login`

1. On success:
   - Tokens are saved to Zustand store
   - Zustand persist middleware auto-saves to localStorage
   - User data is extracted from JWT
   - User is redirected to `/dashboard`

### 2. Protected Routes
- `ProtectedRoute` component checks `isAuthenticated` from Zustand
- If not authenticated, redirects to `/login`
- If authenticated, renders the protected component

### 3. Token Management
- **Access Token:** Auto-attached to all API requests via Axios interceptor
- **Refresh Token:** Used to get new access token when it expires
- **Auto-Refresh:** Runs on mount and every 5 minutes
- **Expiry Check:** Tokens are validated before each request

### 4. Logout Flow

1. User clicks "Log out" in nav-user dropdown

1. `useAuthStore().logout()` is called

1. Backend API is called to invalidate session

1. Zustand store is cleared

1. localStorage is cleared (via persist middleware)

1. User is redirected to `/login`

## API Endpoints

### Base URL
```
http://localhost:8080
```

### Endpoints
```
POST /auth/login
- Body: { userName: string, password: string }
- Response: { accessToken, refreshToken, expiresIn, sessionId, tokenType }

POST /auth/refresh
- Body: { refreshToken: string }
- Response: { accessToken, refreshToken, expiresIn, sessionId, tokenType }

POST /auth/logout
- Body: { sessionId?: string }
- Response: { message: "Logged out successfully" }
```

## Environment Variables

Create a `.env` file in the portal directory:
```
VITE_API_BASE_URL=http://localhost:8080
```

## Key Features

### 1. Zustand Store (`auth-store.ts`)
- **State:** user, tokens, isAuthenticated, isLoading, error
- **Actions:** login, logout, refreshTokens, clearAuth
- **Persist:** Auto-saves tokens to localStorage
- **Auto-Refresh:** Checks token expiry every 5 minutes

### 2. Axios Interceptors (`api-client.ts`)
- **Request:** Auto-attaches access token to all requests
- **Response:** Handles 401 errors, logs API calls in dev mode

### 3. Form Validation (`login-form.tsx`)
- Email validation (must be valid email format)
- Password validation (minimum 6 characters)
- Real-time error messages
- Loading states during API calls

### 4. Token Utilities (`token-storage.ts`)
- Decode JWT tokens
- Check token expiry
- Extract user info from token
- Validate token format

## Usage

### Running the Application
```bash
cd portal
npm install
npm run dev
```

### Testing the Auth Flow

1. Start the backend server (http://localhost:8080)

1. Start the frontend dev server (npm run dev)

1. Navigate to http://localhost:5174/

1. Login with valid credentials

1. Check that you're redirected to dashboard

1. Verify user info appears in nav dropdown

1. Test logout functionality

### Development Tips
- Check browser console for API logs (dev mode only)
- Inspect localStorage for persisted auth state (key: 'auth-storage')
- Use React DevTools to inspect Zustand store state
- Network tab shows all API requests with auth headers

## Security Features

1. **JWT Tokens:** Secure token-based authentication

1. **Auto Token Refresh:** Prevents session expiry

1. **Protected Routes:** Unauthorized users can't access dashboard

1. **HTTPS Ready:** Configure VITE_API_BASE_URL for production

1. **Password Security:** Auto-complete disabled, secure input

1. **Session Management:** Backend tracks all active sessions

## Future Enhancements

1. **Remember Me:** Optional persistent login

1. **Password Reset:** Forgot password flow

1. **2FA:** Two-factor authentication

1. **Session Timeout:** Configurable timeout warnings

1. **Multi-Tenant:** Support tenant selection in login

1. **Social Login:** OAuth providers (Google, GitHub, etc.)

## Troubleshooting

### Login fails with network error
- Check backend is running on http://localhost:8080
- Verify CORS is enabled on backend
- Check browser console for errors

### Token refresh fails
- Check refresh token is valid and not expired
- Verify `/auth/refresh` endpoint is working
- Check localStorage for corrupted auth data

### Protected routes not working
- Check `isAuthenticated` state in Zustand store
- Verify tokens are persisted in localStorage
- Check ProtectedRoute component logic

### User data not showing
- Check JWT token contains user info
- Verify token decoding in `token-storage.ts`
- Check NavUser component props