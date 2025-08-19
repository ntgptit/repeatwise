// lib/presentation/widgets/progress/due_progress_list_item.dart
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/presentation/utils/cycle_formatter.dart';

import '../../../core/navigation/navigation_helper.dart';

class DueProgressListItem extends StatelessWidget {
  final ProgressDetail progress;
  final bool isItemDue;

  const DueProgressListItem({
    super.key,
    required this.progress,
    required this.isItemDue,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final dateFormat = DateFormat('MMM dd, yyyy');

    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(
          color: colorScheme.outlineVariant.withValues(alpha: 0.5),
          width: 1,
        ),
      ),
      child: InkWell(
        onTap: () {
          final String progressId = progress.id;
          debugPrint('Navigating to progress with ID: $progressId');

          if (progressId.isEmpty) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Invalid progress ID')),
            );
            return;
          }

          // Use NavigationHelper.pushWithResult to get result and refresh
          NavigationHelper.pushWithResult(
            context,
            '/progress/$progressId',
          ).then((_) => {}); // Auto-refresh when returning
        },
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  SizedBox(
                    width: 48,
                    height: 48,
                    child: Stack(
                      alignment: Alignment.center,
                      children: [
                        CircularProgressIndicator(
                          value: progress.percentComplete / 100,
                          backgroundColor: colorScheme.surfaceContainerHighest,
                          valueColor: AlwaysStoppedAnimation<Color>(
                            theme.getProgressColor(progress.percentComplete),
                          ),
                          strokeWidth: 4,
                        ),
                        Text(
                          '${progress.percentComplete.toInt()}%',
                          style: theme.textTheme.labelSmall?.copyWith(
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          progress.moduleTitle ?? 'Module ${progress.moduleId}',
                          style: theme.textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.bold,
                          ),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                        const SizedBox(height: 4),
                        Text(
                          'Cycle: ${CycleFormatter.getDisplayName(progress.cyclesStudied)}',
                          style: theme.textTheme.bodyMedium?.copyWith(
                            color: theme.getCycleColor(progress.cyclesStudied),
                          ),
                        ),
                        const SizedBox(height: 4),
                        Row(
                          children: [
                            Icon(
                              isItemDue ? Icons.event_available : Icons.event,
                              size: 16,
                              color: isItemDue
                                  ? colorScheme.error
                                  : colorScheme.onSurfaceVariant,
                            ),
                            const SizedBox(width: 4),
                            Text(
                              progress.nextStudyDate != null
                                  ? dateFormat.format(progress.nextStudyDate!)
                                  : 'Not scheduled',
                              style: theme.textTheme.bodySmall?.copyWith(
                                color: isItemDue
                                    ? colorScheme.error
                                    : colorScheme.onSurfaceVariant,
                                fontWeight: isItemDue ? FontWeight.bold : null,
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                  if (isItemDue)
                    Icon(
                      Icons.notifications_active,
                      color: colorScheme.error,
                      size: 20,
                    ),
                ],
              ),
              if (progress.repetitions.isNotEmpty) ...[
                const SizedBox(height: 8),
                Chip(
                  label: Text('${progress.repetitions.length} repetitions'),
                  backgroundColor: colorScheme.surfaceContainerHigh,
                  side: BorderSide.none,
                  labelStyle: theme.textTheme.bodySmall?.copyWith(
                    color: colorScheme.onSurfaceVariant,
                  ),
                  padding: EdgeInsets.zero,
                  visualDensity: VisualDensity.compact,
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
