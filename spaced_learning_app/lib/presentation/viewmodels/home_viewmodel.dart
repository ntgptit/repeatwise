// lib/presentation/viewmodels/home_viewmodel.dart
import 'package:flutter/material.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/learning_stats_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/progress_viewmodel.dart';

part 'home_viewmodel.g.dart';

enum HomeLoadingStatus { initial, loading, loaded, error }

class HomeState {
  final HomeLoadingStatus status;
  final String? errorMessage;

  const HomeState({required this.status, this.errorMessage});

  const HomeState.initial() : this(status: HomeLoadingStatus.initial);

  HomeState copyWith({HomeLoadingStatus? status, String? errorMessage}) {
    return HomeState(
      status: status ?? this.status,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }
}

@riverpod
class HomeViewModel extends _$HomeViewModel {
  @override
  HomeState build() {
    return const HomeState.initial();
  }

  Future<void> loadInitialData() async {
    debugPrint('[HomeViewModel] loadInitialData - Starting...');

    if (state.status == HomeLoadingStatus.loading) {
      debugPrint('[HomeViewModel] loadInitialData - Already loading, skipping');
      return;
    }

    debugPrint('[HomeViewModel] loadInitialData - Setting state to loading');
    state = state.copyWith(status: HomeLoadingStatus.loading);

    try {
      final user = ref.read(currentUserProvider);
      debugPrint(
        '[HomeViewModel] loadInitialData - Current user: ${user?.id ?? 'null'}',
      );

      // Tải dữ liệu thống kê
      debugPrint('[HomeViewModel] loadInitialData - Loading stats data');
      await ref.read(loadAllStatsProvider(refreshCache: false).future);
      debugPrint(
        '[HomeViewModel] loadInitialData - Stats data loaded successfully',
      );

      // Tải dữ liệu tiến trình nếu người dùng đã đăng nhập
      if (user != null) {
        debugPrint(
          '[HomeViewModel] loadInitialData - Loading progress data for user: ${user.id}',
        );
        final progressNotifier = ref.read(progressStateProvider.notifier);
        debugPrint(
          '[HomeViewModel] loadInitialData - Current progress state: ${ref.read(progressStateProvider).valueOrNull?.length ?? 0} items',
        );

        await progressNotifier.loadDueProgress(user.id);

        final progressAfter = ref.read(progressStateProvider).valueOrNull;
        debugPrint(
          '[HomeViewModel] loadInitialData - Progress data loaded. Items count: ${progressAfter?.length ?? 0}',
        );

        if (progressAfter != null && progressAfter.isNotEmpty) {
          debugPrint(
            '[HomeViewModel] loadInitialData - First progress item: id=${progressAfter.first.id}, moduleTitle=${progressAfter.first.moduleTitle ?? "N/A"}',
          );

          // Debug for due tasks
          final now = DateTime.now();
          final today = DateTime(now.year, now.month, now.day);
          final dueTasks = progressAfter.where((task) {
            if (task.nextStudyDate == null) return false;
            final dueDate = DateTime(
              task.nextStudyDate!.year,
              task.nextStudyDate!.month,
              task.nextStudyDate!.day,
            );
            return !dueDate.isAfter(today);
          }).toList();

          debugPrint(
            '[HomeViewModel] loadInitialData - Due tasks count: ${dueTasks.length}',
          );
          for (int i = 0; i < dueTasks.length && i < 3; i++) {
            final task = dueTasks[i];
            final dueDate = task.nextStudyDate != null
                ? '${task.nextStudyDate!.year}-${task.nextStudyDate!.month}-${task.nextStudyDate!.day}'
                : 'null';

            debugPrint(
              '[HomeViewModel] loadInitialData - Due task $i: id=${task.id}, moduleTitle=${task.moduleTitle ?? "N/A"}, dueDate=$dueDate',
            );
          }
        }
      } else {
        debugPrint(
          '[HomeViewModel] loadInitialData - No user logged in, skipping progress loading',
        );
      }

      debugPrint('[HomeViewModel] loadInitialData - Setting state to loaded');
      state = state.copyWith(status: HomeLoadingStatus.loaded);
      debugPrint('[HomeViewModel] loadInitialData - Completed successfully');
    } catch (e, stackTrace) {
      debugPrint('[HomeViewModel] loadInitialData - Error: $e');
      debugPrint('[HomeViewModel] loadInitialData - Stack trace: $stackTrace');
      state = state.copyWith(
        status: HomeLoadingStatus.error,
        errorMessage: e.toString(),
      );
      debugPrint('[HomeViewModel] loadInitialData - Set error state');
    }
  }

  Future<void> refreshData() async {
    try {
      final user = ref.read(currentUserProvider);

      // Tải lại dữ liệu thống kê
      await ref.read(loadAllStatsProvider(refreshCache: true).future);

      // Tải lại dữ liệu tiến trình nếu người dùng đã đăng nhập
      if (user != null) {
        await ref.read(progressStateProvider.notifier).loadDueProgress(user.id);
      }

      state = state.copyWith(status: HomeLoadingStatus.loaded);
    } catch (e) {
      debugPrint('Error refreshing data: $e');
      state = state.copyWith(
        status: HomeLoadingStatus.error,
        errorMessage: e.toString(),
      );
    }
  }

  bool get isFirstLoading =>
      state.status == HomeLoadingStatus.initial ||
      (state.status == HomeLoadingStatus.loading && state.errorMessage == null);

  bool get hasError => state.status == HomeLoadingStatus.error;

  bool get isLoaded => state.status == HomeLoadingStatus.loaded;
}
