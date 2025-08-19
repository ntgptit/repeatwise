// lib/presentation/widgets/learning/main/module_card.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/extensions/color_extensions.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/learning_module.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/presentation/widgets/learning/module_details_bottom_sheet.dart';

class ModuleCard extends ConsumerWidget {
  final LearningModule module;
  final int index;

  const ModuleCard({super.key, required this.module, required this.index});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    final hasNextStudyDate = module.progressNextStudyDate != null;
    final isOverdue =
        hasNextStudyDate &&
        module.progressNextStudyDate!.isBefore(DateTime.now()) &&
        !DateUtils.isSameDay(module.progressNextStudyDate, DateTime.now());
    final isDueToday =
        hasNextStudyDate &&
        DateUtils.isSameDay(module.progressNextStudyDate, DateTime.now());
    final isDueSoon =
        hasNextStudyDate &&
        module.progressNextStudyDate!.isAfter(DateTime.now()) &&
        module.progressNextStudyDate!.difference(DateTime.now()).inDays <= 2;
    final hasCycleInfo = module.progressCyclesStudied != null;

    const double minCardHeight = 120;

    Color statusColor = colorScheme.primary;
    if (isOverdue) {
      statusColor = colorScheme.error;
    } else if (isDueToday) {
      statusColor = colorScheme.tertiary;
    } else if (isDueSoon) {
      statusColor = colorScheme.warning;
    }

    Color? cycleColor;
    String? cycleText;
    if (hasCycleInfo) {
      cycleText = _formatCycleStudied(module.progressCyclesStudied!);
      cycleColor = _getCycleColor(module.progressCyclesStudied!, colorScheme);
    }

    final dateFormatter = DateFormat('MMM dd, yyyy');

    return ConstrainedBox(
      constraints: const BoxConstraints(minHeight: minCardHeight),
      child: Card(
        margin: const EdgeInsets.only(bottom: AppDimens.spaceS),
        elevation: AppDimens.elevationXS,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppDimens.radiusM),
          side: BorderSide(
            color: colorScheme.outlineVariant.withValues(
              alpha: AppDimens.opacityMedium,
            ),
            width: 1,
          ),
        ),
        child: InkWell(
          onTap: () => _showModuleDetails(context, module),
          borderRadius: BorderRadius.circular(AppDimens.radiusM),
          child: Padding(
            padding: const EdgeInsets.all(AppDimens.paddingL),
            child: IntrinsicHeight(
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(
                    flex: 6,
                    child: Container(
                      padding: const EdgeInsets.only(right: AppDimens.paddingS),
                      child: _buildModuleInfo(
                        theme,
                        colorScheme,
                        cycleText,
                        cycleColor,
                      ),
                    ),
                  ),
                  Expanded(
                    flex: 5,
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: AppDimens.paddingXS,
                      ),
                      child: _buildNextStudyInfo(
                        theme,
                        colorScheme,
                        statusColor,
                        dateFormatter,
                        isOverdue,
                        isDueToday,
                        isDueSoon,
                      ),
                    ),
                  ),
                  Expanded(flex: 2, child: _buildTasksInfo(theme, colorScheme)),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildModuleInfo(
    ThemeData theme,
    ColorScheme colorScheme,
    String? cycleText,
    Color? cycleColor,
  ) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: double.infinity),
          child: Text(
            module.moduleTitle.isEmpty ? 'Unnamed Module' : module.moduleTitle,
            style: theme.textTheme.titleSmall?.copyWith(
              fontWeight: FontWeight.w600,
              color: colorScheme.onSurface,
            ),
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
        ),
        const SizedBox(height: AppDimens.spaceXS),
        Row(
          children: [
            Icon(
              Icons.menu_book,
              size: AppDimens.iconXS,
              color: colorScheme.onSurfaceVariant,
            ),
            const SizedBox(width: AppDimens.spaceXS),
            Expanded(
              child: Text(
                module.bookName.isEmpty ? 'No book assigned' : module.bookName,
                style: theme.textTheme.bodySmall?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ),
          ],
        ),
        if (cycleText != null) ...[
          const SizedBox(height: AppDimens.spaceS),
          Container(
            padding: const EdgeInsets.symmetric(
              horizontal: AppDimens.paddingS,
              vertical: AppDimens.paddingXXS,
            ),
            decoration: BoxDecoration(
              color:
                  cycleColor?.withValues(alpha: AppDimens.opacityLight) ??
                  colorScheme.secondaryContainer,
              borderRadius: BorderRadius.circular(AppDimens.radiusXS),
            ),
            child: Text(
              cycleText,
              style: theme.textTheme.bodySmall?.copyWith(
                color: cycleColor ?? colorScheme.secondary,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ],
      ],
    );
  }

  Widget _buildNextStudyInfo(
    ThemeData theme,
    ColorScheme colorScheme,
    Color statusColor,
    DateFormat dateFormatter,
    bool isOverdue,
    bool isDueToday,
    bool isDueSoon,
  ) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (module.progressNextStudyDate == null)
          Text(
            'Not scheduled',
            style: theme.textTheme.bodyMedium?.copyWith(
              color: colorScheme.onSurfaceVariant,
              fontStyle: FontStyle.italic,
            ),
          )
        else
          Row(
            children: [
              Icon(
                isOverdue
                    ? Icons.event_busy
                    : (isDueToday ? Icons.event_available : Icons.event),
                size: AppDimens.iconS,
                color: statusColor,
              ),
              const SizedBox(width: AppDimens.spaceXS),
              Expanded(
                child: Text(
                  dateFormatter.format(module.progressNextStudyDate!),
                  style: theme.textTheme.bodyMedium?.copyWith(
                    color: statusColor,
                    fontWeight: isOverdue || isDueToday
                        ? FontWeight.bold
                        : null,
                  ),
                ),
              ),
            ],
          ),
        const SizedBox(height: AppDimens.spaceXS),
        if (isOverdue || isDueToday || isDueSoon)
          Container(
            padding: const EdgeInsets.symmetric(
              horizontal: AppDimens.paddingS,
              vertical: AppDimens.paddingXXS,
            ),
            decoration: BoxDecoration(
              color: statusColor.withValues(alpha: AppDimens.opacityLight),
              borderRadius: BorderRadius.circular(AppDimens.radiusXS),
            ),
            child: Text(
              isOverdue ? 'Overdue' : (isDueToday ? 'Due today' : 'Due soon'),
              style: theme.textTheme.bodySmall?.copyWith(
                color: statusColor,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
      ],
    );
  }

  Widget _buildTasksInfo(ThemeData theme, ColorScheme colorScheme) {
    return Center(
      child: Container(
        width: AppDimens.moduleIndicatorSize + AppDimens.paddingS,
        height: AppDimens.moduleIndicatorSize + AppDimens.paddingS,
        decoration: BoxDecoration(
          color: module.progressDueTaskCount > 0
              ? colorScheme.primaryContainer
              : colorScheme.surfaceContainerHigh,
          shape: BoxShape.circle,
        ),
        child: Center(
          child: Text(
            module.progressDueTaskCount.toString(),
            style: theme.textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.bold,
              color: module.progressDueTaskCount > 0
                  ? colorScheme.onPrimaryContainer
                  : colorScheme.onSurfaceVariant,
            ),
          ),
        ),
      ),
    );
  }

  void _showModuleDetails(BuildContext context, LearningModule module) {
    if (module.moduleId.isEmpty) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Invalid module ID')));
      return;
    }
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => ModuleDetailsBottomSheet(
        module: module,
        heroTagPrefix: 'learning_list',
      ),
    );
  }

  String _formatCycleStudied(CycleStudied cycle) {
    switch (cycle) {
      case CycleStudied.firstTime:
        return 'First Time';
      case CycleStudied.firstReview:
        return 'First Review';
      case CycleStudied.secondReview:
        return 'Second Review';
      case CycleStudied.thirdReview:
        return 'Third Review';
      case CycleStudied.moreThanThreeReviews:
        return 'Advanced Review';
    }
  }

  Color _getCycleColor(CycleStudied cycle, ColorScheme colorScheme) {
    switch (cycle) {
      case CycleStudied.firstTime:
        return colorScheme.primary;
      case CycleStudied.firstReview:
        return colorScheme.secondary;
      case CycleStudied.secondReview:
        return colorScheme.tertiary;
      case CycleStudied.thirdReview:
        return colorScheme.warning;
      case CycleStudied.moreThanThreeReviews:
        return Colors.deepPurple;
    }
  }
}
