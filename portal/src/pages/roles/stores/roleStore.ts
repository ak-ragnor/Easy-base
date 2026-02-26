/**
 * SPDX-FileCopyrightText: (c) 2026 EasyBase
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

import { create } from 'zustand';

import roleService from '@/pages/roles/services/roleService.ts';

import type { CreateRoleRequest, RoleDto, UpdateRoleRequest } from '../types/role';

interface RoleState {
  error: string | null;

  createRole: (request: CreateRoleRequest) => Promise<RoleDto>;
  updateRole: (roleId: string, request: UpdateRoleRequest) => Promise<RoleDto>;
  deleteRole: (roleId: string) => Promise<void>;
  clearError: () => void;
}

export const useRoleStore = create<RoleState>()(set => ({
  error: null,

  createRole: async (request: CreateRoleRequest) => {
    return await roleService.createRole(request);
  },

  updateRole: async (roleId: string, request: UpdateRoleRequest) => {
    return await roleService.updateRole(roleId, request);
  },

  deleteRole: async (roleId: string) => {
    await roleService.deleteRole(roleId);
  },

  clearError: () => set({ error: null }),
}));
