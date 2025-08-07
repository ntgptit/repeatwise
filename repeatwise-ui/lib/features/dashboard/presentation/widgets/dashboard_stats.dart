import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/widgets/widgets.dart';

class DashboardStats extends ConsumerWidget {
  const DashboardStats({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Your Progress',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 16),
        GridView.count(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisCount: 2,
          crossAxisSpacing: 16,
          mainAxisSpacing: 16,
          childAspectRatio: 1.5,
          children: const [
            RepeatWiseStatisticsCard(
              title: 'Active Sets',
              value: '12',
              icon: Icons.list,
              color: AppTheme.primaryColor,
            ),
            RepeatWiseStatisticsCard(
              title: 'Completed Cycles',
              value: '45',
              icon: Icons.check_circle,
              color: AppTheme.successColor,
            ),
            RepeatWiseStatisticsCard(
              title: 'Today\'s Reminders',
              value: '3',
              icon: Icons.notifications,
              color: AppTheme.warningColor,
            ),
            RepeatWiseStatisticsCard(
              title: 'Learning Streak',
              value: '7 days',
              icon: Icons.local_fire_department,
              color: AppTheme.errorColor,
            ),
          ],
        ),
      ],
    );
  }
}


