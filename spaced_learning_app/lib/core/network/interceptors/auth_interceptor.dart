// Cập nhật để sử dụng Riverpod thay vì ServiceLocator

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/di/providers.dart';
import 'package:spaced_learning_app/core/services/storage_service.dart';

class AuthInterceptor extends Interceptor {
  final StorageService _storageService;
  final Dio _dio = Dio();
  bool _isRefreshing = false;

  AuthInterceptor()
    : _storageService = ProviderContainer().read(storageServiceProvider);

  @override
  void onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    if (_shouldSkipAuth(options.path)) {
      return handler.next(options);
    }

    final token = await _storageService.getToken();

    if (token != null && token.isNotEmpty) {
      options.headers['Authorization'] = 'Bearer $token';
    }

    handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    if (err.response?.statusCode != 401 || _isRefreshing) {
      return handler.next(err);
    }

    if (_shouldSkipAuth(err.requestOptions.path)) {
      return handler.next(err);
    }

    try {
      final refreshedToken = await _refreshToken();
      if (refreshedToken == null) {
        return handler.next(err);
      }

      final options = err.requestOptions;
      options.headers['Authorization'] = 'Bearer $refreshedToken';
      final response = await _dio.fetch(options);
      return handler.resolve(response);
    } catch (e) {
      return handler.next(err);
    }
  }

  bool _shouldSkipAuth(String path) {
    final authPaths = [
      ApiEndpoints.login,
      ApiEndpoints.register,
      ApiEndpoints.refreshToken,
      ApiEndpoints.validateToken,
    ];

    return authPaths.any((authPath) => path.contains(authPath));
  }

  Future<String?> _refreshToken() async {
    _isRefreshing = true;

    try {
      final refreshToken = await _storageService.getRefreshToken();

      if (refreshToken == null || refreshToken.isEmpty) {
        await _storageService.clearTokens();
        _isRefreshing = false;
        return null;
      }

      final response = await _dio.post(
        ApiEndpoints.refreshToken,
        data: {'refreshToken': refreshToken},
      );

      if (response.statusCode != 200 || response.data == null) {
        await _storageService.clearTokens();
        _isRefreshing = false;
        return null;
      }

      final newToken = response.data['data']['token'];
      final newRefreshToken = response.data['data']['refreshToken'];

      if (newToken == null || newRefreshToken == null) {
        await _storageService.clearTokens();
        _isRefreshing = false;
        return null;
      }

      await _storageService.saveToken(newToken);
      await _storageService.saveRefreshToken(newRefreshToken);
      _isRefreshing = false;
      return newToken;
    } catch (e) {
      await _storageService.clearTokens();
      _isRefreshing = false;
      return null;
    }
  }
}
