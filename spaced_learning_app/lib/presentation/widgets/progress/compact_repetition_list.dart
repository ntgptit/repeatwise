import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/extensions/color_extensions.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/domain/models/repetition.dart';
import 'package:spaced_learning_app/presentation/utils/repetition_utils.dart';
import 'package:spaced_learning_app/presentation/viewmodels/repetition_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/status_section.dart';

import '../../../core/theme/app_dimens.dart';

class CompactRepetitionList extends ConsumerWidget {
  final String progressId;
  final CycleStudied currentCycleStudied;
  final Future<void> Function(String) onMarkCompleted;
  final Future<void> Function(String, DateTime, bool) onReschedule;
  final Future<void> Function() onReload;

  const CompactRepetitionList({
    super.key,
    required this.progressId,
    required this.currentCycleStudied,
    required this.onMarkCompleted,
    required this.onReschedule,
    required this.onReload,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final repetitionsState = ref.watch(repetitionStateProvider);
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return repetitionsState.when(
      data: (repetitions) {
        if (repetitions.isEmpty) {
          return _buildEmptyState(context, ref, colorScheme);
        }

        return _buildContent(context, repetitions, colorScheme);
      },
      loading: () => _buildLoadingState(theme, colorScheme),
      error: (error, stackTrace) => SLErrorView(
        message: error.toString(),
        onRetry: () => ref
            .read(repetitionStateProvider.notifier)
            .loadRepetitionsByProgressId(progressId),
        compact: true,
      ),
    );
  }

  Widget _buildContent(
    BuildContext context,
    List<Repetition> repetitions,
    ColorScheme colorScheme,
  ) {
    // Categorize repetitions by status
    final notStarted =
        repetitions
            .where((r) => r.status == RepetitionStatus.notStarted)
            .toList()
          ..sort(
            (a, b) =>
                a.repetitionOrder.index.compareTo(b.repetitionOrder.index),
          );

    final completed =
        repetitions
            .where((r) => r.status == RepetitionStatus.completed)
            .toList()
          ..sort(RepetitionUtils.compareReviewDates);

    // Group by cycle
    final notStartedByCycle = RepetitionUtils.groupByCycle(notStarted);
    final completedByCycle = RepetitionUtils.groupByCycle(completed);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (notStarted.isNotEmpty)
          StatusSection(
            title: 'Pending Tasks',
            icon: Icons.assignment_outlined,
            containerColor: colorScheme.primary,
            textColor: colorScheme.primary,
            cycleGroups: notStartedByCycle,
            isHistory: false,
            currentCycleStudied: currentCycleStudied,
            onMarkCompleted: onMarkCompleted,
            onReschedule: onReschedule,
          ),

        if (completed.isNotEmpty)
          StatusSection(
            title: 'Completed Tasks',
            icon: Icons.check_circle_outline,
            containerColor: colorScheme.success,
            textColor: colorScheme.success,
            cycleGroups: completedByCycle,
            isHistory: true,
            currentCycleStudied: currentCycleStudied,
            onMarkCompleted: null,
            onReschedule: null,
          ),
      ],
    );
  }

  Widget _buildLoadingState(ThemeData theme, ColorScheme colorScheme) {
    return Container(
      height: 160,
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerLow,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            CircularProgressIndicator(color: colorScheme.primary),
            const SizedBox(height: 12),
            Text(
              'Loading repetitions...',
              style: theme.textTheme.bodyMedium?.copyWith(
                color: colorScheme.onSurface,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEmptyState(
    BuildContext context,
    WidgetRef ref,
    ColorScheme colorScheme,
  ) {
    final theme = Theme.of(context);

    return Container(
      padding: const EdgeInsets.all(AppDimens.paddingXL),
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerLow,
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
        border: Border.all(color: colorScheme.outlineVariant, width: 1.5),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            padding: const EdgeInsets.all(AppDimens.paddingL),
            decoration: BoxDecoration(
              color: colorScheme.primary.withValues(alpha: 0.1),
              shape: BoxShape.circle,
            ),
            child: Icon(
              Icons.schedule,
              size: 64,
              color: colorScheme.primary.withValues(alpha: 0.7),
            ),
          ),
          const SizedBox(height: AppDimens.spaceL),
          Text(
            'No Review Schedule Yet',
            style: theme.textTheme.headlineSmall?.copyWith(
              fontWeight: FontWeight.bold,
              color: colorScheme.onSurface,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: AppDimens.spaceM),
          Text(
            'Create a review schedule to start the spaced repetition learning process',
            style: theme.textTheme.bodyLarge?.copyWith(
              color: colorScheme.onSurfaceVariant,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: AppDimens.spaceXL),
          SLButton(
            text: 'Create Review Schedule',
            type: SLButtonType.primary,
            prefixIcon: Icons.add_circle_outline,
            onPressed: () async {
              await ref
                  .read(repetitionStateProvider.notifier)
                  .createDefaultSchedule(progressId);
              await onReload();
            },
          ),
        ],
      ),
    );
  }
}
