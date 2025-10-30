import type { AxiosRequestConfig } from 'axios'
import { http } from './axiosInstance'
import type { ApiRequestConfig } from '@/api/types/api-response'
import type { PaginatedResponse, PaginationParams } from '@/api/types/pagination'

/**
 * Base API Class
 * Provides consistent convention for all API modules
 * 
 * Convention:
 * - All methods return Promise<T> (data directly, not wrapped)
 * - GET methods use singular names (getById, getList)
 * - POST/PUT/PATCH methods use verbs (create, update, delete)
 * - All methods are async
 * - All methods use TypeScript generics for type safety
 */
export abstract class BaseApi {
  protected baseUrl: string

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl
  }

  /**
   * Get item by ID
   */
  protected async getById<T>(id: string): Promise<T> {
    return http.get<T>(`${this.baseUrl}/${id}`)
  }

  /**
   * Get list of items
   */
  protected async getList<T>(
    params?: Record<string, unknown>,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T[]> {
    return http.get<T[]>(this.baseUrl, {
      ...config,
      params,
    })
  }

  /**
   * Get paginated list
   */
  protected async getPaginated<T>(
    params: PaginationParams,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<PaginatedResponse<T>> {
    return http.get<PaginatedResponse<T>>(this.baseUrl, {
      ...config,
      params,
    })
  }

  /**
   * Create new item
   */
  protected async create<T, D = unknown>(
    data: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    return http.post<T, D>(this.baseUrl, data, config)
  }

  /**
   * Update item by ID
   */
  protected async update<T, D = unknown>(
    id: string,
    data: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    return http.put<T, D>(`${this.baseUrl}/${id}`, data, config)
  }

  /**
   * Partial update item by ID
   */
  protected async patch<T, D = unknown>(
    id: string,
    data: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    return http.patch<T, D>(`${this.baseUrl}/${id}`, data, config)
  }

  /**
   * Delete item by ID
   */
  protected async delete(id: string): Promise<void> {
    await http.delete(`${this.baseUrl}/${id}`)
  }

  /**
   * Custom GET request
   */
  protected async customGet<T>(
    endpoint: string,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    return http.get<T>(`${this.baseUrl}${endpoint}`, config)
  }

  /**
   * Custom POST request
   */
  protected async customPost<T, D = unknown>(
    endpoint: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    return http.post<T, D>(`${this.baseUrl}${endpoint}`, data, config)
  }

  /**
   * Custom PUT request
   */
  protected async customPut<T, D = unknown>(
    endpoint: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    return http.put<T, D>(`${this.baseUrl}${endpoint}`, data, config)
  }

  /**
   * Custom PATCH request
   */
  protected async customPatch<T, D = unknown>(
    endpoint: string,
    data?: D,
    config?: AxiosRequestConfig & ApiRequestConfig,
  ): Promise<T> {
    return http.patch<T, D>(`${this.baseUrl}${endpoint}`, data, config)
  }

  /**
   * Custom DELETE request
   */
  protected async customDelete(endpoint: string): Promise<void> {
    await http.delete(`${this.baseUrl}${endpoint}`)
  }
}
