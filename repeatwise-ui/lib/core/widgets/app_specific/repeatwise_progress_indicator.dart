import 'package:flutter/material.dart';

/// RepeatWise progress indicator widget for learning sets
class RepeatWiseProgressIndicator extends StatelessWidget {
  final double progress;
  final double size;
  final Color? color;
  final String? label;
  final bool showPercentage;

  const RepeatWiseProgressIndicator({
    super.key,
    required this.progress,
    this.size = 60,
    this.color,
    this.label,
    this.showPercentage = true,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final progressColor = color ?? theme.colorScheme.primary;
    final clampedProgress = progress.clamp(0.0, 1.0);

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        SizedBox(
          width: size,
          height: size,
          child: Stack(
            alignment: Alignment.center,
            children: [
              CircularProgressIndicator(
                value: clampedProgress,
                strokeWidth: 4,
                valueColor: AlwaysStoppedAnimation<Color>(progressColor),
                backgroundColor: theme.colorScheme.surfaceVariant,
              ),
              if (showPercentage)
                Text(
                  '${(clampedProgress * 100).toInt()}%',
                  style: theme.textTheme.labelSmall?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: progressColor,
                  ),
                ),
            ],
          ),
        ),
        if (label != null) ...[
          const SizedBox(height: 8),
          Text(
            label!,
            style: theme.textTheme.bodySmall?.copyWith(
              color: theme.colorScheme.onSurfaceVariant,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ],
    );
  }
}
