/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { Component, lazy, Suspense } from 'react';

import type { ErrorInfo, ReactNode } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';

import { useAuthStore } from '@/pages/auth/stores/auth-store';

import { ProtectedRoute } from './components/ProtectedRoute';
import { AuthLayout } from './layouts/AuthLayout';
import { MainLayout } from './layouts/MainLayout';
import { NotFoundPage } from './pages/NotFoundPage';

import './App.css';

// Lazy-loaded route components
const LoginPage = lazy(() =>
  import('./pages/auth/LoginPage').then(m => ({ default: m.LoginPage }))
);
const DashboardPage = lazy(() =>
  import('./pages/dashboard/DashboardPage').then(m => ({ default: m.DashboardPage }))
);
const SettingsPage = lazy(() =>
  import('./pages/settings/SettingsPage').then(m => ({ default: m.SettingsPage }))
);

// Error Boundary
interface ErrorBoundaryState {
  hasError: boolean;
}

class ErrorBoundary extends Component<{ children: ReactNode }, ErrorBoundaryState> {
  state: ErrorBoundaryState = { hasError: false };

  static getDerivedStateFromError(): ErrorBoundaryState {
    return { hasError: true };
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('Uncaught error:', error, info);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex min-h-screen items-center justify-center">
          <div className="text-center">
            <h1 className="text-2xl font-bold">Something went wrong</h1>
            <p className="text-muted-foreground mt-2">An unexpected error occurred.</p>
            <button
              className="bg-primary text-primary-foreground mt-4 rounded-md px-4 py-2"
              onClick={() => {
                this.setState({ hasError: false });
                window.location.href = '/';
              }}
            >
              Try again
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

const LoadingFallback = () => (
  <div className="flex min-h-screen items-center justify-center">
    <div className="text-muted-foreground">Loading...</div>
  </div>
);

const App = () => {
  const isAuthenticated = useAuthStore(state => state.isAuthenticated);

  return (
    <ErrorBoundary>
      <BrowserRouter>
        <Suspense fallback={<LoadingFallback />}>
          <Routes>
            {/* Root route - redirect based on auth status */}
            <Route
              path="/"
              element={
                isAuthenticated ? (
                  <Navigate to="/dashboard" replace />
                ) : (
                  <Navigate to="/login" replace />
                )
              }
            />

            {/* Auth routes - uses AuthLayout (no sidebar) */}
            <Route element={<AuthLayout />}>
              <Route
                path="/login"
                element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginPage />}
              />
            </Route>

            {/* Protected routes - uses MainLayout (with sidebar) */}
            <Route
              element={
                <ProtectedRoute>
                  <MainLayout />
                </ProtectedRoute>
              }
            >
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/settings" element={<SettingsPage />} />
              {/* Add more protected routes here - they all share the same sidebar */}
            </Route>

            {/* 404 - Not found */}
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </Suspense>
      </BrowserRouter>
    </ErrorBoundary>
  );
};

export default App;
