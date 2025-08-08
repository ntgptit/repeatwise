import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../auth/presentation/providers/auth_providers.dart';
import '../widgets/dashboard_stats.dart';
import '../widgets/recent_sets.dart';
import '../widgets/upcoming_reminders.dart';
import '../widgets/set_actions.dart';
import '../widgets/learning_sets.dart';
import '../widgets/progress_summary.dart';

class DashboardPage extends ConsumerWidget {
  const DashboardPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authNotifierProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Dashboard'),
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications),
            onPressed: () {
              // TODO: Navigate to notifications
            },
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              // TODO: Navigate to settings
            },
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          // TODO: Refresh dashboard data
        },
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Welcome message
              if (authState.user != null) ...[
                Text(
                  'Welcome back, ${authState.user!.name}!',
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  'Ready to continue your learning journey?',
                  style: Theme.of(
                    context,
                  ).textTheme.bodyLarge?.copyWith(color: Colors.grey[600]),
                ),
                const SizedBox(height: 24),
              ],

              // Dashboard stats
              const DashboardStats(),
              const SizedBox(height: 24),

              // Progress summary
              const ProgressSummary(),
              const SizedBox(height: 24),

              // Set actions
              const SetActions(),
              const SizedBox(height: 24),

              // Learning sets
              const LearningSets(),
              const SizedBox(height: 24),

              // Recent sets
              const RecentSets(),
              const SizedBox(height: 24),

              // Upcoming reminders
              const UpcomingReminders(),
            ],
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          context.go('/sets/create');
        },
        backgroundColor: AppColors.primary,
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }
}
