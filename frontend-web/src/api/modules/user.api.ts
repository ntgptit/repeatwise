import { BaseApi } from '../http/base.api'
import type { PaginatedResponse } from '@/api/types/pagination'

/**
 * User API Module
 * Follows consistent convention: BaseApi + TypeScript Generics
 */
export interface User {
  id: string
  email: string
  name: string
  createdAt: string
  updatedAt: string
}

export interface UpdateUserRequest {
  name?: string
  timezone?: string
  language?: 'VI' | 'EN'
  theme?: 'LIGHT' | 'DARK' | 'SYSTEM'
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
  confirmNewPassword: string
}

export interface GetUsersParams {
  page?: number
  pageSize?: number
  search?: string
}

class UserApi extends BaseApi {
  constructor() {
    super('/users')
  }

  /**
   * Get current user profile
   */
  async getProfile(): Promise<User> {
    return this.customGet<User>('/profile')
  }

  /**
   * Update user profile
   */
  async updateProfile(data: UpdateUserRequest): Promise<User> {
    return this.customPatch<User, UpdateUserRequest>('/profile', data)
  }

  /**
   * Change password
   */
  async changePassword(data: ChangePasswordRequest): Promise<void> {
    await this.customPost('/change-password', data)
  }

  /**
   * Get users list (admin only)
   */
  async getUsers(params?: GetUsersParams): Promise<PaginatedResponse<User>> {
    return this.getPaginated<User>({
      page: params?.page || 1,
      pageSize: params?.pageSize || 10,
      ...params,
    })
  }
}

// Export singleton instance
export const userApi = new UserApi()