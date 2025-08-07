import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/set.dart';
import '../models/set_cycle.dart';
import '../models/remind_schedule.dart';
import 'widgets.dart';

/// Example page demonstrating all common and app-specific widgets
class WidgetExamplesPage extends ConsumerWidget {
  const WidgetExamplesPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Widget Examples'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildSection('Common Widgets', [
              _buildCommonWidgetsExamples(context),
            ]),
            const SizedBox(height: 32),
            _buildSection('App-Specific Widgets', [
              _buildAppSpecificWidgetsExamples(context),
            ]),
          ],
        ),
      ),
    );
  }

  Widget _buildSection(String title, List<Widget> children) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: const TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 16),
        ...children,
      ],
    );
  }

  Widget _buildCommonWidgetsExamples(BuildContext context) {
    return Column(
      children: [
        // Loading Widget
        _buildExampleCard(
          'RepeatWiseLoadingWidget',
          const RepeatWiseLoadingWidget(
            message: 'Loading data...',
            size: 32,
          ),
        ),

        // Error Widget
        _buildExampleCard(
          'RepeatWiseErrorWidget',
          RepeatWiseErrorWidget(
            message: 'Failed to load data',
            onRetry: () => debugPrint('Retry pressed'),
          ),
        ),

        // Empty State Widget
        _buildExampleCard(
          'RepeatWiseEmptyStateWidget',
          RepeatWiseEmptyStateWidget(
            title: 'No sets found',
            subtitle: 'Create your first learning set to get started',
            onAction: () => debugPrint('Create set pressed'),
            actionLabel: 'Create Set',
          ),
        ),

        // Common Card
        _buildExampleCard(
          'RepeatWiseCard',
          RepeatWiseCard(
            onTap: () => debugPrint('Card tapped'),
            child: const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Card Title',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                SizedBox(height: 8),
                Text('This is a sample card content with Material 3 styling.'),
              ],
            ),
          ),
        ),

        // Common List Tile
        _buildExampleCard(
          'RepeatWiseListTile',
          RepeatWiseListTile(
            leading: const Icon(Icons.list),
            title: const Text('List Item'),
            subtitle: const Text('This is a subtitle'),
            trailing: const Icon(Icons.arrow_forward),
            onTap: () => debugPrint('List tile tapped'),
          ),
        ),

        // Common Button
        _buildExampleCard(
          'RepeatWiseButton',
          Row(
            children: [
              Expanded(
                child: RepeatWiseButton(
                  text: 'Primary Button',
                  icon: Icons.save,
                  onPressed: () => debugPrint('Primary button pressed'),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: RepeatWiseButton(
                  text: 'Destructive',
                  isDestructive: true,
                  onPressed: () => debugPrint('Destructive button pressed'),
                ),
              ),
            ],
          ),
        ),

        // Common Text Field
        _buildExampleCard(
          'RepeatWiseTextField',
          RepeatWiseTextField(
            label: 'Email',
            hint: 'Enter your email',
            prefixIcon: const Icon(Icons.email),
            onChanged: (value) => debugPrint('Text changed: $value'),
          ),
        ),

        // Search Field
        _buildExampleCard(
          'RepeatWiseSearchField',
          RepeatWiseSearchField(
            hint: 'Search sets...',
            onChanged: (query) => debugPrint('Search: $query'),
            onClear: () => debugPrint('Search cleared'),
          ),
        ),

        // Common Chip
        _buildExampleCard(
          'RepeatWiseChip',
          Wrap(
            spacing: 8,
            children: [
              RepeatWiseChip(
                label: 'Active',
                selected: true,
                onTap: () => debugPrint('Chip tapped'),
              ),
              RepeatWiseChip(
                label: 'Completed',
                onTap: () => debugPrint('Chip tapped'),
              ),
              RepeatWiseChip(
                label: 'Removable',
                onDelete: () => debugPrint('Chip deleted'),
              ),
            ],
          ),
        ),

        // Common Badge
        _buildExampleCard(
          'RepeatWiseBadge',
          const Row(
            children: [
              RepeatWiseBadge(
                label: '3',
                child: Icon(Icons.notifications),
              ),
              SizedBox(width: 16),
              RepeatWiseBadge(
                label: '99+',
                child: Icon(Icons.mail),
              ),
            ],
          ),
        ),

        // Common Avatar
        _buildExampleCard(
          'RepeatWiseAvatar',
          const Row(
            children: [
              RepeatWiseAvatar(
                imageUrl: 'https://example.com/avatar.jpg',
                name: 'John Doe',
                size: 48,
              ),
              SizedBox(width: 16),
              RepeatWiseAvatar(
                name: 'Jane Smith',
                size: 48,
              ),
            ],
          ),
        ),

        // Common Divider
        _buildExampleCard(
          'RepeatWiseDivider',
          const Column(
            children: [
              Text('Content above'),
              RepeatWiseDivider(),
              Text('Content below'),
            ],
          ),
        ),

        // Responsive Layout
        _buildExampleCard(
          'RepeatWiseResponsiveLayout',
          RepeatWiseResponsiveLayout(
            mobile: Container(
              padding: const EdgeInsets.all(16),
              color: Colors.blue.withOpacity(0.1),
              child: const Text('Mobile Layout'),
            ),
            tablet: Container(
              padding: const EdgeInsets.all(16),
              color: Colors.green.withOpacity(0.1),
              child: const Text('Tablet Layout'),
            ),
            desktop: Container(
              padding: const EdgeInsets.all(16),
              color: Colors.orange.withOpacity(0.1),
              child: const Text('Desktop Layout'),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildAppSpecificWidgetsExamples(BuildContext context) {
    // Sample data for examples
    final sampleSet = Set(
      id: '1',
      name: 'Sample Learning Set',
      description: 'This is a sample learning set for demonstration',
      totalItems: 25,
      status: SetStatus.active,
      userId: 'user1',
      createdAt: DateTime.now().subtract(const Duration(days: 10)),
      updatedAt: DateTime.now().subtract(const Duration(days: 1)),
      lastReviewedAt: DateTime.now().subtract(const Duration(days: 2)),
    );

    final sampleCycle = SetCycle(
      id: '1',
      setId: '1',
      cycleNo: 1,
      totalItems: 25,
      completedItems: 15,
      status: CycleStatus.inProgress,
      startedAt: DateTime.now().subtract(const Duration(days: 1)),
      createdAt: DateTime.now().subtract(const Duration(days: 5)),
      updatedAt: DateTime.now().subtract(const Duration(days: 1)),
    );

    final sampleReminder = RemindSchedule(
      id: '1',
      setId: '1',
      userId: 'user1',
      remindTime: DateTime.now().add(const Duration(hours: 2)),
      status: RemindStatus.pending,
      message: 'Time to review your learning progress',
      createdAt: DateTime.now().subtract(const Duration(days: 1)),
      updatedAt: DateTime.now(),
    );

    return Column(
      children: [
        // Progress Indicator
        _buildExampleCard(
          'RepeatWiseProgressIndicator',
          const Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              RepeatWiseProgressIndicator(
                progress: 0.25,
                size: 60,
                label: '25%',
              ),
              RepeatWiseProgressIndicator(
                progress: 0.75,
                size: 60,
                label: '75%',
              ),
              RepeatWiseProgressIndicator(
                progress: 1.0,
                size: 60,
                label: '100%',
              ),
            ],
          ),
        ),

        // Set Card
        _buildExampleCard(
          'RepeatWiseSetCard',
          RepeatWiseSetCard(
            set: sampleSet,
            onTap: () => debugPrint('Set card tapped'),
          ),
        ),

        // Cycle Card
        _buildExampleCard(
          'RepeatWiseCycleCard',
          RepeatWiseCycleCard(
            cycle: sampleCycle,
            onTap: () => debugPrint('Cycle card tapped'),
          ),
        ),

        // Reminder Card
        _buildExampleCard(
          'RepeatWiseReminderCard',
          RepeatWiseReminderCard(
            reminder: sampleReminder,
            onTap: () => debugPrint('Reminder card tapped'),
            onToggle: () => debugPrint('Reminder toggled'),
          ),
        ),

        // Streak Counter
        _buildExampleCard(
          'RepeatWiseStreakCounter',
          const RepeatWiseStreakCounter(
            currentStreak: 7,
            longestStreak: 15,
            totalDays: 45,
          ),
        ),

        // Study Timer
        _buildExampleCard(
          'RepeatWiseStudyTimer',
          RepeatWiseStudyTimer(
            durationMinutes: 25,
            onComplete: () => debugPrint('Study session completed'),
            onPause: () => debugPrint('Study session paused'),
            onResume: () => debugPrint('Study session resumed'),
          ),
        ),

        // Quick Action Buttons
        _buildExampleCard(
          'RepeatWiseQuickActionButton',
          Row(
            children: [
              Expanded(
                child: RepeatWiseQuickActionButton(
                  label: 'Create Set',
                  icon: Icons.add,
                  onTap: () => debugPrint('Create set pressed'),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: RepeatWiseQuickActionButton(
                  label: 'Start Study',
                  icon: Icons.play_arrow,
                  onTap: () => debugPrint('Start study pressed'),
                ),
              ),
            ],
          ),
        ),

        // Statistics Cards
        _buildExampleCard(
          'RepeatWiseStatisticsCard',
          const Column(
            children: [
              RepeatWiseStatisticsCard(
                title: 'Active Sets',
                value: '12',
                icon: Icons.list,
                subtitle: 'This week',
              ),
              SizedBox(height: 16),
              RepeatWiseStatisticsCard(
                title: 'Completed Cycles',
                value: '45',
                icon: Icons.check_circle,
                subtitle: 'Total',
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildExampleCard(String title, Widget child) {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 12),
            child,
          ],
        ),
      ),
    );
  }
}
