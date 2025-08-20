// lib/presentation/screens/profile/widgets/profile_info_card.dart

import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/user.dart';

class ProfileInfoCard extends StatelessWidget {
  final User user;
  final VoidCallback onEditPressed;

  const ProfileInfoCard({
    super.key,
    required this.user,
    required this.onEditPressed,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      elevation: 0,
      color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.5),
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingS),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Personal Info',
                  style: theme.textTheme.titleLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                IconButton(
                  icon: Container(
                    padding: const EdgeInsets.all(AppDimens.paddingXS),
                    decoration: BoxDecoration(
                      color: colorScheme.primary.withValues(alpha: 0.1),
                      shape: BoxShape.circle,
                    ),
                    child: Icon(
                      Icons.edit,
                      color: colorScheme.primary,
                      size: 20,
                    ),
                  ),
                  onPressed: onEditPressed,
                  tooltip: 'Edit',
                ),
              ],
            ),
            const SizedBox(height: AppDimens.spaceM),
            _buildInfoItem(
              theme,
              Icons.person,
              'Display Name',
              user.displayName ?? 'Not set',
            ),
            _buildInfoItem(theme, Icons.email, 'Email', user.email),
            if (user.roles?.isNotEmpty ?? false)
              _buildInfoItem(
                theme,
                Icons.badge,
                'Roles',
                user.roles!.join(', '),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoItem(
    ThemeData theme,
    IconData icon,
    String label,
    String value,
  ) {
    final colorScheme = theme.colorScheme;

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: AppDimens.paddingM),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.all(AppDimens.paddingM),
            decoration: BoxDecoration(
              color: colorScheme.primary.withValues(alpha: 0.1),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(icon, color: colorScheme.primary, size: 24),
          ),
          const SizedBox(width: AppDimens.spaceL),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  label,
                  style: theme.textTheme.labelMedium?.copyWith(
                    color: colorScheme.onSurface.withValues(alpha: 0.6),
                  ),
                ),
                const SizedBox(height: AppDimens.spaceXS),
                Text(
                  value,
                  style: theme.textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
