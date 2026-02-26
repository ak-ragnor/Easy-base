/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { create } from 'zustand';

import userService from '@/pages/users/services/userService.ts';

import type { CreateUserRequest, UpdateUserRequest, UserDto } from '../types/user';

interface UserState {
  error: string | null;

  createUser: (request: CreateUserRequest) => Promise<UserDto>;
  updateUser: (userId: string, request: UpdateUserRequest) => Promise<UserDto>;
  deleteUser: (userId: string) => Promise<void>;
  clearError: () => void;
}

export const useUserStore = create<UserState>()(set => ({
  error: null,

  createUser: async (request: CreateUserRequest) => {
    return await userService.createUser(request);
  },

  updateUser: async (userId: string, request: UpdateUserRequest) => {
    return await userService.updateUser(userId, request);
  },

  deleteUser: async (userId: string) => {
    await userService.deleteUser(userId);
  },

  clearError: () => set({ error: null }),
}));
