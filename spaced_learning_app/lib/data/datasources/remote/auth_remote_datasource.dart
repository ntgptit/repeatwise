import 'package:flutter/material.dart';
import '../../../core/constants/api_endpoints.dart';
import '../../../core/exceptions/app_exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../domain/models/auth_response.dart';

abstract class AuthRemoteDataSource {
  Future<AuthResponse> login(String usernameOrEmail, String password);
  Future<AuthResponse> register(
    String username,
    String email,
    String password,
    String firstName,
    String lastName,
  );
  Future<AuthResponse> refreshToken(String refreshToken);
}

class AuthRemoteDataSourceImpl implements AuthRemoteDataSource {
  final ApiClient _apiClient;

  AuthRemoteDataSourceImpl(this._apiClient);

  @override
  Future<AuthResponse> login(String usernameOrEmail, String password) async {
    try {
      final data = {'usernameOrEmail': usernameOrEmail, 'password': password};

      debugPrint('Calling login API with usernameOrEmail: $usernameOrEmail');

      final response = await _apiClient.post(ApiEndpoints.login, data: data);

      if (response == null) {
        throw AuthenticationException('Login failed: No response received');
      }

      if (response['success'] != true) {
        throw AuthenticationException(
          'Login failed: ${response['message'] ?? "Unknown error"}',
        );
      }

      if (response['data'] == null || response['data']['token'] == null) {
        throw AuthenticationException(
          'Login failed: Authentication token not found in response',
        );
      }
      return AuthResponse.fromJson(response['data']);
    } on AppException {
      rethrow;
    } catch (e) {
      throw AuthenticationException('Failed to login: $e');
    }
  }

  @override
  Future<AuthResponse> register(
    String username,
    String email,
    String password,
    String firstName,
    String lastName,
  ) async {
    try {
      final data = {
        'username': username,
        'email': email,
        'password': password,
        'firstName': firstName,
        'lastName': lastName,
      };

      final response = await _apiClient.post(ApiEndpoints.register, data: data);

      if (response['success'] != true || response['data'] == null) {
        throw AuthenticationException(
          'Failed to register: ${response['message']}',
        );
      }

      return login(email, password);
    } on AppException {
      rethrow;
    } catch (e) {
      throw AuthenticationException('Failed to register: $e');
    }
  }

  @override
  Future<AuthResponse> refreshToken(String refreshToken) async {
    try {
      final data = {'refreshToken': refreshToken};

      final response = await _apiClient.post(
        ApiEndpoints.refreshToken,
        data: data,
      );

      if (response['success'] != true || response['data'] == null) {
        throw AuthenticationException(
          'Failed to refresh token: ${response['message']}',
        );
      }

      return AuthResponse.fromJson(response['data']);
    } on AppException {
      rethrow;
    } catch (e) {
      throw AuthenticationException('Failed to refresh token: $e');
    }
  }
}
