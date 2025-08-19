import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/domain/models/learning_insight.dart';
import 'package:spaced_learning_app/domain/models/learning_stats.dart';

import '../../core/di/providers.dart';

part 'learning_stats_viewmodel.g.dart';

@riverpod
class LearningStatsState extends _$LearningStatsState {
  @override
  Future<LearningStatsDTO?> build() async {
    return _loadStats();
  }

  Future<LearningStatsDTO?> _loadStats({bool refreshCache = false}) async {
    try {
      final stats = await ref
          .read(learningStatsRepositoryProvider)
          .getDashboardStats(refreshCache: refreshCache);
      return stats;
    } catch (e, st) {
      throw AsyncError(e, st);
    }
  }

  Future<void> loadDashboardStats({bool refreshCache = false}) async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(
      () => _loadStats(refreshCache: refreshCache),
    );
  }
}

@riverpod
class LearningInsights extends _$LearningInsights {
  @override
  Future<List<LearningInsightRespone>> build() async {
    return _loadInsights();
  }

  Future<List<LearningInsightRespone>> _loadInsights() async {
    try {
      final insights = await ref
          .read(learningStatsRepositoryProvider)
          .getLearningInsights();
      return insights;
    } catch (e, st) {
      throw AsyncError(e, st);
    }
  }

  Future<void> loadLearningInsights() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(_loadInsights);
  }
}

@riverpod
Future<void> loadAllStats(
  Ref ref, {
  @Default(false) required bool refreshCache,
}) async {
  try {
    final statsRepo = ref.read(learningStatsRepositoryProvider);

    await Future.wait([
      statsRepo.getDashboardStats(refreshCache: refreshCache),
      statsRepo.getLearningInsights(),
    ]);

    // Invalidate để trigger rebuild và gọi lại build()
    ref.invalidate(learningStatsStateProvider);
    ref.invalidate(learningInsightsProvider);
  } catch (e) {
    debugPrint('Error loading all stats: $e');
    rethrow;
  }
}
