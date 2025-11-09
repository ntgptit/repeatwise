import type { AxiosInstance } from 'axios';
import { env } from '@/config/env';

export const loggerInterceptor = (instance: AxiosInstance): void => {
  if (!env.enableApiLogging) {return;}

  instance.interceptors.request.use(
    (config) => {
      console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`);
      return config;
    },
    (error) => Promise.reject(error)
  );

  instance.interceptors.response.use(
    (response) => {
      console.log(`[API Response] ${response.status} ${response.config.url}`);
      return response;
    },
    (error) => Promise.reject(error)
  );
};

export default loggerInterceptor;
