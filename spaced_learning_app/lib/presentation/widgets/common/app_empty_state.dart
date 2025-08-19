import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';

class SLEmptyState extends StatelessWidget {
  final IconData? icon;
  final String title;
  final String? message;
  final String? buttonText;
  final VoidCallback? onButtonPressed;
  final double? iconSize;

  const SLEmptyState({
    super.key,
    this.icon = Icons.inbox_outlined,
    required this.title,
    this.message,
    this.buttonText,
    this.onButtonPressed,
    this.iconSize = AppDimens.iconXXL,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingXL),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisSize: MainAxisSize.min,
          children: [
            if (icon != null)
              Icon(
                icon,
                size: iconSize,
                color: colorScheme.primary.withValues(
                  alpha: AppDimens.opacityHigh,
                ),
              ),
            const SizedBox(height: AppDimens.spaceXL),
            Text(
              title,
              textAlign: TextAlign.center,
              style: theme.textTheme.titleLarge!.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            if (message != null) ...[
              const SizedBox(height: AppDimens.spaceS),
              Text(
                message!,
                textAlign: TextAlign.center,
                style: theme.textTheme.bodyMedium!.copyWith(
                  color: colorScheme.onSurface.withValues(
                    alpha: AppDimens.opacityHigh,
                  ),
                ),
              ),
            ],
            if (buttonText != null && onButtonPressed != null) ...[
              const SizedBox(height: AppDimens.spaceXL),
              SLButton(
                text: buttonText!,
                onPressed: onButtonPressed,
                type: SLButtonType.primary,
                prefixIcon: Icons.refresh,
              ),
            ],
          ],
        ),
      ),
    );
  }
}
