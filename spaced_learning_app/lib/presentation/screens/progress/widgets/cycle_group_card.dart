import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/extensions/color_extensions.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/domain/models/repetition.dart';
import 'package:spaced_learning_app/presentation/utils/cycle_formatter.dart';
import 'package:spaced_learning_app/presentation/utils/repetition_utils.dart';
import 'package:spaced_learning_app/presentation/screens/progress/widgets/repetition_card.dart';

class CycleGroupCard extends StatelessWidget {
  final String cycleKey;
  final List<Repetition> repetitions;
  final bool isHistory;
  final CycleStudied currentCycleStudied;
  final Future<void> Function(String)? onMarkCompleted;
  final Future<void> Function(String, DateTime, bool)? onReschedule;

  const CycleGroupCard({
    super.key,
    required this.cycleKey,
    required this.repetitions,
    required this.isHistory,
    required this.currentCycleStudied,
    this.onMarkCompleted,
    this.onReschedule,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    final isCurrentCycle = RepetitionUtils.isCurrentCycle(
      repetitions,
      currentCycleStudied,
    );
    final cycleNumber = int.tryParse(cycleKey.replaceAll('Cycle ', '')) ?? 1;
    final cycleName = isCurrentCycle
        ? currentCycleStudied
        : CycleFormatter.mapNumberToCycleStudied(cycleNumber);

    // Get cycle color from formatter
    final baseCycleColor = CycleFormatter.getColor(cycleName, context);

    // Adjust color based on status
    final cycleColor = isHistory
        ? baseCycleColor.withValues(alpha: AppDimens.opacityVeryHigh)
        : isCurrentCycle
        ? baseCycleColor
        : baseCycleColor.withValues(alpha: AppDimens.opacityVeryHigh);

    if (!isHistory) {
      repetitions.sort(
        (a, b) => a.repetitionOrder.index.compareTo(b.repetitionOrder.index),
      );
    }

    // Setup border and background colors for Material 3
    final borderColor = isHistory
        ? cycleColor.withValues(alpha: AppDimens.opacitySemi)
        : isCurrentCycle
        ? cycleColor
        : cycleColor.withValues(alpha: AppDimens.opacitySemi);

    final backgroundColor = isHistory
        ? colorScheme.surfaceContainerLowest
        : isCurrentCycle
        ? cycleColor.withValues(alpha: 0.05)
        : colorScheme.surfaceContainerLowest;

    return Padding(
      padding: const EdgeInsets.only(bottom: AppDimens.spaceM),
      child: Card(
        elevation: 0,
        margin: EdgeInsets.zero,
        color: backgroundColor,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppDimens.radiusM),
          side: BorderSide(
            color: borderColor,
            width: isCurrentCycle ? 1.5 : 1.0,
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(AppDimens.paddingM),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildCycleHeader(
                theme,
                isHistory,
                isCurrentCycle,
                cycleColor,
                cycleName,
                colorScheme,
              ),
              Divider(
                height: AppDimens.spaceL,
                thickness: 1,
                color: borderColor.withValues(alpha: 0.3),
              ),
              ...repetitions.map(
                (repetition) => RepetitionCard(
                  repetition: repetition,
                  isHistory: isHistory,
                  onMarkCompleted: isHistory
                      ? null
                      : () => onMarkCompleted?.call(repetition.id),
                  onReschedule: isHistory
                      ? null
                      : (currentDate) => onReschedule?.call(
                          repetition.id,
                          currentDate,
                          false,
                        ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildCycleHeader(
    ThemeData theme,
    bool isHistory,
    bool isCurrentCycle,
    Color cycleColor,
    CycleStudied cycleName,
    ColorScheme colorScheme,
  ) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(AppDimens.paddingXS),
          decoration: BoxDecoration(
            color: cycleColor.withValues(alpha: 0.1),
            shape: BoxShape.circle,
          ),
          child: Icon(
            CycleFormatter.getIcon(cycleName),
            size: AppDimens.iconM,
            color: cycleColor,
          ),
        ),
        const SizedBox(width: AppDimens.spaceS),
        Text(
          CycleFormatter.format(cycleName),
          style: theme.textTheme.titleMedium?.copyWith(
            fontWeight: isCurrentCycle ? FontWeight.bold : FontWeight.w500,
            color: cycleColor,
          ),
        ),
        const SizedBox(width: AppDimens.spaceM),
        if (isCurrentCycle) _buildCurrentCycleBadge(colorScheme),
        if (isHistory) _buildCompletedBadge(colorScheme),
      ],
    );
  }

  Widget _buildCurrentCycleBadge(ColorScheme colorScheme) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingM,
        vertical: AppDimens.paddingXXS,
      ),
      decoration: BoxDecoration(
        color: Colors.amber,
        borderRadius: BorderRadius.circular(AppDimens.radiusXL),
      ),
      child: const Text(
        'CURRENT',
        style: TextStyle(
          color: Colors.black,
          fontWeight: FontWeight.bold,
          fontSize: 12,
        ),
      ),
    );
  }

  Widget _buildCompletedBadge(ColorScheme colorScheme) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingM,
        vertical: AppDimens.paddingXXS,
      ),
      decoration: BoxDecoration(
        color: colorScheme.success,
        borderRadius: BorderRadius.circular(AppDimens.radiusXL),
      ),
      child: Text(
        'COMPLETED',
        style: TextStyle(
          color: colorScheme.onSuccess,
          fontWeight: FontWeight.bold,
          fontSize: 12,
        ),
      ),
    );
  }
}
