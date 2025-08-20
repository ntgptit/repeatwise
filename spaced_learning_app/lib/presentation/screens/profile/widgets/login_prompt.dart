// lib/presentation/screens/profile/widgets/login_prompt.dart

import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/navigation/navigation_helper.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';

class ProfileLoginPrompt extends StatelessWidget {
  const ProfileLoginPrompt({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Center(
      child: Card(
        elevation: 4,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        margin: const EdgeInsets.all(AppDimens.paddingL),
        child: Padding(
          padding: const EdgeInsets.all(AppDimens.paddingXL),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                Icons.account_circle,
                size: 80,
                color: theme.colorScheme.primary.withValues(alpha: 0.6),
              ),
              const SizedBox(height: AppDimens.spaceL),
              Text('Please log in', style: theme.textTheme.titleLarge),
              const SizedBox(height: AppDimens.spaceM),
              Text(
                'You need to be logged in to view your profile',
                textAlign: TextAlign.center,
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: theme.colorScheme.onSurface.withValues(alpha: 0.7),
                ),
              ),
              const SizedBox(height: AppDimens.spaceXL),
              SLButton(
                text: 'Log In',
                type: SLButtonType.primary,
                prefixIcon: Icons.login,
                isFullWidth: true,
                onPressed: () =>
                    NavigationHelper.clearStackAndGo(context, '/login'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
