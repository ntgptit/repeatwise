import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/domain/models/user.dart';
import 'package:spaced_learning_app/domain/repositories/user_repository.dart';

class UserRepositoryImpl implements UserRepository {
  final ApiClient _apiClient;

  UserRepositoryImpl(this._apiClient);

  @override
  Future<User> getCurrentUser() async {
    try {
      final response = await _apiClient.get(ApiEndpoints.currentUser);

      if (response['success'] != true || response['data'] == null) {
        throw AuthenticationException(
          'Failed to get current user: ${response['message']}',
        );
      }

      return User.fromJson(response['data']);
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get current user: $e');
    }
  }

  @override
  Future<User> updateUser(
    String id, {
    String? displayName,
    String? password,
  }) async {
    try {
      final data = <String, dynamic>{};

      if (displayName != null) {
        data['displayName'] = displayName;
      }

      if (password != null) {
        data['password'] = password;
      }

      final response = await _apiClient.put(
        '${ApiEndpoints.users}/$id',
        data: data,
      );

      if (response['success'] != true || response['data'] == null) {
        throw BadRequestException(
          'Failed to update user: ${response['message']}',
        );
      }

      return User.fromJson(response['data']);
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to update user: $e');
    }
  }

  @override
  Future<bool> checkEmailExists(String email) async {
    try {
      // Assuming this is a placeholder implementation
      return false;
    } catch (e) {
      return false;
    }
  }
}
