/**
 * Base API Client
 *
 * Axios instance with default configuration and interceptors.
 * Foundation for all API calls in the application.
 *
 * @module api/clients/base
 */

import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios';
import { apiConfig } from '@/config';
import { logger } from '@/common/services';

/**
 * Create configured axios instance
 */
const createAxiosInstance = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: apiConfig.baseURL,
    timeout: apiConfig.timeout,
    headers: apiConfig.headers.common,
    withCredentials: apiConfig.withCredentials,
  });

  // Request interceptor for logging (if enabled)
  if (apiConfig.enableLogging) {
    instance.interceptors.request.use(
      (config) => {
        logger.debug('API Request', {
          method: config.method?.toUpperCase(),
          url: config.url,
          params: config.params,
        });
        return config;
      },
      (error) => {
        logger.error('API Request Error', error);
        return Promise.reject(error);
      }
    );
  }

  // Response interceptor for logging (if enabled)
  if (apiConfig.enableLogging) {
    instance.interceptors.response.use(
      (response) => {
        logger.debug('API Response', {
          status: response.status,
          url: response.config.url,
          data: response.data,
        });
        return response;
      },
      (error) => {
        logger.error('API Response Error', error);
        return Promise.reject(error);
      }
    );
  }

  return instance;
};

/**
 * Base API client instance
 */
export const apiClient = createAxiosInstance();

/**
 * Base API client class with typed methods
 */
export class BaseApiClient {
  protected client: AxiosInstance;

  constructor(client: AxiosInstance = apiClient) {
    this.client = client;
  }

  /**
   * GET request
   *
   * @param url - Request URL
   * @param config - Axios config
   * @returns Response data
   */
  async get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.get<T>(url, config);
    return response.data;
  }

  /**
   * POST request
   *
   * @param url - Request URL
   * @param data - Request body
   * @param config - Axios config
   * @returns Response data
   */
  async post<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig
  ): Promise<T> {
    const response = await this.client.post<T>(url, data, config);
    return response.data;
  }

  /**
   * PUT request
   *
   * @param url - Request URL
   * @param data - Request body
   * @param config - Axios config
   * @returns Response data
   */
  async put<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig
  ): Promise<T> {
    const response = await this.client.put<T>(url, data, config);
    return response.data;
  }

  /**
   * PATCH request
   *
   * @param url - Request URL
   * @param data - Request body
   * @param config - Axios config
   * @returns Response data
   */
  async patch<T = unknown, D = unknown>(
    url: string,
    data?: D,
    config?: AxiosRequestConfig
  ): Promise<T> {
    const response = await this.client.patch<T>(url, data, config);
    return response.data;
  }

  /**
   * DELETE request
   *
   * @param url - Request URL
   * @param config - Axios config
   * @returns Response data
   */
  async delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.delete<T>(url, config);
    return response.data;
  }

  /**
   * HEAD request
   *
   * @param url - Request URL
   * @param config - Axios config
   * @returns Response headers
   */
  async head(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse> {
    return this.client.head(url, config);
  }

  /**
   * OPTIONS request
   *
   * @param url - Request URL
   * @param config - Axios config
   * @returns Response
   */
  async options(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse> {
    return this.client.options(url, config);
  }

  /**
   * Get axios instance (for advanced usage)
   */
  getClient(): AxiosInstance {
    return this.client;
  }
}

/**
 * Default base client instance
 */
export const baseClient = new BaseApiClient(apiClient);

/**
 * Re-export for convenience
 */
export default apiClient;
