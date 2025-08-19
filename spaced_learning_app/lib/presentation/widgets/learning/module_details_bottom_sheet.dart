// lib/presentation/widgets/learning/module_details_bottom_sheet.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/learning_module.dart';
import 'package:spaced_learning_app/presentation/screens/modules/module_detail_screen.dart';
import 'package:spaced_learning_app/presentation/utils/cycle_formatter.dart';

class ModuleDetailsBottomSheet extends ConsumerWidget {
  final LearningModule module;
  final String? heroTagPrefix;

  const ModuleDetailsBottomSheet({
    super.key,
    required this.module,
    this.heroTagPrefix,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final textTheme = theme.textTheme;
    final mediaQuery = MediaQuery.of(context);

    return Container(
      key: const Key('module_details_bottom_sheet'),
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerLow,
        borderRadius: const BorderRadius.vertical(
          top: Radius.circular(AppDimens.radiusL),
        ),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          _buildDragHandle(colorScheme),
          Flexible(
            child: SingleChildScrollView(
              padding: EdgeInsets.only(
                left: AppDimens.paddingL,
                right: AppDimens.paddingL,
                bottom: AppDimens.paddingXL + mediaQuery.padding.bottom,
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildModuleTitle(colorScheme, textTheme),
                  _buildBookInfo(colorScheme, textTheme),
                  const Divider(height: AppDimens.paddingXXL),
                  _buildModuleDetailsSection(context, colorScheme, textTheme),
                  const Divider(height: AppDimens.paddingXXL),
                  _buildDatesSection(colorScheme, textTheme),
                  if (module.studyHistory.isNotEmpty) ...[
                    const SizedBox(height: AppDimens.spaceXL),
                    _buildStudyHistorySection(colorScheme, textTheme),
                  ],
                  const SizedBox(height: AppDimens.spaceXL),
                  _buildActionButtons(context),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDragHandle(ColorScheme colorScheme) {
    return Padding(
      padding: const EdgeInsets.only(
        top: AppDimens.paddingM,
        bottom: AppDimens.paddingS,
      ),
      child: Container(
        key: const Key('drag_handle'),
        width: AppDimens.moduleIndicatorSize,
        height: AppDimens.dividerThickness * 2,
        decoration: BoxDecoration(
          color: colorScheme.onSurfaceVariant.withValues(
            alpha: AppDimens.opacitySemi,
          ),
          borderRadius: BorderRadius.circular(AppDimens.radiusS),
        ),
      ),
    );
  }

  Widget _buildModuleTitle(ColorScheme colorScheme, TextTheme textTheme) {
    Widget titleWidget = Text(
      module.moduleTitle.isEmpty ? 'Unnamed Module' : module.moduleTitle,
      style: textTheme.headlineSmall?.copyWith(color: colorScheme.onSurface),
      key: const Key('module_title'),
    );

    if (heroTagPrefix != null) {
      titleWidget = Hero(
        tag:
            '${heroTagPrefix}_${module.bookNo}_${module.moduleNo}_${module.moduleTitle}',
        child: Material(color: Colors.transparent, child: titleWidget),
      );
    }

    return Padding(
      padding: const EdgeInsets.only(bottom: AppDimens.paddingXS),
      child: titleWidget,
    );
  }

  Widget _buildBookInfo(ColorScheme colorScheme, TextTheme textTheme) {
    return Padding(
      padding: const EdgeInsets.only(
        top: AppDimens.paddingS,
        bottom: AppDimens.paddingM,
      ),
      child: Row(
        children: [
          Icon(
            Icons.book_outlined,
            color: colorScheme.primary,
            size: AppDimens.iconS,
            key: const Key('book_icon'),
          ),
          const SizedBox(width: AppDimens.spaceS),
          Expanded(
            child: Text(
              'From: ${module.bookName.isEmpty ? 'No Book' : module.bookName}',
              style: textTheme.titleMedium?.copyWith(
                color: colorScheme.primary,
              ),
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              key: const Key('book_info'),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSectionTitle(
    ColorScheme colorScheme,
    TextTheme textTheme,
    String title, {
    IconData icon = Icons.info_outline,
    Key? key,
  }) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: AppDimens.paddingM),
      child: Row(
        children: [
          Icon(icon, size: AppDimens.iconM, color: colorScheme.primary),
          const SizedBox(width: AppDimens.spaceS),
          Text(
            title,
            style: textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.bold,
              color: colorScheme.primary,
            ),
            key: key,
          ),
        ],
      ),
    );
  }

  Widget _buildModuleDetailsSection(
    BuildContext context,
    ColorScheme colorScheme,
    TextTheme textTheme,
  ) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle(
          colorScheme,
          textTheme,
          'Module Details',
          key: const Key('details_section_title'),
        ),
        Wrap(
          spacing: AppDimens.spaceL,
          runSpacing: AppDimens.spaceL,
          children: [
            _buildDetailItem(
              context,
              colorScheme,
              textTheme,
              'Word Count',
              module.moduleWordCount.toString(),
              Icons.text_fields,
              key: const Key('word_count_item'),
            ),
            if (module.progressLatestPercentComplete != null)
              _buildDetailItem(
                context,
                colorScheme,
                textTheme,
                'Progress',
                '${module.progressLatestPercentComplete}%',
                Icons.show_chart_outlined,
                progressValue: module.progressLatestPercentComplete! / 100.0,
                key: const Key('progress_item'),
              ),
            if (module.progressCyclesStudied != null)
              _buildDetailItem(
                context,
                colorScheme,
                textTheme,
                'Cycle',
                CycleFormatter.format(module.progressCyclesStudied!),
                Icons.autorenew,
                color: CycleFormatter.getColor(
                  module.progressCyclesStudied!,
                  context,
                ),
                key: const Key('cycle_item'),
              ),
            if (module.progressDueTaskCount > 0)
              _buildDetailItem(
                context,
                colorScheme,
                textTheme,
                'Tasks',
                module.progressDueTaskCount.toString(),
                Icons.checklist_outlined,
                key: const Key('tasks_item'),
              ),
          ],
        ),
      ],
    );
  }

  Widget _buildDetailItem(
    BuildContext context,
    ColorScheme colorScheme,
    TextTheme textTheme,
    String label,
    String value,
    IconData icon, {
    double? progressValue,
    Color? color,
    Key? key,
  }) {
    final effectiveColor = color ?? colorScheme.primary;
    final bgColor = colorScheme.surfaceContainerLowest;
    final borderColor = colorScheme.outlineVariant;

    final screenWidth = MediaQuery.of(context).size.width;
    final availableWidth =
        screenWidth - (AppDimens.paddingL * 2) - AppDimens.spaceL;
    final itemWidth = availableWidth / 2;

    Color progressIndicatorColor = colorScheme.error;
    if (progressValue != null) {
      if (progressValue >= 0.9) {
        progressIndicatorColor = colorScheme.tertiary;
      } else if (progressValue >= 0.7) {
        progressIndicatorColor = colorScheme.secondary;
      }
    }

    return Container(
      key: key,
      width: itemWidth,
      constraints: const BoxConstraints(minHeight: AppDimens.thumbnailSizeS),
      padding: const EdgeInsets.all(AppDimens.paddingM),
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
        border: Border.all(color: borderColor),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Row(
            children: [
              Icon(icon, size: AppDimens.iconS, color: effectiveColor),
              const SizedBox(width: AppDimens.spaceS),
              Expanded(
                child: Text(
                  label,
                  style: textTheme.bodyMedium?.copyWith(
                    color: colorScheme.onSurfaceVariant,
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
          const SizedBox(height: AppDimens.spaceXXS),
          Text(
            value,
            style: textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.bold,
              color: colorScheme.onSurface,
            ),
            overflow: TextOverflow.ellipsis,
          ),
          const SizedBox(height: AppDimens.spaceS),
          if (progressValue != null)
            LinearProgressIndicator(
              value: progressValue,
              backgroundColor: colorScheme.surfaceContainerHighest,
              valueColor: AlwaysStoppedAnimation<Color>(progressIndicatorColor),
              borderRadius: BorderRadius.circular(AppDimens.radiusXXS),
              minHeight: AppDimens.lineProgressHeight,
            )
          else
            const SizedBox(height: AppDimens.lineProgressHeight),
        ],
      ),
    );
  }

  Widget _buildDatesSection(ColorScheme colorScheme, TextTheme textTheme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle(
          colorScheme,
          textTheme,
          'Important Dates',
          icon: Icons.calendar_month_outlined,
          key: const Key('dates_section_title'),
        ),
        const SizedBox(height: AppDimens.spaceS),
        if (module.progressFirstLearningDate != null)
          _buildDateItem(
            colorScheme,
            textTheme,
            'First Learning',
            module.progressFirstLearningDate!,
            Icons.play_circle_outline,
            key: const Key('first_learning_date'),
          ),
        if (module.progressNextStudyDate != null) ...[
          const SizedBox(height: AppDimens.spaceL),
          _buildDateItem(
            colorScheme,
            textTheme,
            'Next Study',
            module.progressNextStudyDate!,
            Icons.event_available_outlined,
            isDue:
                DateUtils.isSameDay(
                  module.progressNextStudyDate,
                  DateTime.now(),
                ) ||
                module.progressNextStudyDate!.isBefore(DateTime.now()),
            key: const Key('next_study_date'),
          ),
        ],
      ],
    );
  }

  Widget _buildDateItem(
    ColorScheme colorScheme,
    TextTheme textTheme,
    String label,
    DateTime date,
    IconData icon, {
    bool isDue = false,
    Key? key,
  }) {
    final Color effectiveColor = isDue
        ? colorScheme.error
        : colorScheme.primary;
    final Color labelColor = colorScheme.onSurfaceVariant;
    final Color dateColor = isDue ? colorScheme.error : colorScheme.onSurface;

    return Row(
      key: key,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Icon(icon, color: effectiveColor, size: AppDimens.iconM),
        const SizedBox(width: AppDimens.spaceM),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                label,
                style: textTheme.bodyMedium?.copyWith(color: labelColor),
              ),
              Text(
                DateFormat('EEEE, MMMM d, yyyy').format(date),
                style: textTheme.titleMedium?.copyWith(
                  color: dateColor,
                  fontWeight: isDue ? FontWeight.bold : null,
                ),
              ),
            ],
          ),
        ),
        if (isDue)
          Padding(
            padding: const EdgeInsets.only(left: AppDimens.spaceS),
            child: Chip(
              label: const Text('Due'),
              labelStyle: textTheme.labelSmall?.copyWith(
                color: colorScheme.onErrorContainer,
              ),
              backgroundColor: colorScheme.errorContainer,
              padding: EdgeInsets.zero,
              visualDensity: VisualDensity.compact,
              side: BorderSide.none,
            ),
          ),
      ],
    );
  }

  Widget _buildStudyHistorySection(
    ColorScheme colorScheme,
    TextTheme textTheme,
  ) {
    final studyHistory =
        module.studyHistory
            .map((s) => DateTime.tryParse(s))
            .whereType<DateTime>()
            .toList()
          ..sort((a, b) => b.compareTo(a));

    final displayHistory = studyHistory.take(7).toList();
    final remainingCount = studyHistory.length - displayHistory.length;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle(
          colorScheme,
          textTheme,
          'Study History',
          icon: Icons.history_edu_outlined,
          key: const Key('history_section_title'),
        ),
        const SizedBox(height: AppDimens.spaceS),
        _buildHistoryItems(colorScheme, textTheme, displayHistory),
        if (remainingCount > 0) ...[
          const SizedBox(height: AppDimens.spaceS),
          Padding(
            padding: const EdgeInsets.only(left: AppDimens.paddingS),
            child: Text(
              '+ $remainingCount more session${remainingCount > 1 ? 's' : ''}',
              style: textTheme.bodySmall?.copyWith(
                color: colorScheme.onSurfaceVariant,
                fontStyle: FontStyle.italic,
              ),
              key: const Key('more_sessions_text'),
            ),
          ),
        ],
      ],
    );
  }

  Widget _buildHistoryItems(
    ColorScheme colorScheme,
    TextTheme textTheme,
    List<DateTime> displayHistory,
  ) {
    final today = DateUtils.dateOnly(DateTime.now());

    return Wrap(
      key: const Key('history_items'),
      spacing: AppDimens.spaceS,
      runSpacing: AppDimens.spaceS,
      children: displayHistory.map((date) {
        final itemDate = DateUtils.dateOnly(date);
        final isToday = itemDate.isAtSameMomentAs(today);

        final Color bgColor;
        final Color fgColor;
        final Border border;

        if (isToday) {
          bgColor = colorScheme.primaryContainer;
          fgColor = colorScheme.onPrimaryContainer;
          border = Border.all(color: colorScheme.primary);
        } else {
          bgColor = colorScheme.surfaceContainerHighest;
          fgColor = colorScheme.onSurfaceVariant;
          border = Border.all(color: colorScheme.outlineVariant);
        }

        final itemTextStyle = textTheme.labelMedium;

        return Tooltip(
          message: DateFormat('MMMM d, yyyy').format(date),
          child: Container(
            padding: const EdgeInsets.symmetric(
              horizontal: AppDimens.paddingM,
              vertical: AppDimens.paddingXS,
            ),
            decoration: BoxDecoration(
              color: bgColor,
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
              border: border,
            ),
            child: Text(
              DateFormat('MMM d').format(date),
              style: itemTextStyle?.copyWith(
                fontWeight: isToday ? FontWeight.bold : FontWeight.normal,
                color: fgColor,
              ),
            ),
          ),
        );
      }).toList(),
    );
  }

  Widget _buildActionButtons(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(top: AppDimens.paddingL),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          OutlinedButton.icon(
            key: const Key('close_button'),
            icon: const Icon(Icons.close),
            label: const Text('Close'),
            onPressed: () => Navigator.pop(context),
          ),
          const SizedBox(width: AppDimens.spaceM),
          ElevatedButton.icon(
            key: const Key('study_button'),
            icon: const Icon(Icons.play_arrow),
            label: const Text('Start Studying'),
            onPressed: () {
              Navigator.pop(context);
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) =>
                      ModuleDetailScreen(moduleId: module.moduleId),
                ),
              );
            },
          ),
        ],
      ),
    );
  }
}
