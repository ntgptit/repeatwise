import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:pretty_dio_logger/pretty_dio_logger.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../models/api_exception.dart';
import '../config/network_config.dart';
import 'storage_service.dart';
import 'network_interceptor.dart';
import 'error_handler.dart';

part 'dio_service.g.dart';

class DioService {
  static const String baseUrl = 'http://localhost:8080/api/v1'; // Use direct value instead of method call
  static const Duration timeout = Duration(seconds: 30);
  
  late final Dio _dio;
  final StorageService _storageService;

  DioService(this._storageService) {
    _dio = Dio(_createDioOptions());
    _setupInterceptors();
  }

  BaseOptions _createDioOptions() {
    return BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: NetworkConfig.connectTimeout,
      receiveTimeout: NetworkConfig.receiveTimeout,
      sendTimeout: NetworkConfig.sendTimeout,
      headers: NetworkConfig.defaultHeaders,
    );
  }

  void _setupInterceptors() {
    // Network interceptor for retry logic
    _dio.interceptors.add(NetworkInterceptor());

    // Auth interceptor
    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final token = await _storageService.getToken();
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          handler.next(options);
        },
        onError: (error, handler) {
          _handleError(error);
          handler.next(error);
        },
      ),
    );

    // Logger interceptor (only in debug mode)
    _dio.interceptors.add(
      PrettyDioLogger(
        requestHeader: true,
        requestBody: true,
        responseHeader: true,
        responseBody: true,
        error: true,
        compact: true,
        maxWidth: 90,
      ),
    );
  }

  void _handleError(DioException error) {
    // Use ErrorHandler to handle DioException with ErrorResponse parsing
    final apiException = ErrorHandler.handleDioException(error);
    
    // Handle authentication errors by clearing token
    if (ErrorHandler.isAuthenticationError(apiException)) {
      _storageService.removeToken();
    }
    
    throw apiException;
  }



  // Generic GET request
  Future<T> get<T>(
    String path, {
    Map<String, dynamic>? queryParameters,
    T Function(dynamic)? fromJson,
  }) async {
    try {
      final response = await _dio.get(
        path,
        queryParameters: queryParameters,
      );
      
      if (fromJson != null) {
        return fromJson(response.data);
      }
      return response.data as T;
    } on DioException catch (e) {
      _handleError(e);
      rethrow;
    }
  }

  // Generic POST request
  Future<T> post<T>(
    String path, {
    dynamic data,
    Map<String, dynamic>? queryParameters,
    T Function(dynamic)? fromJson,
  }) async {
    try {
      final response = await _dio.post(
        path,
        data: data,
        queryParameters: queryParameters,
      );
      
      if (fromJson != null) {
        return fromJson(response.data);
      }
      return response.data as T;
    } on DioException catch (e) {
      _handleError(e);
      rethrow;
    }
  }

  // Generic PUT request
  Future<T> put<T>(
    String path, {
    dynamic data,
    Map<String, dynamic>? queryParameters,
    T Function(dynamic)? fromJson,
  }) async {
    try {
      final response = await _dio.put(
        path,
        data: data,
        queryParameters: queryParameters,
      );
      
      if (fromJson != null) {
        return fromJson(response.data);
      }
      return response.data as T;
    } on DioException catch (e) {
      _handleError(e);
      rethrow;
    }
  }

  // Generic DELETE request
  Future<T> delete<T>(
    String path, {
    dynamic data,
    Map<String, dynamic>? queryParameters,
    T Function(dynamic)? fromJson,
  }) async {
    try {
      final response = await _dio.delete(
        path,
        data: data,
        queryParameters: queryParameters,
      );
      
      if (fromJson != null) {
        return fromJson(response.data);
      }
      return response.data as T;
    } on DioException catch (e) {
      _handleError(e);
      rethrow;
    }
  }

  // Generic PATCH request
  Future<T> patch<T>(
    String path, {
    dynamic data,
    Map<String, dynamic>? queryParameters,
    T Function(dynamic)? fromJson,
  }) async {
    try {
      final response = await _dio.patch(
        path,
        data: data,
        queryParameters: queryParameters,
      );
      
      if (fromJson != null) {
        return fromJson(response.data);
      }
      return response.data as T;
    } on DioException catch (e) {
      _handleError(e);
      rethrow;
    }
  }

  void dispose() {
    _dio.close();
  }
}

@riverpod
DioService dioService(Ref ref) {
  final storageService = ref.watch(storageServiceProvider);
  final service = DioService(storageService);
  ref.onDispose(() => service.dispose());
  return service;
}
