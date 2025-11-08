import type { AxiosInstance } from 'axios'
import { STORAGE_KEYS } from '@/config/app.config'
import { localStorageService } from '@/common/services'

export const authInterceptor = (instance: AxiosInstance): void => {
  instance.interceptors.request.use(
    config => {
      const token = localStorageService.get<string>(STORAGE_KEYS.ACCESS_TOKEN)
      if (!token) {
        return config
      }

      config.headers.Authorization = `Bearer ${token}`
      return config
    },
    error => Promise.reject(error)
  )
}

export default authInterceptor
