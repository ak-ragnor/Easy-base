/**
 * SPDX-FileCopyrightText: (c) 2025 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import type { SortingState } from '@tanstack/react-table';
import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function sortingStateToBackendSort(sorting: SortingState): string {
  if (sorting.length === 0) {
    return '';
  }
  return sorting.map(s => (s.desc ? `-${s.id}` : s.id)).join(',');
}
