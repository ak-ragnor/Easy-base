/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { useCallback, useRef, useState } from 'react';

import { Plus } from 'lucide-react';
import { toast } from 'sonner';

import { Button } from '@/components/ui/button';
import { PageHeader } from '@/layouts/PageHeader';

import { DeleteRoleDialog } from './components/DeleteRoleDialog';
import { RoleFormSheet } from './components/RoleFormSheet';
import { RolesTable } from './components/RolesTable';
import { useRoleStore } from './stores/roleStore.ts';
import type { CreateRoleRequest, RoleDto, UpdateRoleRequest } from './types/role';

export const RolesPage = () => {
  const { createRole, updateRole, deleteRole } = useRoleStore();

  const refetchRef = useRef<(() => void) | null>(null);

  const [sheetOpen, setSheetOpen] = useState(false);
  const [editingRole, setEditingRole] = useState<RoleDto | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deletingRole, setDeletingRole] = useState<RoleDto | null>(null);

  const handleAddRole = () => {
    setEditingRole(null);
    setSheetOpen(true);
  };

  const handleEdit = useCallback((role: RoleDto) => {
    setEditingRole(role);
    setSheetOpen(true);
  }, []);

  const handleDeleteClick = useCallback((role: RoleDto) => {
    setDeletingRole(role);
    setDeleteDialogOpen(true);
  }, []);

  const handleFormSubmit = useCallback(
    async (values: CreateRoleRequest | UpdateRoleRequest, roleId?: string) => {
      try {
        if (roleId) {
          await updateRole(roleId, values as UpdateRoleRequest);
          toast.success('Role updated successfully');
        } else {
          await createRole(values as CreateRoleRequest);
          toast.success('Role created successfully');
        }
        refetchRef.current?.();
      } catch (error) {
        const message = error instanceof Error ? error.message : 'An error occurred';
        toast.error(message);
        throw error;
      }
    },
    [createRole, updateRole]
  );

  const handleDeleteConfirm = useCallback(
    async (roleId: string) => {
      try {
        await deleteRole(roleId);
        toast.success('Role deleted successfully');
        refetchRef.current?.();
      } catch (error) {
        const message = error instanceof Error ? error.message : 'Failed to delete role';
        toast.error(message);
        throw error;
      }
    },
    [deleteRole]
  );

  return (
    <>
      <PageHeader breadcrumbs={[{ label: 'Roles' }]} />

      <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
        <RolesTable
          onEdit={handleEdit}
          onDelete={handleDeleteClick}
          onRefetchRef={refetchRef}
          actions={
            <Button onClick={handleAddRole}>
              <Plus className="size-4" />
              Add Role
            </Button>
          }
        />
      </div>

      <RoleFormSheet
        open={sheetOpen}
        onOpenChange={setSheetOpen}
        role={editingRole}
        onSubmit={handleFormSubmit}
      />

      <DeleteRoleDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        role={deletingRole}
        onConfirm={handleDeleteConfirm}
      />
    </>
  );
};
