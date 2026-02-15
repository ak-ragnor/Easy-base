/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { useMemo } from 'react';

import type { FilterFn } from '@tanstack/react-table';

import { DataTable } from '@/components/DataTable';

import type { UserDto } from '../types/user';

import { getUsersTableColumns } from './users-table-columns';

interface UsersTableProps {
  users: UserDto[];
  onEdit: (user: UserDto) => void;
  onDelete: (user: UserDto) => void;
}

const usersSearchFilter: FilterFn<UserDto> = (row, _columnId, filterValue: string) => {
  const q = filterValue.toLowerCase();
  const user = row.original;
  return (
    user.email.toLowerCase().includes(q) ||
    user.firstName.toLowerCase().includes(q) ||
    user.lastName.toLowerCase().includes(q) ||
    user.displayName.toLowerCase().includes(q)
  );
};

export const UsersTable = ({ users, onEdit, onDelete }: UsersTableProps) => {
  const columns = useMemo(() => getUsersTableColumns({ onEdit, onDelete }), [onEdit, onDelete]);

  return (
    <DataTable
      columns={columns}
      data={users}
      searchPlaceholder="Search users..."
      searchFilterFn={usersSearchFilter}
    />
  );
};
