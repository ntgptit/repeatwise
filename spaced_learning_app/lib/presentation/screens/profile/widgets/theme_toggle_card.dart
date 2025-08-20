// lib/presentation/screens/profile/widgets/theme_toggle_card.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/app_theme_data.dart';

class ThemeToggleCard extends ConsumerWidget {
  const ThemeToggleCard({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    final themeMode = ref.watch(themeModeStateProvider);
    final isDarkMode = themeMode == ThemeMode.dark;

    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      elevation: 0,
      color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.5),
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingS),
        child: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(AppDimens.paddingM),
              decoration: BoxDecoration(
                color: isDarkMode
                    ? colorScheme.tertiary.withValues(alpha: 0.1)
                    : colorScheme.primary.withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Icon(
                isDarkMode ? Icons.dark_mode : Icons.light_mode,
                color: isDarkMode ? colorScheme.tertiary : colorScheme.primary,
                size: 24,
              ),
            ),
            const SizedBox(width: AppDimens.spaceL),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Dark Mode',
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  Text(
                    isDarkMode ? 'On' : 'Off',
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: colorScheme.onSurface.withValues(alpha: 0.6),
                    ),
                  ),
                ],
              ),
            ),
            Switch(
              value: isDarkMode,
              onChanged: (value) =>
                  ref.read(themeModeStateProvider.notifier).toggleTheme(),
              activeColor: colorScheme.tertiary,
            ),
          ],
        ),
      ),
    );
  }
}
