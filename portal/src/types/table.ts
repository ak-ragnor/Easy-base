/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

export type SearchMode = 'search' | 'eql';

export interface TableQueryParams {
  page: number;
  size: number;
  sort: string;
  search?: string;
  filter?: string;
}

export interface PagedResult<T> {
  data: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface ApiPagedResponse<T> extends PagedResult<T> {
  success: boolean;
  message?: string;
  statusCode: number;
  timestamp: string;
}

export interface UseTableQueryOptions<TData> {
  queryFn: (params: TableQueryParams) => Promise<PagedResult<TData>>;
  defaultPageSize?: number;
  defaultSort?: string;
}
