// lib/presentation/widgets/home/insights/insight_item.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart';

class InsightItem extends StatelessWidget {
  final String message;
  final IconData icon;
  final String colorType;
  final ThemeData theme;
  final ColorScheme colorScheme;

  const InsightItem({
    super.key,
    required this.message,
    required this.icon,
    required this.colorType,
    required this.theme,
    required this.colorScheme,
  });

  @override
  Widget build(BuildContext context) {
    final color = colorScheme.getStatColor(colorType);

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: AppDimens.paddingM),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, size: AppDimens.iconM, color: color),
          const SizedBox(width: AppDimens.spaceM),
          Expanded(
            child: Text(
              message,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
