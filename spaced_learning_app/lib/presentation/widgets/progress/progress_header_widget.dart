// lib/presentation/widgets/progress/progress_header_widget.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/domain/models/repetition.dart';
import 'package:spaced_learning_app/presentation/utils/cycle_formatter.dart';
import 'package:spaced_learning_app/presentation/utils/snackbar_utils.dart';
import 'package:spaced_learning_app/presentation/viewmodels/repetition_viewmodel.dart';
import 'package:url_launcher/url_launcher.dart';

extension ProgressDetailExtension on ProgressDetail {
  int get completedRepetitions =>
      repetitions.where((r) => r.status == RepetitionStatus.completed).length;

  int get totalRepetitions => repetitions.length;

  double get clampedPercentComplete => percentComplete.clamp(0, 100);

  bool get isOverdue =>
      nextStudyDate != null && nextStudyDate!.isBefore(DateTime.now());

  String get formattedStartDate => firstLearningDate != null
      ? DateFormat.yMMMd().format(firstLearningDate!)
      : 'Not started';

  String get formattedNextDate => nextStudyDate != null
      ? DateFormat.yMMMd().format(nextStudyDate!)
      : 'Not scheduled';

  // Getter để lấy URL từ module
  // String? get moduleUrl {
  //   // Đây là trường hợp URL có thể được truy cập từ một thuộc tính trong progress
  //   // Thay đổi logic này nếu cần thiết theo cấu trúc dữ liệu thực tế của ứng dụng
  //   return null; // Mặc định trả về null, cần điều chỉnh theo cấu trúc dữ liệu thực
  // }
}

class ProgressHeaderWidget extends ConsumerWidget {
  final ProgressDetail progress;
  final VoidCallback onCycleCompleteDialogRequested;

  const ProgressHeaderWidget({
    super.key,
    required this.progress,
    required this.onCycleCompleteDialogRequested,
  });

  // Thêm hàm để mở URL - sửa lỗi use_build_context_synchronously
  Future<void> _launchModuleUrl(BuildContext context, String? url) async {
    // Lưu trữ giá trị mounted trước khi thực hiện async operation
    final bool isContextMounted = context.mounted;

    if (url == null || url.isEmpty) {
      if (isContextMounted) {
        SnackBarUtils.show(context, 'No URL available for this module');
      }
      return;
    }

    final Uri uri = Uri.parse(url);
    try {
      if (await canLaunchUrl(uri)) {
        await launchUrl(uri, mode: LaunchMode.externalApplication);
      } else {
        if (isContextMounted) {
          SnackBarUtils.show(context, 'Could not launch URL: $url');
        }
      }
    } catch (e) {
      if (isContextMounted) {
        SnackBarUtils.show(context, 'Error opening URL: ${e.toString()}');
      }
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Card(
      elevation: AppDimens.elevationNone,
      color: colorScheme.surface,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusXXL),
      ),
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildTitleSection(theme, context), // Truyền context vào đây
            const SizedBox(height: AppDimens.spaceL),
            _buildProgressSection(theme),
            const SizedBox(height: AppDimens.spaceL),
            _buildCycleInfoSection(context, ref, theme),
            const SizedBox(height: AppDimens.spaceL),
            _buildDatesSection(theme),
          ],
        ),
      ),
    );
  }

  Widget _buildTitleSection(ThemeData theme, BuildContext context) {
    // Thêm context
    final colorScheme = theme.colorScheme;

    final String? url = progress.moduleUrl;
    final bool hasUrl = url != null && url.isNotEmpty;

    return Row(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        CircleAvatar(
          radius: AppDimens.avatarSizeL / 2,
          backgroundColor: colorScheme.primaryContainer,
          child: Icon(
            Icons.menu_book_rounded,
            size: AppDimens.iconL,
            color: colorScheme.onPrimaryContainer,
          ),
        ),
        const SizedBox(width: AppDimens.spaceM),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                progress.moduleTitle ?? 'Learning Module',
                style: theme.textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: colorScheme.onSurface,
                ),
                maxLines: 2,
              ),
              if (hasUrl) const SizedBox(height: AppDimens.spaceXS),
              if (hasUrl)
                Text(
                  'Learning material available online',
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: colorScheme.onSurfaceVariant,
                    fontStyle: FontStyle.italic,
                  ),
                ),
            ],
          ),
        ),
        if (hasUrl)
          IconButton(
            icon: Icon(Icons.open_in_new, color: colorScheme.primary),
            tooltip: 'Open learning materials',
            onPressed: () =>
                _launchModuleUrl(context, url), // Sử dụng context ở đây
          ),
      ],
    );
  }

  Widget _buildProgressSection(ThemeData theme) {
    final colorScheme = theme.colorScheme;
    final progressValue = progress.clampedPercentComplete / 100;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Overall Progress',
          style: theme.textTheme.titleMedium?.copyWith(
            fontWeight: FontWeight.w600,
            color: colorScheme.onSurface,
          ),
        ),
        const SizedBox(height: AppDimens.spaceS),
        ProgressBar(value: progressValue, colorScheme: colorScheme),
        const SizedBox(height: AppDimens.spaceXS),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'Progress',
              style: theme.textTheme.bodyMedium?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
            Card(
              color: colorScheme.primaryContainer,
              margin: EdgeInsets.zero,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(AppDimens.radiusL),
              ),
              child: Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: AppDimens.paddingS,
                  vertical: AppDimens.paddingXS,
                ),
                child: Text(
                  '${progress.clampedPercentComplete.toInt()}%',
                  style: theme.textTheme.labelLarge?.copyWith(
                    color: colorScheme.onPrimaryContainer,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildCycleInfoSection(
    BuildContext context, // Thêm context
    WidgetRef ref,
    ThemeData theme,
  ) {
    final colorScheme = theme.colorScheme;
    final cycleInfo = ref.watch(getCycleInfoProvider(progress.cyclesStudied));
    final cycleColor = CycleFormatter.getColor(progress.cyclesStudied, context);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Container(
              decoration: BoxDecoration(
                color: cycleColor.withValues(alpha: AppDimens.opacitySemi),
                // Sử dụng withOpacity
                shape: BoxShape.circle,
              ),
              padding: const EdgeInsets.all(AppDimens.paddingS),
              child: Icon(
                CycleFormatter.getIcon(progress.cyclesStudied),
                color: cycleColor,
                size: AppDimens.iconL,
              ),
            ),
            const SizedBox(width: AppDimens.spaceM),
            Expanded(
              child: Text(
                'Cycle: ${CycleFormatter.getDisplayName(progress.cyclesStudied)}',
                style: theme.textTheme.titleMedium?.copyWith(
                  color: cycleColor,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            Card(
              elevation: AppDimens.elevationNone,
              color: colorScheme.surfaceContainerHighest,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(AppDimens.radiusXXL),
                side: BorderSide(
                  color: cycleColor,
                  width: AppDimens.outlineButtonBorderWidth,
                ),
              ),
              child: Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: AppDimens.paddingS,
                  vertical: AppDimens.paddingXS,
                ),
                child: Text(
                  '${progress.completedRepetitions} / ${progress.totalRepetitions}',
                  style: theme.textTheme.bodyMedium?.copyWith(
                    color: cycleColor,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceS),
        Card(
          elevation: AppDimens.elevationNone,
          color: colorScheme.surfaceContainerHighest,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
          ),
          child: Padding(
            padding: const EdgeInsets.all(AppDimens.paddingS),
            child: Row(
              children: [
                Icon(
                  Icons.info_outline,
                  size: AppDimens.iconM,
                  color: cycleColor,
                ),
                const SizedBox(width: AppDimens.spaceS),
                Expanded(
                  child: Text(
                    cycleInfo,
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: colorScheme.onSurfaceVariant,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildDatesSection(ThemeData theme) {
    final colorScheme = theme.colorScheme;

    return Card(
      elevation: AppDimens.elevationNone,
      color: colorScheme.surfaceContainerHighest,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
      ),
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingM),
        child: IntrinsicHeight(
          child: Row(
            children: [
              Expanded(
                child: DateInfoBlock(
                  label: 'Start Date',
                  value: progress.formattedStartDate,
                  icon: Icons.calendar_today_outlined,
                  color: colorScheme.primary,
                  theme: theme,
                ),
              ),
              VerticalDivider(
                width: AppDimens.spaceXL,
                thickness: AppDimens.dividerThickness,
                color: colorScheme.outlineVariant,
              ),
              Expanded(
                child: DateInfoBlock(
                  label: 'Next Review',
                  value: progress.formattedNextDate,
                  icon: Icons.event_note,
                  color: progress.isOverdue
                      ? colorScheme.error
                      : colorScheme.secondary,
                  isHighlighted: progress.isOverdue,
                  theme: theme,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class ProgressBar extends StatelessWidget {
  final double value;
  final ColorScheme colorScheme;

  const ProgressBar({
    super.key,
    required this.value,
    required this.colorScheme,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      height: AppDimens.lineProgressHeightL,
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerHighest,
        borderRadius: BorderRadius.circular(AppDimens.radiusS),
      ),
      clipBehavior: Clip.antiAlias,
      child: LayoutBuilder(
        builder: (context, constraints) {
          return TweenAnimationBuilder<double>(
            duration: const Duration(milliseconds: AppDimens.durationM),
            curve: Curves.easeInOut,
            tween: Tween(begin: 0, end: value),
            builder: (context, animatedValue, child) {
              return Stack(
                children: [
                  Container(
                    width: constraints.maxWidth * animatedValue,
                    decoration: const BoxDecoration(
                      gradient: LinearGradient(
                        colors: [Colors.blue, Colors.teal],
                      ),
                    ),
                  ),
                ],
              );
            },
          );
        },
      ),
    );
  }
}

class DateInfoBlock extends StatelessWidget {
  final String label;
  final String value;
  final IconData icon;
  final Color color;
  final ThemeData theme;
  final bool isHighlighted;

  const DateInfoBlock({
    super.key,
    required this.label,
    required this.value,
    required this.icon,
    required this.color,
    required this.theme,
    this.isHighlighted = false,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = theme.colorScheme;

    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          decoration: BoxDecoration(
            color: color.withValues(alpha: AppDimens.opacitySemi),
            // Sử dụng withOpacity
            shape: BoxShape.circle,
          ),
          padding: const EdgeInsets.all(AppDimens.paddingXS),
          child: Icon(icon, size: AppDimens.iconS, color: color),
        ),
        const SizedBox(width: AppDimens.spaceS),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                label,
                style: theme.textTheme.labelMedium?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
              ),
              const SizedBox(height: AppDimens.spaceXXS),
              Text(
                value,
                style: theme.textTheme.titleSmall?.copyWith(
                  color: isHighlighted
                      ? colorScheme.error
                      : colorScheme.onSurface,
                  fontWeight: FontWeight.bold,
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ],
          ),
        ),
      ],
    );
  }
}
