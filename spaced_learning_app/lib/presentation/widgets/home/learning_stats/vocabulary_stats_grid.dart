// lib/presentation/widgets/home/learning_stats/vocabulary_stats_grid.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart';
import 'package:spaced_learning_app/domain/models/learning_stats.dart';
import 'package:spaced_learning_app/presentation/widgets/home/learning_stats/stat_grid_item.dart';

class VocabularyStatsGrid extends StatelessWidget {
  final LearningStatsDTO stats;
  final ThemeData theme;
  final bool isSmallScreen;

  const VocabularyStatsGrid({
    super.key,
    required this.stats,
    required this.theme,
    this.isSmallScreen = false,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = theme.colorScheme;
    final completionRate = stats.vocabularyCompletionRate.toStringAsFixed(1);
    final weeklyRate = stats.weeklyNewWordsRate.toStringAsFixed(1);

    return GridView.count(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisCount: isSmallScreen ? 2 : 4,
      childAspectRatio: isSmallScreen ? 1.3 : 0.6,
      mainAxisSpacing: AppDimens.spaceS,
      crossAxisSpacing: AppDimens.spaceS,
      children: [
        StatGridItem(
          theme: theme,
          value: '${stats.totalWords}',
          label: 'Total\nWords',
          iconData: Icons.library_books,
          color: colorScheme.getStatColor('success'),
        ),
        StatGridItem(
          theme: theme,
          value: '${stats.learnedWords}',
          label: 'Learned\nWords',
          iconData: Icons.spellcheck,
          color: colorScheme.getStatColor('success'),
          additionalInfo: '$completionRate%',
        ),
        StatGridItem(
          theme: theme,
          value: '${stats.pendingWords}',
          label: 'Pending\nWords',
          iconData: Icons.hourglass_bottom,
          color: colorScheme.getStatColor('warningDark'),
        ),
        StatGridItem(
          theme: theme,
          value: weeklyRate,
          label: 'Weekly\nRate',
          iconData: Icons.trending_up,
          color: colorScheme.getStatColor('info'),
          additionalInfo: '%',
        ),
      ],
    );
  }
}
