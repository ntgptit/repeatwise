import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class StatItemWidget extends StatelessWidget {
  final String value;
  final String label;
  final IconData icon;
  final Color color;

  const StatItemWidget({
    super.key,
    required this.value,
    required this.label,
    required this.icon,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          padding: const EdgeInsets.all(AppDimens.paddingS),
          decoration: BoxDecoration(
            color: color.withValues(alpha: AppDimens.opacityLight),
            shape: BoxShape.circle,
          ),
          child: Icon(icon, color: color, size: AppDimens.iconM),
        ),
        const SizedBox(height: AppDimens.spaceS),
        Text(
          value,
          style: theme.textTheme.bodyLarge?.copyWith(
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
        Text(
          label,
          style: theme.textTheme.bodySmall?.copyWith(
            color: colorScheme.onSurfaceVariant,
          ),
        ),
      ],
    );
  }
}
