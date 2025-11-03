import { Layers } from 'lucide-react';
import { Outlet } from 'react-router-dom';

/**
 * AuthLayout - Layout for authentication pages (login, register, etc.)
 * Simple centered layout without sidebar
 */
export const AuthLayout = () => {
  return (
    <div className="grid min-h-svh lg:grid-cols-1">
      <div className="flex flex-col gap-4 p-6 md:p-10">
        <div className="flex justify-center gap-2 p-4 md:justify-start">
          <a href="#" className="flex items-center gap-2 font-medium">
            <div className="bg-primary text-primary-foreground flex size-6 items-center justify-center rounded-md">
              <Layers className="size-4" />
            </div>
            Easy Base
          </a>
        </div>
        <div className="flex flex-1 items-center justify-center">
          <div className="w-full max-w-xs">
            {/* Outlet renders the child route component (e.g., LoginPage) */}
            <Outlet />
          </div>
        </div>
      </div>
    </div>
  );
};
