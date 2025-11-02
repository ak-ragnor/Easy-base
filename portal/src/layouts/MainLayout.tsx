import { Outlet } from 'react-router-dom';
import { AppSidebar } from '@/components/app-sidebar';
import { SidebarProvider, SidebarInset } from '@/components/ui/sidebar';

/**
 * MainLayout - Layout with sidebar for authenticated pages
 * Wraps all protected routes with a persistent sidebar
 */
export const MainLayout = () => {
  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        {/* Outlet renders the child route component */}
        <Outlet />
      </SidebarInset>
    </SidebarProvider>
  );
};
