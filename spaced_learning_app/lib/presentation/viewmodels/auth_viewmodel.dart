// lib/presentation/viewmodels/auth_viewmodel.dart
import 'package:flutter/foundation.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/domain/models/auth/register_request.dart';
import 'package:spaced_learning_app/domain/models/auth_response.dart';
import 'package:spaced_learning_app/domain/models/user.dart';

import '../../core/di/providers.dart';

part 'auth_viewmodel.g.dart';

@Riverpod(keepAlive: true)
class AuthState extends _$AuthState {
  @override
  Future<bool> build() async {
    return _checkAuthentication();
  }

  Future<bool> _checkAuthentication() async {
    try {
      final token = await ref.read(storageServiceProvider).getToken();
      if (token == null || token.isEmpty) {
        return false;
      }

      try {
        final isValid = await ref
            .read(authServiceProvider)
            .validateToken(token);

        if (isValid) {
          final userData = await ref.read(storageServiceProvider).getUserData();
          if (userData != null) {
            ref
                .read(currentUserProvider.notifier)
                .updateUser(User.fromJson(userData));
          }
          return true;
        }

        await ref.read(storageServiceProvider).clearTokens();
        return false;
      } catch (e) {
        debugPrint('Token validation error: $e');
        await ref.read(storageServiceProvider).clearTokens();
        return false;
      }
    } catch (e) {
      debugPrint('Critical authentication check error: $e');
      // Handle secure storage errors by resetting storage
      await ref.read(storageServiceProvider).resetSecureStorage();
      return false;
    }
  }

  Future<bool> login(String usernameOrEmail, String password) async {
    state = const AsyncValue.loading();
    try {
      final response = await ref
          .read(authServiceProvider)
          .login(usernameOrEmail, password);
      await _handleAuthResponse(response);
      state = const AsyncValue.data(true);
      return true;
    } catch (e) {
      debugPrint('Login error: $e');
      state = AsyncValue.error(e, StackTrace.current);
      return false;
    }
  }

  Future<bool> register(
    String username,
    String email,
    String password,
    String? firstName,
    String? lastName,
  ) async {
    state = const AsyncValue.loading();
    try {
      final request = RegisterRequest(
        username: username,
        email: email,
        password: password,
        firstName: firstName,
        lastName: lastName,
      );

      final user = await ref.read(authServiceProvider).register(request);

      // Sau khi register thành công, tự động login
      final loginResponse = await ref
          .read(authServiceProvider)
          .login(username, password);

      await _handleAuthResponse(loginResponse);
      state = const AsyncValue.data(true);
      return true;
    } catch (e) {
      debugPrint('Registration error: $e');
      state = AsyncValue.error(e, StackTrace.current);
      return false;
    }
  }

  Future<bool> refreshToken() async {
    try {
      final refreshToken = await ref
          .read(storageServiceProvider)
          .getRefreshToken();
      if (refreshToken == null || refreshToken.isEmpty) {
        return false;
      }

      final response = await ref
          .read(authServiceProvider)
          .refreshToken(refreshToken);

      await _handleAuthResponse(response);
      return true;
    } catch (e) {
      debugPrint('Refresh token error: $e');
      await ref.read(storageServiceProvider).clearTokens();
      return false;
    }
  }

  Future<void> logout() async {
    state = const AsyncValue.loading();
    try {
      await ref.read(storageServiceProvider).clearTokens();
      await ref.read(storageServiceProvider).clearUserData();
      ref.read(currentUserProvider.notifier).updateUser(null);
      state = const AsyncValue.data(false);
    } catch (e) {
      debugPrint('Logout error: $e');
      // Even if there's an error, we still want to clear the user state
      ref.read(currentUserProvider.notifier).updateUser(null);
      state = AsyncValue.error(e, StackTrace.current);
    }
  }

  Future<void> _handleAuthResponse(AuthResponse response) async {
    try {
      await ref.read(storageServiceProvider).saveToken(response.token);
      if (response.refreshToken != null) {
        await ref
            .read(storageServiceProvider)
            .saveRefreshToken(response.refreshToken!);
      }
      await ref
          .read(storageServiceProvider)
          .saveUserData(response.user.toJson());
      ref.read(currentUserProvider.notifier).updateUser(response.user);
    } catch (e) {
      debugPrint('Error handling auth response: $e');
      // If we can't save the auth data, ensure user state is consistent
      ref.read(currentUserProvider.notifier).updateUser(response.user);
      rethrow;
    }
  }
}

@Riverpod(keepAlive: true)
class CurrentUser extends _$CurrentUser {
  @override
  User? build() {
    return null;
  }

  void updateUser(User? user) {
    state = user;
  }
}

@riverpod
class AuthError extends _$AuthError {
  @override
  String? build() {
    final authState = ref.watch(authStateProvider);
    return authState.hasError ? authState.error.toString() : null;
  }

  void clearError() {
    state = null;
  }
}
