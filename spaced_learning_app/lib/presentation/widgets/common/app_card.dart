// lib/presentation/widgets/common/app_card.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

// Enum to define different Material 3 card styles
enum SLCardType {
  elevated, // Default, uses a subtle shadow and surface tint.
  filled, // Less emphasis, often uses a muted background.
  outlined, // No shadow, uses a border.
}

class SLCard extends StatelessWidget {
  // Content structure properties
  final Widget? title;
  final Widget? subtitle;
  final Widget? leading;
  final Widget? trailing;
  final Widget? content; // Main content area below the header
  final Widget? child; // If provided, overrides the default content structure.
  final List<Widget>? actions; // Typically buttons at the bottom

  // Interaction property
  final VoidCallback? onTap;

  // Layout and Styling properties
  final EdgeInsetsGeometry
  padding; // Internal padding for the default content structure or custom child
  final EdgeInsetsGeometry margin; // External margin for the SLCard itself
  final double? elevation; // User-defined elevation, overrides type default
  final double borderRadius;
  final ShapeBorder? shape; // User-defined shape, overrides type default
  final Color?
  backgroundColor; // User-defined background, overrides type default
  final Color?
  surfaceTintColor; // M3 surface tint color, especially for elevated cards
  final Color? highlightColor; // For InkWell
  final Color? shadowColor; // For the Card's shadow
  final SLCardType type; // Predefined Material 3 card type

  // Advanced styling (can override type defaults)
  final bool useGradient;
  final LinearGradient? customGradient;
  final bool
  applyOuterShadow; // Custom shadow effect separate from Card's elevation

  const SLCard({
    super.key,
    this.title,
    this.subtitle,
    this.leading,
    this.trailing,
    this.content,
    this.child,
    this.actions,
    this.onTap,
    this.padding = const EdgeInsets.all(
      AppDimens.paddingL,
    ), // Default internal padding
    this.margin = const EdgeInsets.symmetric(
      vertical: AppDimens.paddingS,
    ), // Default external margin
    this.elevation,
    this.borderRadius = AppDimens.radiusL, // Default M3-like radius (12.0)
    this.shape,
    this.backgroundColor,
    this.surfaceTintColor,
    this.highlightColor,
    this.shadowColor,
    this.type = SLCardType.elevated, // Default to elevated card type
    this.useGradient = false,
    this.customGradient,
    this.applyOuterShadow = false,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    // Determine Material 3 type-specific defaults
    double typeDefaultElevation;
    Color typeDefaultBackgroundColor;
    Color? typeDefaultSurfaceTintColor;
    ShapeBorder typeDefaultShape = RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(borderRadius),
    );

    switch (type) {
      case SLCardType.elevated:
        typeDefaultElevation =
            AppDimens.elevationXS; // M3: 1.0 for elevated cards
        typeDefaultBackgroundColor = colorScheme.surfaceContainerLow;
        typeDefaultSurfaceTintColor = colorScheme.surfaceTint;
        break;
      case SLCardType.filled:
        typeDefaultElevation =
            AppDimens.elevationNone; // M3: 0.0 for filled cards
        typeDefaultBackgroundColor = colorScheme.surfaceContainerHighest;
        break;
      case SLCardType.outlined:
        typeDefaultElevation =
            AppDimens.elevationNone; // M3: 0.0 for outlined cards
        typeDefaultBackgroundColor = colorScheme.surface;
        typeDefaultShape = RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(borderRadius),
          side: BorderSide(
            color: colorScheme.outlineVariant,
            width: 1.0,
          ), // M3 outline
        );
        break;
    }

    // User-provided values override type defaults.
    final effectiveElevation = elevation ?? typeDefaultElevation;
    // If useGradient is true, the Card's own background color should be transparent.
    final cardMaterialColor = useGradient
        ? Colors.transparent
        : backgroundColor ?? typeDefaultBackgroundColor;
    final effectiveShape = shape ?? typeDefaultShape;
    final effectiveShadowColor =
        shadowColor ?? theme.shadowColor; // Use theme's default shadow color.
    final effectiveSurfaceTintColor = useGradient
        ? null
        : (surfaceTintColor ?? typeDefaultSurfaceTintColor);

    // Construct the internal content of the card.
    // If a custom `child` is provided, use it; otherwise, build the default layout.
    // The padding is applied here to the content container.
    Widget cardInternalContent = Padding(
      padding: padding,
      child: child ?? _buildDefaultContent(theme, colorScheme),
    );

    // If a gradient is specified, wrap the content in a Container with the gradient.
    // This container will be the direct child of the Card.
    if (useGradient) {
      cardInternalContent = Container(
        decoration: BoxDecoration(
          gradient:
              customGradient ??
              LinearGradient(
                // Default gradient if none provided
                colors: [
                  colorScheme.primaryContainer.withValues(alpha: 0.5),
                  colorScheme.secondaryContainer.withValues(alpha: 0.5),
                ],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
          borderRadius: BorderRadius.circular(
            borderRadius,
          ), // Ensure gradient respects border radius.
        ),
        child: cardInternalContent,
      );
    }

    // Wrap with InkWell for tap interactions if onTap is defined.
    if (onTap != null) {
      cardInternalContent = InkWell(
        onTap: onTap,
        highlightColor: highlightColor,
        // User can override, otherwise M3 default ripple.
        splashColor: colorScheme.primary.withValues(alpha: 0.12),
        // M3 standard splash.
        borderRadius: (effectiveShape is RoundedRectangleBorder)
            ? effectiveShape.borderRadius.resolve(Directionality.of(context))
            : BorderRadius.circular(borderRadius),
        // Fallback for other shapes.
        customBorder: (effectiveShape is RoundedRectangleBorder)
            ? null
            : effectiveShape,
        child: cardInternalContent,
      );
    }

    // The main Material Card widget.
    final Widget materialCard = Card(
      margin: EdgeInsets.zero,
      // Margin is handled by the outer Container or by the SLCard user.
      elevation: useGradient ? 0 : effectiveElevation,
      // If gradient, inner container shows depth.
      shape: effectiveShape,
      color: cardMaterialColor,
      // Will be transparent if useGradient is true.
      surfaceTintColor: effectiveSurfaceTintColor,
      shadowColor: effectiveShadowColor,
      clipBehavior: Clip.antiAlias,
      // Essential for border radius with InkWell/gradient.
      child: cardInternalContent,
    );

    // Apply a custom outer shadow if requested and not using a gradient (to avoid visual clutter).
    if (applyOuterShadow && !useGradient) {
      return Container(
        margin: margin, // Overall margin for the SLCard.
        decoration: BoxDecoration(
          // Ensure the shadow's shape matches the card's shape.
          borderRadius: (effectiveShape is RoundedRectangleBorder)
              ? effectiveShape.borderRadius.resolve(Directionality.of(context))
              : BorderRadius.circular(borderRadius),
          boxShadow: [
            BoxShadow(
              color: effectiveShadowColor.withValues(alpha: 0.08),
              // Softer, more diffuse shadow.
              blurRadius: AppDimens.shadowRadiusL,
              // Larger blur.
              spreadRadius: 0,
              // No spread.
              offset: const Offset(
                0,
                AppDimens.shadowOffsetS + 1,
              ), // Slight vertical offset.
            ),
          ],
        ),
        child: materialCard,
      );
    }

    // Return the Card, wrapped in Padding for the margin.
    return Padding(padding: margin, child: materialCard);
  }

  // Builds the default content structure (header, content, actions) if `child` is not provided.
  Widget _buildDefaultContent(ThemeData theme, ColorScheme colorScheme) {
    final hasHeaderContent =
        title != null ||
        subtitle != null ||
        leading != null ||
        trailing != null;
    final hasActions = actions != null && actions!.isNotEmpty;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        // Header Section (leading, title, subtitle, trailing)
        if (hasHeaderContent)
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              if (leading != null)
                Padding(
                  padding: const EdgeInsets.only(right: AppDimens.spaceL),
                  child: leading,
                ),
              if (title != null || subtitle != null)
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      if (title != null)
                        DefaultTextStyle(
                          style: theme.textTheme.titleLarge!.copyWith(
                            // M3 titleLarge for card titles
                            color: colorScheme.onSurface,
                            fontWeight: FontWeight.w600,
                          ),
                          child: title!,
                        ),
                      if (title != null && subtitle != null)
                        const SizedBox(height: AppDimens.spaceXS),
                      if (subtitle != null)
                        DefaultTextStyle(
                          style: theme.textTheme.bodyMedium!.copyWith(
                            color: colorScheme.onSurfaceVariant,
                          ),
                          child: subtitle!,
                        ),
                    ],
                  ),
                ),
              if (trailing != null)
                Padding(
                  padding: const EdgeInsets.only(left: AppDimens.spaceL),
                  child: trailing,
                ),
            ],
          ),

        // Space between header and main content
        if (hasHeaderContent && content != null)
          const SizedBox(height: AppDimens.spaceM), // Consistent spacing
        // Main content area
        if (content != null) content!,

        // Space between main content and actions
        if (content != null && hasActions)
          const SizedBox(height: AppDimens.spaceM),

        // Actions Section (buttons)
        if (hasActions)
          Padding(
            // Padding specifically for the actions row to align them nicely.
            padding: const EdgeInsets.only(top: AppDimens.spaceS),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: actions!.map((action) {
                final isFirstAction = actions!.indexOf(action) == 0;
                return Padding(
                  padding: EdgeInsets.only(
                    left: isFirstAction ? 0 : AppDimens.spaceS,
                  ),
                  child: action,
                );
              }).toList(),
            ),
          ),
      ],
    );
  }
}
