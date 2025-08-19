import 'package:dio/dio.dart';
import 'package:spaced_learning_app/domain/models/auth_response.dart';
import 'package:spaced_learning_app/domain/models/auth/auth_request.dart';
import 'package:spaced_learning_app/domain/models/auth/register_request.dart';
import 'package:spaced_learning_app/domain/models/auth/refresh_token_request.dart';
import 'package:spaced_learning_app/domain/models/user.dart';

class AuthService {
  final Dio _dio;
  static const String _baseUrl = '/api/v1/auth';

  AuthService(this._dio);

  /// POST /api/v1/auth/login
  Future<AuthResponse> login(String usernameOrEmail, String password) async {
    try {
      final request = AuthRequest(
        usernameOrEmail: usernameOrEmail,
        password: password,
      );

      final response = await _dio.post(
        '$_baseUrl/login',
        data: request.toJson(),
      );

      // API trả về DataResponse<AuthResponse>
      final data = response.data['data'];
      return AuthResponse.fromJson(data);
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// POST /api/v1/auth/register
  Future<User> register(RegisterRequest request) async {
    try {
      final response = await _dio.post(
        '$_baseUrl/register',
        data: request.toJson(),
      );

      // API trả về DataResponse<UserResponse>
      final data = response.data['data'];
      return User.fromJson(data);
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// POST /api/v1/auth/refresh-token
  Future<AuthResponse> refreshToken(String refreshToken) async {
    try {
      final request = RefreshTokenRequest(refreshToken: refreshToken);

      final response = await _dio.post(
        '$_baseUrl/refresh-token',
        data: request.toJson(),
      );

      // API trả về DataResponse<AuthResponse>
      final data = response.data['data'];
      return AuthResponse.fromJson(data);
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// GET /api/v1/auth/validate
  Future<bool> validateToken(String token) async {
    try {
      final response = await _dio.get(
        '$_baseUrl/validate',
        queryParameters: {'token': token},
      );

      // API trả về SuccessResponse với success field
      return response.data['success'] == true;
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) {
        return false;
      }
      throw _handleDioError(e);
    }
  }

  Exception _handleDioError(DioException e) {
    if (e.response != null) {
      final statusCode = e.response!.statusCode;
      final data = e.response!.data;

      switch (statusCode) {
        case 400:
          return Exception(data['message'] ?? 'Bad request');
        case 401:
          return Exception(data['message'] ?? 'Unauthorized');
        case 403:
          return Exception(data['message'] ?? 'Forbidden');
        case 404:
          return Exception(data['message'] ?? 'Not found');
        case 409:
          return Exception(data['message'] ?? 'Conflict');
        case 422:
          return Exception(data['message'] ?? 'Validation error');
        case 500:
          return Exception(data['message'] ?? 'Internal server error');
        default:
          return Exception(data['message'] ?? 'Unknown error');
      }
    } else if (e.type == DioExceptionType.connectionTimeout ||
        e.type == DioExceptionType.receiveTimeout) {
      return Exception('Connection timeout');
    } else if (e.type == DioExceptionType.connectionError) {
      return Exception('No internet connection');
    } else {
      return Exception('Network error: ${e.message}');
    }
  }
}
