/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { create } from 'zustand';

import userService from '@/pages/users/services/user-service';

import type { CreateUserRequest, UpdateUserRequest, UserDto } from '../types/user';

interface UserState {
  users: UserDto[];
  isLoading: boolean;
  error: string | null;

  fetchUsers: () => Promise<void>;
  createUser: (request: CreateUserRequest) => Promise<void>;
  updateUser: (userId: string, request: UpdateUserRequest) => Promise<void>;
  deleteUser: (userId: string) => Promise<void>;
  clearError: () => void;
}

export const useUserStore = create<UserState>()((set, get) => ({
  users: [],
  isLoading: false,
  error: null,

  fetchUsers: async () => {
    set({ isLoading: true, error: null });
    try {
      const users = await userService.getUsers();
      set({ users, isLoading: false });
    } catch (error) {
      set({
        error: error instanceof Error ? error.message : 'Failed to fetch users',
        isLoading: false,
      });
    }
  },

  createUser: async (request: CreateUserRequest) => {
    const user = await userService.createUser(request);
    set({ users: [...get().users, user] });
  },

  updateUser: async (userId: string, request: UpdateUserRequest) => {
    const updated = await userService.updateUser(userId, request);
    set({ users: get().users.map(u => (u.id === userId ? updated : u)) });
  },

  deleteUser: async (userId: string) => {
    await userService.deleteUser(userId);
    set({ users: get().users.filter(u => u.id !== userId) });
  },

  clearError: () => set({ error: null }),
}));
