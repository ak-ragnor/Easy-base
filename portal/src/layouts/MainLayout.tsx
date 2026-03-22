/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { Suspense } from 'react';

import { Loader2 } from 'lucide-react';
import { Outlet } from 'react-router-dom';

import { AppSidebar } from '@/components/app-sidebar';
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar';

const ContentLoader = () => (
  <div className="flex flex-1 items-center justify-center">
    <Loader2 className="text-muted-foreground h-8 w-8 animate-spin" />
  </div>
);

/**
 * MainLayout - Layout with sidebar for authenticated pages
 * Wraps all protected routes with a persistent sidebar
 */
export const MainLayout = () => {
  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <Suspense fallback={<ContentLoader />}>
          <Outlet />
        </Suspense>
      </SidebarInset>
    </SidebarProvider>
  );
};
