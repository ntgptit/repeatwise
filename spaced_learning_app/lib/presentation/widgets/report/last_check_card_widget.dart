// lib/presentation/widgets/report/last_check_card_widget.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/utils/date_utils.dart';
import 'package:spaced_learning_app/presentation/widgets/report/info_row_widget.dart';

class LastCheckCardWidget extends StatelessWidget {
  final DateTime? lastCheckTime;
  final bool lastCheckResult;
  final int lastCheckTaskCount;
  final String? lastCheckError;

  const LastCheckCardWidget({
    super.key,
    required this.lastCheckTime,
    required this.lastCheckResult,
    required this.lastCheckTaskCount,
    this.lastCheckError,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Card(
      elevation: AppDimens.elevationM,
      color: colorScheme.surfaceContainerLowest,
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Last Check Results',
              style: theme.textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: AppDimens.spaceM),
            const Divider(height: 1),
            const SizedBox(height: AppDimens.spaceM),
            InfoRowWidget(
              label: 'Time:',
              value: lastCheckTime != null
                  ? AppDateUtils.formatDate(
                      lastCheckTime!,
                      format: 'dd/MM/yyyy HH:mm:ss',
                    )
                  : 'No data',
              icon: Icons.access_time,
            ),
            const SizedBox(height: AppDimens.spaceM),
            InfoRowWidget(
              label: 'Status:',
              value: lastCheckResult ? 'Success' : 'Failed',
              icon: lastCheckResult ? Icons.check_circle : Icons.error,
              valueColor: lastCheckResult
                  ? colorScheme.primary
                  : colorScheme.error,
            ),
            const SizedBox(height: AppDimens.spaceM),
            InfoRowWidget(
              label: 'Tasks:',
              value: lastCheckTaskCount > 0
                  ? '$lastCheckTaskCount tasks due'
                  : 'No tasks due',
              icon: Icons.assignment,
            ),
            if (lastCheckError != null) ...[
              const SizedBox(height: AppDimens.spaceM),
              InfoRowWidget(
                label: 'Error:',
                value: lastCheckError!,
                icon: Icons.warning_amber,
                valueColor: colorScheme.error,
              ),
            ],
          ],
        ),
      ),
    );
  }
}
