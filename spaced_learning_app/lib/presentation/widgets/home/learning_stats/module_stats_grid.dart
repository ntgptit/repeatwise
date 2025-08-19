// lib/presentation/widgets/home/learning_stats/module_stats_grid.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart';
import 'package:spaced_learning_app/domain/models/learning_stats.dart';
import 'package:spaced_learning_app/presentation/widgets/home/learning_stats/stat_grid_item.dart';

class ModuleStatsGrid extends StatelessWidget {
  final LearningStatsDTO stats;
  final ThemeData theme;
  final bool isSmallScreen;

  const ModuleStatsGrid({
    super.key,
    required this.stats,
    required this.theme,
    this.isSmallScreen = false,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = theme.colorScheme;
    final cycleStats = stats.cycleStats;

    return GridView.count(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisCount: isSmallScreen ? 3 : 4,
      childAspectRatio: isSmallScreen ? 0.8 : 0.8,
      mainAxisSpacing: AppDimens.spaceS,
      crossAxisSpacing: AppDimens.spaceS,
      children: [
        StatGridItem(
          theme: theme,
          value: stats.totalModules.toString(),
          label: 'Total\nModules',
          iconData: Icons.menu_book,
          color: colorScheme.getStatColor('warning'),
        ),
        StatGridItem(
          theme: theme,
          value: '${cycleStats['NOT_STUDIED'] ?? 0}',
          label: 'Not\nStudied',
          iconData: Icons.visibility_off,
          color: colorScheme.getStatColor('neutral'),
        ),
        StatGridItem(
          theme: theme,
          value: '${cycleStats['FIRST_TIME'] ?? 0}',
          label: '1st\nTime',
          iconData: Icons.play_circle_fill,
          color: colorScheme.getStatColor('success'),
        ),
        StatGridItem(
          theme: theme,
          value: '${cycleStats['FIRST_REVIEW'] ?? 0}',
          label: '1st\nReview',
          iconData: Icons.rotate_right,
          color: colorScheme.getStatColor('secondary'),
        ),
        StatGridItem(
          theme: theme,
          value: '${cycleStats['SECOND_REVIEW'] ?? 0}',
          label: '2nd\nReview',
          iconData: Icons.rotate_90_degrees_ccw,
          color: colorScheme.getStatColor('info'),
        ),
        StatGridItem(
          theme: theme,
          value: '${cycleStats['THIRD_REVIEW'] ?? 0}',
          label: '3rd\nReview',
          iconData: Icons.change_circle,
          color: colorScheme.getStatColor('secondary'),
        ),
        StatGridItem(
          theme: theme,
          value: '${cycleStats['MORE_THAN_THREE_REVIEWS'] ?? 0}',
          label: '4th+\nReviews',
          iconData: Icons.loop,
          color: colorScheme.getStatColor('tertiary'),
        ),
      ],
    );
  }
}
