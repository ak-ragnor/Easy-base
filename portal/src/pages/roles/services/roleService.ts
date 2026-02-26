/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import apiClient from '@/lib/api-client';
import type { ApiPagedResponse, PagedResult, TableQueryParams } from '@/types/table';

import type { CreateRoleRequest, RoleDto, UpdateRoleRequest } from '../types/role';

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  errors?: string[];
  statusCode: number;
  timestamp: string;
}

class RoleService {
  private readonly BASE_PATH = '/easy-base/api/roles';

  async getRoleById(roleId: string): Promise<RoleDto> {
    const response = await apiClient.get<ApiResponse<RoleDto>>(`${this.BASE_PATH}/${roleId}`);
    return response.data.data;
  }

  async createRole(request: CreateRoleRequest): Promise<RoleDto> {
    const response = await apiClient.post<ApiResponse<RoleDto>>(this.BASE_PATH, request);
    return response.data.data;
  }

  async updateRole(roleId: string, request: UpdateRoleRequest): Promise<RoleDto> {
    const response = await apiClient.put<ApiResponse<RoleDto>>(
      `${this.BASE_PATH}/${roleId}`,
      request
    );
    return response.data.data;
  }

  async deleteRole(roleId: string): Promise<void> {
    await apiClient.delete(`${this.BASE_PATH}/${roleId}`);
  }

  async queryRoles(params: TableQueryParams): Promise<PagedResult<RoleDto>> {
    const response = await apiClient.get<ApiPagedResponse<RoleDto>>(this.BASE_PATH, {
      params: {
        page: params.page,
        size: params.size,
        ...(params.sort && { sort: params.sort }),
        ...(params.search && { search: params.search }),
        ...(params.filter && { filter: params.filter }),
      },
    });
    const { data, page, size, totalElements, totalPages, first, last } = response.data;
    return { data, page, size, totalElements, totalPages, first, last };
  }
}

export const roleService = new RoleService();
export default roleService;
