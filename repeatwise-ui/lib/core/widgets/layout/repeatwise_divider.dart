import 'package:flutter/material.dart';

/// RepeatWise divider with Material 3 design
class RepeatWiseDivider extends StatelessWidget {
  final double height;
  final EdgeInsetsGeometry? margin;
  final Color? color;

  const RepeatWiseDivider({
    super.key,
    this.height = 1,
    this.margin,
    this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      height: height,
      margin: margin ?? const EdgeInsets.symmetric(vertical: 8),
      color: color ?? Theme.of(context).colorScheme.outline.withOpacity(0.2),
    );
  }
}
