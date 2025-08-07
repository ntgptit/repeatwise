import 'package:flutter/material.dart';
import '../layout/repeatwise_card.dart';

/// RepeatWise streak counter widget for displaying learning streaks
class RepeatWiseStreakCounter extends StatelessWidget {
  final int currentStreak;
  final int longestStreak;
  final int totalDays;

  const RepeatWiseStreakCounter({
    super.key,
    required this.currentStreak,
    required this.longestStreak,
    required this.totalDays,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return RepeatWiseCard(
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              _buildStreakItem(
                context,
                'Current',
                currentStreak.toString(),
                Icons.local_fire_department,
                theme.colorScheme.primary,
              ),
              _buildStreakItem(
                context,
                'Longest',
                longestStreak.toString(),
                Icons.emoji_events,
                theme.colorScheme.secondary,
              ),
              _buildStreakItem(
                context,
                'Total',
                totalDays.toString(),
                Icons.calendar_month,
                theme.colorScheme.tertiary,
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStreakItem(
    BuildContext context,
    String label,
    String value,
    IconData icon,
    Color color,
  ) {
    final theme = Theme.of(context);
    
    return Column(
      children: [
        Icon(
          icon,
          color: color,
          size: 32,
        ),
        const SizedBox(height: 8),
        Text(
          value,
          style: theme.textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
        Text(
          label,
          style: theme.textTheme.bodySmall?.copyWith(
            color: theme.colorScheme.onSurfaceVariant,
          ),
        ),
      ],
    );
  }
}
