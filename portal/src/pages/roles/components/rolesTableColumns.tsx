/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import type { ColumnDef } from '@tanstack/react-table';
import { Check, Copy, MoreHorizontal, Pencil, Trash2, X } from 'lucide-react';
import { toast } from 'sonner';

import { DataTableColumnHeader } from '@/components/DataTableColumnHeader';
import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

import type { RoleDto } from '../types/role';

interface RolesTableColumnsOptions {
  onEdit: (role: RoleDto) => void;
  onDelete: (role: RoleDto) => void;
}

export const getRolesTableColumns = ({
  onEdit,
  onDelete,
}: RolesTableColumnsOptions): Array<ColumnDef<RoleDto, unknown>> => [
  {
    id: 'select',
    header: ({ table }) => (
      <Checkbox
        checked={
          table.getIsAllPageRowsSelected() || (table.getIsSomePageRowsSelected() && 'indeterminate')
        }
        onCheckedChange={value => table.toggleAllPageRowsSelected(!!value)}
        aria-label="Select all"
      />
    ),
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={value => row.toggleSelected(!!value)}
        aria-label="Select row"
      />
    ),
    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: 'id',
    header: 'ID',
    cell: ({ row }) => {
      const id = row.getValue<string>('id');
      return (
        <div className="flex items-center gap-1">
          <span className="text-muted-foreground font-mono text-xs">{id.slice(0, 8)}…</span>
          <Button
            variant="ghost"
            size="icon"
            className="size-5 shrink-0"
            onClick={() => {
              navigator.clipboard.writeText(id);
              toast.success('ID copied');
            }}
          >
            <Copy className="size-3" />
            <span className="sr-only">Copy ID</span>
          </Button>
        </div>
      );
    },
  },
  {
    accessorKey: 'name',
    header: ({ column }) => <DataTableColumnHeader column={column} title="Name" />,
    cell: ({ row }) => <span className="font-medium">{row.getValue<string>('name')}</span>,
  },
  {
    accessorKey: 'description',
    header: 'Description',
    cell: ({ row }) => (
      <span className="text-muted-foreground">{row.getValue<string>('description') || '—'}</span>
    ),
  },
  {
    accessorKey: 'active',
    header: 'Active',
    cell: ({ row }) =>
      row.getValue<boolean>('active') ? (
        <Check className="size-4 text-green-500" />
      ) : (
        <X className="text-muted-foreground size-4" />
      ),
  },
  {
    accessorKey: 'system',
    header: 'System',
    cell: ({ row }) =>
      row.getValue<boolean>('system') ? (
        <Check className="size-4 text-blue-500" />
      ) : (
        <X className="text-muted-foreground size-4" />
      ),
  },
  {
    accessorKey: 'createdDate',
    header: ({ column }) => <DataTableColumnHeader column={column} title="Created At" />,
    cell: ({ row }) => {
      const val = row.getValue<string>('createdDate');
      return val ? new Date(val).toLocaleDateString() : '—';
    },
  },
  {
    accessorKey: 'lastModifiedDate',
    header: ({ column }) => <DataTableColumnHeader column={column} title="Updated At" />,
    cell: ({ row }) => {
      const val = row.getValue<string>('lastModifiedDate');
      return val ? new Date(val).toLocaleDateString() : '—';
    },
  },
  {
    id: 'actions',
    enableHiding: false,
    cell: ({ row }) => {
      const role = row.original;
      return (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon" className="size-8">
              <MoreHorizontal className="size-4" />
              <span className="sr-only">Open menu</span>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem onClick={() => onEdit(role)}>
              <Pencil className="size-4" />
              Edit
            </DropdownMenuItem>
            <DropdownMenuItem variant="destructive" onClick={() => onDelete(role)}>
              <Trash2 className="size-4" />
              Delete
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      );
    },
  },
];
