// lib/presentation/widgets/learning/learning_filter_bar/filter_date_selector.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class FilterDateSelector extends ConsumerWidget {
  final DateTime? selectedDate;
  final VoidCallback onDateSelected;
  final VoidCallback onDateCleared;

  const FilterDateSelector({
    super.key,
    required this.selectedDate,
    required this.onDateSelected,
    required this.onDateCleared,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Filter by Date',
          style: theme.textTheme.labelMedium?.copyWith(
            color: colorScheme.onSurface.withValues(
              alpha: AppDimens.opacityHigh,
            ),
          ),
        ),
        const SizedBox(height: AppDimens.spaceXS),
        SizedBox(
          height: 48,
          child: selectedDate == null
              ? OutlinedButton.icon(
                  icon: Icon(
                    Icons.calendar_today,
                    size: AppDimens.iconS,
                    color: colorScheme.primary,
                  ),
                  label: Text(
                    'Select Date',
                    style: TextStyle(color: colorScheme.primary),
                  ),
                  onPressed: onDateSelected,
                  style: OutlinedButton.styleFrom(
                    minimumSize: const Size.fromHeight(48),
                    padding: const EdgeInsets.symmetric(
                      horizontal: AppDimens.paddingL,
                      vertical: AppDimens.paddingM,
                    ),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(AppDimens.radiusM),
                    ),
                    side: BorderSide(
                      color: colorScheme.outline.withValues(
                        alpha: AppDimens.opacitySemi,
                      ),
                    ),
                  ),
                )
              : Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: AppDimens.paddingL,
                    vertical: AppDimens.paddingM - 1,
                  ),
                  decoration: BoxDecoration(
                    border: Border.all(color: colorScheme.primary, width: 2),
                    borderRadius: BorderRadius.circular(AppDimens.radiusM),
                  ),
                  child: Row(
                    children: [
                      Icon(
                        Icons.calendar_today,
                        size: AppDimens.iconS,
                        color: colorScheme.primary,
                      ),
                      const SizedBox(width: AppDimens.spaceS),
                      Expanded(
                        child: Text(
                          DateFormat('MMM dd, yyyy').format(selectedDate!),
                          style: TextStyle(
                            color: colorScheme.primary,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      InkWell(
                        onTap: onDateCleared,
                        borderRadius: BorderRadius.circular(AppDimens.radiusS),
                        child: Padding(
                          padding: const EdgeInsets.all(AppDimens.paddingXS),
                          child: Icon(
                            Icons.close,
                            size: AppDimens.iconS,
                            color: colorScheme.primary,
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
}
