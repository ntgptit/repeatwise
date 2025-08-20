// lib/presentation/screens/progress/widgets/due_progress_empty_state.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/utils/date_utils.dart';

class DueProgressEmptyState extends StatelessWidget {
  final DateTime? selectedDate;
  final VoidCallback onRefresh;

  const DueProgressEmptyState({
    super.key,
    required this.selectedDate,
    required this.onRefresh,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 120,
              height: 120,
              decoration: BoxDecoration(
                color: colorScheme.surfaceContainerHighest,
                shape: BoxShape.circle,
              ),
              child: Icon(
                Icons.check_circle_outline,
                size: 64,
                color: colorScheme.tertiary.withValues(alpha: 0.5),
              ),
            ),
            const SizedBox(height: 24),
            Text(
              selectedDate == null
                  ? 'No modules due for review today!'
                  : 'No modules due for review by ${AppDateUtils.formatDate(selectedDate!)}',
              style: theme.textTheme.titleLarge,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            Text(
              'Great job keeping up with your studies!',
              style: theme.textTheme.bodyLarge?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 32),
            FilledButton.icon(
              icon: const Icon(Icons.refresh),
              label: const Text('Refresh'),
              onPressed: onRefresh,
            ),
          ],
        ),
      ),
    );
  }
}
