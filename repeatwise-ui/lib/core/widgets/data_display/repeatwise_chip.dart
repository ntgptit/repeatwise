import 'package:flutter/material.dart';

/// RepeatWise chip with Material 3 design
class RepeatWiseChip extends StatelessWidget {
  final String label;
  final VoidCallback? onTap;
  final VoidCallback? onDelete;
  final bool selected;
  final Color? backgroundColor;
  final Color? foregroundColor;

  const RepeatWiseChip({
    super.key,
    required this.label,
    this.onTap,
    this.onDelete,
    this.selected = false,
    this.backgroundColor,
    this.foregroundColor,
  });

  @override
  Widget build(BuildContext context) {
    return FilterChip(
      label: Text(label),
      selected: selected,
      onSelected: onTap != null ? (_) => onTap!() : null,
      onDeleted: onDelete,
      backgroundColor: backgroundColor,
      selectedColor: Theme.of(context).colorScheme.primaryContainer,
      checkmarkColor: Theme.of(context).colorScheme.onPrimaryContainer,
      labelStyle: TextStyle(
        color: foregroundColor ?? Theme.of(context).colorScheme.onSurface,
      ),
    );
  }
}
