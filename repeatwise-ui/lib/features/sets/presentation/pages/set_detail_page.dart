import 'package:flutter/material.dart' hide DateUtils;
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/utils.dart';

class SetDetailPage extends ConsumerWidget {
  final String setId;

  const SetDetailPage({super.key, required this.setId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Set Details'),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: () {
              // TODO: Navigate to edit set
            },
          ),
          IconButton(
            icon: const Icon(Icons.more_vert),
            onPressed: () {
              // TODO: Show more options
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Set info card
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        CircleAvatar(
                          backgroundColor: AppTheme.primaryColor.withOpacity(0.1),
                          child: const Icon(
                            Icons.list,
                            color: AppTheme.primaryColor,
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Sample Set',
                                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              Text(
                                'A sample set for demonstration',
                                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                  color: Colors.grey[600],
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    const Row(
                      children: [
                        Expanded(
                          child: _InfoItem(
                            label: 'Status',
                            value: 'Active',
                            color: AppTheme.successColor,
                          ),
                        ),
                        Expanded(
                          child: _InfoItem(
                            label: 'Progress',
                            value: '75%',
                            color: AppTheme.primaryColor,
                          ),
                        ),
                        Expanded(
                          child: _InfoItem(
                            label: 'Cycles',
                            value: '3/5',
                            color: AppTheme.warningColor,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),
            
            // Actions
            Text(
              'Actions',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () {
                      // TODO: Start cycle
                    },
                    icon: const Icon(Icons.play_arrow),
                    label: const Text('Start Cycle'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppTheme.successColor,
                      foregroundColor: Colors.white,
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: () {
                      // TODO: Schedule reminder
                    },
                    icon: const Icon(Icons.notifications),
                    label: const Text('Remind'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),
            
            // Recent cycles
            Text(
              'Recent Cycles',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: 5,
              itemBuilder: (context, index) {
                return _CycleCard(
                  cycleNo: index + 1,
                  status: index < 3 ? 'Completed' : index == 3 ? 'In Progress' : 'Pending',
                  startDate: DateTime.now().subtract(Duration(days: index * 2)),
                  endDate: index < 3 ? DateTime.now().subtract(Duration(days: index * 2 - 1)) : null,
                );
              },
            ),
            const SizedBox(height: 24),
            
            // Reminders
            Text(
              'Upcoming Reminders',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: 2,
              itemBuilder: (context, index) {
                return _ReminderCard(
                  time: DateTime.now().add(Duration(hours: index + 2)),
                  message: 'Time to review cycle ${index + 1}',
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}

class _InfoItem extends StatelessWidget {
  final String label;
  final String value;
  final Color color;

  const _InfoItem({
    required this.label,
    required this.value,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(
          label,
          style: TextStyle(
            fontSize: 12,
            color: Colors.grey[600],
          ),
        ),
        const SizedBox(height: 4),
        Text(
          value,
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
      ],
    );
  }
}

class _CycleCard extends StatelessWidget {
  final int cycleNo;
  final String status;
  final DateTime startDate;
  final DateTime? endDate;

  const _CycleCard({
    required this.cycleNo,
    required this.status,
    required this.startDate,
    this.endDate,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: _getStatusColor(status).withOpacity(0.1),
          child: Text(
            cycleNo.toString(),
            style: TextStyle(
              color: _getStatusColor(status),
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        title: Text('Cycle $cycleNo'),
        subtitle: Text(
          'Started: ${DateUtils.formatFullDate(startDate)}',
          style: const TextStyle(fontSize: 12),
        ),
        trailing: StatusUtils.createStatusBadge(status, _getStatusColor(status)),
      ),
    );
  }

  Color _getStatusColor(String status) {
    return StatusUtils.getCycleStatusColor(status);
  }
}

class _ReminderCard extends StatelessWidget {
  final DateTime time;
  final String message;

  const _ReminderCard({
    required this.time,
    required this.message,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: AppTheme.warningColor.withOpacity(0.1),
          child: const Icon(
            Icons.notifications,
            color: AppTheme.warningColor,
          ),
        ),
        title: Text(message),
        subtitle: Text(
          DateUtils.formatDateTime(time),
          style: const TextStyle(fontSize: 12),
        ),
        trailing: IconButton(
          icon: const Icon(Icons.check),
          onPressed: () {
            // TODO: Mark as completed
          },
        ),
      ),
    );
  }
}
