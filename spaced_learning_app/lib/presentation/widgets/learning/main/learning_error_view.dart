// lib/presentation/widgets/learning/main/learning_error_view.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class LearningErrorView extends ConsumerWidget {
  final String errorMessage;
  final VoidCallback onRetry;

  const LearningErrorView({
    super.key,
    required this.errorMessage,
    required this.onRetry,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Center(
      child: Container(
        padding: const EdgeInsets.all(AppDimens.paddingXXL),
        decoration: BoxDecoration(
          color: colorScheme.errorContainer.withValues(
            alpha: AppDimens.opacityVeryHigh,
          ),
          borderRadius: BorderRadius.circular(AppDimens.radiusL),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withValues(alpha: AppDimens.opacityMedium),
              blurRadius: AppDimens.shadowRadiusL,
              offset: const Offset(0, AppDimens.shadowOffsetM),
            ),
          ],
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              Icons.error_outline,
              size: AppDimens.iconXXL,
              color: colorScheme.onErrorContainer,
            ),
            const SizedBox(height: AppDimens.spaceL),
            Text(
              'Oops! Something went wrong',
              style: theme.textTheme.titleLarge?.copyWith(
                color: colorScheme.onErrorContainer,
              ),
            ),
            const SizedBox(height: AppDimens.spaceS),
            Text(
              errorMessage,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: colorScheme.onErrorContainer,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: AppDimens.spaceXL),
            ElevatedButton.icon(
              onPressed: onRetry,
              icon: const Icon(Icons.refresh),
              label: const Text('Try Again'),
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(
                  horizontal: AppDimens.paddingXL,
                  vertical: AppDimens.paddingM,
                ),
                backgroundColor: colorScheme.onErrorContainer,
                foregroundColor: colorScheme.errorContainer,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
