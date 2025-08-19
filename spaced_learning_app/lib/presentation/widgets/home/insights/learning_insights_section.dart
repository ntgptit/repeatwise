// lib/presentation/widgets/home/insights/learning_insights_section.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/presentation/viewmodels/learning_stats_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/home/insights/learning_insights_widget.dart';

class LearningInsightsSection extends ConsumerWidget {
  const LearningInsightsSection({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final insightsAsync = ref.watch(learningInsightsProvider);
    final statsAsync = ref.watch(learningStatsStateProvider);
    final theme = Theme.of(context);

    return insightsAsync.when(
      data: (insights) {
        if (insights.isEmpty) return const SizedBox.shrink();

        // Consolidate data for the insights widget
        final stats = statsAsync.valueOrNull;
        final vocabularyRate = stats?.weeklyNewWordsRate ?? 0.0;
        final streakDays = stats?.streakDays ?? 0;
        final pendingWords = stats?.pendingWords ?? 0;
        final dueToday = stats?.dueToday ?? 0;

        return LearningInsightsWidget(
          vocabularyRate: vocabularyRate,
          streakDays: streakDays,
          pendingWords: pendingWords,
          dueToday: dueToday,
          theme: theme,
        );
      },
      loading: () => const SizedBox.shrink(),
      error: (_, __) => const SizedBox.shrink(),
    );
  }
}
