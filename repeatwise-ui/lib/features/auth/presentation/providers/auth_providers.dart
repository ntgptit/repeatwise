// ignore_for_file: unused_import

import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../../../core/models/api_response.dart';
import '../../../../core/models/user.dart';
import '../../../../core/services/storage_service.dart';
import '../../domain/usecases/login_usecase.dart';
import '../../domain/usecases/register_usecase.dart';
import '../../domain/usecases/get_current_user_usecase.dart';

part 'auth_providers.g.dart';

@riverpod
class AuthNotifier extends _$AuthNotifier {
  @override
  AuthState build() {
    return const AuthState(
      isLoading: false,
      isInitialized: false,
      user: null,
      error: null,
    );
  }

  Future<void> login(String emailOrUsername, String password) async {
    state = state.copyWith(isLoading: true, error: null);

    try {
      final loginUseCase = ref.read(loginUseCaseProvider);
      final response = await loginUseCase(emailOrUsername, password);

      if (response.isSuccess) {
        state = state.copyWith(
          isLoading: false,
          isInitialized: true,
          user: response.data,
          error: null,
        );
        return;
      }

      state = state.copyWith(isLoading: false, error: response.error);
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'An unexpected error occurred: $e',
      );
    }
  }

  Future<void> register(
    String name,
    String username,
    String email,
    String password,
  ) async {
    state = state.copyWith(isLoading: true, error: null);

    try {
      final registerUseCase = ref.read(registerUseCaseProvider);
      final response = await registerUseCase(name, username, email, password);

      if (response.isSuccess) {
        state = state.copyWith(
          isLoading: false,
          isInitialized: true,
          user: response.data,
          error: null,
        );
        return;
      }

      state = state.copyWith(isLoading: false, error: response.error);
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'An unexpected error occurred: $e',
      );
    }
  }

  Future<void> logout() async {
    state = state.copyWith(isLoading: true, error: null);

    try {
      // Clear stored data
      final storageService = ref.read(storageServiceProvider);
      await storageService.removeToken();
      await storageService.removeUser();

      state = const AuthState(
        isLoading: false,
        isInitialized: true,
        user: null,
        error: null,
      );
    } catch (e) {
      state = state.copyWith(isLoading: false, error: 'Logout failed: $e');
    }
  }

  void clearError() {
    state = state.copyWith(error: null);
  }

  Future<void> getCurrentUser() async {
    state = state.copyWith(isLoading: true, error: null);

    try {
      final getCurrentUserUseCase = ref.read(getCurrentUserUseCaseProvider);
      final response = await getCurrentUserUseCase();

      if (response.isSuccess) {
        state = state.copyWith(
          isLoading: false,
          isInitialized: true,
          user: response.data,
          error: null,
        );
        return;
      }

      state = state.copyWith(isLoading: false, error: response.error);
      // If getting current user fails, clear authentication
      await logout();
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: 'An unexpected error occurred: $e',
      );
      // If getting current user fails, clear authentication
      await logout();
    }
  }

  void setAuthenticatedUser(Map<String, dynamic> userData) {
    try {
      final user = User.fromJson(userData);
      state = state.copyWith(isInitialized: true, user: user, error: null);
    } catch (e) {
      // If user data is invalid, clear it
      logout();
    }
  }

  Future<void> initializeAuth() async {
    try {
      final storageService = ref.read(storageServiceProvider);
      final token = await storageService.getToken();
      final userData = await storageService.getUser();

      if (token != null && userData != null) {
        // Try to get current user from API to validate token
        await getCurrentUser();
        return;
      }

      // No stored auth data, set initialized without user
      state = state.copyWith(isInitialized: true);
    } catch (e) {
      // Clear invalid data
      final storageService = ref.read(storageServiceProvider);
      await storageService.removeToken();
      await storageService.removeUser();
      state = state.copyWith(isInitialized: true);
    }
  }
}

class AuthState {
  final bool isLoading;
  final bool isInitialized;
  final User? user;
  final String? error;

  const AuthState({
    required this.isLoading,
    this.isInitialized = false,
    this.user,
    this.error,
  });

  AuthState copyWith({
    bool? isLoading,
    bool? isInitialized,
    User? user,
    String? error,
  }) {
    return AuthState(
      isLoading: isLoading ?? this.isLoading,
      isInitialized: isInitialized ?? this.isInitialized,
      user: user ?? this.user,
      error: error ?? this.error,
    );
  }

  bool get isAuthenticated => user != null;
}
