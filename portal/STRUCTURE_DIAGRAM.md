# Portal Structure - Visual Guide

## Application Flow

```
User Visit
    ↓
┌─────────────────┐
│ App.tsx │ ← React Router
│ (BrowserRouter)│
└────────┬────────┘
         │
         ├─→ Authenticated? → No ─→ /login (AuthLayout)
         │ │
         │ ↓
         │ LoginPage
         │ │
         │ ↓
         │ [Login Success]
         │ │
         └─→ Authenticated? → Yes ──────┘
                    ↓
           /dashboard (MainLayout)
                    ↓
              ┌────────────────┐
              │ MainLayout │
              │ ┌──────────┐ │
              │ │ Sidebar │ │
              │ │ │ │
              │ │ - Dashboard│
              │ │ - Settings │
              │ │ - Users │
              │ │ │ │
              │ └──────────┘ │
              │ │
              │ ┌──────────┐ │
              │ │ Outlet │ │ ← Page renders here
              │ │ │ │
              │ └──────────┘ │
              └────────────────┘
```

---

## Layout System

### AuthLayout (Public Pages)

```
┌────────────────────────────────────┐
│ AuthLayout │
│ │
│ ┌─────────────┐ │
│ │ Easy Base │ (Logo/Header) │
│ └─────────────┘ │
│ │
│ ┌─────────────┐ │
│ │ │ │
│ │ Outlet │ ← LoginPage │
│ │ │ │
│ └─────────────┘ │
│ │
└────────────────────────────────────┘
```

### MainLayout (Authenticated Pages)

```
┌───────────────────────────────────────────────────┐
│ MainLayout │
│ │
│ ┌────────┐ ┌────────────────────────────────┐ │
│ │ │ │ Header │ │
│ │ │ │ ┌──┐ ┌──────────────────┐ │ │
│ │ │ │ │≡ │ │ Breadcrumbs │ │ │
│ │ │ │ └──┘ └──────────────────┘ │ │
│ │ │ └────────────────────────────────┘ │
│ │ Side- │ │
│ │ bar │ ┌────────────────────────────────┐ │
│ │ │ │ │ │
│ │ - Dash │ │ Page Content │ │
│ │ - Set │ │ (Outlet) │ │
│ │ - User │ │ │ │
│ │ │ │ │ │
│ │ ───────│ │ │ │
│ │ │ │ │ │
│ │ [User] │ │ │ │
│ │ Logout│ │ │ │
│ │ │ └────────────────────────────────┘ │
│ └────────┘ │
└───────────────────────────────────────────────────┘
```

---

## Folder Structure Tree

```
portal/src/
│
├── 📁 layouts/
│ ├── 📄 MainLayout.tsx ← With Sidebar
│ └── 📄 AuthLayout.tsx ← Without Sidebar
│
├── 📁 pages/
│ ├── 📁 auth/
│ │ └── 📄 LoginPage.tsx
│ ├── 📁 dashboard/
│ │ └── 📄 DashboardPage.tsx
│ ├── 📁 settings/
│ │ └── 📄 SettingsPage.tsx
│ └── 📄 NotFoundPage.tsx
│
├── 📁 components/
│ ├── 📁 ui/ ← shadcn components
│ │ ├── button.tsx
│ │ ├── sidebar.tsx
│ │ └── ...
│ ├── 📁 layout/
│ │ └── 📄 PageHeader.tsx ← Reusable header
│ ├── 📄 app-sidebar.tsx ← Sidebar navigation
│ ├── 📄 nav-user.tsx ← User dropdown
│ ├── 📄 login-form.tsx ← Login form
│ └── 📄 ProtectedRoute.tsx ← Auth guard
│
├── 📁 stores/
│ └── 📄 auth-store.ts ← Zustand store
│
├── 📁 services/
│ └── 📄 auth-service.ts ← API calls
│
├── 📁 lib/
│ ├── 📄 api-client.ts ← Axios + interceptors
│ └── 📄 token-storage.ts ← Token utils
│
├── 📁 types/
│ └── 📄 auth.ts ← TypeScript types
│
├── 📄 App.tsx ← Routes
└── 📄 main.tsx ← Entry point
```

---

## Component Hierarchy

### Authenticated Page Example

```
App
 └── BrowserRouter
      └── Routes
           └── Route (MainLayout - Protected)
                └── MainLayout
                     ├── SidebarProvider
                     │ ├── AppSidebar
                     │ │ ├── SidebarHeader
                     │ │ ├── SidebarContent
                     │ │ │ ├── NavMain
                     │ │ │ ├── NavProjects
                     │ │ │ └── NavSecondary
                     │ │ └── SidebarFooter
                     │ │ └── NavUser
                     │ │
                     │ └── SidebarInset
                     │ └── Outlet
                     │ └── DashboardPage
                     │ ├── PageHeader
                     │ │ ├── SidebarTrigger
                     │ │ └── Breadcrumb
                     │ └── Content
```

### Login Page Example

```
App
 └── BrowserRouter
      └── Routes
           └── Route (AuthLayout)
                └── AuthLayout
                     ├── Header (Logo)
                     └── Outlet
                          └── LoginPage
                               └── LoginForm
                                    ├── Formik
                                    ├── Input (Email)
                                    ├── Input (Password)
                                    └── Button (Submit)
```

---

## Route Structure

```
/ (root)
├── /login ← AuthLayout
│ └── LoginPage
│
├── /dashboard ← MainLayout (Protected)
│ └── DashboardPage
│
├── /settings ← MainLayout (Protected)
│ └── SettingsPage
│
└── * (404)
    └── NotFoundPage
```

---

## Data Flow

### Authentication Flow

```
1. User enters credentials
        ↓
   LoginForm.tsx
        ↓
   useAuthStore().login()
        ↓
   auth-service.ts → POST /api/auth/login
        ↓
   api-client.ts (Axios interceptor)
        ↓
   Response: { accessToken, refreshToken, ... }
        ↓
   Zustand store updates
        ↓
   localStorage persists tokens
        ↓
   isAuthenticated = true
        ↓
   Navigate to /dashboard
        ↓
   MainLayout renders with sidebar
```

### API Request Flow

```
Component makes API call
        ↓
   api-client.ts
        ↓
   Request Interceptor:
   - Read token from localStorage
   - Attach Authorization: Bearer {token}
        ↓
   Axios sends request
        ↓
   Response Interceptor:
   - Handle errors
   - Log in dev mode
        ↓
   Return data to component
```

---

## State Management

```
┌─────────────────────────────────────┐
│ Zustand Store │
│ (auth-store.ts) │
│ │
│ State: │
│ - user │
│ - accessToken │
│ - refreshToken │
│ - isAuthenticated │
│ - isLoading │
│ - error │
│ │
│ Actions: │
│ - login() │
│ - logout() │
│ - refreshTokens() │
│ - clearAuth() │
└─────────────────────────────────────┘
          ↓ Persist
┌─────────────────────────────────────┐
│ localStorage │
│ (auth-storage) │
│ │
│ { accessToken, refreshToken, │
│ sessionId, user } │
└─────────────────────────────────────┘
```

---

## Adding a New Feature

### Example: Adding a "Users" page

```
Step 1: Create page component
   portal/src/pages/users/UsersPage.tsx

Step 2: Add route in App.tsx
   <Route path="/users" element={<UsersPage />} />

Step 3: Add to sidebar
   app-sidebar.tsx → data.navMain

Step 4: Create API service (if needed)
   services/users-service.ts

Step 5: Create Zustand store (if needed)
   stores/users-store.ts
```

**Result:**
```
Users Page is now:
✓ Accessible at /users
✓ Protected by authentication
✓ Has persistent sidebar
✓ Shows in navigation
✓ Follows consistent layout
```

---

## Key Files Reference

| File | Purpose | When to Edit |
|------|---------|-------------|
| `App.tsx` | Define routes | Add new pages |
| `app-sidebar.tsx` | Sidebar navigation | Update menu items |
| `MainLayout.tsx` | Authenticated layout | Change sidebar behavior |
| `AuthLayout.tsx` | Public page layout | Change login page look |
| `PageHeader.tsx` | Page header component | Modify header structure |
| `auth-store.ts` | Auth state | Add auth logic |
| `api-client.ts` | HTTP client | Modify interceptors |

This structure is scalable, maintainable, and follows React + shadcn best practices!