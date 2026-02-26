/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import type { Column } from '@tanstack/react-table';
import { ArrowDown, ArrowUp, ArrowUpDown } from 'lucide-react';

import { Button } from '@/components/ui/button';

interface DataTableColumnHeaderProps<TData, TValue> {
  column: Column<TData, TValue>;
  title: string;
}

export const DataTableColumnHeader = <TData, TValue>({
  column,
  title,
}: DataTableColumnHeaderProps<TData, TValue>) => {
  const sorted = column.getIsSorted();

  return (
    <Button
      variant="ghost"
      size="sm"
      className="-ml-3"
      onClick={() => column.toggleSorting(sorted === 'asc')}
    >
      {title}
      {sorted === 'asc' ? (
        <ArrowUp className="ml-2 size-4" />
      ) : sorted === 'desc' ? (
        <ArrowDown className="ml-2 size-4" />
      ) : (
        <ArrowUpDown className="ml-2 size-4" />
      )}
    </Button>
  );
};
