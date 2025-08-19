// lib/presentation/widgets/learning/learning_filter_bar/filter_stats_row.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class FilterStatsRow extends ConsumerWidget {
  final int totalCount;
  final int dueCount;
  final int completeCount;
  final int activeFilterCount;
  final bool showFilter;
  final VoidCallback onToggleFilter;

  const FilterStatsRow({
    super.key,
    required this.totalCount,
    required this.dueCount,
    required this.completeCount,
    required this.activeFilterCount,
    required this.showFilter,
    required this.onToggleFilter,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingXS,
        vertical: AppDimens.paddingM,
      ),
      child: Row(
        children: [
          Expanded(
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _buildStatCard(
                  count: totalCount,
                  label: 'Total',
                  icon: Icons.menu_book,
                  color: colorScheme.primary,
                  theme: theme,
                ),
                _buildStatCard(
                  count: dueCount,
                  label: 'Due',
                  icon: Icons.access_time,
                  color: colorScheme.error,
                  theme: theme,
                  highlight: dueCount > 0,
                ),
                _buildStatCard(
                  count: completeCount,
                  label: 'Learned',
                  icon: Icons.check_circle_outline,
                  color: colorScheme.tertiary,
                  theme: theme,
                ),
              ],
            ),
          ),
          SizedBox(
            width: 32,
            child: IconButton(
              icon: Stack(
                children: [
                  Icon(
                    showFilter ? Icons.filter_list_off : Icons.filter_list,
                    color: activeFilterCount > 0
                        ? colorScheme.primary
                        : colorScheme.onSurfaceVariant,
                    size: AppDimens.iconL,
                  ),
                  if (activeFilterCount > 0)
                    Positioned(
                      right: 0,
                      top: 0,
                      child: Container(
                        padding: const EdgeInsets.all(AppDimens.paddingXXS),
                        decoration: BoxDecoration(
                          color: colorScheme.primary,
                          shape: BoxShape.circle,
                        ),
                        constraints: const BoxConstraints(
                          minWidth: AppDimens.badgeIconPadding * 2 + 6,
                          minHeight: AppDimens.badgeIconPadding * 2 + 6,
                        ),
                        child: Text(
                          '$activeFilterCount',
                          style: theme.textTheme.labelSmall?.copyWith(
                            color: colorScheme.onPrimary,
                            fontSize: AppDimens.fontXXS,
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ),
                    ),
                ],
              ),
              tooltip: showFilter ? 'Hide filters' : 'Show filters',
              padding: const EdgeInsets.all(AppDimens.paddingXS),
              constraints: const BoxConstraints(),
              visualDensity: VisualDensity.compact,
              onPressed: onToggleFilter,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStatCard({
    required int count,
    required String label,
    required IconData icon,
    required Color color,
    required ThemeData theme,
    bool highlight = false,
  }) {
    final colorScheme = theme.colorScheme;
    final brightness = highlight ? 1.0 : 0.8;

    return Expanded(
      child: Card(
        elevation: 0,
        margin: const EdgeInsets.symmetric(horizontal: AppDimens.paddingXS),
        color: color.withValues(alpha: AppDimens.opacityLight),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppDimens.radiusM),
        ),
        child: Padding(
          padding: const EdgeInsets.symmetric(
            horizontal: AppDimens.paddingM,
            vertical: AppDimens.paddingS,
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    icon,
                    size: AppDimens.iconS,
                    color: color.withValues(alpha: brightness),
                  ),
                  const SizedBox(width: AppDimens.spaceXS),
                  Text(
                    label,
                    style: theme.textTheme.labelMedium?.copyWith(
                      color: colorScheme.onSurface.withValues(
                        alpha: AppDimens.opacityHigh,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: AppDimens.spaceXS),
              Text(
                count.toString(),
                style: theme.textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: color.withValues(alpha: brightness),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
