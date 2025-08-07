import 'package:flutter/material.dart';
import '../../models/set.dart';
import '../../utils/date_utils.dart' as AppDateUtils;
import '../layout/repeatwise_card.dart';

/// RepeatWise set card widget for displaying learning sets
class RepeatWiseSetCard extends StatelessWidget {
  final Set set;
  final VoidCallback? onTap;
  final VoidCallback? onLongPress;

  const RepeatWiseSetCard({
    super.key,
    required this.set,
    this.onTap,
    this.onLongPress,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return RepeatWiseCard(
      onTap: onTap,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      set.name,
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.w600,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    ...[
                    const SizedBox(height: 4),
                    Text(
                      set.description,
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                  ],
                ),
              ),
              const SizedBox(width: 12),
              _buildStatusChip(context),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              Icon(
                Icons.schedule,
                size: 16,
                color: theme.colorScheme.onSurfaceVariant,
              ),
              const SizedBox(width: 4),
              Text(
                '${set.totalItems} items',
                style: theme.textTheme.bodySmall?.copyWith(
                  color: theme.colorScheme.onSurfaceVariant,
                ),
              ),
              const Spacer(),
              if (set.lastReviewedAt != null)
                Text(
                  'Reviewed ${AppDateUtils.DateUtils.formatRelativeDate(set.lastReviewedAt!)}',
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: theme.colorScheme.onSurfaceVariant,
                  ),
                ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStatusChip(BuildContext context) {
    final theme = Theme.of(context);
    Color backgroundColor;
    Color textColor;
    String text;

    switch (set.status) {
      case SetStatus.active:
        backgroundColor = theme.colorScheme.primaryContainer;
        textColor = theme.colorScheme.onPrimaryContainer;
        text = 'Active';
        break;
      case SetStatus.paused:
        backgroundColor = theme.colorScheme.tertiaryContainer;
        textColor = theme.colorScheme.onTertiaryContainer;
        text = 'Paused';
        break;
      case SetStatus.completed:
        backgroundColor = theme.colorScheme.secondaryContainer;
        textColor = theme.colorScheme.onSecondaryContainer;
        text = 'Completed';
        break;
      case SetStatus.inactive:
        // TODO: Handle this case.
        throw UnimplementedError();
      case SetStatus.archived:
        // TODO: Handle this case.
        throw UnimplementedError();
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        text,
        style: theme.textTheme.labelSmall?.copyWith(
          color: textColor,
          fontWeight: FontWeight.w500,
        ),
      ),
    );
  }


}
