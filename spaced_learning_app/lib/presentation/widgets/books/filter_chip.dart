import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class FilterChipWidget extends StatelessWidget {
  final String label;
  final Color color;
  final VoidCallback onDeleted;

  const FilterChipWidget({
    super.key,
    required this.label,
    required this.color,
    required this.onDeleted,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Chip(
      label: Text(
        label,
        overflow: TextOverflow.ellipsis,
        style: theme.textTheme.labelMedium?.copyWith(
          color: color,
          fontSize: AppDimens.fontS,
        ),
      ),
      deleteIcon: const Icon(Icons.close, size: AppDimens.iconXS),
      onDeleted: onDeleted,
      backgroundColor: color.withValues(alpha: 0.1),
      deleteIconColor: color,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
        side: BorderSide(color: color.withValues(alpha: 0.2)),
      ),
      materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
      visualDensity: VisualDensity.compact,
      padding: const EdgeInsets.symmetric(horizontal: AppDimens.paddingXS),
    );
  }
}
