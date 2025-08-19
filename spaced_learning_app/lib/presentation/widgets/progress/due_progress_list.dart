// lib/presentation/widgets/progress/due_progress_list.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/presentation/viewmodels/progress_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/due_progress_empty_state.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/due_progress_list_item.dart';

class DueProgressList extends ConsumerWidget {
  final DateTime? selectedDate;
  final bool isLoading;
  final AnimationController animationController;
  final Animation<double> fadeAnimation;
  final VoidCallback onRefresh;

  const DueProgressList({
    super.key,
    required this.selectedDate,
    required this.isLoading,
    required this.animationController,
    required this.fadeAnimation,
    required this.onRefresh,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final progressAsync = ref.watch(progressStateProvider);
    final theme = Theme.of(context);

    return progressAsync.when(
      data: (progressRecords) {
        if (progressRecords.isEmpty && progressAsync.isLoading) {
          return const Center(child: SLLoadingIndicator());
        }

        if (progressAsync.hasError) {
          return Center(
            child: SLErrorView(
              message: progressAsync.error.toString(),
              onRetry: onRefresh,
            ),
          );
        }

        if (isLoading) {
          return const Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                SLLoadingIndicator(),
                SizedBox(height: 16),
                Text('Loading progress information...'),
              ],
            ),
          );
        }

        if (progressRecords.isEmpty) {
          return DueProgressEmptyState(
            selectedDate: selectedDate,
            onRefresh: onRefresh,
          );
        }

        final today = DateTime.now();
        final todayProgressRecords = <ProgressDetail>[];
        final overdueProgressRecords = <ProgressDetail>[];
        final upcomingProgressRecords = <ProgressDetail>[];

        for (final progress in progressRecords) {
          if (progress.nextStudyDate == null) continue;

          final progressDate = DateTime(
            progress.nextStudyDate!.year,
            progress.nextStudyDate!.month,
            progress.nextStudyDate!.day,
          );
          final nowDate = DateTime(today.year, today.month, today.day);

          if (progressDate.isAtSameMomentAs(nowDate)) {
            todayProgressRecords.add(progress);
            continue;
          }
          if (progressDate.isBefore(nowDate)) {
            overdueProgressRecords.add(progress);
            continue;
          }
          upcomingProgressRecords.add(progress);
        }

        return FadeTransition(
          opacity: fadeAnimation,
          child: RefreshIndicator(
            onRefresh: () async => onRefresh(),
            child: ListView(
              padding: const EdgeInsets.only(bottom: 80),
              children: [
                if (overdueProgressRecords.isNotEmpty) ...[
                  _buildSectionHeader(
                    theme,
                    'Overdue',
                    Icons.warning_amber,
                    theme.colorScheme.error,
                  ),
                  ...overdueProgressRecords.map(
                    (progress) => DueProgressListItem(
                      progress: progress,
                      isItemDue: true,
                    ),
                  ),
                  const SizedBox(height: 24),
                ],
                if (todayProgressRecords.isNotEmpty) ...[
                  _buildSectionHeader(
                    theme,
                    'Due Today',
                    Icons.today,
                    theme.colorScheme.tertiary,
                  ),
                  ...todayProgressRecords.map(
                    (progress) => DueProgressListItem(
                      progress: progress,
                      isItemDue: true,
                    ),
                  ),
                  const SizedBox(height: 24),
                ],
                if (upcomingProgressRecords.isNotEmpty &&
                    selectedDate != null) ...[
                  _buildSectionHeader(
                    theme,
                    'Upcoming',
                    Icons.event,
                    theme.colorScheme.primary,
                  ),
                  ...upcomingProgressRecords.map(
                    (progress) => DueProgressListItem(
                      progress: progress,
                      isItemDue: false,
                    ),
                  ),
                ],
                if (todayProgressRecords.isEmpty &&
                    overdueProgressRecords.isEmpty &&
                    upcomingProgressRecords.isEmpty)
                  DueProgressEmptyState(
                    selectedDate: selectedDate,
                    onRefresh: onRefresh,
                  ),
              ],
            ),
          ),
        );
      },
      loading: () => const Center(child: SLLoadingIndicator()),
      error: (error, _) => Center(
        child: SLErrorView(message: error.toString(), onRetry: onRefresh),
      ),
    );
  }

  Widget _buildSectionHeader(
    ThemeData theme,
    String title,
    IconData icon,
    Color color,
  ) => Padding(
    padding: const EdgeInsets.only(bottom: 8.0, left: 4.0),
    child: Row(
      children: [
        Icon(icon, size: 20, color: color),
        const SizedBox(width: 8),
        Text(
          title,
          style: theme.textTheme.titleMedium?.copyWith(
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
      ],
    ),
  );
}
