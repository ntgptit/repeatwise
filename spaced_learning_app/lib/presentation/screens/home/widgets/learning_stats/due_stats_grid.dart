// lib/presentation/screens/home/widgets/learning_stats/due_stats_grid.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart';
import 'package:spaced_learning_app/domain/models/learning_stats.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/learning_stats/stat_grid_item.dart';

class DueStatsGrid extends StatelessWidget {
  final LearningStatsDTO stats;
  final ThemeData theme;
  final bool isSmallScreen;

  const DueStatsGrid({
    super.key,
    required this.stats,
    required this.theme,
    this.isSmallScreen = false,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = theme.colorScheme;

    return GridView.count(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisCount: isSmallScreen ? 2 : 4,
      childAspectRatio: isSmallScreen ? 0.8 : 0.6,
      mainAxisSpacing: AppDimens.spaceS,
      crossAxisSpacing: AppDimens.spaceS,
      children: [
        StatGridItem(
          theme: theme,
          value: '${stats.dueToday}',
          label: 'Due\nToday',
          iconData: Icons.today,
          color: colorScheme.getStatColor('warning'),
          additionalInfo: '${stats.wordsDueToday} words',
        ),
        StatGridItem(
          theme: theme,
          value: '${stats.dueThisWeek}',
          label: 'Due\nThis Week',
          iconData: Icons.view_week,
          color: colorScheme.getStatColor('warningDark'),
          additionalInfo: '${stats.wordsDueThisWeek} words',
        ),
        StatGridItem(
          theme: theme,
          value: '${stats.dueThisMonth}',
          label: 'Due\nThis Month',
          iconData: Icons.calendar_month,
          color: colorScheme.getStatColor('primary'),
          additionalInfo: '${stats.wordsDueThisMonth} words',
        ),
        StatGridItem(
          theme: theme,
          value: '${stats.completedToday}',
          label: 'Completed\nToday',
          iconData: Icons.check_circle,
          color: colorScheme.getStatColor('successDark'),
          additionalInfo: '${stats.wordsCompletedToday} words',
        ),
      ],
    );
  }
}
