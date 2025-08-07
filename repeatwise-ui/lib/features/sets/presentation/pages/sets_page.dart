import 'package:flutter/material.dart' hide DateUtils;
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/utils.dart';

class SetsPage extends ConsumerWidget {
  const SetsPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Sets'),
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () {
              // TODO: Implement search
            },
          ),
          IconButton(
            icon: const Icon(Icons.filter_list),
            onPressed: () {
              // TODO: Show filter options
            },
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          // TODO: Refresh sets data
        },
        child: ListView.builder(
          padding: const EdgeInsets.all(16.0),
          itemCount: 10, // TODO: Replace with actual data
          itemBuilder: (context, index) {
            return _SetListItem(
              name: 'Set ${index + 1}',
              description: 'Description for set ${index + 1}',
              status: index % 3 == 0 ? 'Active' : index % 3 == 1 ? 'Inactive' : 'Archived',
              lastReviewed: DateTime.now().subtract(Duration(days: index * 2)),
              progress: (index + 1) * 0.1,
            );
          },
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // TODO: Navigate to create set
        },
        backgroundColor: AppTheme.primaryColor,
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }
}

class _SetListItem extends StatelessWidget {
  final String name;
  final String description;
  final String status;
  final DateTime lastReviewed;
  final double progress;

  const _SetListItem({
    required this.name,
    required this.description,
    required this.status,
    required this.lastReviewed,
    required this.progress,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ListTile(
        contentPadding: const EdgeInsets.all(16),
        leading: CircleAvatar(
          backgroundColor: _getStatusColor(status).withOpacity(0.1),
          child: Icon(
            Icons.list,
            color: _getStatusColor(status),
          ),
        ),
        title: Text(
          name,
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 4),
            Text(description),
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  child: LinearProgressIndicator(
                    value: progress,
                    backgroundColor: Colors.grey[300],
                  ),
                ),
                const SizedBox(width: 8),
                Text(
                  '${(progress * 100).toInt()}%',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey[600],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                StatusUtils.createStatusBadge(status, _getStatusColor(status)),
                const Spacer(),
                Text(
                  'Last: ${DateUtils.formatShortDate(lastReviewed)}',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey[500],
                  ),
                ),
              ],
            ),
          ],
        ),
        trailing: PopupMenuButton<String>(
          itemBuilder: (context) => [
            UiUtils.createPopupMenuItem(
              value: 'edit',
              icon: Icons.edit,
              text: 'Edit',
            ),
            UiUtils.createPopupMenuItem(
              value: 'duplicate',
              icon: Icons.copy,
              text: 'Duplicate',
            ),
            UiUtils.createPopupMenuItem(
              value: 'archive',
              icon: Icons.archive,
              text: 'Archive',
            ),
            UiUtils.createPopupMenuItem(
              value: 'delete',
              icon: Icons.delete,
              text: 'Delete',
              iconColor: Colors.red,
              textColor: Colors.red,
            ),
          ],
          onSelected: (value) {
            // TODO: Handle menu selection
          },
        ),
        onTap: () {
          // TODO: Navigate to set detail
        },
      ),
    );
  }

  Color _getStatusColor(String status) {
    return StatusUtils.getSetStatusColor(status);
  }
}
