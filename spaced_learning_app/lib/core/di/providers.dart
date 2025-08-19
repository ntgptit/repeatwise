import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/core/services/auth_service.dart';
import 'package:spaced_learning_app/core/services/storage_service.dart';
import 'package:spaced_learning_app/core/services/user_service.dart';

part 'providers.g.dart';

// CORE SERVICES

@riverpod
ApiClient apiClient(Ref ref) => ApiClient();

@riverpod
StorageService storageService(Ref ref) => StorageService();

// API SERVICES

@riverpod
AuthService authService(Ref ref) =>
    AuthService(ref.read(apiClientProvider).dio);

@riverpod
UserService userService(Ref ref) =>
    UserService(ref.read(apiClientProvider).dio);
