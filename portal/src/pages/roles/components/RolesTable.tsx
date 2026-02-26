/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { useCallback, useEffect, useMemo } from 'react';

import type { RefObject } from 'react';

import { DataTable } from '@/components/DataTable';
import { useTableQuery } from '@/hooks/useTableQuery.ts';

import roleService from '../services/roleService.ts';
import type { RoleDto } from '../types/role';

import { getRolesTableColumns } from './rolesTableColumns.tsx';

interface RolesTableProps {
  onEdit: (role: RoleDto) => void;
  onDelete: (role: RoleDto) => void;
  onRefetchRef?: RefObject<(() => void) | null>;
}

export const RolesTable = ({ onEdit, onDelete, onRefetchRef }: RolesTableProps) => {
  const tableQuery = useTableQuery<RoleDto>({
    queryFn: useCallback(params => roleService.queryRoles(params), []),
    defaultPageSize: 20,
    defaultSort: '-updatedAt',
  });

  useEffect(() => {
    if (onRefetchRef) {
      onRefetchRef.current = tableQuery.refetch;
    }
  }, [tableQuery.refetch, onRefetchRef]);

  const columns = useMemo(() => getRolesTableColumns({ onEdit, onDelete }), [onEdit, onDelete]);

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
        tableQuery.setPage(pi);
        tableQuery.setPageSize(ps);
      }}
      onSortingChange={tableQuery.setSort}
      searchMode={tableQuery.searchMode}
      searchValue={tableQuery.searchValue}
      onSearchModeChange={tableQuery.setSearchMode}
      onSearchValueChange={tableQuery.setSearchValue}
    />
  );
};
