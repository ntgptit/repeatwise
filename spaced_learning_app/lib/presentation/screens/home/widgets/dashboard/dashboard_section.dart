// lib/presentation/screens/home/widgets/dashboard/dashboard_section.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/domain/models/completion_stats.dart';
import 'package:spaced_learning_app/domain/models/due_stats.dart';
import 'package:spaced_learning_app/domain/models/module_stats.dart';
import 'package:spaced_learning_app/domain/models/streak_stats.dart';
import 'package:spaced_learning_app/domain/models/vocabulary_stats.dart';
import 'package:spaced_learning_app/presentation/viewmodels/learning_stats_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/dashboard/stats_card.dart';

class DashboardSection extends ConsumerWidget {
  const DashboardSection({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final statsAsync = ref.watch(learningStatsStateProvider);

    return statsAsync.when(
      data: (stats) {
        if (stats == null) {
          return _buildEmptyStatsCard(context, ref);
        }

        return StatsCard(
          moduleStats: ModuleStats(
            totalModules: stats.totalModules,
            cycleStats: stats.cycleStats,
          ),
          dueStats: DueStats(
            dueToday: stats.dueToday,
            dueThisWeek: stats.dueThisWeek,
            dueThisMonth: stats.dueThisMonth,
            wordsDueToday: stats.wordsDueToday,
            wordsDueThisWeek: stats.wordsDueThisWeek,
            wordsDueThisMonth: stats.wordsDueThisMonth,
          ),
          completionStats: CompletionStats(
            completedToday: stats.completedToday,
            completedThisWeek: stats.completedThisWeek,
            completedThisMonth: stats.completedThisMonth,
            wordsCompletedToday: stats.wordsCompletedToday,
            wordsCompletedThisWeek: stats.wordsCompletedThisWeek,
            wordsCompletedThisMonth: stats.wordsCompletedThisMonth,
          ),
          streakStats: StreakStats(
            streakDays: stats.streakDays,
            streakWeeks: stats.streakWeeks,
          ),
          vocabularyStats: VocabularyStats(
            totalWords: stats.totalWords,
            learnedWords: stats.learnedWords,
            pendingWords: stats.pendingWords,
            vocabularyCompletionRate: stats.vocabularyCompletionRate,
            weeklyNewWordsRate: stats.weeklyNewWordsRate,
          ),
          onViewProgress: () => _navigateToProgress(context),
        );
      },
      loading: () => const SizedBox.shrink(),
      error: (_, __) => _buildEmptyStatsCard(context, ref),
    );
  }

  Widget _buildEmptyStatsCard(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Center(
      child: Column(
        children: [
          Text(
            'No statistics available',
            style: theme.textTheme.titleMedium?.copyWith(
              color: colorScheme.onSurface,
            ),
          ),
          const SizedBox(height: 16),
          SLButton(
            text: 'Load Statistics',
            type: SLButtonType.primary,
            onPressed: () => _refreshStats(context, ref),
          ),
        ],
      ),
    );
  }

  void _refreshStats(BuildContext context, WidgetRef ref) {
    ref.read(loadAllStatsProvider(refreshCache: true).future);
  }

  void _navigateToProgress(BuildContext context) {
    GoRouter.of(context).go('/learning');
  }
}
