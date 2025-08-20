// lib/presentation/screens/home/widgets/insights/learning_insights_widget.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/insights/insight_item.dart';

class LearningInsightsWidget extends StatelessWidget {
  final double vocabularyRate;
  final int streakDays;
  final int pendingWords;
  final int dueToday;
  final ThemeData? theme; // Optional theme override

  const LearningInsightsWidget({
    super.key,
    required this.vocabularyRate,
    required this.streakDays,
    required this.pendingWords,
    required this.dueToday,
    this.theme,
  });

  @override
  Widget build(BuildContext context) {
    final currentTheme = theme ?? Theme.of(context);
    final colorScheme = currentTheme.colorScheme;
    final textTheme = currentTheme.textTheme;

    return Card(
      margin: EdgeInsets.zero,
      elevation: AppDimens.elevationS,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
      ),
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildHeader(colorScheme, textTheme),
            const SizedBox(height: AppDimens.spaceM),
            const Divider(height: AppDimens.dividerThickness),
            const SizedBox(height: AppDimens.spaceS),
            _buildInsightsList(context, colorScheme, textTheme),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader(ColorScheme colorScheme, TextTheme textTheme) {
    return Row(
      children: [
        Icon(
          Icons.insights_outlined,
          color: colorScheme.getStatColor('tertiary'),
          size: AppDimens.iconM,
        ),
        const SizedBox(width: AppDimens.spaceS),
        Text(
          'Learning Insights',
          style: textTheme.titleLarge?.copyWith(
            color: colorScheme.onSurface,
            fontWeight: FontWeight.w600,
          ),
        ),
      ],
    );
  }

  Widget _buildInsightsList(
    BuildContext context,
    ColorScheme colorScheme,
    TextTheme textTheme,
  ) {
    final theme = Theme.of(context);

    return Column(
      children: [
        InsightItem(
          message:
              'You learn ${vocabularyRate.toStringAsFixed(1)}% new vocabulary each week',
          icon: Icons.trending_up,
          colorType: 'secondary',
          theme: theme,
          colorScheme: colorScheme,
        ),
        InsightItem(
          message: 'Your current streak is $streakDays days - keep going!',
          icon: Icons.local_fire_department_outlined,
          colorType: 'tertiary',
          theme: theme,
          colorScheme: colorScheme,
        ),
        InsightItem(
          message: 'You have $pendingWords words pending to learn',
          icon: Icons.hourglass_empty_outlined,
          colorType: 'primary',
          theme: theme,
          colorScheme: colorScheme,
        ),
        InsightItem(
          message:
              'Complete today\'s $dueToday sessions to maintain your streak',
          icon: Icons.today_outlined,
          colorType: 'warning',
          theme: theme,
          colorScheme: colorScheme,
        ),
      ],
    );
  }
}
