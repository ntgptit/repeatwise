// lib/presentation/widgets/learning/learning_help_dialog.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/presentation/utils/cycle_formatter.dart';

class LearningHelpDialog extends ConsumerWidget {
  const LearningHelpDialog({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final size = MediaQuery.of(context).size;

    final bool isSmallScreen = size.width < AppDimens.breakpointS;
    final double maxDialogWidth = isSmallScreen ? size.width * 0.95 : 500.0;
    final double maxDialogHeight = size.height * 0.7;

    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
      ),
      elevation: AppDimens.elevationM,
      backgroundColor: colorScheme.surface,
      child: Container(
        constraints: BoxConstraints(
          maxWidth: maxDialogWidth,
          maxHeight: maxDialogHeight,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Dialog header
            Padding(
              padding: const EdgeInsets.all(AppDimens.paddingL),
              child: Row(
                children: [
                  Icon(
                    Icons.help_outline,
                    color: colorScheme.primary,
                    size: AppDimens.iconL,
                  ),
                  const SizedBox(width: AppDimens.spaceS),
                  Expanded(
                    child: Text(
                      'Learning Progress Help',
                      style: theme.textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.w600,
                        color: colorScheme.onSurface,
                      ),
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.close),
                    onPressed: () => Navigator.of(context).pop(),
                    tooltip: 'Close',
                    padding: EdgeInsets.zero,
                    color: colorScheme.onSurfaceVariant,
                  ),
                ],
              ),
            ),
            const Divider(height: AppDimens.dividerThickness),

            // Dialog content
            Flexible(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(AppDimens.paddingL),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'This screen shows your learning progress across all modules.',
                      style: theme.textTheme.bodyMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                        color: colorScheme.onSurface,
                      ),
                    ),
                    const SizedBox(height: AppDimens.spaceL),

                    _buildHelpSection(
                      context,
                      'Features',
                      [
                        '• Use the Book filter to view modules from a specific book',
                        '• Use the Date filter to see modules due on a specific date',
                        '• Click on any module to see detailed information',
                        '• The Progress column shows your completion percentage',
                        '• Due dates highlighted in red are approaching soon',
                        '• Export your data using the Export button in the footer',
                      ],
                      icon: Icons.list_alt,
                      iconColor: colorScheme.primary,
                    ),

                    const SizedBox(height: AppDimens.spaceL),

                    _buildHelpSection(
                      context,
                      'Learning Cycles',
                      [
                        for (final cycle in CycleStudied.values)
                          '• ${CycleFormatter.format(cycle)}: ${CycleFormatter.getDescription(cycle)}',
                      ],
                      icon: Icons.repeat,
                      iconColor: colorScheme.secondary,
                    ),

                    const SizedBox(height: AppDimens.spaceL),

                    _buildHelpSection(
                      context,
                      'Tips',
                      [
                        '• Review your learning material according to the spaced repetition schedule',
                        '• Focus on modules that are due soon',
                        '• Complete all repetitions in a cycle before moving to the next one',
                        '• Use the export feature to keep track of your progress over time',
                      ],
                      icon: Icons.lightbulb_outline,
                      iconColor: colorScheme.tertiary,
                    ),
                  ],
                ),
              ),
            ),

            // Dialog footer
            const Divider(height: AppDimens.dividerThickness),
            Padding(
              padding: const EdgeInsets.all(AppDimens.paddingM),
              child: TextButton(
                onPressed: () => Navigator.of(context).pop(),
                style: TextButton.styleFrom(
                  foregroundColor: colorScheme.primary,
                  padding: const EdgeInsets.symmetric(
                    horizontal: AppDimens.paddingL,
                    vertical: AppDimens.paddingM,
                  ),
                ),
                child: const Text('Close'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHelpSection(
    BuildContext context,
    String title,
    List<String> items, {
    IconData? icon,
    Color? iconColor,
  }) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final size = MediaQuery.of(context).size;
    final isCompact = size.width < AppDimens.breakpointXS;

    final effectiveIconColor = iconColor ?? colorScheme.primary;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            if (icon != null) ...[
              Icon(icon, size: AppDimens.iconM, color: effectiveIconColor),
              const SizedBox(width: AppDimens.spaceS),
            ],
            Text(
              title,
              style: theme.textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
                color: effectiveIconColor,
              ),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceXS),
        ...items.map(
          (item) => Padding(
            padding: EdgeInsets.only(
              bottom: isCompact ? AppDimens.spaceXXS : AppDimens.spaceXS,
              left: isCompact ? 0 : AppDimens.paddingXS,
            ),
            child: Text(
              item,
              style: isCompact
                  ? theme.textTheme.bodySmall?.copyWith(
                      color: colorScheme.onSurface.withValues(
                        alpha: AppDimens.opacityHigh,
                      ),
                    )
                  : theme.textTheme.bodyMedium?.copyWith(
                      color: colorScheme.onSurface,
                    ),
            ),
          ),
        ),
      ],
    );
  }
}
