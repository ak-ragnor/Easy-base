import { PageHeader } from '@/layouts/PageHeader.tsx';

/**
 * SettingsPage - Application settings page
 * Example of another page using MainLayout with sidebar
 */
export const SettingsPage = () => {
  return (
    <>
      <PageHeader breadcrumbs={[{ label: 'Settings', href: '/settings' }, { label: 'General' }]} />
      <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
        <div className="rounded-lg border p-6">
          <h2 className="mb-4 text-2xl font-bold">Settings</h2>
          <p className="text-muted-foreground">Configure your application settings here.</p>
        </div>
      </div>
    </>
  );
};
