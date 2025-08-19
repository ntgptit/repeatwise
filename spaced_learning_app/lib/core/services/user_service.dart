import 'package:dio/dio.dart';
import 'package:spaced_learning_app/domain/models/user.dart';
import 'package:spaced_learning_app/domain/models/user/user_update_request.dart';

class UserService {
  final Dio _dio;
  static const String _baseUrl = '/api/v1/users';

  UserService(this._dio);

  /// GET /api/v1/users/me
  Future<User> getCurrentUser() async {
    try {
      final response = await _dio.get('$_baseUrl/me');

      // API trả về DataResponse<UserResponse>
      final data = response.data['data'];
      return User.fromJson(data);
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// GET /api/v1/users/{id}
  Future<User> getUserById(String id) async {
    try {
      final response = await _dio.get('$_baseUrl/$id');

      // API trả về DataResponse<UserDetailedResponse>
      final data = response.data['data'];
      return User.fromJson(data);
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// PUT /api/v1/users/{id}
  Future<User> updateUser(String id, UserUpdateRequest request) async {
    try {
      final response = await _dio.put(
        '$_baseUrl/$id',
        data: request.toJson(),
      );

      // API trả về DataResponse<UserResponse>
      final data = response.data['data'];
      return User.fromJson(data);
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// DELETE /api/v1/users/{id}
  Future<void> deleteUser(String id) async {
    try {
      await _dio.delete('$_baseUrl/$id');
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// POST /api/v1/users/{id}/restore
  Future<User> restoreUser(String id) async {
    try {
      final response = await _dio.post('$_baseUrl/$id/restore');

      // API trả về DataResponse<UserResponse>
      final data = response.data['data'];
      return User.fromJson(data);
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// GET /api/v1/users (Admin only)
  Future<Map<String, dynamic>> getAllUsers({int page = 0, int size = 20}) async {
    try {
      final response = await _dio.get(
        _baseUrl,
        queryParameters: {
          'page': page,
          'size': size,
        },
      );

      // API trả về PageResponse<UserDetailedResponse>
      return response.data;
    } on DioException catch (e) {
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
          return Exception(data['message'] ?? 'User not found');
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
