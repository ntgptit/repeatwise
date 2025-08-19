import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/learning_insight.dart';

class LearningInsightsCard extends StatelessWidget {
  final List<LearningInsightRespone> insights;
  final String? title;
  final VoidCallback? onViewMorePressed;
  final ThemeData? theme;

  const LearningInsightsCard({
    super.key,
    required this.insights,
    this.title,
    this.onViewMorePressed,
    this.theme,
  });

  @override
  Widget build(BuildContext context) {
    final currentTheme = theme ?? Theme.of(context);
    final colorScheme = currentTheme.colorScheme;
    final textTheme = currentTheme.textTheme;

    final sortedInsights = List<LearningInsightRespone>.from(insights)
      ..sort((a, b) => a.priority.compareTo(b.priority));
    final displayInsights = sortedInsights.length > 4
        ? sortedInsights.sublist(0, 4)
        : sortedInsights;

    return Card(
      elevation: AppDimens.elevationS,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
      ),
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildHeader(currentTheme),
            const SizedBox(height: AppDimens.spaceS),
            const Divider(),
            const SizedBox(height: AppDimens.spaceS),
            if (displayInsights.isEmpty)
              Center(
                child: Padding(
                  padding: const EdgeInsets.symmetric(
                    vertical: AppDimens.paddingL,
                  ),
                  child: Text(
                    'No insights available',
                    style: textTheme.bodyMedium?.copyWith(
                      color: colorScheme.onSurfaceVariant,
                    ),
                  ),
                ),
              )
            else
              ...displayInsights.map(
                (insight) => _buildInsightItem(context, insight, currentTheme),
              ),
            if (onViewMorePressed != null && insights.length > 4) ...[
              const SizedBox(height: AppDimens.spaceS),
              Align(
                alignment: Alignment.centerRight,
                child: TextButton(
                  onPressed: onViewMorePressed,
                  child: Text(
                    'View More',
                    style: textTheme.labelLarge?.copyWith(
                      color: colorScheme.primary,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildHeader(ThemeData theme) {
    final colorScheme = theme.colorScheme;

    return Row(
      children: [
        Icon(
          Icons.lightbulb_outline,
          color: colorScheme.tertiary,
          size: AppDimens.iconM,
        ),
        const SizedBox(width: AppDimens.spaceS),
        Text(
          title ?? 'Learning Insights',
          style: theme.textTheme.titleLarge?.copyWith(
            color: colorScheme.onSurface,
            fontWeight: FontWeight.w600,
          ),
        ),
      ],
    );
  }

  Widget _buildInsightItem(
    BuildContext context,
    LearningInsightRespone insight,
    ThemeData theme,
  ) {
    final color = _getColorFromString(insight.color, theme);
    final icon = _getIconFromString(insight.icon);

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: AppDimens.paddingS),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, color: color, size: AppDimens.iconM),
          const SizedBox(width: AppDimens.spaceM),
          Expanded(
            child: Text(
              insight.message,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Color _getColorFromString(String colorName, ThemeData theme) {
    final colorScheme = theme.colorScheme;

    switch (colorName.toLowerCase()) {
      case 'primary':
      case 'blue':
        return colorScheme.primary;
      case 'secondary':
      case 'purple':
        return colorScheme.secondary;
      case 'tertiary':
        return colorScheme.tertiary;
      case 'error':
      case 'red':
        return colorScheme.error;
      case 'surface':
        return colorScheme.surface;
      case 'onSurface':
        return colorScheme.onSurface;
      case 'neutral':
        return colorScheme.onSurfaceVariant;
      case 'success':
      case 'green':
      case 'teal':
        return colorScheme.tertiary;
      case 'warning':
      case 'orange':
      case 'amber':
        return colorScheme.secondary;
      case 'info':
      case 'indigo':
        return colorScheme.primaryContainer;
      default:
        return colorScheme.onSurfaceVariant;
    }
  }

  IconData _getIconFromString(String iconName) {
    switch (iconName.toLowerCase()) {
      case 'trending_up':
        return Icons.trending_up;
      case 'local_fire_department':
        return Icons.local_fire_department;
      case 'menu_book':
        return Icons.menu_book;
      case 'today':
        return Icons.today;
      case 'check_circle':
        return Icons.check_circle;
      case 'star':
        return Icons.star;
      case 'emoji_events':
        return Icons.emoji_events;
      case 'lightbulb':
        return Icons.lightbulb;
      case 'info':
        return Icons.info;
      case 'warning':
        return Icons.warning;
      case 'error':
        return Icons.error;
      default:
        return Icons.info_outline;
    }
  }
}
