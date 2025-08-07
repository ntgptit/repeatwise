import 'package:flutter/material.dart' hide DateUtils;
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/theme.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../../core/utils/utils.dart';

class UpcomingReminders extends ConsumerWidget {
  const UpcomingReminders({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        UiUtils.createSectionHeader(
          title: 'Upcoming Reminders',
          actionText: 'View All',
          onActionPressed: () {
            // TODO: Navigate to all reminders
          },
        ),
        const SizedBox(height: 16),
        // TODO: Replace with actual data from provider
        ListView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          itemCount: 2,
          itemBuilder: (context, index) {
            return RepeatWiseCard(
              child: RepeatWiseListTile(
                leading: const CircleAvatar(
                  backgroundColor: AppColors.warningContainer,
                  child: Icon(
                    Icons.notifications,
                    color: AppColors.warning,
                  ),
                ),
                title: Text(
                  'Set ${index + 1}',
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                subtitle: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const SizedBox(height: 4),
                    Text('Time to review your ${index + 1}${index == 0 ? 'st' : 'nd'} set!'),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Icon(
                          Icons.access_time,
                          size: 16,
                          color: Colors.grey[600],
                        ),
                        const SizedBox(width: 4),
                        Text(
                          DateUtils.formatRelativeTime(DateTime.now().add(Duration(hours: index + 2))),
                          style: const TextStyle(
                            fontSize: AppDimens.textSize12,
                            color: AppColors.gray600,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
                trailing: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    IconButton(
                      icon: const Icon(Icons.check),
                      onPressed: () {
                        // TODO: Mark as completed
                      },
                    ),
                    IconButton(
                      icon: const Icon(Icons.snooze),
                      onPressed: () {
                        // TODO: Snooze reminder
                      },
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ],
    );
  }


}
