// lib/presentation/widgets/progress/due_progress_summary.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/presentation/viewmodels/progress_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';

class DueProgressSummary extends ConsumerWidget {
  final VoidCallback onRefresh;

  const DueProgressSummary({super.key, required this.onRefresh});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final progressAsync = ref.watch(progressStateProvider);
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return progressAsync.when(
      data: (progressRecords) {
        if (progressRecords.isEmpty && progressAsync.isLoading) {
          return const SizedBox.shrink();
        }

        if (progressAsync.hasError) {
          return const SizedBox.shrink();
        }

        final totalCount = progressRecords.length;
        final dueCount = progressRecords.where(_isDue).length;
        final completedPercent = totalCount == 0
            ? 0
            : ((totalCount - dueCount) / totalCount * 100).toInt();

        return Padding(
          padding: const EdgeInsets.only(bottom: 16.0),
          child: Card(
            elevation: 0,
            color: colorScheme.surfaceContainerLowest,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
              side: BorderSide(
                color: colorScheme.outlineVariant.withValues(alpha: 0.5),
                width: 1,
              ),
            ),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Row(
                children: [
                  _buildStatItem(
                    theme,
                    'Total',
                    totalCount.toString(),
                    Icons.book,
                    colorScheme.primary,
                  ),
                  const SizedBox(width: 8),
                  _buildStatItem(
                    theme,
                    'Due',
                    dueCount.toString(),
                    Icons.event_available,
                    dueCount > 0
                        ? colorScheme.error
                        : colorScheme.onSurfaceVariant,
                  ),
                  const SizedBox(width: 8),
                  _buildStatItem(
                    theme,
                    'Completed',
                    '$completedPercent%',
                    Icons.task_alt,
                    colorScheme.tertiary,
                  ),
                ],
              ),
            ),
          ),
        );
      },
      loading: () => const Center(child: SLLoadingIndicator()),
      error: (_, __) => const SizedBox.shrink(),
    );
  }

  bool _isDue(ProgressDetail progress) {
    if (progress.nextStudyDate == null) return false;

    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final nextDate = DateTime(
      progress.nextStudyDate!.year,
      progress.nextStudyDate!.month,
      progress.nextStudyDate!.day,
    );

    return nextDate.compareTo(today) <= 0;
  }

  Widget _buildStatItem(
    ThemeData theme,
    String label,
    String value,
    IconData icon,
    Color color,
  ) => Expanded(
    child: Column(
      children: [
        Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 16, color: color),
            const SizedBox(width: 4),
            Text(
              label,
              style: theme.textTheme.labelMedium?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
            ),
          ],
        ),
        const SizedBox(height: 4),
        Text(
          value,
          style: theme.textTheme.titleLarge?.copyWith(
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
      ],
    ),
  );
}
