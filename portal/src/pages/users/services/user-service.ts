/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import apiClient from '@/lib/api-client';

import type { ApiResponse, CreateUserRequest, UpdateUserRequest, UserDto } from '../types/user';

class UserService {
  private readonly BASE_PATH = '/easy-base/api/users';

  async getUsers(): Promise<UserDto[]> {
    const response = await apiClient.get<ApiResponse<UserDto[]>>(this.BASE_PATH);
    return response.data.data;
  }

  async getUserById(userId: string): Promise<UserDto> {
    const response = await apiClient.get<ApiResponse<UserDto>>(`${this.BASE_PATH}/${userId}`);
    return response.data.data;
  }

  async createUser(request: CreateUserRequest): Promise<UserDto> {
    const response = await apiClient.post<ApiResponse<UserDto>>(this.BASE_PATH, request);
    return response.data.data;
  }

  async updateUser(userId: string, request: UpdateUserRequest): Promise<UserDto> {
    const response = await apiClient.put<ApiResponse<UserDto>>(
      `${this.BASE_PATH}/${userId}`,
      request
    );
    return response.data.data;
  }

  async deleteUser(userId: string): Promise<void> {
    await apiClient.delete(`${this.BASE_PATH}/${userId}`);
  }
}

export const userService = new UserService();
export default userService;
