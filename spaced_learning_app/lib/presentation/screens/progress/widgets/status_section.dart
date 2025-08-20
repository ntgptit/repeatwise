import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/domain/models/repetition.dart';
import 'package:spaced_learning_app/presentation/screens/progress/widgets/cycle_group_card.dart';

class StatusSection extends StatelessWidget {
  final String title;
  final IconData icon;
  final Color containerColor;
  final Color textColor;
  final Map<String, List<Repetition>> cycleGroups;
  final bool isHistory;
  final CycleStudied currentCycleStudied;
  final Future<void> Function(String)? onMarkCompleted;
  final Future<void> Function(String, DateTime, bool)? onReschedule;

  const StatusSection({
    super.key,
    required this.title,
    required this.icon,
    required this.containerColor,
    required this.textColor,
    required this.cycleGroups,
    required this.isHistory,
    required this.currentCycleStudied,
    this.onMarkCompleted,
    this.onReschedule,
  });

  @override
  Widget build(BuildContext context) {
    if (cycleGroups.isEmpty) return const SizedBox.shrink();

    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    final sortedKeys = _getSortedCycleKeys();
    final titleIconColor = isHistory
        ? colorScheme.primary.withValues(alpha: AppDimens.opacityVeryHigh)
        : textColor;

    return Padding(
      padding: const EdgeInsets.only(bottom: AppDimens.spaceXL),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildSectionHeader(theme, titleIconColor),
          const SizedBox(height: AppDimens.spaceM),
          ...sortedKeys.map(
            (key) => CycleGroupCard(
              cycleKey: key,
              repetitions: cycleGroups[key]!,
              isHistory: isHistory,
              currentCycleStudied: currentCycleStudied,
              onMarkCompleted: onMarkCompleted,
              onReschedule: onReschedule,
            ),
          ),
        ],
      ),
    );
  }

  List<String> _getSortedCycleKeys() {
    final keys = cycleGroups.keys.toList();
    if (isHistory) {
      keys.sort((a, b) {
        final aNum = int.tryParse(a.replaceAll(RegExp(r'\D'), '')) ?? 0;
        final bNum = int.tryParse(b.replaceAll(RegExp(r'\D'), '')) ?? 0;
        return bNum.compareTo(aNum);
      });
    }
    return keys;
  }

  Widget _buildSectionHeader(ThemeData theme, Color iconColor) {
    final totalTasks = _getTotalRepetitions();

    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(AppDimens.paddingM),
          decoration: BoxDecoration(
            color: containerColor.withValues(alpha: 0.1),
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
          ),
          child: Icon(icon, color: iconColor, size: AppDimens.iconM),
        ),
        const SizedBox(width: AppDimens.spaceM),
        Text(
          title,
          style: theme.textTheme.titleLarge?.copyWith(
            fontWeight: FontWeight.bold,
            color: isHistory
                ? theme.colorScheme.onSurface.withValues(
                    alpha: AppDimens.opacityVeryHigh,
                  )
                : textColor,
          ),
        ),
        const SizedBox(width: AppDimens.spaceS),
        if (!isHistory)
          Container(
            padding: const EdgeInsets.symmetric(
              horizontal: AppDimens.paddingS,
              vertical: AppDimens.paddingXXS,
            ),
            decoration: BoxDecoration(
              color: textColor.withValues(alpha: 0.1),
              borderRadius: BorderRadius.circular(AppDimens.radiusS),
            ),
            child: Text(
              '$totalTasks tasks',
              style: theme.textTheme.labelMedium?.copyWith(
                color: textColor,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
      ],
    );
  }

  int _getTotalRepetitions() =>
      cycleGroups.values.fold(0, (sum, reps) => sum + reps.length);
}
