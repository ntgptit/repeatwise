/**
 * API Usage Examples
 * 
 * This file demonstrates how to use the API client with full type safety
 */

import { authApi, userApi, folderApi } from '@/api/modules'
import { ApiErrorHandler } from '@/api/http/error.handler'
import type { LoginResponse, UserProfile } from '@/api/modules/auth.api'
import type { User } from '@/api/modules/user.api'
import type { Folder } from '@/api/modules/folder.api'

// ==========================================
// Example 1: Basic Usage with Type Safety
// ==========================================

export async function exampleBasicUsage() {
  // Login - Full type safety
  const loginResult: LoginResponse = await authApi.login({
    email: 'user@example.com',
    password: 'password123',
  })

  // Get profile - Type inferred automatically
  const profile = await userApi.getProfile()
  // profile type: UserProfile

  // Get folders - Type inferred automatically
  const folders = await folderApi.getFolders()
  // folders type: Folder[]
}

// ==========================================
// Example 2: Error Handling
// ==========================================

export async function exampleErrorHandling() {
  try {
    const folders = await folderApi.getFolders()
  } catch (error) {
    // Type-safe error extraction
    const apiError = ApiErrorHandler.extractError(error)
    const message = ApiErrorHandler.getErrorMessage(error)
    const status = ApiErrorHandler.getErrorStatus(error)
    const validationErrors = ApiErrorHandler.getValidationErrors(error)

    // Custom error handling
    if (status === 404) {
      // Handle not found
    } else if (status === 422) {
      // Handle validation errors
      Object.entries(validationErrors).forEach(([field, messages]) => {
        console.error(`${field}: ${messages.join(', ')}`)
      })
    }
  }
}

// ==========================================
// Example 3: Pagination
// ==========================================

export async function examplePagination() {
  const result = await userApi.getUsers({
    page: 1,
    pageSize: 20,
    search: 'john',
  })

  // result type: PaginatedResponse<User>
  console.log('Total:', result.total)
  console.log('Page:', result.page)
  console.log('Items:', result.items)
  console.log('Has next:', result.hasNext)
}

// ==========================================
// Example 4: CRUD Operations
// ==========================================

export async function exampleCRUD() {
  // Create
  const newFolder = await folderApi.create({
    name: 'New Folder',
    parentId: null,
  })

  // Read
  const folder = await folderApi.getById(newFolder.id)

  // Update
  const updatedFolder = await folderApi.update(newFolder.id, {
    name: 'Updated Folder',
  })

  // Delete
  await folderApi.delete(newFolder.id)
}

// ==========================================
// Example 5: Custom Endpoints
// ==========================================

export async function exampleCustomEndpoints() {
  // Get folder tree (custom endpoint)
  const tree = await folderApi.getFolderTree()

  // Move folder (custom endpoint)
  const movedFolder = await folderApi.move('folder-id', 'new-parent-id')
}
