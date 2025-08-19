// lib/presentation/widgets/common/sl_toggle_switch.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

enum SLToggleSwitchSize { small, medium, large }

enum SLToggleSwitchType { standard, outlined, card }

class SLToggleSwitch extends StatelessWidget {
  final String? title;
  final String? subtitle;
  final bool value;
  final ValueChanged<bool> onChanged;
  final IconData? icon;
  final Color? activeColor;
  final Color? activeTrackColor;
  final Color? inactiveThumbColor;
  final Color? inactiveTrackColor;
  final SLToggleSwitchSize size;
  final SLToggleSwitchType type;
  final bool enabled;
  final VoidCallback? onLongPress;
  final EdgeInsetsGeometry? contentPadding;
  final Widget? leading;
  final Widget? trailing;

  const SLToggleSwitch({
    super.key,
    this.title,
    this.subtitle,
    required this.value,
    required this.onChanged,
    this.icon,
    this.activeColor,
    this.activeTrackColor,
    this.inactiveThumbColor,
    this.inactiveTrackColor,
    this.size = SLToggleSwitchSize.medium,
    this.type = SLToggleSwitchType.standard,
    this.enabled = true,
    this.onLongPress,
    this.contentPadding,
    this.leading,
    this.trailing,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    // Determine colors based on theme and provided colors
    final effectiveActiveColor = activeColor ?? colorScheme.primary;
    final effectiveActiveTrackColor =
        activeTrackColor ??
        effectiveActiveColor.withValues(alpha: AppDimens.opacitySemi);
    final effectiveInactiveThumbColor =
        inactiveThumbColor ?? colorScheme.outline;
    final effectiveInactiveTrackColor =
        inactiveTrackColor ?? colorScheme.surfaceContainerHighest;

    // Determine icon widget
    final effectiveLeading =
        leading ??
        (icon != null
            ? Icon(
                icon,
                color: value && enabled
                    ? effectiveActiveColor
                    : effectiveInactiveThumbColor,
                size: _getIconSize(),
              )
            : null);

    // Determine padding
    final effectivePadding =
        contentPadding ??
        EdgeInsets.symmetric(
          horizontal: _getPaddingHorizontal(),
          vertical: _getPaddingVertical(),
        );

    // Build switch based on type
    switch (type) {
      case SLToggleSwitchType.standard:
        return SizedBox(
          width: double.infinity,
          child: SwitchListTile(
            title: title != null
                ? Text(title!, style: _getTitleStyle(theme))
                : null,
            subtitle: subtitle != null
                ? Text(subtitle!, style: _getSubtitleStyle(theme, colorScheme))
                : null,
            value: value && enabled,
            onChanged: enabled ? onChanged : null,
            secondary: effectiveLeading,
            activeColor: effectiveActiveColor,
            activeTrackColor: effectiveActiveTrackColor,
            inactiveThumbColor: effectiveInactiveThumbColor,
            inactiveTrackColor: effectiveInactiveTrackColor,
            contentPadding: effectivePadding,
            dense: size == SLToggleSwitchSize.small,
          ),
        );

      case SLToggleSwitchType.outlined:
        return Container(
          width: double.infinity,
          decoration: BoxDecoration(
            border: Border.all(
              color: value && enabled
                  ? effectiveActiveColor
                  : colorScheme.outlineVariant,
              width: 1.5,
            ),
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
          ),
          child: SwitchListTile(
            title: title != null
                ? Text(title!, style: _getTitleStyle(theme))
                : null,
            subtitle: subtitle != null
                ? Text(subtitle!, style: _getSubtitleStyle(theme, colorScheme))
                : null,
            value: value && enabled,
            onChanged: enabled ? onChanged : null,
            secondary: effectiveLeading,
            activeColor: effectiveActiveColor,
            activeTrackColor: effectiveActiveTrackColor,
            inactiveThumbColor: effectiveInactiveThumbColor,
            inactiveTrackColor: effectiveInactiveTrackColor,
            contentPadding: effectivePadding,
            dense: size == SLToggleSwitchSize.small,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
            ),
          ),
        );

      case SLToggleSwitchType.card:
        return SizedBox(
          width: double.infinity,
          child: Card(
            elevation: AppDimens.elevationS,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
              side: BorderSide(
                color: value && enabled
                    ? effectiveActiveColor.withValues(
                        alpha: AppDimens.opacitySemi,
                      )
                    : Colors.transparent,
                width: 1.5,
              ),
            ),
            color: colorScheme.surfaceContainerLowest,
            child: SwitchListTile(
              title: title != null
                  ? Text(title!, style: _getTitleStyle(theme))
                  : null,
              subtitle: subtitle != null
                  ? Text(
                      subtitle!,
                      style: _getSubtitleStyle(theme, colorScheme),
                    )
                  : null,
              value: value && enabled,
              onChanged: enabled ? onChanged : null,
              secondary: effectiveLeading,
              activeColor: effectiveActiveColor,
              activeTrackColor: effectiveActiveTrackColor,
              inactiveThumbColor: effectiveInactiveThumbColor,
              inactiveTrackColor: effectiveInactiveTrackColor,
              contentPadding: effectivePadding,
              dense: size == SLToggleSwitchSize.small,
            ),
          ),
        );
    }
  }

  // Helper methods for styling
  TextStyle? _getTitleStyle(ThemeData theme) {
    switch (size) {
      case SLToggleSwitchSize.small:
        return theme.textTheme.bodyMedium?.copyWith(
          fontWeight: FontWeight.w500,
        );
      case SLToggleSwitchSize.medium:
        return theme.textTheme.titleSmall;
      case SLToggleSwitchSize.large:
        return theme.textTheme.titleMedium;
    }
  }

  TextStyle? _getSubtitleStyle(ThemeData theme, ColorScheme colorScheme) {
    switch (size) {
      case SLToggleSwitchSize.small:
        return theme.textTheme.bodySmall?.copyWith(
          color: colorScheme.onSurfaceVariant,
          fontSize: AppDimens.fontXS,
        );
      case SLToggleSwitchSize.medium:
        return theme.textTheme.bodySmall?.copyWith(
          color: colorScheme.onSurfaceVariant,
        );
      case SLToggleSwitchSize.large:
        return theme.textTheme.bodyMedium?.copyWith(
          color: colorScheme.onSurfaceVariant,
        );
    }
  }

  double _getIconSize() {
    switch (size) {
      case SLToggleSwitchSize.small:
        return AppDimens.iconS;
      case SLToggleSwitchSize.medium:
        return AppDimens.iconM;
      case SLToggleSwitchSize.large:
        return AppDimens.iconL;
    }
  }

  double _getPaddingHorizontal() {
    switch (size) {
      case SLToggleSwitchSize.small:
        return AppDimens.paddingS;
      case SLToggleSwitchSize.medium:
        return AppDimens.paddingM;
      case SLToggleSwitchSize.large:
        return AppDimens.paddingL;
    }
  }

  double _getPaddingVertical() {
    switch (size) {
      case SLToggleSwitchSize.small:
        return AppDimens.paddingXS;
      case SLToggleSwitchSize.medium:
        return AppDimens.paddingS;
      case SLToggleSwitchSize.large:
        return AppDimens.paddingM;
    }
  }
}
