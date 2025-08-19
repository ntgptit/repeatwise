// lib/presentation/widgets/learning/main/learning_app_bar.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class LearningAppBar extends ConsumerWidget {
  final bool isScrolled;
  final VoidCallback onRefresh;
  final VoidCallback onHelp;

  const LearningAppBar({
    super.key,
    required this.isScrolled,
    required this.onRefresh,
    required this.onHelp,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final colorScheme = Theme.of(context).colorScheme;

    return SliverAppBar(
      title: const Text('Learning Progress'),
      pinned: true,
      floating: true,
      elevation: isScrolled ? AppDimens.elevationS : 0,
      shadowColor: Colors.black.withValues(alpha: AppDimens.opacityLight),
      backgroundColor: isScrolled
          ? colorScheme.surface
          : colorScheme.surface.withValues(alpha: AppDimens.opacityVeryHigh),
      foregroundColor: colorScheme.onSurface,
      leading: IconButton(
        icon: const Icon(Icons.arrow_back),
        onPressed: () {
          final router = GoRouter.of(context);
          if (router.canPop()) {
            router.pop();
            return;
          }
          router.go('/');
        },
        tooltip: 'Back',
      ),
      actions: [
        IconButton(
          icon: const Icon(Icons.refresh),
          iconSize: AppDimens.iconL,
          tooltip: 'Refresh data',
          padding: const EdgeInsets.all(AppDimens.paddingS),
          onPressed: onRefresh,
        ),
        IconButton(
          icon: const Icon(Icons.help_outline),
          iconSize: AppDimens.iconL,
          tooltip: 'Help',
          padding: const EdgeInsets.all(AppDimens.paddingS),
          onPressed: onHelp,
        ),
      ],
    );
  }
}
