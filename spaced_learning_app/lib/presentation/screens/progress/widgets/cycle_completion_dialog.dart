// lib/presentation/screens/progress/widgets/cycle_completion_dialog.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class CycleCompletionDialog extends StatelessWidget {
  const CycleCompletionDialog({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return AlertDialog(
      title: Row(
        children: [
          Icon(Icons.celebration, color: colorScheme.tertiary),
          const SizedBox(width: AppDimens.spaceM),
          const Text('Learning Cycle Completed!'),
        ],
      ),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildInfoSection(
            icon: Icons.check_circle,
            text: 'You have completed all repetitions in this cycle.',
            colorScheme: colorScheme,
          ),
          const SizedBox(height: AppDimens.spaceL),
          _buildInfoSection(
            icon: Icons.schedule,
            text:
                'The system has automatically scheduled a new review cycle with adjusted intervals based on your learning performance.',
            colorScheme: colorScheme,
          ),
          const SizedBox(height: AppDimens.spaceL),
          _buildInfoSection(
            icon: Icons.auto_graph,
            text:
                'Keep up with regular reviews to maximize your learning efficiency.',
            colorScheme: colorScheme,
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Got it'),
        ),
      ],
    );
  }

  Widget _buildInfoSection({
    required IconData icon,
    required String text,
    required ColorScheme colorScheme,
  }) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Icon(icon, color: colorScheme.primary, size: AppDimens.iconM),
        const SizedBox(width: AppDimens.spaceM),
        Expanded(child: Text(text)),
      ],
    );
  }
}
