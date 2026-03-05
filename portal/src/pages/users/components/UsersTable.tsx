/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { useCallback, useEffect, useMemo } from 'react';

import type { ReactNode, RefObject } from 'react';

import { DataTable } from '@/components/DataTable';
import { useTableQuery } from '@/hooks/useTableQuery.ts';

import userService from '../services/userService.ts';
import type { UserDto } from '../types/user';

import { getUsersTableColumns } from './usersTableColumns.tsx';

interface UsersTableProps {
  onEdit: (user: UserDto) => void;
  onDelete: (user: UserDto) => void;
  onRefetchRef?: RefObject<(() => void) | null>;
  actions?: ReactNode;
}

export const UsersTable = ({ onEdit, onDelete, onRefetchRef, actions }: UsersTableProps) => {
  const tableQuery = useTableQuery<UserDto>({
    queryFn: useCallback(params => userService.queryUsers(params), []),
    defaultPageSize: 10,
    defaultSort: '-updatedAt',
  });

  useEffect(() => {
    if (onRefetchRef) {
      onRefetchRef.current = tableQuery.refetch;
    }
  }, [tableQuery.refetch, onRefetchRef]);

  const columns = useMemo(() => getUsersTableColumns({ onEdit, onDelete }), [onEdit, onDelete]);

  return (
    <DataTable
      mode="server"
      columns={columns}
      data={tableQuery.data}
      totalElements={tableQuery.totalElements}
      pageCount={tableQuery.totalPages}
      pageIndex={tableQuery.pageIndex}
      pageSize={tableQuery.pageSize}
      isLoading={tableQuery.isLoading || tableQuery.isPending}
      onPaginationChange={(pi, ps) => {
        if (ps !== tableQuery.pageSize) {
          tableQuery.setPageSize(ps);
        } else {
          tableQuery.setPage(pi);
        }
      }}
      onSortingChange={tableQuery.setSort}
      searchMode={tableQuery.searchMode}
      searchValue={tableQuery.searchValue}
      onSearchModeChange={tableQuery.setSearchMode}
      onSearchValueChange={tableQuery.setSearchValue}
      actions={actions}
    />
  );
};
