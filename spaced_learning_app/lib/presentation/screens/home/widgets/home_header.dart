// lib/presentation/screens/home/widgets/home_header.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/user.dart';

class HomeHeader extends StatelessWidget {
  final User user;

  const HomeHeader({super.key, required this.user});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Text(
          'Welcome back,',
          style: theme.textTheme.bodyLarge?.copyWith(
            color: colorScheme.onSurface.withValues(
              alpha: AppDimens.opacityHigh,
            ),
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: AppDimens.spaceXS),
        Text(
          user.displayName ?? user.email,
          style: theme.textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
            color: colorScheme.primary,
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: AppDimens.spaceS),
        Text(
          'Ready to continue your learning journey?',
          style: theme.textTheme.bodyMedium?.copyWith(
            fontStyle: FontStyle.italic,
            color: colorScheme.onSurface.withValues(
              alpha: AppDimens.opacityHigh,
            ),
          ),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }
}
