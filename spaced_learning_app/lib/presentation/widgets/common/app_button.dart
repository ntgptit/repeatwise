import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/extensions/color_extensions.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

enum SLButtonType {
  primary,
  secondary,
  outline,
  text,
  ghost,
  error,
  success,
  warning,
  gradient,
}

enum SLButtonSize { tiny, small, medium, large, xlarge }

class SLButton extends StatelessWidget {
  final String text;
  final VoidCallback? onPressed;
  final SLButtonType type;
  final SLButtonSize size;
  final IconData? prefixIcon;
  final IconData? suffixIcon;
  final Widget? prefixWidget;
  final Widget? suffixWidget;
  final bool isLoading;
  final bool isFullWidth;
  final double? width;
  final Color? backgroundColor;
  final Color? textColor;
  final Color? iconColor;
  final Color? borderColor;
  final Color? loadingColor;
  final double? elevation;
  final BorderRadius? customBorderRadius;
  final LinearGradient? customGradient;

  const SLButton({
    super.key,
    required this.text,
    this.onPressed,
    this.type = SLButtonType.primary,
    this.size = SLButtonSize.medium,
    this.prefixIcon,
    this.suffixIcon,
    this.prefixWidget,
    this.suffixWidget,
    this.isLoading = false,
    this.isFullWidth = false,
    this.width,
    this.backgroundColor,
    this.textColor,
    this.iconColor,
    this.borderColor,
    this.loadingColor,
    this.elevation,
    this.customBorderRadius,
    this.customGradient,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    EdgeInsets padding;
    double height;
    double iconSize;
    double borderRadius;
    TextStyle textStyle;

    // Configure size parameters
    switch (size) {
      case SLButtonSize.tiny:
        padding = const EdgeInsets.symmetric(
          horizontal: AppDimens.paddingS,
          vertical: AppDimens.paddingXS,
        );
        height = AppDimens.buttonHeightS;
        iconSize = AppDimens.iconXS;
        borderRadius = AppDimens.radiusXS + 2;
        textStyle = theme.textTheme.bodySmall!.copyWith(
          fontWeight: FontWeight.w600,
          letterSpacing: 0.25,
        );
        break;
      case SLButtonSize.small:
        padding = const EdgeInsets.symmetric(
          horizontal: AppDimens.paddingM,
          vertical: AppDimens.paddingS,
        );
        height = AppDimens.buttonHeightM;
        iconSize = AppDimens.iconS;
        borderRadius = AppDimens.radiusS;
        textStyle = theme.textTheme.bodySmall!.copyWith(
          fontWeight: FontWeight.w600,
          letterSpacing: 0.5,
        );
        break;
      case SLButtonSize.medium:
        padding = const EdgeInsets.symmetric(
          horizontal: AppDimens.paddingL + 4,
          vertical: AppDimens.paddingM,
        );
        height = AppDimens.buttonHeightL;
        iconSize = AppDimens.iconM;
        borderRadius = AppDimens.radiusM;
        textStyle = theme.textTheme.bodyMedium!.copyWith(
          fontWeight: FontWeight.w600,
          letterSpacing: 0.5,
        );
        break;
      case SLButtonSize.large:
        padding = const EdgeInsets.symmetric(
          horizontal: AppDimens.paddingXL,
          vertical: AppDimens.paddingL,
        );
        height = AppDimens.buttonHeightXL;
        iconSize = AppDimens.iconL;
        borderRadius = AppDimens.radiusL;
        textStyle = theme.textTheme.bodyLarge!.copyWith(
          fontWeight: FontWeight.w600,
          letterSpacing: 0.5,
        );
        break;
      case SLButtonSize.xlarge:
        padding = const EdgeInsets.symmetric(
          horizontal: AppDimens.paddingXXL,
          vertical: AppDimens.paddingL + 4,
        );
        height = AppDimens.buttonHeightXL + 8;
        iconSize = AppDimens.iconXL - 4;
        borderRadius = AppDimens.radiusL + 4;
        textStyle = theme.textTheme.titleMedium!.copyWith(
          fontWeight: FontWeight.w600,
          letterSpacing: 0.5,
        );
        break;
    }

    // Configure style based on button type
    Color defaultBackgroundColor;
    Color defaultTextColor;
    Color defaultBorderColor;
    double defaultElevation;
    LinearGradient? defaultGradient;

    switch (type) {
      case SLButtonType.primary:
        defaultBackgroundColor = colorScheme.primary;
        defaultTextColor = colorScheme.onPrimary;
        defaultBorderColor = Colors.transparent;
        defaultElevation = AppDimens.elevationS;
        break;
      case SLButtonType.secondary:
        defaultBackgroundColor = colorScheme.secondary;
        defaultTextColor = colorScheme.onSecondary;
        defaultBorderColor = Colors.transparent;
        defaultElevation = AppDimens.elevationS;
        break;
      case SLButtonType.outline:
        defaultBackgroundColor = Colors.transparent;
        defaultTextColor = colorScheme.primary;
        defaultBorderColor = colorScheme.outline;
        defaultElevation = 0;
        break;
      case SLButtonType.text:
        defaultBackgroundColor = Colors.transparent;
        defaultTextColor = colorScheme.primary;
        defaultBorderColor = Colors.transparent;
        defaultElevation = 0;
        break;
      case SLButtonType.ghost:
        defaultBackgroundColor = colorScheme.primary.withValues(
          alpha: AppDimens.opacityLight,
        );
        defaultTextColor = colorScheme.primary;
        defaultBorderColor = Colors.transparent;
        defaultElevation = 0;
        break;
      case SLButtonType.error:
        defaultBackgroundColor = colorScheme.error;
        defaultTextColor = colorScheme.onError;
        defaultBorderColor = Colors.transparent;
        defaultElevation = AppDimens.elevationS;
        break;
      case SLButtonType.success:
        defaultBackgroundColor = colorScheme.success;
        defaultTextColor = colorScheme.onSuccess;
        defaultBorderColor = Colors.transparent;
        defaultElevation = AppDimens.elevationS;
        break;
      case SLButtonType.warning:
        defaultBackgroundColor = colorScheme.warning;
        defaultTextColor = colorScheme.onWarning;
        defaultBorderColor = Colors.transparent;
        defaultElevation = AppDimens.elevationS;
        break;
      case SLButtonType.gradient:
        defaultBackgroundColor = Colors.transparent;
        defaultTextColor = Colors.white;
        defaultBorderColor = Colors.transparent;
        defaultElevation = AppDimens.elevationS;
        defaultGradient =
            customGradient ??
            LinearGradient(
              colors: [colorScheme.primary, colorScheme.secondary],
            );
        break;
    }

    final effectiveBackgroundColor = backgroundColor ?? defaultBackgroundColor;
    final effectiveTextColor = textColor ?? defaultTextColor;
    final effectiveIconColor = iconColor ?? effectiveTextColor;
    final effectiveBorderColor = borderColor ?? defaultBorderColor;
    final effectiveLoadingColor = loadingColor ?? effectiveTextColor;
    final effectiveElevation = elevation ?? defaultElevation;
    final effectiveBorderRadius =
        customBorderRadius ?? BorderRadius.circular(borderRadius);

    // Build gradient button if type is gradient
    if (type == SLButtonType.gradient) {
      return SizedBox(
        width: width ?? (isFullWidth ? double.infinity : null),
        height: height,
        child: DecoratedBox(
          decoration: BoxDecoration(
            gradient: defaultGradient,
            borderRadius: effectiveBorderRadius,
            boxShadow: effectiveElevation > 0
                ? [
                    BoxShadow(
                      color: Colors.black.withValues(
                        alpha: AppDimens.opacitySemi,
                      ),
                      spreadRadius: 0,
                      blurRadius: effectiveElevation * 2,
                      offset: Offset(0, effectiveElevation / 2),
                    ),
                  ]
                : null,
          ),
          child: ElevatedButton(
            onPressed: isLoading ? null : onPressed,
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.transparent,
              disabledBackgroundColor: Colors.transparent,
              shadowColor: Colors.transparent,
              padding: padding,
              shape: RoundedRectangleBorder(
                borderRadius: effectiveBorderRadius,
              ),
            ),
            child: _buildContent(
              effectiveTextColor,
              effectiveIconColor,
              effectiveLoadingColor,
              iconSize,
              textStyle,
            ),
          ),
        ),
      );
    }

    // Build regular button based on type
    Widget button;
    switch (type) {
      case SLButtonType.primary:
      case SLButtonType.secondary:
      case SLButtonType.error:
      case SLButtonType.success:
      case SLButtonType.warning:
      case SLButtonType.ghost:
        button = ElevatedButton(
          onPressed: isLoading ? null : onPressed,
          style: ElevatedButton.styleFrom(
            foregroundColor: effectiveTextColor,
            backgroundColor: effectiveBackgroundColor,
            shape: RoundedRectangleBorder(
              borderRadius: effectiveBorderRadius,
              side: BorderSide(color: effectiveBorderColor),
            ),
            padding: padding,
            minimumSize: Size(0, height),
            elevation: effectiveElevation,
            disabledBackgroundColor: colorScheme.onSurface.withValues(
              alpha: AppDimens.opacityMedium,
            ),
            disabledForegroundColor: colorScheme.onSurface.withValues(
              alpha: AppDimens.opacityDisabled,
            ),
            shadowColor: type == SLButtonType.ghost ? Colors.transparent : null,
          ),
          child: _buildContent(
            effectiveTextColor,
            effectiveIconColor,
            effectiveLoadingColor,
            iconSize,
            textStyle,
          ),
        );
        break;

      case SLButtonType.outline:
        button = OutlinedButton(
          onPressed: isLoading ? null : onPressed,
          style: OutlinedButton.styleFrom(
            foregroundColor: effectiveTextColor,
            shape: RoundedRectangleBorder(borderRadius: effectiveBorderRadius),
            side: BorderSide(
              color: effectiveBorderColor,
              width: AppDimens.outlineButtonBorderWidth,
            ),
            padding: padding,
            minimumSize: Size(0, height),
            backgroundColor: effectiveBackgroundColor,
            disabledForegroundColor: colorScheme.onSurface.withValues(
              alpha: AppDimens.opacityDisabled,
            ),
          ),
          child: _buildContent(
            effectiveTextColor,
            effectiveIconColor,
            effectiveLoadingColor,
            iconSize,
            textStyle,
          ),
        );
        break;

      case SLButtonType.text:
        button = TextButton(
          onPressed: isLoading ? null : onPressed,
          style: TextButton.styleFrom(
            foregroundColor: effectiveTextColor,
            shape: RoundedRectangleBorder(borderRadius: effectiveBorderRadius),
            padding: padding,
            minimumSize: Size(0, height),
            backgroundColor: effectiveBackgroundColor,
            disabledForegroundColor: colorScheme.onSurface.withValues(
              alpha: AppDimens.opacityDisabled,
            ),
          ),
          child: _buildContent(
            effectiveTextColor,
            effectiveIconColor,
            effectiveLoadingColor,
            iconSize,
            textStyle,
          ),
        );
        break;

      case SLButtonType.gradient:
        button = ElevatedButton(
          onPressed: isLoading ? null : onPressed,
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.transparent,
            shadowColor: Colors.transparent,
            foregroundColor: effectiveTextColor,
            padding: padding,
            minimumSize: Size(0, height),
          ),
          child: _buildContent(
            effectiveTextColor,
            effectiveIconColor,
            effectiveLoadingColor,
            iconSize,
            textStyle,
          ),
        );
        break;
    }

    // Handle width constraints
    if (width != null) {
      return SizedBox(width: width, child: button);
    }

    return isFullWidth
        ? SizedBox(width: double.infinity, child: button)
        : button;
  }

  Widget _buildContent(
    Color textColor,
    Color iconColor,
    Color loadingColor,
    double iconSize,
    TextStyle baseTextStyle,
  ) {
    // Show loading indicator when in loading state
    if (isLoading) {
      return SizedBox(
        width: size == SLButtonSize.tiny || size == SLButtonSize.small
            ? AppDimens.iconS
            : AppDimens.iconM,
        height: size == SLButtonSize.tiny || size == SLButtonSize.small
            ? AppDimens.iconS
            : AppDimens.iconM,
        child: CircularProgressIndicator(
          strokeWidth: size == SLButtonSize.tiny || size == SLButtonSize.small
              ? AppDimens.lineProgressHeight / 2
              : AppDimens.lineProgressHeight * 0.625,
          valueColor: AlwaysStoppedAnimation<Color>(loadingColor),
        ),
      );
    }

    // Build button content with icons and text
    final List<Widget> children = [];

    if (prefixWidget != null) {
      children.add(prefixWidget!);
      children.add(
        SizedBox(
          width: size == SLButtonSize.tiny
              ? AppDimens.spaceXS
              : AppDimens.spaceS,
        ),
      );
    } else if (prefixIcon != null) {
      children.add(Icon(prefixIcon, size: iconSize, color: iconColor));
      children.add(
        SizedBox(
          width: size == SLButtonSize.tiny
              ? AppDimens.spaceXS
              : AppDimens.spaceS,
        ),
      );
    }

    children.add(Text(text, style: baseTextStyle.copyWith(color: textColor)));

    if (suffixWidget != null) {
      children.add(
        SizedBox(
          width: size == SLButtonSize.tiny
              ? AppDimens.spaceXS
              : AppDimens.spaceS,
        ),
      );
      children.add(suffixWidget!);
    } else if (suffixIcon != null) {
      children.add(
        SizedBox(
          width: size == SLButtonSize.tiny
              ? AppDimens.spaceXS
              : AppDimens.spaceS,
        ),
      );
      children.add(Icon(suffixIcon, size: iconSize, color: iconColor));
    }

    return Row(
      mainAxisSize: MainAxisSize.min,
      mainAxisAlignment: MainAxisAlignment.center,
      children: children,
    );
  }
}
