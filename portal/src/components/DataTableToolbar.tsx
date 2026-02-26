/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import type { Table } from '@tanstack/react-table';
import { ChevronDown, Search, X } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Input } from '@/components/ui/input';
import type { SearchMode } from '@/types/table';

// Server mode toolbar props
interface ServerToolbarProps {
  mode: 'server';
  searchMode: SearchMode;
  searchValue: string;
  onSearchModeChange: (mode: SearchMode) => void;
  onSearchValueChange: (value: string) => void;
  table: Table<unknown>;
}

// Client mode toolbar props
interface ClientToolbarProps {
  mode?: 'client';
  globalFilter: string;
  onGlobalFilterChange: (value: string) => void;
  searchPlaceholder?: string;
  table: Table<unknown>;
}

type DataTableToolbarProps = ServerToolbarProps | ClientToolbarProps;

export const DataTableToolbar = (props: DataTableToolbarProps) => {
  const isServer = props.mode === 'server';

  const searchPlaceholder = isServer
    ? props.searchMode === 'eql'
      ? "e.g. email eq 'john@test.com'"
      : 'Search...'
    : (props.searchPlaceholder ?? 'Search...');

  const searchValue = isServer ? props.searchValue : props.globalFilter;

  const onSearchChange = isServer ? props.onSearchValueChange : props.onGlobalFilterChange;

  return (
    <div className="flex items-center gap-2">
      {/* Search/EQL toggle (server mode only) */}
      {isServer && (
        <div role="group" className="flex">
          <Button
            variant={props.searchMode === 'search' ? 'default' : 'outline'}
            size="sm"
            className="rounded-r-none"
            onClick={() => props.onSearchModeChange('search')}
          >
            Search
          </Button>
          <Button
            variant={props.searchMode === 'eql' ? 'default' : 'outline'}
            size="sm"
            className="-ml-px rounded-l-none"
            onClick={() => props.onSearchModeChange('eql')}
          >
            EQL
          </Button>
        </div>
      )}

      {/* Search input */}
      <div className="relative max-w-sm flex-1">
        {!isServer && (
          <Search className="text-muted-foreground absolute top-1/2 left-3 size-4 -translate-y-1/2" />
        )}
        <Input
          placeholder={searchPlaceholder}
          value={searchValue}
          onChange={e => onSearchChange(e.target.value)}
          className={isServer ? 'pr-8' : 'pr-8 pl-9'}
        />
        {searchValue.length > 0 && (
          <Button
            variant="ghost"
            size="icon"
            className="absolute top-1/2 right-1 size-6 -translate-y-1/2"
            onClick={() => onSearchChange('')}
          >
            <X className="size-3" />
            <span className="sr-only">Clear search</span>
          </Button>
        )}
      </div>

      {/* Column visibility dropdown */}
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="outline" size="sm">
            Columns
            <ChevronDown className="ml-2 size-4" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          {props.table
            .getAllColumns()
            .filter(column => column.getCanHide())
            .map(column => (
              <DropdownMenuCheckboxItem
                key={column.id}
                className="capitalize"
                checked={column.getIsVisible()}
                onCheckedChange={value => column.toggleVisibility(!!value)}
              >
                {column.id}
              </DropdownMenuCheckboxItem>
            ))}
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
};
