import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';
import 'package:spaced_learning_app/presentation/widgets/common/sl_toggle_switch.dart';

class RescheduleDialog {
  static Future<Map<String, dynamic>?> show(
    BuildContext context, {
    required DateTime initialDate,
    required String title,
  }) async {
    return showDialog<Map<String, dynamic>>(
      context: context,
      barrierDismissible: false,
      builder: (dialogContext) => ProviderScope(
        child: Dialog(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusL),
          ),
          insetPadding: const EdgeInsets.symmetric(
            horizontal: 16,
            vertical: 24,
          ),
          child: ConstrainedBox(
            constraints: const BoxConstraints(minWidth: 320, maxWidth: 440),
            child: Padding(
              padding: const EdgeInsets.all(AppDimens.paddingM),
              child: _RescheduleDialogContent(
                initialDate: initialDate,
                title: title,
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class _RescheduleDialogContent extends ConsumerStatefulWidget {
  final DateTime initialDate;
  final String title;

  const _RescheduleDialogContent({
    required this.initialDate,
    required this.title,
  });

  @override
  ConsumerState<_RescheduleDialogContent> createState() =>
      _RescheduleDialogContentState();
}

class _RescheduleDialogContentState
    extends ConsumerState<_RescheduleDialogContent> {
  late DateTime _selectedDate;
  bool _rescheduleFollowing = true;

  @override
  void initState() {
    super.initState();
    _selectedDate = widget.initialDate;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        _buildHeader(context, widget.title),
        const SizedBox(height: AppDimens.spaceM),
        _buildDatePicker(context),
        const SizedBox(height: AppDimens.spaceM),
        _buildRescheduleSwitchOption(context),
        const SizedBox(height: AppDimens.spaceL),
        _buildActionButtons(context),
      ],
    );
  }

  Widget _buildHeader(BuildContext context, String title) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Row(
      children: [
        Icon(Icons.event, color: colorScheme.primary, size: AppDimens.iconM),
        const SizedBox(width: AppDimens.spaceM),
        Expanded(child: Text(title, style: theme.textTheme.titleLarge)),
      ],
    );
  }

  Widget _buildDatePicker(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Container(
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerLow,
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
      ),
      height: 340,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.all(AppDimens.paddingM),
            child: Text('Select new date:', style: theme.textTheme.titleMedium),
          ),
          Expanded(
            child: Theme(
              data: theme.copyWith(
                colorScheme: colorScheme.copyWith(
                  primary: colorScheme.primary,
                  onPrimary: colorScheme.onPrimary,
                  surface: colorScheme.surface,
                ),
              ),
              child: CalendarDatePicker(
                initialDate: _selectedDate,
                firstDate: DateTime.now().subtract(const Duration(days: 7)),
                lastDate: DateTime.now().add(const Duration(days: 365)),
                onDateChanged: (date) => setState(() => _selectedDate = date),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRescheduleSwitchOption(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Container(
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerLow,
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
        border: Border.all(
          color: colorScheme.outlineVariant.withValues(
            alpha: AppDimens.opacityMediumHigh,
          ),
        ),
      ),
      child: SLToggleSwitch(
        title: 'Reschedule following repetitions',
        subtitle: 'Adjust all future repetitions based on this new date',
        value: _rescheduleFollowing,
        onChanged: (value) => setState(() => _rescheduleFollowing = value),
        type: SLToggleSwitchType.standard,
        size: SLToggleSwitchSize.medium,
        icon: Icons.repeat,
        contentPadding: const EdgeInsets.symmetric(
          horizontal: AppDimens.paddingM,
          vertical: AppDimens.paddingXS,
        ),
      ),
    );
  }

  Widget _buildActionButtons(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        SLButton(
          text: 'Cancel',
          type: SLButtonType.text,
          size: SLButtonSize.small,
          onPressed: () => Navigator.pop(context),
        ),
        const SizedBox(width: AppDimens.spaceM),
        SLButton(
          text: 'Reschedule',
          type: SLButtonType.primary,
          prefixIcon: Icons.calendar_today,
          size: SLButtonSize.small,
          onPressed: () => Navigator.pop(context, {
            'date': _selectedDate,
            'rescheduleFollowing': _rescheduleFollowing,
          }),
        ),
      ],
    );
  }
}
