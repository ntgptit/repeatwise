import 'package:flutter/material.dart' hide DateUtils;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/models/set.dart';
import '../../../../core/theme/app_theme.dart';
import '../../../../core/utils/utils.dart';

class SetListItem extends ConsumerWidget {
  final Set set;
  final VoidCallback? onTap;
  final VoidCallback? onEdit;
  final VoidCallback? onDelete;
  final VoidCallback? onStartLearning;
  final VoidCallback? onMarkAsMastered;

  const SetListItem({
    super.key,
    required this.set,
    this.onTap,
    this.onEdit,
    this.onDelete,
    this.onStartLearning,
    this.onMarkAsMastered,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ListTile(
        contentPadding: const EdgeInsets.all(16),
        leading: CircleAvatar(
          backgroundColor: _getStatusColor(set.status).withOpacity(0.1),
          child: Icon(Icons.list, color: _getStatusColor(set.status)),
        ),
        title: Text(
          set.name,
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 4),
            if (set.description != null && set.description!.isNotEmpty)
              Text(set.description!),
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  child: LinearProgressIndicator(
                    value: set.progress,
                    backgroundColor: Colors.grey[300],
                  ),
                ),
                const SizedBox(width: 8),
                Text(
                  set.progressPercentage,
                  style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                StatusUtils.createStatusBadge(
                  set.status.name.toUpperCase(),
                  _getStatusColor(set.status),
                ),
                const Spacer(),
                Text(
                  '${set.completedCycles}/${set.wordCount} cycles',
                  style: TextStyle(fontSize: 12, color: Colors.grey[500]),
                ),
              ],
            ),
            if (set.lastReviewedAt != null) ...[
              const SizedBox(height: 4),
              Text(
                'Last: ${DateUtils.formatShortDate(set.lastReviewedAt!)}',
                style: TextStyle(fontSize: 12, color: Colors.grey[500]),
              ),
            ],
          ],
        ),
        trailing: PopupMenuButton<String>(
          itemBuilder: (context) => [
            if (set.isActive && !set.isLearning)
              UiUtils.createPopupMenuItem(
                value: 'start_learning',
                icon: Icons.play_arrow,
                text: 'Start Learning',
              ),
            if (set.isLearning)
              UiUtils.createPopupMenuItem(
                value: 'mark_mastered',
                icon: Icons.check_circle,
                text: 'Mark as Mastered',
              ),
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
            switch (value) {
              case 'start_learning':
                onStartLearning?.call();
                break;
              case 'mark_mastered':
                onMarkAsMastered?.call();
                break;
              case 'edit':
                onEdit?.call();
                break;
              case 'duplicate':
                // TODO: Implement duplicate functionality
                break;
              case 'archive':
                // TODO: Implement archive functionality
                break;
              case 'delete':
                _showDeleteConfirmation(context);
                break;
            }
          },
        ),
        onTap: onTap,
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

  void _showDeleteConfirmation(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Set'),
        content: Text(
          'Are you sure you want to delete "${set.name}"? This action cannot be undone.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              onDelete?.call();
            },
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
  }
}
