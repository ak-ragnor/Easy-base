import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './stores/auth-store'
import { LoginPage } from './pages/Login'
import { DashboardPage } from './pages/Dashboard'
import { ProtectedRoute } from './components/ProtectedRoute'

import './App.css'

const App = () => {
  console.log('App component rendering...')

  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
  console.log('isAuthenticated:', isAuthenticated)

  return (
    <BrowserRouter>
      <div style={{ minHeight: '100vh', background: 'white' }}>
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

          {/* Login route - redirect to dashboard if already authenticated */}
          <Route
            path="/login"
            element={
              isAuthenticated ? (
                <Navigate to="/dashboard" replace />
              ) : (
                <LoginPage />
              )
            }
          />

          {/* Protected dashboard route */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <DashboardPage />
              </ProtectedRoute>
            }
          />

          {/* Catch all - redirect to root */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </BrowserRouter>
  )
}

export default App
