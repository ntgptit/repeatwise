// lib/presentation/screens/home/widgets/home_app_bar.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/utils/snackbar_utils.dart';

class HomeAppBar extends StatelessWidget implements PreferredSizeWidget {
  final bool isDarkMode;
  final VoidCallback onThemeToggle;
  final VoidCallback? onMenuPressed;

  const HomeAppBar({
    super.key,
    required this.isDarkMode,
    required this.onThemeToggle,
    this.onMenuPressed,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return AppBar(
      leading: IconButton(
        icon: Icon(
          Icons.menu,
          size: AppDimens.iconL,
          color: colorScheme.onSurface,
        ),
        onPressed: onMenuPressed,
        tooltip: 'Menu',
      ),
      title: Text(
        'Spaced Learning',
        style: theme.textTheme.titleLarge?.copyWith(
          color: colorScheme.onSurface,
        ),
      ),
      backgroundColor: colorScheme.surface,
      actions: [
        _buildThemeToggleButton(theme, colorScheme),
        _buildNotificationsButton(context, theme, colorScheme),
      ],
    );
  }

  Widget _buildThemeToggleButton(ThemeData theme, ColorScheme colorScheme) {
    return IconButton(
      icon: Icon(
        isDarkMode ? Icons.light_mode : Icons.dark_mode,
        size: AppDimens.iconL,
        color: colorScheme.onSurface,
      ),
      onPressed: onThemeToggle,
      tooltip: 'Toggle theme',
    );
  }

  Widget _buildNotificationsButton(
    BuildContext context,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    return IconButton(
      icon: Icon(
        Icons.notifications_outlined,
        size: AppDimens.iconL,
        color: colorScheme.onSurface,
      ),
      onPressed: () => _showNotificationsSnackBar(context, theme),
      tooltip: 'Notifications',
    );
  }

  void _showNotificationsSnackBar(BuildContext context, ThemeData theme) {
    SnackBarUtils.show(
      context,
      'Notifications coming soon',
      backgroundColor: theme.colorScheme.primary,
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}
