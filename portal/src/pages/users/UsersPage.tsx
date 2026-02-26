/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { useCallback, useRef, useState } from 'react';

import { Plus } from 'lucide-react';
import { toast } from 'sonner';

import { Button } from '@/components/ui/button';
import { PageHeader } from '@/layouts/PageHeader';

import { DeleteUserDialog } from './components/DeleteUserDialog';
import { UserFormSheet } from './components/UserFormSheet';
import { UsersTable } from './components/UsersTable';
import { useUserStore } from './stores/userStore.ts';
import type { CreateUserRequest, UpdateUserRequest, UserDto } from './types/user';

export const UsersPage = () => {
  const { createUser, updateUser, deleteUser } = useUserStore();

  const refetchRef = useRef<(() => void) | null>(null);

  const [sheetOpen, setSheetOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<UserDto | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deletingUser, setDeletingUser] = useState<UserDto | null>(null);

  const handleAddUser = () => {
    setEditingUser(null);
    setSheetOpen(true);
  };

  const handleEdit = useCallback((user: UserDto) => {
    setEditingUser(user);
    setSheetOpen(true);
  }, []);

  const handleDeleteClick = useCallback((user: UserDto) => {
    setDeletingUser(user);
    setDeleteDialogOpen(true);
  }, []);

  const handleFormSubmit = useCallback(
    async (values: CreateUserRequest | UpdateUserRequest, userId?: string) => {
      try {
        if (userId) {
          await updateUser(userId, values as UpdateUserRequest);
          toast.success('User updated successfully');
        } else {
          await createUser(values as CreateUserRequest);
          toast.success('User created successfully');
        }
        refetchRef.current?.();
      } catch (error) {
        const message = error instanceof Error ? error.message : 'An error occurred';
        toast.error(message);
        throw error;
      }
    },
    [createUser, updateUser]
  );

  const handleDeleteConfirm = useCallback(
    async (userId: string) => {
      try {
        await deleteUser(userId);
        toast.success('User deleted successfully');
        refetchRef.current?.();
      } catch (error) {
        const message = error instanceof Error ? error.message : 'Failed to delete user';
        toast.error(message);
        throw error;
      }
    },
    [deleteUser]
  );

  return (
    <>
      <PageHeader breadcrumbs={[{ label: 'Users' }]} />

      <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
        <div className="flex items-center justify-end">
          <Button onClick={handleAddUser}>
            <Plus className="size-4" />
            Add User
          </Button>
        </div>

        <UsersTable onEdit={handleEdit} onDelete={handleDeleteClick} onRefetchRef={refetchRef} />
      </div>

      <UserFormSheet
        open={sheetOpen}
        onOpenChange={setSheetOpen}
        user={editingUser}
        onSubmit={handleFormSubmit}
      />

      <DeleteUserDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        user={deletingUser}
        onConfirm={handleDeleteConfirm}
      />
    </>
  );
};
