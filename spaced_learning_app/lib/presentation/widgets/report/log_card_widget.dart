// lib/presentation/widgets/report/log_card_widget.dart
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/viewmodels/daily_task_report_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/report/log_entry_widget.dart';

class LogCardWidget extends StatelessWidget {
  final List<LogEntry> logEntries;
  final VoidCallback onClearLogs;

  const LogCardWidget({
    super.key,
    required this.logEntries,
    required this.onClearLogs,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final dateFormat = DateFormat('dd/MM/yyyy HH:mm:ss');

    return Card(
      elevation: AppDimens.elevationM,
      color: colorScheme.surfaceContainerLowest,
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Check Logs',
                  style: theme.textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                TextButton.icon(
                  onPressed: onClearLogs,
                  icon: const Icon(Icons.delete_outline, size: 18),
                  label: const Text('Clear Logs'),
                  style: TextButton.styleFrom(
                    foregroundColor: colorScheme.error,
                  ),
                ),
              ],
            ),
            const SizedBox(height: AppDimens.spaceM),
            const Divider(height: 1),
            const SizedBox(height: AppDimens.spaceM),
            _buildLogList(
              logs: logEntries,
              theme: theme,
              colorScheme: colorScheme,
              dateFormat: dateFormat,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildLogList({
    required List<LogEntry> logs,
    required ThemeData theme,
    required ColorScheme colorScheme,
    required DateFormat dateFormat,
  }) {
    if (logs.isEmpty) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(AppDimens.paddingXL),
          child: Column(
            children: [
              Icon(
                Icons.history,
                size: AppDimens.iconXXL,
                color: colorScheme.onSurfaceVariant.withValues(alpha: 0.3),
              ),
              const SizedBox(height: AppDimens.spaceM),
              Text(
                'No logs available',
                style: theme.textTheme.bodyLarge?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
              ),
            ],
          ),
        ),
      );
    }

    return ListView.separated(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      itemCount: logs.length,
      separatorBuilder: (context, index) =>
          const Divider(height: AppDimens.spaceL),
      itemBuilder: (context, index) {
        final log = logs[index];
        return LogEntryWidget(log: log, dateFormat: dateFormat);
      },
    );
  }
}
