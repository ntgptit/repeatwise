import 'package:flutter/material.dart' hide DateUtils;
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/utils.dart';
import '../../../../core/models/set.dart';
import '../../../../core/models/set_cycle.dart';
import '../../providers/set_providers.dart';

class SetDetailPage extends ConsumerWidget {
  final String setId;

  const SetDetailPage({super.key, required this.setId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final setAsync = ref.watch(setDetailNotifierProvider(setId));

    return Scaffold(
      appBar: AppBar(
        title: const Text('Set Details'),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: () {
              Navigator.of(context).pushNamed('/sets/$setId/edit');
            },
          ),
          IconButton(
            icon: const Icon(Icons.more_vert),
            onPressed: () {
              _showMoreOptions(context, ref);
            },
          ),
        ],
      ),
      body: setAsync.when(
        data: (set) {
          if (set == null) {
            return const Center(child: Text('Set not found'));
          }

          return SingleChildScrollView(
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
                              backgroundColor: AppTheme.primaryColor
                                  .withOpacity(0.1),
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
                                    set.name,
                                    style: Theme.of(context)
                                        .textTheme
                                        .headlineSmall
                                        ?.copyWith(fontWeight: FontWeight.bold),
                                  ),
                                  if (set.description != null &&
                                      set.description!.isNotEmpty)
                                    Text(
                                      set.description!,
                                      style: Theme.of(context)
                                          .textTheme
                                          .bodyMedium
                                          ?.copyWith(color: Colors.grey[600]),
                                    ),
                                ],
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 16),
                        Row(
                          children: [
                            Expanded(
                              child: _InfoItem(
                                label: 'Status',
                                value: set.status.name.toUpperCase(),
                                color: _getStatusColor(set.status),
                              ),
                            ),
                            Expanded(
                              child: _InfoItem(
                                label: 'Progress',
                                value: set.progressPercentage,
                                color: AppTheme.primaryColor,
                              ),
                            ),
                            Expanded(
                              child: _InfoItem(
                                label: 'Cycles',
                                value:
                                    '${set.completedCycles}/${set.wordCount}',
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
                  style: Theme.of(
                    context,
                  ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                Row(
                  children: [
                    if (set.isActive && !set.isLearning)
                      Expanded(
                        child: ElevatedButton.icon(
                          onPressed: () => _startLearning(context, ref),
                          icon: const Icon(Icons.play_arrow),
                          label: const Text('Start Learning'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: AppTheme.successColor,
                            foregroundColor: Colors.white,
                          ),
                        ),
                      ),
                    if (set.isLearning)
                      Expanded(
                        child: ElevatedButton.icon(
                          onPressed: () => _markAsMastered(context, ref),
                          icon: const Icon(Icons.check_circle),
                          label: const Text('Mark as Mastered'),
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
                  style: Theme.of(
                    context,
                  ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                if (set.cycles != null && set.cycles!.isNotEmpty)
                  ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: set.cycles!.length,
                    itemBuilder: (context, index) {
                      final cycle = set.cycles![index];
                      return _CycleCard(
                        cycleNo: cycle.cycleNo,
                        status: cycle.statusDisplayName,
                        startDate: cycle.startedAt ?? cycle.createdAt,
                        endDate: cycle.completedAt,
                      );
                    },
                  )
                else
                  const Card(
                    child: Padding(
                      padding: EdgeInsets.all(16.0),
                      child: Text('No cycles yet'),
                    ),
                  ),
                const SizedBox(height: 24),

                // Statistics
                Text(
                  'Statistics',
                  style: Theme.of(
                    context,
                  ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                _buildStatisticsCard(context, ref, setId),
              ],
            ),
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, stack) => Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.error_outline, size: 64, color: AppTheme.errorColor),
              const SizedBox(height: 16),
              Text(
                'Error loading set',
                style: Theme.of(context).textTheme.headlineSmall,
              ),
              const SizedBox(height: 8),
              Text(
                error.toString(),
                style: Theme.of(
                  context,
                ).textTheme.bodyMedium?.copyWith(color: Colors.grey[600]),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: () {
                  ref
                      .read(setDetailNotifierProvider(setId).notifier)
                      .refreshSet();
                },
                child: const Text('Retry'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Color _getStatusColor(SetStatus status) {
    switch (status) {
      case SetStatus.active:
        return AppTheme.successColor;
      case SetStatus.inactive:
        return AppTheme.warningColor;
      case SetStatus.archived:
        return Colors.grey;
      case SetStatus.paused:
        return AppTheme.warningColor;
      case SetStatus.completed:
        return AppTheme.successColor;
      case SetStatus.learning:
        return AppTheme.primaryColor;
    }
  }

  void _showMoreOptions(BuildContext context, WidgetRef ref) {
    showModalBottomSheet(
      context: context,
      builder: (context) => Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          ListTile(
            leading: const Icon(Icons.edit),
            title: const Text('Edit Set'),
            onTap: () {
              Navigator.of(context).pop();
              Navigator.of(context).pushNamed('/sets/$setId/edit');
            },
          ),
          ListTile(
            leading: const Icon(Icons.copy),
            title: const Text('Duplicate Set'),
            onTap: () {
              Navigator.of(context).pop();
              // TODO: Implement duplicate functionality
            },
          ),
          ListTile(
            leading: const Icon(Icons.archive),
            title: const Text('Archive Set'),
            onTap: () {
              Navigator.of(context).pop();
              // TODO: Implement archive functionality
            },
          ),
          ListTile(
            leading: const Icon(Icons.delete, color: Colors.red),
            title: const Text(
              'Delete Set',
              style: TextStyle(color: Colors.red),
            ),
            onTap: () {
              Navigator.of(context).pop();
              _showDeleteConfirmation(context, ref);
            },
          ),
        ],
      ),
    );
  }

  void _showDeleteConfirmation(BuildContext context, WidgetRef ref) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Set'),
        content: const Text(
          'Are you sure you want to delete this set? This action cannot be undone.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () async {
              Navigator.of(context).pop();
              try {
                await ref.read(setsNotifierProvider.notifier).deleteSet(setId);
                if (context.mounted) {
                  Navigator.of(context).pop();
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Set deleted successfully'),
                      backgroundColor: AppTheme.successColor,
                    ),
                  );
                }
              } catch (e) {
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('Error deleting set: $e'),
                      backgroundColor: AppTheme.errorColor,
                    ),
                  );
                }
              }
            },
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
  }

  void _startLearning(BuildContext context, WidgetRef ref) async {
    try {
      await ref.read(setDetailNotifierProvider(setId).notifier).startLearning();
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Started learning set'),
            backgroundColor: AppTheme.successColor,
          ),
        );
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Error starting learning: $e'),
            backgroundColor: AppTheme.errorColor,
          ),
        );
      }
    }
  }

  void _markAsMastered(BuildContext context, WidgetRef ref) async {
    try {
      await ref
          .read(setDetailNotifierProvider(setId).notifier)
          .markAsMastered();
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Set marked as mastered'),
            backgroundColor: AppTheme.successColor,
          ),
        );
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Error marking as mastered: $e'),
            backgroundColor: AppTheme.errorColor,
          ),
        );
      }
    }
  }

  Widget _buildStatisticsCard(
    BuildContext context,
    WidgetRef ref,
    String setId,
  ) {
    final statsAsync = ref.watch(setStatisticsNotifierProvider(setId));

    return statsAsync.when(
      data: (stats) {
        if (stats == null) {
          return const Card(
            child: Padding(
              padding: EdgeInsets.all(16.0),
              child: Text('No statistics available'),
            ),
          );
        }

        return Card(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Learning Statistics',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 16),
                // TODO: Display statistics based on the actual data structure
                const Text('Statistics will be displayed here'),
              ],
            ),
          ),
        );
      },
      loading: () => const Card(
        child: Padding(
          padding: EdgeInsets.all(16.0),
          child: Center(child: CircularProgressIndicator()),
        ),
      ),
      error: (error, stack) => Card(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Text('Error loading statistics: $error'),
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
        Text(label, style: TextStyle(fontSize: 12, color: Colors.grey[600])),
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
        trailing: StatusUtils.createStatusBadge(
          status,
          _getStatusColor(status),
        ),
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

  const _ReminderCard({required this.time, required this.message});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: AppTheme.warningColor.withOpacity(0.1),
          child: const Icon(Icons.notifications, color: AppTheme.warningColor),
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
