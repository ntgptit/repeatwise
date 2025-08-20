// lib/presentation/screens/report/widgets/log_entry_widget.dart
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/utils/date_utils.dart';
import 'package:spaced_learning_app/presentation/viewmodels/daily_task_report_viewmodel.dart';

class LogEntryWidget extends StatelessWidget {
  final LogEntry log;
  final DateFormat dateFormat;

  const LogEntryWidget({
    super.key,
    required this.log,
    required this.dateFormat,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Icon(
              log.isSuccess ? Icons.check_circle : Icons.error,
              size: AppDimens.iconS,
              color: log.isSuccess ? colorScheme.primary : colorScheme.error,
            ),
            const SizedBox(width: AppDimens.spaceXS),
            Text(
              AppDateUtils.formatDate(
                log.timestamp,
                format: 'dd/MM/yyyy HH:mm:ss',
              ),
              style: theme.textTheme.bodySmall?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceXS),
        Text(log.message, style: theme.textTheme.bodyMedium),
        if (log.detail != null) ...[
          const SizedBox(height: AppDimens.spaceXS),
          Text(
            log.detail!,
            style: theme.textTheme.bodySmall?.copyWith(
              color: log.isSuccess
                  ? colorScheme.onSurfaceVariant
                  : colorScheme.error,
            ),
          ),
        ],
      ],
    );
  }
}
