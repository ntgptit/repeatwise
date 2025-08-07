import 'package:flutter/material.dart';

/// RepeatWise card widget with Material 3 design
class RepeatWiseCard extends StatelessWidget {
  final Widget child;
  final EdgeInsetsGeometry? padding;
  final EdgeInsetsGeometry? margin;
  final VoidCallback? onTap;
  final bool elevated;

  const RepeatWiseCard({
    super.key,
    required this.child,
    this.padding,
    this.margin,
    this.onTap,
    this.elevated = true,
  });

  @override
  Widget build(BuildContext context) {
    final card = Card(
      elevation: elevated ? 1 : 0,
      margin: margin ?? const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Padding(
        padding: padding ?? const EdgeInsets.all(16),
        child: child,
      ),
    );

    if (onTap != null) {
      return InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: card,
      );
    }

    return card;
  }
}
