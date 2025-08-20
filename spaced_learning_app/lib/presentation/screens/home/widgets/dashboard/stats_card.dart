// lib/presentation/screens/home/widgets/dashboard/stats_card.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/domain/models/completion_stats.dart';
import 'package:spaced_learning_app/domain/models/due_stats.dart';
import 'package:spaced_learning_app/domain/models/learning_stats.dart';
import 'package:spaced_learning_app/domain/models/module_stats.dart';
import 'package:spaced_learning_app/domain/models/streak_stats.dart';
import 'package:spaced_learning_app/domain/models/vocabulary_stats.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/learning_stats/learning_stats_card.dart';

class StatsCard extends StatelessWidget {
  final ModuleStats moduleStats;
  final DueStats dueStats;
  final CompletionStats completionStats;
  final StreakStats streakStats;
  final VocabularyStats vocabularyStats;
  final VoidCallback? onViewProgress;

  const StatsCard({
    super.key,
    required this.moduleStats,
    required this.dueStats,
    required this.completionStats,
    required this.streakStats,
    required this.vocabularyStats,
    this.onViewProgress,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    // Construct the LearningStatsDTO for use with the stats card
    final statsDTO = LearningStatsDTO(
      totalModules: moduleStats.totalModules,
      cycleStats: moduleStats.cycleStats,
      dueToday: dueStats.dueToday,
      dueThisWeek: dueStats.dueThisWeek,
      dueThisMonth: dueStats.dueThisMonth,
      wordsDueToday: dueStats.wordsDueToday,
      wordsDueThisWeek: dueStats.wordsDueThisWeek,
      wordsDueThisMonth: dueStats.wordsDueThisMonth,
      completedToday: completionStats.completedToday,
      wordsCompletedToday: completionStats.wordsCompletedToday,
      streakDays: streakStats.streakDays,
      streakWeeks: streakStats.streakWeeks,
      longestStreakDays: streakStats.streakDays,
      // Assuming same for simplicity
      totalWords: vocabularyStats.totalWords,
      learnedWords: vocabularyStats.learnedWords,
      pendingWords: vocabularyStats.pendingWords,
      vocabularyCompletionRate: vocabularyStats.vocabularyCompletionRate,
      weeklyNewWordsRate: vocabularyStats.weeklyNewWordsRate,
    );

    return LearningStatsCard(
      stats: statsDTO,
      onViewDetailPressed: onViewProgress,
      theme: theme,
    );
  }
}
