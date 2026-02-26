/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { useCallback, useEffect, useState } from 'react';

import type {
  PagedResult,
  SearchMode,
  TableQueryParams,
  UseTableQueryOptions,
} from '@/types/table';

export interface UseTableQueryResult<TData> {
  data: TData[];
  totalElements: number;
  totalPages: number;
  isLoading: boolean;
  isPending: boolean;
  error: string | null;
  pageIndex: number;
  pageSize: number;
  sort: string;
  searchMode: SearchMode;
  searchValue: string;
  setPage: (page: number) => void;
  setPageSize: (size: number) => void;
  setSort: (sort: string) => void;
  setSearchMode: (mode: SearchMode) => void;
  setSearchValue: (value: string) => void;
  refetch: () => void;
}

export function useTableQuery<TData>({
  queryFn,
  defaultPageSize = 20,
  defaultSort = '',
}: UseTableQueryOptions<TData>): UseTableQueryResult<TData> {
  const [_data, _setData] = useState<TData[]>([]);
  const [_totalElements, _setTotalElements] = useState(0);
  const [_totalPages, _setTotalPages] = useState(0);
  const [_isLoading, _setIsLoading] = useState(false);
  const [_error, _setError] = useState<string | null>(null);
  const [_pageIndex, _setPageIndex] = useState(0);
  const [_pageSize, _setPageSize] = useState(defaultPageSize);
  const [_sort, _setSort] = useState(defaultSort);
  const [_searchMode, _setSearchMode] = useState<SearchMode>('search');
  const [_searchValue, _setSearchValue] = useState('');
  const [_debouncedSearchValue, _setDebouncedSearchValue] = useState('');
  const [_isPending, _setIsPending] = useState(false);
  const [_fetchTrigger, _setFetchTrigger] = useState(0);

  // Debounce search value — clears pending when settled
  useEffect(() => {
    const timer = setTimeout(() => {
      _setDebouncedSearchValue(_searchValue);
      _setPageIndex(0);
      _setIsPending(false);
    }, 1000);
    return () => clearTimeout(timer);
  }, [_searchValue]);

  // Fetch effect
  useEffect(() => {
    let cancelled = false;

    const fetch = async () => {
      _setIsLoading(true);
      _setError(null);

      const params: TableQueryParams = {
        page: _pageIndex,
        size: _pageSize,
        sort: _sort,
      };

      if (_debouncedSearchValue) {
        if (_searchMode === 'search') {
          params.search = _debouncedSearchValue;
        } else {
          params.filter = _debouncedSearchValue;
        }
      }

      try {
        const result: PagedResult<TData> = await queryFn(params);
        if (!cancelled) {
          _setData(result.data);
          _setTotalElements(result.totalElements);
          _setTotalPages(result.totalPages);
        }
      } catch (err) {
        if (!cancelled) {
          _setError(err instanceof Error ? err.message : 'Failed to fetch data');
        }
      } finally {
        if (!cancelled) {
          _setIsLoading(false);
        }
      }
    };

    void fetch();

    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [_pageIndex, _pageSize, _sort, _searchMode, _debouncedSearchValue, _fetchTrigger]);

  const setPage = useCallback((page: number) => {
    _setPageIndex(page);
  }, []);

  const setPageSize = useCallback((size: number) => {
    _setPageSize(size);
    _setPageIndex(0);
  }, []);

  const setSort = useCallback((sort: string) => {
    _setSort(sort);
    _setPageIndex(0);
  }, []);

  const setSearchMode = useCallback((mode: SearchMode) => {
    _setSearchMode(mode);
    _setSearchValue('');
    _setDebouncedSearchValue('');
    _setIsPending(false);
    _setPageIndex(0);
  }, []);

  const setSearchValue = useCallback((value: string) => {
    _setSearchValue(value);
    _setIsPending(true);
  }, []);

  const refetch = useCallback(() => {
    _setFetchTrigger(prev => prev + 1);
  }, []);

  return {
    data: _data,
    totalElements: _totalElements,
    totalPages: _totalPages,
    isLoading: _isLoading,
    isPending: _isPending,
    error: _error,
    pageIndex: _pageIndex,
    pageSize: _pageSize,
    sort: _sort,
    searchMode: _searchMode,
    searchValue: _searchValue,
    setPage,
    setPageSize,
    setSort,
    setSearchMode,
    setSearchValue,
    refetch,
  };
}
