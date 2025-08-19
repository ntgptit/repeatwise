// Không cần thay đổi gì nhiều, chỉ cần cập nhật import nếu cần

import 'dart:io';

import 'package:dio/dio.dart';
import 'package:spaced_learning_app/core/constants/app_constants.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/network/interceptors/auth_interceptor.dart';
import 'package:spaced_learning_app/core/network/interceptors/logging_interceptor.dart';

class ApiClient {
  late final Dio _dio;

  ApiClient() {
    _dio = Dio();

    _dio.options.baseUrl = AppConstants.baseUrl;
    _dio.options.connectTimeout = const Duration(
      milliseconds: AppConstants.connectTimeout,
    );
    _dio.options.receiveTimeout = const Duration(
      milliseconds: AppConstants.receiveTimeout,
    );
    _dio.options.contentType = Headers.jsonContentType;
    _dio.options.responseType = ResponseType.json;

    _dio.interceptors.add(LoggingInterceptor());
    _dio.interceptors.add(AuthInterceptor());
  }

  Future<dynamic> get(
    String url, {
    Map<String, dynamic>? queryParameters,
  }) async {
    try {
      final response = await _dio.get(url, queryParameters: queryParameters);
      return _processResponse(response);
    } catch (e) {
      return _handleError(e, url);
    }
  }

  Future<dynamic> post(String url, {dynamic data}) async {
    try {
      final response = await _dio.post(url, data: data);
      return _processResponse(response);
    } catch (e) {
      return _handleError(e, url);
    }
  }

  Future<dynamic> put(String url, {dynamic data}) async {
    try {
      final response = await _dio.put(url, data: data);
      return _processResponse(response);
    } catch (e) {
      return _handleError(e, url);
    }
  }

  Future<dynamic> delete(String url) async {
    try {
      final response = await _dio.delete(url);
      return _processResponse(response);
    } catch (e) {
      return _handleError(e, url);
    }
  }

  dynamic _processResponse(Response response) {
    final statusCode = response.statusCode;

    if (statusCode == 200 || statusCode == 201) {
      return response.data;
    }

    if (statusCode == 204) {
      return null;
    }

    if (statusCode == 400) {
      throw BadRequestException(
        response.data?['message'] ?? 'Bad request',
        response.requestOptions.path,
        response.data?['errors'],
      );
    }

    if (statusCode == 401) {
      throw AuthenticationException(
        response.data?['message'] ?? 'Authentication failed',
        response.requestOptions.path,
      );
    }

    if (statusCode == 403) {
      throw ForbiddenException(
        response.data?['message'] ?? 'Access denied',
        response.requestOptions.path,
      );
    }

    if (statusCode == 404) {
      throw NotFoundException(
        response.data?['message'] ?? 'Resource not found',
        response.requestOptions.path,
      );
    }

    if (statusCode == 500 ||
        statusCode == 502 ||
        statusCode == 503 ||
        statusCode == 504) {
      throw ServerException(
        response.data?['message'] ?? 'Server error',
        response.requestOptions.path,
      );
    }

    throw HttpException(
      statusCode ?? 0,
      response.data?['message'] ?? 'Unknown error occurred',
      response.requestOptions.path,
    );
  }

  dynamic _handleError(dynamic e, String? url) {
    if (e is DioException) {
      if (e.error is SocketException) {
        throw NoInternetException();
      }

      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw TimeoutException('Request timeout', url);
      }

      if (e.response != null) {
        return _processResponse(e.response!);
      }
    }

    if (e is AppException) {
      throw e;
    }

    throw UnexpectedException('Unexpected error occurred', url);
  }
}
