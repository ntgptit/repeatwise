import 'dart:convert';
import 'package:dio/dio.dart';

import '../models/api_exception.dart';
import '../models/error_response.dart';

class ErrorHandler {
  /// Parse ErrorResponse from DioException response data
  static ErrorResponse? parseErrorResponse(DioException error) {
    if (error.response?.data == null) return null;
    
    try {
      final responseData = error.response!.data;
      
      if (responseData is Map<String, dynamic>) {
        return ErrorResponse.fromJson(responseData);
      } else if (responseData is String) {
        final parsed = json.decode(responseData) as Map<String, dynamic>;
        return ErrorResponse.fromJson(parsed);
      }
    } catch (e) {
      // If parsing fails, return null to use fallback error handling
      return null;
    }
    
    return null;
  }

  /// Handle DioException and return appropriate ApiException
  /// Applies "Return Early" philosophy
  static ApiException handleDioException(DioException error) {
    // Early return for connection errors
    if (error.type == DioExceptionType.connectionError) {
      return ApiException(
        message: 'No internet connection',
        statusCode: 0,
      );
    }

    // Early return for timeout errors
    if (error.type == DioExceptionType.connectionTimeout ||
        error.type == DioExceptionType.sendTimeout ||
        error.type == DioExceptionType.receiveTimeout) {
      return ApiException(
        message: 'Connection timeout',
        statusCode: 408,
      );
    }

    // Early return for cancelled requests
    if (error.type == DioExceptionType.cancel) {
      return ApiException(
        message: 'Request cancelled',
        statusCode: 0,
      );
    }

    // Handle bad response errors
    if (error.type == DioExceptionType.badResponse) {
      return _handleBadResponse(error);
    }

    // Default case for other errors
    return ApiException(
      message: 'Network error occurred',
      statusCode: 0,
    );
  }

  /// Handle bad response errors with ErrorResponse parsing
  static ApiException _handleBadResponse(DioException error) {
    final statusCode = error.response?.statusCode ?? 0;
    
    // Try to parse ErrorResponse first
    final errorResponse = parseErrorResponse(error);
    if (errorResponse != null) {
      return ApiException(
        message: errorResponse.message,
        statusCode: statusCode,
        details: {
          'success': errorResponse.success,
          'errors': errorResponse.errors,
          'timestamp': errorResponse.timestamp?.toIso8601String(),
        },
      );
    }

    // Fallback to status code based handling
    switch (statusCode) {
      case 401:
        return ApiException(
          message: 'Unauthorized access',
          statusCode: 401,
        );
      case 403:
        return ApiException(
          message: 'Access forbidden',
          statusCode: 403,
        );
      case 404:
        return ApiException(
          message: 'Resource not found',
          statusCode: 404,
        );
      case 422:
        return ApiException(
          message: _extractValidationMessage(error.response?.data),
          statusCode: 422,
        );
      case >= 500:
        return ApiException(
          message: 'Server error',
          statusCode: statusCode,
        );
      default:
        return ApiException(
          message: _extractErrorMessage(error.response?.data),
          statusCode: statusCode,
        );
    }
  }

  /// Extract error message from response data
  static String _extractErrorMessage(dynamic responseData) {
    if (responseData == null) return 'An error occurred';
    
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

  /// Extract validation message from response data
  static String _extractValidationMessage(dynamic responseData) {
    if (responseData == null) return 'Validation failed';
    
    if (responseData is Map<String, dynamic>) {
      final errors = responseData['errors'];
      if (errors is Map<String, dynamic> && errors.isNotEmpty) {
        return errors.values.first.toString();
      }
      return (responseData['message'] as String?) ?? 'Validation failed';
    }
    
    return 'Validation failed';
  }

  /// Check if error is authentication related
  static bool isAuthenticationError(ApiException error) {
    return error.statusCode == 401 || error.statusCode == 403;
  }

  /// Check if error is network related
  static bool isNetworkError(ApiException error) {
    return error.statusCode == 0;
  }

  /// Check if error is server related
  static bool isServerError(ApiException error) {
    return error.statusCode >= 500;
  }

  /// Check if error is client related (4xx)
  static bool isClientError(ApiException error) {
    return error.statusCode >= 400 && error.statusCode < 500;
  }
}
