// Đảm bảo import themeModeStateProvider
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/constants/app_constants.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';

import '../../../core/theme/app_theme_data.dart';

class SLDrawer extends ConsumerWidget {
  const SLDrawer({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    // Sử dụng themeModeStateProvider thay vì isDarkModeProvider
    final themeMode = ref.watch(themeModeStateProvider);
    final isDarkMode = themeMode == ThemeMode.dark;

    final user = ref.watch(currentUserProvider);
    final userEmail = user?.email ?? 'Not signed in';
    final userName = user?.displayName ?? user?.username ?? 'Guest';
    final isSignedIn = ref.watch(authStateProvider).valueOrNull ?? false;

    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: [
          UserAccountsDrawerHeader(
            decoration: BoxDecoration(color: colorScheme.primary),
            accountName: Text(userName),
            accountEmail: Text(userEmail),
            currentAccountPicture: CircleAvatar(
              backgroundColor: colorScheme.primaryContainer,
              child: Text(
                userName.isNotEmpty ? userName[0].toUpperCase() : 'G',
                style: theme.textTheme.headlineMedium?.copyWith(
                  color: colorScheme.onPrimaryContainer,
                ),
              ),
            ),
          ),
          _buildNavItem(
            context,
            'Home',
            Icons.home,
            () => GoRouter.of(context).go('/'),
            colorScheme,
          ),
          _buildNavItem(
            context,
            'Books Library',
            Icons.book,
            () => GoRouter.of(context).go('/books'),
            colorScheme,
          ),
          _buildNavItem(
            context,
            'Learning Progress',
            Icons.bar_chart,
            () => GoRouter.of(context).go('/learning'),
            colorScheme,
          ),
          _buildNavItem(
            context,
            'Due Progress',
            Icons.calendar_today,
            () => GoRouter.of(context).go('/due-progress'),
            colorScheme,
          ),
          _buildNavItem(
            context,
            'Profile',
            Icons.person,
            () => GoRouter.of(context).go('/profile'),
            colorScheme,
          ),
          const Divider(),
          _buildSettingsItem(context, 'App Settings', [
            _buildSettingTile(
              context,
              'Dark Mode',
              Icons.dark_mode,
              Switch(
                value: isDarkMode,
                // Sử dụng themeModeStateProvider.notifier.toggleTheme
                onChanged: (_) =>
                    ref.read(themeModeStateProvider.notifier).toggleTheme(),
              ),
              colorScheme,
            ),
            _buildSettingTile(
              context,
              'Reminders',
              Icons.notifications,
              IconButton(
                icon: const Icon(Icons.chevron_right),
                onPressed: () => GoRouter.of(context).go('/settings/reminders'),
              ),
              colorScheme,
              onTap: () {
                Navigator.of(
                  context,
                ).pop(); // Đóng drawer trước khi chuyển hướng
                GoRouter.of(context).go('/settings/reminders');
              },
            ),
            _buildSettingTile(
              context,
              'Check Status',
              Icons.checklist,
              IconButton(
                icon: const Icon(Icons.chevron_right),
                onPressed: () => GoRouter.of(context).go('/task-report'),
              ),
              colorScheme,
              onTap: () {
                Navigator.of(
                  context,
                ).pop(); // Đóng drawer trước khi chuyển hướng
                GoRouter.of(context).go('/task-report');
              },
            ),
          ], colorScheme),
          _buildSettingsItem(context, 'Help & Support', [
            _buildSettingTile(
              context,
              'About Spaced Repetition',
              Icons.help_outline,
              IconButton(
                icon: const Icon(Icons.chevron_right),
                onPressed: () =>
                    GoRouter.of(context).go('/help/spaced-repetition'),
              ),
              colorScheme,
              onTap: () {
                Navigator.of(
                  context,
                ).pop(); // Đóng drawer trước khi chuyển hướng
                GoRouter.of(context).go('/help/spaced-repetition');
              },
            ),
            _buildSettingTile(
              context,
              'App Version',
              Icons.info_outline,
              Text(
                'v1.0.0',
                style: theme.textTheme.bodySmall?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
              ),
              colorScheme,
            ),
          ], colorScheme),
          if (isSignedIn) ...[
            const Divider(),
            _buildNavItem(
              context,
              'Logout',
              Icons.logout,
              () async {
                await ref.read(authStateProvider.notifier).logout();
                if (context.mounted) {
                  GoRouter.of(context).go('/login');
                }
              },
              colorScheme,
              isDestructive: true,
            ),
          ],
          const SizedBox(height: AppDimens.spaceXL),
          Padding(
            padding: const EdgeInsets.symmetric(
              horizontal: AppDimens.paddingL,
              vertical: AppDimens.paddingS,
            ),
            child: Text(
              AppConstants.appName,
              style: theme.textTheme.titleMedium?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
              textAlign: TextAlign.center,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildNavItem(
    BuildContext context,
    String title,
    IconData icon,
    VoidCallback onTap,
    ColorScheme colorScheme, {
    bool isDestructive = false,
  }) {
    return ListTile(
      leading: Icon(
        icon,
        color: isDestructive ? colorScheme.error : colorScheme.primary,
      ),
      title: Text(
        title,
        style: TextStyle(
          color: isDestructive ? colorScheme.error : colorScheme.onSurface,
        ),
      ),
      onTap: () {
        Navigator.of(context).pop(); // Close drawer
        onTap();
      },
    );
  }

  Widget _buildSettingsItem(
    BuildContext context,
    String title,
    List<Widget> children,
    ColorScheme colorScheme,
  ) {
    return ExpansionTile(
      title: Text(title),
      leading: Icon(Icons.settings, color: colorScheme.primary),
      childrenPadding: const EdgeInsets.only(left: AppDimens.paddingXL),
      children: children,
    );
  }

  Widget _buildSettingTile(
    BuildContext context,
    String title,
    IconData icon,
    Widget trailing,
    ColorScheme colorScheme, {
    VoidCallback? onTap,
  }) {
    return ListTile(
      contentPadding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingM,
      ),
      leading: Icon(icon, color: colorScheme.onSurfaceVariant),
      title: Text(title),
      trailing: trailing,
      dense: true,
      onTap: onTap,
    );
  }
}
