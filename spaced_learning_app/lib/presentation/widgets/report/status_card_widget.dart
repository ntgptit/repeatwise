// lib/presentation/widgets/report/status_card_widget.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class StatusCardWidget extends StatelessWidget {
  final bool isActive;
  final ValueChanged<bool> onToggle;

  const StatusCardWidget({
    super.key,
    required this.isActive,
    required this.onToggle,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Card(
      elevation: AppDimens.elevationM,
      color: colorScheme.surfaceContainerLowest,
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  isActive ? Icons.check_circle : Icons.error_outline,
                  color: isActive ? colorScheme.primary : colorScheme.error,
                  size: AppDimens.iconL,
                ),
                const SizedBox(width: AppDimens.spaceM),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Automatic check status',
                        style: theme.textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: AppDimens.spaceXS),
                      Text(
                        isActive ? 'Active' : 'Inactive',
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: isActive
                              ? colorScheme.primary
                              : colorScheme.error,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
                SizedBox(
                  width: 70, // Fixed width for the switch
                  child: Switch(
                    value: isActive,
                    onChanged: onToggle,
                    activeColor: colorScheme.primary,
                  ),
                ),
              ],
            ),
            const SizedBox(height: AppDimens.spaceM),
            const Divider(height: 1),
            const SizedBox(height: AppDimens.spaceM),
            Text(
              'Check time: 00:05 AM daily',
              style: theme.textTheme.bodyMedium,
            ),
            const SizedBox(height: AppDimens.spaceS),
            Text(
              'The system will automatically check due tasks and schedule reminder notifications.',
              style: theme.textTheme.bodySmall?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
