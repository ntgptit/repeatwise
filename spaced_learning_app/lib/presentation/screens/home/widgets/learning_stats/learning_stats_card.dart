// lib/presentation/screens/home/widgets/learning_stats/learning_stats_card.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/learning_stats.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/learning_stats/due_stats_grid.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/learning_stats/module_stats_grid.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/learning_stats/streak_stats_grid.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/learning_stats/vocabulary_stats_grid.dart';

class LearningStatsCard extends StatelessWidget {
  final LearningStatsDTO stats;
  final VoidCallback? onViewDetailPressed;
  final ThemeData? theme; // Optional theme override

  const LearningStatsCard({
    super.key,
    required this.stats,
    this.onViewDetailPressed,
    this.theme,
  });

  @override
  Widget build(BuildContext context) {
    final currentTheme = theme ?? Theme.of(context);
    final size = MediaQuery.of(context).size;
    final isSmallScreen = size.width < AppDimens.breakpointXS;
    final _ = currentTheme.colorScheme;

    return Card(
      elevation: currentTheme.cardTheme.elevation ?? AppDimens.elevationS,
      shape:
          currentTheme.cardTheme.shape ??
          RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusL),
          ),
      child: Padding(
        padding: EdgeInsets.all(
          isSmallScreen ? AppDimens.paddingM : AppDimens.paddingL,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildCardHeader(currentTheme),
            SizedBox(
              height: isSmallScreen ? AppDimens.spaceM : AppDimens.spaceL,
            ),
            ModuleStatsGrid(
              stats: stats,
              theme: currentTheme,
              isSmallScreen: isSmallScreen,
            ),
            const Divider(height: AppDimens.spaceXXL),
            DueStatsGrid(
              stats: stats,
              theme: currentTheme,
              isSmallScreen: isSmallScreen,
            ),
            const Divider(height: AppDimens.spaceXXL),
            VocabularyStatsGrid(
              stats: stats,
              theme: currentTheme,
              isSmallScreen: isSmallScreen,
            ),
            const Divider(height: AppDimens.spaceXXL),
            StreakStatsGrid(
              stats: stats,
              theme: currentTheme,
              isSmallScreen: isSmallScreen,
            ),
            if (onViewDetailPressed != null) ...[
              const SizedBox(height: AppDimens.spaceL),
              Align(
                alignment: Alignment.centerRight,
                child: TextButton.icon(
                  icon: const Icon(Icons.analytics_outlined),
                  label: const Text('View Details'),
                  onPressed: onViewDetailPressed,
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildCardHeader(ThemeData theme) {
    return Row(
      children: [
        Icon(Icons.book_online, color: theme.iconTheme.color),
        const SizedBox(width: AppDimens.spaceS),
        Text('Learning Statistics', style: theme.textTheme.titleLarge),
      ],
    );
  }
}
