# Portal Structure Documentation

## Overview

The portal follows a modern React application structure with **layout-based routing** using React Router v6 and shadcn/ui components.

## Folder Structure

```
portal/src/
├── layouts/                    # Layout components
│   ├── MainLayout.tsx          # Layout with sidebar (authenticated pages)
│   └── AuthLayout.tsx          # Layout without sidebar (login, public pages)
│
├── pages/                      # Page components organized by feature
│   ├── auth/
│   │   └── LoginPage.tsx       # Login page
│   ├── dashboard/
│   │   └── DashboardPage.tsx   # Main dashboard
│   ├── settings/
│   │   └── SettingsPage.tsx    # Settings page (example)
│   └── NotFoundPage.tsx        # 404 page
│
├── components/
│   ├── ui/                     # shadcn/ui components (auto-generated)
│   ├── layout/                 # Layout-specific reusable components
│   │   └── PageHeader.tsx      # Reusable header with breadcrumbs
│   ├── app-sidebar.tsx         # Sidebar navigation
│   ├── nav-user.tsx            # User dropdown menu
│   ├── login-form.tsx          # Login form
│   └── ProtectedRoute.tsx      # Route guard for authenticated routes
│
├── stores/
│   └── auth-store.ts           # Zustand auth state management
│
├── services/
│   └── auth-service.ts         # Authentication API calls
│
├── lib/
│   ├── api-client.ts           # Axios instance with interceptors
│   └── token-storage.ts        # Token utilities
│
├── types/
│   └── auth.ts                 # TypeScript types
│
├── hooks/
│   └── use-mobile.ts           # Custom hooks
│
├── App.tsx                     # Main app with routes
└── main.tsx                    # Entry point
```

---

## Key Concepts

### 1. Layout Components

Layouts wrap multiple pages and provide consistent UI structure.

#### **MainLayout** (`/layouts/MainLayout.tsx`)
- Used for **authenticated pages**
- Includes persistent **sidebar navigation**
- Uses `<Outlet />` to render child routes

**Usage:**
```tsx
// In App.tsx
<Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
  <Route path="/dashboard" element={<DashboardPage />} />
  <Route path="/settings" element={<SettingsPage />} />
</Route>
```

#### **AuthLayout** (`/layouts/AuthLayout.tsx`)
- Used for **public/auth pages** (login, register)
- No sidebar - centered content
- Uses `<Outlet />` to render child routes

**Usage:**
```tsx
// In App.tsx
<Route element={<AuthLayout />}>
  <Route path="/login" element={<LoginPage />} />
</Route>
```

---

### 2. Page Structure

Pages are organized by **feature/domain**:

```
pages/
├── auth/           # Authentication pages
├── dashboard/      # Dashboard-related pages
├── settings/       # Settings pages
└── ...             # More features as needed
```

#### Example Page Component:

```tsx
// pages/dashboard/DashboardPage.tsx
import { PageHeader } from '@/components/layout/PageHeader';

export const DashboardPage = () => {
  return (
    <>
      <PageHeader
        breadcrumbs={[
          { label: 'Dashboard', href: '/dashboard' },
          { label: 'Overview' },
        ]}
      />
      <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
        {/* Page content */}
      </div>
    </>
  );
};
```

**Important:**
- Pages within `MainLayout` should NOT include `<SidebarProvider>` or `<AppSidebar>` - the layout handles that
- Each page should use `<PageHeader>` for consistent header/breadcrumbs
- Content should be wrapped in proper padding classes

---

### 3. Routing Structure

Routes are defined in `App.tsx` using **nested routing**:

```tsx
<BrowserRouter>
  <Routes>
    {/* Root redirect */}
    <Route path="/" element={<Navigate to="/dashboard" />} />

    {/* Auth routes (no sidebar) */}
    <Route element={<AuthLayout />}>
      <Route path="/login" element={<LoginPage />} />
    </Route>

    {/* Protected routes (with sidebar) */}
    <Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/settings" element={<SettingsPage />} />
    </Route>

    {/* 404 */}
    <Route path="*" element={<NotFoundPage />} />
  </Routes>
</BrowserRouter>
```

**Key Points:**
- Nested routes share the parent layout
- `<Outlet />` in layouts renders the child route
- All routes under `MainLayout` automatically get the sidebar
- Protected routes wrapped with `<ProtectedRoute>`

---

### 4. Adding a New Page

#### Step 1: Create the Page Component

```tsx
// pages/users/UsersPage.tsx
import { PageHeader } from '@/components/layout/PageHeader';

export const UsersPage = () => {
  return (
    <>
      <PageHeader
        breadcrumbs={[
          { label: 'Users', href: '/users' },
          { label: 'All Users' },
        ]}
      />
      <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
        <h1>Users Page</h1>
        {/* Your content */}
      </div>
    </>
  );
};
```

#### Step 2: Add Route to App.tsx

```tsx
import { UsersPage } from './pages/users/UsersPage'

// Inside the MainLayout route group:
<Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
  <Route path="/dashboard" element={<DashboardPage />} />
  <Route path="/settings" element={<SettingsPage />} />
  <Route path="/users" element={<UsersPage />} />  {/* NEW */}
</Route>
```

#### Step 3: Add to Sidebar (Optional)

```tsx
// components/app-sidebar.tsx
const data = {
  navMain: [
    { title: "Dashboard", url: "/dashboard", icon: SquareTerminal },
    { title: "Users", url: "/users", icon: Users },  // NEW
    { title: "Settings", url: "/settings", icon: Settings2 },
  ],
  // ...
}
```

---

### 5. Reusable Components

#### **PageHeader** (`/components/layout/PageHeader.tsx`)

Provides consistent header with sidebar trigger and breadcrumbs.

**Props:**
```tsx
interface PageHeaderProps {
  breadcrumbs: Array<{
    label: string;
    href?: string;  // Last breadcrumb should not have href
  }>;
}
```

**Usage:**
```tsx
<PageHeader
  breadcrumbs={[
    { label: 'Settings', href: '/settings' },
    { label: 'General' },  // Last item = current page
  ]}
/>
```

---

### 6. Sidebar Navigation

The sidebar (`app-sidebar.tsx`) is defined once and shared across all protected routes.

**Update Navigation:**
```tsx
const data = {
  navMain: [
    {
      title: "Dashboard",
      url: "/dashboard",
      icon: SquareTerminal,
      isActive: true,
    },
    {
      title: "Settings",
      url: "/settings",
      icon: Settings2,
      items: [          // Optional submenu
        { title: "General", url: "/settings" },
        { title: "Team", url: "/settings/team" },
      ],
    },
  ],
  navSecondary: [
    { title: "Support", url: "#", icon: LifeBuoy },
  ],
  projects: [
    { name: "Project 1", url: "#", icon: Frame },
  ],
}
```

---

### 7. Authentication Flow

1. **Unauthenticated User:**
   - Visits `/` → Redirected to `/login`
   - `/login` uses `AuthLayout` (no sidebar)
   - After login → Redirected to `/dashboard`

2. **Authenticated User:**
   - Visits `/` → Redirected to `/dashboard`
   - `/dashboard` uses `MainLayout` (with sidebar)
   - Sidebar persists across all protected routes
   - Can logout via user dropdown

---

## Benefits of This Structure

✅ **Consistent Layout** - Sidebar appears on all authenticated pages automatically
✅ **Clean Separation** - Auth pages vs. App pages have different layouts
✅ **Reusable Components** - `PageHeader`, layouts can be reused
✅ **Easy to Scale** - Add new pages without duplicating layout code
✅ **Type-Safe** - Full TypeScript support
✅ **shadcn Integration** - Uses shadcn/ui components throughout
✅ **Maintainable** - Clear folder structure by feature

---

## Common Patterns

### Pattern 1: Public Page (No Auth Required)

```tsx
// Add to App.tsx outside protected routes
<Route path="/about" element={<AboutPage />} />
```

### Pattern 2: Protected Page with Sidebar

```tsx
// Add inside MainLayout group
<Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
  <Route path="/new-page" element={<NewPage />} />
</Route>
```

### Pattern 3: Nested Routes (Settings Example)

```tsx
<Route path="/settings" element={<SettingsLayout />}>
  <Route index element={<SettingsGeneral />} />
  <Route path="team" element={<SettingsTeam />} />
  <Route path="billing" element={<SettingsBilling />} />
</Route>
```

---

## File Naming Conventions

- **Layouts:** `PascalCase` + `Layout` suffix (e.g., `MainLayout.tsx`)
- **Pages:** `PascalCase` + `Page` suffix (e.g., `DashboardPage.tsx`)
- **Components:** `PascalCase` (e.g., `PageHeader.tsx`)
- **Utilities:** `kebab-case` (e.g., `api-client.ts`)

---

## Next Steps

1. **Add more pages** following the structure above
2. **Customize sidebar** navigation in `app-sidebar.tsx`
3. **Create sub-layouts** for complex features (e.g., Settings)
4. **Add page-specific components** in their respective folders
5. **Implement real features** in place of placeholder content

This structure is production-ready and scales well as your application grows!
