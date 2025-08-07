import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:pretty_dio_logger/pretty_dio_logger.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../models/api_response.dart';
import '../models/api_exception.dart';
import '../config/network_config.dart';
import 'storage_service.dart';
import 'network_interceptor.dart';

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
    switch (error.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        throw ApiException(
          message: 'Connection timeout',
          statusCode: 408,
        );
      case DioExceptionType.badResponse:
        final statusCode = error.response?.statusCode;
        final responseData = error.response?.data;
        
        if (statusCode == 401) {
          // Handle unauthorized - clear token and redirect to login
          _storageService.removeToken();
          throw ApiException(
            message: 'Unauthorized access',
            statusCode: 401,
          );
        } else if (statusCode == 403) {
          throw ApiException(
            message: 'Access forbidden',
            statusCode: 403,
          );
        } else if (statusCode == 404) {
          throw ApiException(
            message: 'Resource not found',
            statusCode: 404,
          );
        } else if (statusCode == 422) {
          final message = _extractValidationMessage(responseData);
          throw ApiException(
            message: message,
            statusCode: 422,
          );
        } else if (statusCode! >= 500) {
          throw ApiException(
            message: 'Server error',
            statusCode: statusCode,
          );
        } else {
          final message = _extractErrorMessage(responseData);
          throw ApiException(
            message: message,
            statusCode: statusCode,
          );
        }
      case DioExceptionType.cancel:
        throw ApiException(
          message: 'Request cancelled',
          statusCode: 0,
        );
      case DioExceptionType.connectionError:
        throw ApiException(
          message: 'No internet connection',
          statusCode: 0,
        );
      default:
        throw ApiException(
          message: 'Network error occurred',
          statusCode: 0,
        );
    }
  }

  String _extractErrorMessage(dynamic responseData) {
    if (responseData is Map<String, dynamic>) {
      return (responseData['message'] as String?) ?? 
             (responseData['error'] as String?) ?? 
             'An error occurred';
    } else if (responseData is String) {
      try {
        final parsed = json.decode(responseData);
        return (parsed['message'] as String?) ?? 'An error occurred';
      } catch (e) {
        return responseData;
      }
    }
    return 'An error occurred';
  }

  String _extractValidationMessage(dynamic responseData) {
    if (responseData is Map<String, dynamic>) {
      final errors = responseData['errors'];
      if (errors is Map<String, dynamic>) {
        return errors.values.first.toString();
      }
      return (responseData['message'] as String?) ?? 'Validation failed';
    }
    return 'Validation failed';
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
