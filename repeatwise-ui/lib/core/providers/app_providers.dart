import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:go_router/go_router.dart';

import '../routing/app_router.dart';
import '../services/api_repository.dart';
import '../services/storage_service.dart';
import '../services/dio_service.dart';
import '../models/user.dart';

part 'app_providers.g.dart';

@riverpod
GoRouter appRouter(Ref ref) {
  return AppRouter.createRouter();
}

@riverpod
ApiRepository apiRepository(Ref ref) {
  final dioService = ref.watch(dioServiceProvider);
  final storageService = ref.watch(storageServiceProvider);
  return ApiRepository(dioService, storageService);
}

@riverpod
StorageService storageService(Ref ref) {
  return StorageService();
}

@riverpod
class AppState extends _$AppState {
  @override
  AppStateData build() {
    return const AppStateData(
      isLoading: false,
      isAuthenticated: false,
      currentUser: null,
    );
  }

  void setLoading(bool loading) {
    state = state.copyWith(isLoading: loading);
  }

  void setAuthenticated(bool authenticated) {
    state = state.copyWith(isAuthenticated: authenticated);
  }

  void setCurrentUser(User? user) {
    state = state.copyWith(currentUser: user);
  }
}

class AppStateData {
  final bool isLoading;
  final bool isAuthenticated;
  final User? currentUser;

  const AppStateData({
    required this.isLoading,
    required this.isAuthenticated,
    this.currentUser,
  });

  AppStateData copyWith({
    bool? isLoading,
    bool? isAuthenticated,
    User? currentUser,
  }) {
    return AppStateData(
      isLoading: isLoading ?? this.isLoading,
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
      currentUser: currentUser ?? this.currentUser,
    );
  }
}
