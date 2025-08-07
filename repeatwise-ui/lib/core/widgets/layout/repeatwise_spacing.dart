import 'package:flutter/material.dart';

/// RepeatWise spacing widget
class RepeatWiseSpacing extends StatelessWidget {
  final double height;
  final double width;

  const RepeatWiseSpacing({
    super.key,
    this.height = 16,
    this.width = 0,
  });

  const RepeatWiseSpacing.vertical(double height) : this(height: height, width: 0);
  const RepeatWiseSpacing.horizontal(double width) : this(height: 0, width: width);

  @override
  Widget build(BuildContext context) {
    return SizedBox(height: height, width: width);
  }
}
