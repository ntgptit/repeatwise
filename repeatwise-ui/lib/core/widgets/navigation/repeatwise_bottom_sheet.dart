import 'package:flutter/material.dart';

/// RepeatWise bottom sheet with Material 3 design
class RepeatWiseBottomSheet extends StatelessWidget {
  final Widget child;
  final String? title;
  final List<Widget>? actions;
  final bool isScrollControlled;
  final bool showDragHandle;

  const RepeatWiseBottomSheet({
    super.key,
    required this.child,
    this.title,
    this.actions,
    this.isScrollControlled = false,
    this.showDragHandle = true,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surface,
        borderRadius: const BorderRadius.vertical(top: Radius.circular(28)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (showDragHandle) ...[
            Container(
              margin: const EdgeInsets.only(top: 12),
              width: 32,
              height: 4,
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.outline.withOpacity(0.4),
                borderRadius: BorderRadius.circular(2),
              ),
            ),
          ],
          if (title != null) ...[
            Padding(
              padding: const EdgeInsets.fromLTRB(24, 16, 24, 8),
              child: Row(
                children: [
                  Expanded(
                    child: Text(
                      title!,
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                  ),
                  if (actions != null) ...actions!,
                ],
              ),
            ),
          ],
          Flexible(
            child: Padding(
              padding: const EdgeInsets.fromLTRB(24, 0, 24, 24),
              child: child,
            ),
          ),
        ],
      ),
    );
  }
}
