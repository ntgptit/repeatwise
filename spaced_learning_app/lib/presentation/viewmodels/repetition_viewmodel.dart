// lib/presentation/viewmodels/repetition_viewmodel.dart
import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/utils/string_utils.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/domain/models/repetition.dart';

import '../../core/di/providers.dart';

part 'repetition_viewmodel.g.dart';

@riverpod
class RepetitionState extends _$RepetitionState {
  @override
  Future<List<Repetition>> build() async {
    return [];
  }

  Future<void> loadRepetitionsByProgressId(String progressId) async {
    final sanitizedId = StringUtils.sanitizeId(
      progressId,
      source: 'RepetitionViewModel',
    );
    if (sanitizedId == null) {
      state = AsyncValue.error(
        'Invalid progress ID: Empty ID provided',
        StackTrace.current,
      );
      return;
    }

    state = const AsyncValue.loading();

    try {
      debugPrint('Loading repetitions for progressId: $sanitizedId');
      final repetitions = await ref
          .read(repetitionRepositoryProvider)
          .getRepetitionsByProgressId(sanitizedId);
      debugPrint('Loaded ${repetitions.length} repetitions');

      if (repetitions.isNotEmpty) {
        for (int i = 0; i < min(repetitions.length, 3); i++) {
          final rep = repetitions[i];
          debugPrint(
            'Repetition $i: ID=${rep.id}, Order=${rep.repetitionOrder}, Status=${rep.status}',
          );
        }
      }

      state = AsyncValue.data(repetitions);
    } catch (e) {
      debugPrint('Failed to load repetitions by progress: $e');
      state = AsyncValue.error(e, StackTrace.current);
    }
  }

  Future<List<Repetition>> createDefaultSchedule(
    String moduleProgressId,
  ) async {
    final sanitizedId = StringUtils.sanitizeId(
      moduleProgressId,
      source: 'RepetitionViewModel',
    );
    if (sanitizedId == null) {
      throw Exception('Invalid progress ID: Empty ID provided');
    }

    try {
      debugPrint('Creating default schedule for progressId: $sanitizedId');
      final schedule = await ref
          .read(repetitionRepositoryProvider)
          .createDefaultSchedule(sanitizedId);
      debugPrint('Created schedule with ${schedule.length} repetitions');

      await loadRepetitionsByProgressId(sanitizedId);

      return schedule;
    } catch (e) {
      debugPrint('Failed to create repetition schedule: $e');
      rethrow;
    }
  }

  Future<Repetition?> updateRepetition(
    String id, {
    RepetitionStatus? status,
    DateTime? reviewDate,
    bool rescheduleFollowing = false,
    double? percentComplete,
  }) async {
    final sanitizedId = StringUtils.sanitizeId(
      id,
      source: 'RepetitionViewModel',
    );
    if (sanitizedId == null) {
      throw Exception('Invalid repetition ID: Empty ID provided');
    }

    try {
      debugPrint('Updating repetition: $sanitizedId');
      debugPrint(
        'Status: $status, ReviewDate: $reviewDate, RescheduleFollowing: $rescheduleFollowing',
      );

      final repetition = await ref
          .read(repetitionRepositoryProvider)
          .updateRepetition(
            sanitizedId,
            status: status,
            reviewDate: reviewDate,
            rescheduleFollowing: rescheduleFollowing,
            percentComplete: percentComplete,
          );

      ref
          .read(selectedRepetitionProvider.notifier)
          .updateSelectedRepetition(repetition);

      final repetitions = state.valueOrNull ?? [];
      final index = repetitions.indexWhere((r) => r.id == sanitizedId);
      if (index >= 0) {
        final updatedList = [...repetitions]
          ..replaceRange(index, index + 1, [repetition]);
        state = AsyncValue.data(updatedList);
      }

      if (rescheduleFollowing) {
        await loadRepetitionsByProgressId(repetition.moduleProgressId);
      }

      debugPrint('Repetition updated successfully');
      return repetition;
    } catch (e) {
      debugPrint('Failed to update repetition: $e');
      return null;
    }
  }

  Future<bool> areAllRepetitionsCompleted(String progressId) async {
    final sanitizedId = StringUtils.sanitizeId(
      progressId,
      source: 'RepetitionViewModel',
    );
    if (sanitizedId == null) {
      throw Exception('Invalid progress ID: Empty ID provided');
    }

    try {
      debugPrint('Checking completion status for progressId: $sanitizedId');

      List<Repetition> repetitionsToCheck;
      final currentData = state.valueOrNull ?? [];
      final bool currentDataValid =
          currentData.isNotEmpty &&
          currentData.first.moduleProgressId == sanitizedId;

      if (!currentDataValid) {
        debugPrint(
          'No cached repetitions for progressId: $sanitizedId, loading from repository',
        );
        repetitionsToCheck = await ref
            .read(repetitionRepositoryProvider)
            .getRepetitionsByProgressId(sanitizedId);
      } else {
        repetitionsToCheck = currentData;
      }

      if (repetitionsToCheck.isEmpty) {
        debugPrint('No repetitions found for this progress');
        return false;
      }

      final totalCount = repetitionsToCheck.length;
      final completedCount = repetitionsToCheck
          .where((r) => r.status == RepetitionStatus.completed)
          .length;

      final isCompleted = completedCount >= totalCount;
      debugPrint(
        'Completion check: $completedCount/$totalCount completed. All completed: $isCompleted',
      );
      return isCompleted;
    } catch (e) {
      debugPrint('Error checking completion status: $e');
      return false;
    }
  }

  void clearRepetitions() {
    state = const AsyncValue.data([]);
    ref.read(selectedRepetitionProvider.notifier).clearSelectedRepetition();
  }

  int min(int a, int b) {
    return a < b ? a : b;
  }
}

@riverpod
class SelectedRepetition extends _$SelectedRepetition {
  @override
  Repetition? build() {
    return null;
  }

  void updateSelectedRepetition(Repetition repetition) {
    state = repetition;
  }

  void clearSelectedRepetition() {
    state = null;
  }
}

@riverpod
String getCycleInfo(Ref ref, CycleStudied cycle) {
  switch (cycle) {
    case CycleStudied.firstTime:
      return 'You are in the first learning cycle. Complete 5 review sessions to move to the next cycle.';
    case CycleStudied.firstReview:
      return 'You are in the first review cycle. Complete all 5 review sessions to proceed.';
    case CycleStudied.secondReview:
      return 'You are in the second review cycle. You\'re doing great!';
    case CycleStudied.thirdReview:
      return 'You are in the third review cycle. You almost have this mastered!';
    case CycleStudied.moreThanThreeReviews:
      return 'You\'ve completed more than 3 review cycles. The knowledge is well reinforced!';
  }
}
