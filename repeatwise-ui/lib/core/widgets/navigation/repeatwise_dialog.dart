import 'package:flutter/material.dart';

/// RepeatWise dialog with Material 3 design
class RepeatWiseDialog extends StatelessWidget {
  final String title;
  final Widget content;
  final List<Widget>? actions;
  final bool barrierDismissible;

  const RepeatWiseDialog({
    super.key,
    required this.title,
    required this.content,
    this.actions,
    this.barrierDismissible = true,
  });

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(title),
      content: content,
      actions: actions,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(28),
      ),
    );
  }
}
