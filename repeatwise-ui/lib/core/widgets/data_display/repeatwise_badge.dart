import 'package:flutter/material.dart';

/// RepeatWise badge with Material 3 design
class RepeatWiseBadge extends StatelessWidget {
  final Widget child;
  final String? label;
  final Color? backgroundColor;
  final Color? labelColor;
  final bool isLabelVisible;

  const RepeatWiseBadge({
    super.key,
    required this.child,
    this.label,
    this.backgroundColor,
    this.labelColor,
    this.isLabelVisible = true,
  });

  @override
  Widget build(BuildContext context) {
    if (label == null || !isLabelVisible) {
      return child;
    }

    return Badge(
      label: Text(
        label!,
        style: TextStyle(
          color: labelColor ?? Theme.of(context).colorScheme.onError,
          fontSize: 12,
        ),
      ),
      backgroundColor: backgroundColor ?? Theme.of(context).colorScheme.error,
      child: child,
    );
  }
}
