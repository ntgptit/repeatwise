import 'package:flutter/material.dart';

/// RepeatWise button with Material 3 design
class RepeatWiseButton extends StatelessWidget {
  final String text;
  final VoidCallback? onPressed;
  final bool isLoading;
  final IconData? icon;
  final ButtonStyle? style;
  final bool isDestructive;

  const RepeatWiseButton({
    super.key,
    required this.text,
    this.onPressed,
    this.isLoading = false,
    this.icon,
    this.style,
    this.isDestructive = false,
  });

  @override
  Widget build(BuildContext context) {
    final buttonStyle = style ??
        (isDestructive
            ? FilledButton.styleFrom(
                backgroundColor: Theme.of(context).colorScheme.error,
                foregroundColor: Theme.of(context).colorScheme.onError,
              )
            : null);

    return FilledButton.icon(
      onPressed: isLoading ? null : onPressed,
      icon: isLoading
          ? SizedBox(
              width: 16,
              height: 16,
              child: CircularProgressIndicator(
                strokeWidth: 2,
                valueColor: AlwaysStoppedAnimation<Color>(
                  Theme.of(context).colorScheme.onPrimary,
                ),
              ),
            )
          : icon != null
              ? Icon(icon)
              : const SizedBox.shrink(),
      label: Text(text),
      style: buttonStyle,
    );
  }
}
