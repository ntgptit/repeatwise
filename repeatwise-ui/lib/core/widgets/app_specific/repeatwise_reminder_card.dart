import 'package:flutter/material.dart';
import '../../models/remind_schedule.dart';
import '../../utils/date_utils.dart' as AppDateUtils;
import '../layout/repeatwise_card.dart';

/// RepeatWise reminder card widget for displaying reminder schedules
class RepeatWiseReminderCard extends StatelessWidget {
  final RemindSchedule reminder;
  final VoidCallback? onTap;
  final VoidCallback? onToggle;

  const RepeatWiseReminderCard({
    super.key,
    required this.reminder,
    this.onTap,
    this.onToggle,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return RepeatWiseCard(
      onTap: onTap,
      child: Row(
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Icon(
                      Icons.notifications,
                      size: 20,
                      color: theme.colorScheme.primary,
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        'Reminder',
                        style: theme.textTheme.titleSmall?.copyWith(
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 4),
                Text(
                  AppDateUtils.DateUtils.formatTime(reminder.remindTime),
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: theme.colorScheme.onSurfaceVariant,
                  ),
                ),
                if (reminder.message != null) ...[
                  const SizedBox(height: 4),
                  Text(
                    reminder.message!,
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: theme.colorScheme.onSurfaceVariant,
                    ),
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ],
            ),
          ),
          const SizedBox(width: 12),
          Switch(
            value: reminder.status == RemindStatus.pending,
            onChanged: (_) => onToggle?.call(),
          ),
        ],
      ),
    );
  }


}
