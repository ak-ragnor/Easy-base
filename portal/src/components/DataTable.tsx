/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { useState } from 'react';

import {
  type ColumnDef,
  type ColumnFiltersState,
  type FilterFn,
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  type SortingState,
  useReactTable,
  type VisibilityState,
} from '@tanstack/react-table';

import { DataTableToolbar } from '@/components/DataTableToolbar';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { sortingStateToBackendSort } from '@/lib/utils';
import type { SearchMode } from '@/types/table';

// --- Server mode props ---
interface ServerModeProps<TData> {
  mode: 'server';
  columns: Array<ColumnDef<TData, unknown>>;
  data: TData[];
  totalElements: number;
  pageCount: number;
  pageIndex: number;
  pageSize: number;
  isLoading: boolean;
  onPaginationChange: (pageIndex: number, pageSize: number) => void;
  onSortingChange: (sort: string) => void;
  searchMode: SearchMode;
  searchValue: string;
  onSearchModeChange: (mode: SearchMode) => void;
  onSearchValueChange: (value: string) => void;
}

// --- Client mode props ---
interface ClientModeProps<TData> {
  mode?: 'client';
  columns: Array<ColumnDef<TData, unknown>>;
  data: TData[];
  searchPlaceholder?: string;
  searchFilterFn?: FilterFn<TData>;
  pageSize?: number;
}

type DataTableProps<TData> = ServerModeProps<TData> | ClientModeProps<TData>;

export const DataTable = <TData,>(props: DataTableProps<TData>) => {
  const isServer = props.mode === 'server';

  // Client-only state
  const [_sorting, _setSorting] = useState<SortingState>([]);
  const [_columnFilters, _setColumnFilters] = useState<ColumnFiltersState>([]);
  const [_globalFilter, _setGlobalFilter] = useState('');
  const [_columnVisibility, _setColumnVisibility] = useState<VisibilityState>({});
  const [_rowSelection, _setRowSelection] = useState({});

  // Server-mode sorting state (for visual indicator only, actual sort is managed externally)
  const [_serverSorting, _setServerSorting] = useState<SortingState>([]);

  const serverProps = isServer ? props : null;
  const clientProps = !isServer ? props : null;

  const table = useReactTable<TData>({
    data: props.data,
    columns: props.columns,
    getCoreRowModel: getCoreRowModel(),
    ...(isServer
      ? {
          manualPagination: true,
          manualSorting: true,
          pageCount: serverProps!.pageCount,
          state: {
            sorting: _serverSorting,
            columnVisibility: _columnVisibility,
            rowSelection: _rowSelection,
            pagination: {
              pageIndex: serverProps!.pageIndex,
              pageSize: serverProps!.pageSize,
            },
          },
          onSortingChange: updater => {
            const next = typeof updater === 'function' ? updater(_serverSorting) : updater;
            _setServerSorting(next);
            serverProps!.onSortingChange(sortingStateToBackendSort(next));
          },
          onPaginationChange: updater => {
            const current = {
              pageIndex: serverProps!.pageIndex,
              pageSize: serverProps!.pageSize,
            };
            const next = typeof updater === 'function' ? updater(current) : updater;
            serverProps!.onPaginationChange(next.pageIndex, next.pageSize);
          },
          onColumnVisibilityChange: _setColumnVisibility,
          onRowSelectionChange: _setRowSelection,
          getSortedRowModel: getSortedRowModel(),
        }
      : {
          getPaginationRowModel: getPaginationRowModel(),
          getSortedRowModel: getSortedRowModel(),
          getFilteredRowModel: getFilteredRowModel(),
          onSortingChange: _setSorting,
          onColumnFiltersChange: _setColumnFilters,
          onGlobalFilterChange: _setGlobalFilter,
          onColumnVisibilityChange: _setColumnVisibility,
          onRowSelectionChange: _setRowSelection,
          globalFilterFn: clientProps!.searchFilterFn,
          state: {
            sorting: _sorting,
            columnFilters: _columnFilters,
            globalFilter: _globalFilter,
            columnVisibility: _columnVisibility,
            rowSelection: _rowSelection,
          },
          initialState: {
            pagination: { pageSize: clientProps!.pageSize ?? 10 },
          },
        }),
  });

  const visibleColumnCount = table.getVisibleLeafColumns().length;

  return (
    <div className="space-y-4">
      {/* Toolbar */}
      {isServer ? (
        <DataTableToolbar
          mode="server"
          searchMode={serverProps!.searchMode}
          searchValue={serverProps!.searchValue}
          onSearchModeChange={serverProps!.onSearchModeChange}
          onSearchValueChange={serverProps!.onSearchValueChange}
          table={table as unknown as Parameters<typeof DataTableToolbar>[0]['table']}
        />
      ) : (
        <DataTableToolbar
          mode="client"
          globalFilter={_globalFilter}
          onGlobalFilterChange={_setGlobalFilter}
          searchPlaceholder={clientProps!.searchPlaceholder}
          table={table as unknown as Parameters<typeof DataTableToolbar>[0]['table']}
        />
      )}

      {/* Table */}
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map(headerGroup => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map(header => (
                  <TableHead key={header.id}>
                    {header.isPlaceholder
                      ? null
                      : flexRender(header.column.columnDef.header, header.getContext())}
                  </TableHead>
                ))}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {isServer && serverProps!.isLoading ? (
              // Skeleton rows while loading
              Array.from({ length: serverProps!.pageSize }).map((_, i) => (
                <TableRow key={`skeleton-${i}`}>
                  {Array.from({ length: visibleColumnCount }).map((_, j) => (
                    <TableCell key={`skeleton-cell-${j}`}>
                      <Skeleton className="h-4 w-full" />
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : table.getRowModel().rows.length > 0 ? (
              table.getRowModel().rows.map(row => (
                <TableRow key={row.id} data-state={row.getIsSelected() && 'selected'}>
                  {row.getVisibleCells().map(cell => (
                    <TableCell key={cell.id}>
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={visibleColumnCount} className="h-24 text-center">
                  No results.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      {/* Pagination footer */}
      {isServer ? (
        <div className="flex items-center justify-between px-2">
          <div className="text-muted-foreground text-sm">
            Page {serverProps!.pageIndex + 1} of {serverProps!.pageCount} &middot;{' '}
            {serverProps!.totalElements} results
          </div>
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => table.previousPage()}
              disabled={!table.getCanPreviousPage() || serverProps!.isLoading}
            >
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => table.nextPage()}
              disabled={!table.getCanNextPage() || serverProps!.isLoading}
            >
              Next
            </Button>
          </div>
        </div>
      ) : (
        <div className="flex items-center justify-between px-2">
          <div className="text-muted-foreground text-sm">
            {table.getFilteredSelectedRowModel().rows.length} of{' '}
            {table.getFilteredRowModel().rows.length} row(s) selected.
          </div>
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => table.previousPage()}
              disabled={!table.getCanPreviousPage()}
            >
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => table.nextPage()}
              disabled={!table.getCanNextPage()}
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};
