// lib/presentation/viewmodels/progress_viewmodel.dart
import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/events/app_events.dart';
import 'package:spaced_learning_app/core/utils/string_utils.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';

import '../../core/di/providers.dart';
import 'auth_viewmodel.dart';

part 'progress_viewmodel.g.dart';

@riverpod
class ProgressState extends _$ProgressState {
  @override
  Future<List<ProgressDetail>> build() async {
    final user = ref.read(currentUserProvider);
    if (user == null) return [];
    final result = await ref
        .read(progressRepositoryProvider)
        .getDueProgress(user.id);
    debugPrint('[ProgressState.build] Loaded ${result.length} items');
    return result;
  }

  Future<void> loadDueProgress(
    String userId, {
    DateTime? studyDate,
    int page = 0,
    int size = 20,
  }) async {
    final sanitizedId = StringUtils.sanitizeId(
      userId,
      source: 'ProgressViewModel',
    );
    if (sanitizedId == null) {
      throw Exception('Invalid user ID: Empty ID provided');
    }

    state = const AsyncValue.loading();
    try {
      final result = await ref
          .read(progressRepositoryProvider)
          .getDueProgress(
            sanitizedId,
            studyDate: studyDate,
            page: page,
            size: size,
          );
      debugPrint(
        '[ProgressViewModel] Received ${result.length} records from repository for due progress.',
      );
      state = AsyncValue.data(result);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }

  Future<ProgressDetail?> createProgress({
    required String moduleId,
    String? userId,
    DateTime? firstLearningDate,
    CycleStudied? cyclesStudied,
    DateTime? nextStudyDate,
    double? percentComplete,
  }) async {
    final sanitizedModuleId = StringUtils.sanitizeId(
      moduleId,
      source: 'ProgressViewModel',
    );
    if (sanitizedModuleId == null) {
      throw Exception('Invalid module ID: Empty ID provided');
    }

    try {
      debugPrint('Creating progress for moduleId: $sanitizedModuleId');
      final progress = await ref
          .read(progressRepositoryProvider)
          .createProgress(
            moduleId: sanitizedModuleId,
            userId: userId,
            firstLearningDate: firstLearningDate,
            cyclesStudied: cyclesStudied,
            nextStudyDate: nextStudyDate,
            percentComplete: percentComplete,
          );

      if (userId != null) {
        ref
            .read(eventBusProvider)
            .fire(ProgressChangedEvent(userId: userId, hasDueTasks: true));
      }

      debugPrint('Progress created successfully: ${progress.id}');
      return progress;
    } catch (e) {
      debugPrint('Failed to create progress: $e');
      return null;
    }
  }

  Future<ProgressDetail?> updateProgress(
    String id, {
    DateTime? firstLearningDate,
    CycleStudied? cyclesStudied,
    DateTime? nextStudyDate,
    double? percentComplete,
  }) async {
    final sanitizedId = StringUtils.sanitizeId(id, source: 'ProgressViewModel');
    if (sanitizedId == null) {
      throw Exception('Invalid progress ID: Empty ID provided');
    }

    try {
      debugPrint('Updating progress with id: $sanitizedId');
      final progress = await ref
          .read(progressRepositoryProvider)
          .updateProgress(
            sanitizedId,
            firstLearningDate: firstLearningDate,
            cyclesStudied: cyclesStudied,
            nextStudyDate: nextStudyDate,
            percentComplete: percentComplete,
          );

      final userData = await ref.read(storageServiceProvider).getUserData();
      final userId = userData?['id'];
      if (userId != null) {
        ref
            .read(eventBusProvider)
            .fire(
              TaskCompletedEvent(
                userId: userId.toString(),
                progressId: sanitizedId,
              ),
            );
      }

      debugPrint('Progress updated successfully');
      return progress;
    } catch (e) {
      debugPrint('Error updating progress: $e');
      return null;
    }
  }
}

@riverpod
class SelectedProgress extends _$SelectedProgress {
  @override
  Future<ProgressDetail?> build() async {
    return null;
  }

  Future<void> loadProgressDetails(String id) async {
    final sanitizedId = StringUtils.sanitizeId(id, source: 'SelectedProgress');
    if (sanitizedId == null) {
      state = const AsyncValue.data(null);
      return;
    }

    state = const AsyncValue.loading();

    try {
      final progress = await ref
          .read(progressRepositoryProvider)
          .getProgressById(sanitizedId);
      state = AsyncValue.data(progress);
    } catch (e) {
      state = AsyncValue.error(e, StackTrace.current);
    }
  }

  Future<void> loadModuleProgress(String moduleId) async {
    final sanitizedId = StringUtils.sanitizeId(
      moduleId,
      source: 'SelectedProgress',
    );
    if (sanitizedId == null) {
      state = const AsyncValue.data(null);
      return;
    }

    state = const AsyncValue.loading();

    try {
      final progressList = await ref
          .read(progressRepositoryProvider)
          .getProgressByModuleId(sanitizedId, page: 0, size: 1);

      if (progressList.isEmpty) {
        state = const AsyncValue.data(null);
        return;
      }

      final progressId = progressList[0].id;
      debugPrint('Found progress with ID: $progressId for module: $moduleId');

      final progressDetail = await ref
          .read(progressRepositoryProvider)
          .getProgressById(progressId);
      state = AsyncValue.data(progressDetail);
    } catch (e) {
      state = AsyncValue.error(e, StackTrace.current);
    }
  }

  void clearSelectedProgress() {
    state = const AsyncValue.data(null);
  }
}

@riverpod
bool isUpdatingProgress(Ref ref) {
  final progressState = ref.watch(progressStateProvider);
  return progressState.isLoading;
}

@riverpod
List<ProgressDetail> todayDueTasks(Ref ref) {
  final progressList = ref.watch(progressStateProvider).valueOrNull ?? [];
  if (progressList.isEmpty) return [];

  final now = DateTime.now().toUtc();
  final todayUtc = DateTime.utc(now.year, now.month, now.day);

  final dueTasks = progressList.where((progress) {
    final next = progress.nextStudyDate;
    if (next == null) return false;

    final normalized = DateTime.utc(next.year, next.month, next.day);
    final isDue = !normalized.isAfter(todayUtc);

    debugPrint(
      '[todayDueTasksProvider] id=${progress.id} '
      'nextStudyDate=$next normalized=$normalized today=$todayUtc => isDue=$isDue',
    );

    return isDue;
  }).toList();

  debugPrint('[todayDueTasksProvider] Total due tasks: ${dueTasks.length}');
  return dueTasks;
}

// @riverpod
// int todayDueTasksCount(Ref ref) {
//   return ref.watch(todayDueTasksProvider).length;
// }

@riverpod
Future<List<ProgressDetail>> trackedProgressState(Ref ref) {
  debugPrint(
    '[TrackedProgressState] >>> Triggered build() at ${DateTime.now()}',
  );
  // debugPrint(StackTrace.current.toString());
  return ref.watch(progressStateProvider.future);
}
