// lib/presentation/screens/home/widgets/learning_stats/streak_stats_grid.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart';
import 'package:spaced_learning_app/domain/models/learning_stats.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/learning_stats/stat_grid_item.dart';

class StreakStatsGrid extends StatelessWidget {
  final LearningStatsDTO stats;
  final ThemeData theme;
  final bool isSmallScreen;

  const StreakStatsGrid({
    super.key,
    required this.stats,
    required this.theme,
    this.isSmallScreen = false,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = theme.colorScheme;
    final starColor = colorScheme.getStatColor('warning');
    final onStarColor = theme.brightness == Brightness.light
        ? Colors.white
        : theme.colorScheme.surface;

    return GridView.count(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisCount: isSmallScreen ? 2 : 4,
      childAspectRatio: isSmallScreen ? 0.8 : 0.75,
      mainAxisSpacing: AppDimens.spaceS,
      crossAxisSpacing: AppDimens.spaceS,
      children: [
        StatGridItem(
          theme: theme,
          value: '${stats.streakDays}',
          label: 'Day\nStreak',
          iconData: Icons.local_fire_department,
          color: colorScheme.getStatColor('error'),
          showStar: stats.streakDays >= 7,
          starColor: starColor,
          onStarColor: onStarColor,
        ),
        StatGridItem(
          theme: theme,
          value: '${stats.streakWeeks}',
          label: 'Week\nStreak',
          iconData: Icons.date_range,
          color: colorScheme.getStatColor('secondary'),
          showStar: stats.streakWeeks >= 4,
          starColor: starColor,
          onStarColor: onStarColor,
        ),
        StatGridItem(
          theme: theme,
          value: '${stats.longestStreakDays}',
          label: 'Longest\nStreak',
          iconData: Icons.emoji_events,
          color: colorScheme.getStatColor('warning'),
          additionalInfo: 'days',
        ),
        if (!isSmallScreen) const SizedBox.shrink(),
      ],
    );
  }
}
