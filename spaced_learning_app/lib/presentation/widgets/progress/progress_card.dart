// lib/presentation/widgets/progress/progress_card.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart'; // For getProgressColor
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_card.dart'; // Using SLCard

class ProgressCard extends ConsumerWidget {
  final ProgressDetail progress;
  final bool isDue; // Indicates if the progress item is due
  final String?
  subtitle; // Optional subtitle, e.g., original module title if different
  final VoidCallback? onTap;

  const ProgressCard({
    super.key,
    required this.progress,
    this.isDue = false,
    this.subtitle,
    this.onTap,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final textTheme = theme.textTheme;

    // Determine the color of the progress indicator based on completion percentage
    final progressIndicatorColor = theme.getProgressColor(
      progress.percentComplete,
    );
    // Determine the text color for due dates, highlighting if due
    final dateTextColor = isDue
        ? colorScheme.error
        : colorScheme.onSurfaceVariant;

    return SLCard(
      onTap: onTap,
      padding: const EdgeInsets.all(AppDimens.paddingL),
      backgroundColor: colorScheme.surfaceContainer,
      // M3 surface color
      elevation: AppDimens.elevationXS,
      // Subtle elevation
      margin: const EdgeInsets.symmetric(vertical: AppDimens.spaceS),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center, // Align items centrally
        children: [
          // Progress Indicator (Circular)
          SizedBox(
            width: AppDimens.circularProgressSizeL + AppDimens.paddingXS,
            // Slightly larger tap area
            height: AppDimens.circularProgressSizeL + AppDimens.paddingXS,
            child: Stack(
              alignment: Alignment.center,
              children: [
                CircularProgressIndicator(
                  value: progress.percentComplete / 100,
                  backgroundColor: colorScheme.surfaceContainerHighest
                      .withValues(alpha: 0.5),
                  // Softer background
                  strokeWidth: AppDimens.lineProgressHeight + 2,
                  // Thicker stroke
                  valueColor: AlwaysStoppedAnimation<Color>(
                    progressIndicatorColor,
                  ),
                  strokeCap: StrokeCap.round, // Rounded stroke caps
                ),
                Text(
                  '${progress.percentComplete.toInt()}%',
                  style: textTheme.labelSmall?.copyWith(
                    fontWeight: FontWeight.bold,
                    color:
                        progressIndicatorColor, // Text color matches progress
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(width: AppDimens.spaceL), // Increased spacing
          // Progress Details (Title, Subtitle, Dates)
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  progress.moduleTitle ?? 'Module ${progress.moduleId}',
                  style: textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.w600,
                    // Slightly less bold than header title
                    color: colorScheme.onSurface,
                  ),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
                // Display subtitle if provided
                if (subtitle != null && subtitle!.isNotEmpty) ...[
                  const SizedBox(height: AppDimens.spaceXXS),
                  Text(
                    subtitle!,
                    style: textTheme.bodySmall?.copyWith(
                      color: colorScheme.onSurfaceVariant,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
                const SizedBox(height: AppDimens.spaceS), // Consistent spacing
                // Next Study Date
                Row(
                  children: [
                    Icon(
                      isDue
                          ? Icons.notification_important_rounded
                          : Icons.event_note_outlined, // Contextual icon
                      size: AppDimens.iconXS,
                      color: dateTextColor,
                    ),
                    const SizedBox(width: AppDimens.spaceXS),
                    Text(
                      progress.nextStudyDate != null
                          ? 'Next: ${DateFormat.yMMMd().format(progress.nextStudyDate!)}'
                          : 'Next: Not scheduled',
                      style: textTheme.bodySmall?.copyWith(
                        color: dateTextColor,
                        fontWeight: isDue ? FontWeight.bold : null,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          // Chevron icon to indicate tappable item
          if (onTap != null)
            Icon(
              Icons.chevron_right_rounded, // Rounded icon
              color: colorScheme.onSurfaceVariant,
              size: AppDimens.iconM,
            ),
        ],
      ),
    );
  }
}
