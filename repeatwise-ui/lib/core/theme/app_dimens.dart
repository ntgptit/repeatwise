import 'package:flutter/material.dart';

/// Centralized dimensions for the RepeatWise app
class AppDimens {
  // Private constructor to prevent instantiation
  AppDimens._();

  // Spacing
  static const double spacing2 = 2.0;
  static const double spacing4 = 4.0;
  static const double spacing8 = 8.0;
  static const double spacing12 = 12.0;
  static const double spacing16 = 16.0;
  static const double spacing20 = 20.0;
  static const double spacing24 = 24.0;
  static const double spacing32 = 32.0;
  static const double spacing40 = 40.0;
  static const double spacing48 = 48.0;
  static const double spacing56 = 56.0;
  static const double spacing64 = 64.0;

  // Border radius
  static const double radius4 = 4.0;
  static const double radius8 = 8.0;
  static const double radius12 = 12.0;
  static const double radius16 = 16.0;
  static const double radius20 = 20.0;
  static const double radius24 = 24.0;
  static const double radius32 = 32.0;
  static const double radius50 = 50.0;

  // Icon sizes
  static const double iconSize12 = 12.0;
  static const double iconSize16 = 16.0;
  static const double iconSize20 = 20.0;
  static const double iconSize24 = 24.0;
  static const double iconSize32 = 32.0;
  static const double iconSize40 = 40.0;
  static const double iconSize48 = 48.0;
  static const double iconSize56 = 56.0;
  static const double iconSize64 = 64.0;
  static const double iconSize80 = 80.0;

  // Avatar sizes
  static const double avatarSize24 = 24.0;
  static const double avatarSize32 = 32.0;
  static const double avatarSize40 = 40.0;
  static const double avatarSize48 = 48.0;
  static const double avatarSize56 = 56.0;
  static const double avatarSize64 = 64.0;
  static const double avatarSize80 = 80.0;
  static const double avatarSize100 = 100.0;

  // Button heights
  static const double buttonHeight40 = 40.0;
  static const double buttonHeight48 = 48.0;
  static const double buttonHeight56 = 56.0;
  static const double buttonHeight64 = 64.0;

  // Input field heights
  static const double inputHeight40 = 40.0;
  static const double inputHeight48 = 48.0;
  static const double inputHeight56 = 56.0;

  // Card dimensions
  static const double cardElevation1 = 1.0;
  static const double cardElevation2 = 2.0;
  static const double cardElevation4 = 4.0;
  static const double cardElevation8 = 8.0;

  // Progress indicator
  static const double progressStrokeWidth2 = 2.0;
  static const double progressStrokeWidth4 = 4.0;
  static const double progressStrokeWidth6 = 6.0;

  // Loading indicator
  static const double loadingSize20 = 20.0;
  static const double loadingSize24 = 24.0;
  static const double loadingSize32 = 32.0;

  // Text sizes
  static const double textSize10 = 10.0;
  static const double textSize12 = 12.0;
  static const double textSize14 = 14.0;
  static const double textSize16 = 16.0;
  static const double textSize18 = 18.0;
  static const double textSize20 = 20.0;
  static const double textSize24 = 24.0;
  static const double textSize28 = 28.0;
  static const double textSize32 = 32.0;
  static const double textSize36 = 36.0;
  static const double textSize48 = 48.0;

  // App bar
  static const double appBarHeight = 56.0;
  static const double appBarElevation = 0.0;

  // Bottom navigation
  static const double bottomNavHeight = 56.0;
  static const double bottomNavElevation = 8.0;

  // Floating action button
  static const double fabSize = 56.0;
  static const double fabMiniSize = 40.0;

  // Divider
  static const double dividerThickness = 1.0;

  // Border width
  static const double borderWidth1 = 1.0;
  static const double borderWidth2 = 2.0;
  static const double borderWidth3 = 3.0;

  // Shadow
  static const double shadowBlurRadius = 4.0;
  static const double shadowSpreadRadius = 0.0;
  static const double shadowOffsetY = 2.0;

  // Animation durations
  static const Duration animationDurationFast = Duration(milliseconds: 150);
  static const Duration animationDurationNormal = Duration(milliseconds: 300);
  static const Duration animationDurationSlow = Duration(milliseconds: 500);

  // Padding and margin helpers
  static const EdgeInsets paddingAll8 = EdgeInsets.all(spacing8);
  static const EdgeInsets paddingAll12 = EdgeInsets.all(spacing12);
  static const EdgeInsets paddingAll16 = EdgeInsets.all(spacing16);
  static const EdgeInsets paddingAll24 = EdgeInsets.all(spacing24);

  static const EdgeInsets paddingHorizontal8 = EdgeInsets.symmetric(horizontal: spacing8);
  static const EdgeInsets paddingHorizontal12 = EdgeInsets.symmetric(horizontal: spacing12);
  static const EdgeInsets paddingHorizontal16 = EdgeInsets.symmetric(horizontal: spacing16);
  static const EdgeInsets paddingHorizontal24 = EdgeInsets.symmetric(horizontal: spacing24);

  static const EdgeInsets paddingVertical4 = EdgeInsets.symmetric(vertical: spacing4);
  static const EdgeInsets paddingVertical8 = EdgeInsets.symmetric(vertical: spacing8);
  static const EdgeInsets paddingVertical12 = EdgeInsets.symmetric(vertical: spacing12);
  static const EdgeInsets paddingVertical16 = EdgeInsets.symmetric(vertical: spacing16);
  static const EdgeInsets paddingVertical24 = EdgeInsets.symmetric(vertical: spacing24);

  // Margin helpers
  static const EdgeInsets marginAll8 = EdgeInsets.all(spacing8);
  static const EdgeInsets marginAll12 = EdgeInsets.all(spacing12);
  static const EdgeInsets marginAll16 = EdgeInsets.all(spacing16);
  static const EdgeInsets marginAll24 = EdgeInsets.all(spacing24);

  static const EdgeInsets marginHorizontal16 = EdgeInsets.symmetric(horizontal: spacing16);
  static const EdgeInsets marginHorizontal24 = EdgeInsets.symmetric(horizontal: spacing24);

  static const EdgeInsets marginVertical8 = EdgeInsets.symmetric(vertical: spacing8);
  static const EdgeInsets marginVertical12 = EdgeInsets.symmetric(vertical: spacing12);
  static const EdgeInsets marginVertical16 = EdgeInsets.symmetric(vertical: spacing16);
  static const EdgeInsets marginVertical24 = EdgeInsets.symmetric(vertical: spacing24);

  // Border radius helpers
  static const BorderRadius borderRadius4 = BorderRadius.all(Radius.circular(radius4));
  static const BorderRadius borderRadius8 = BorderRadius.all(Radius.circular(radius8));
  static const BorderRadius borderRadius12 = BorderRadius.all(Radius.circular(radius12));
  static const BorderRadius borderRadius16 = BorderRadius.all(Radius.circular(radius16));
  static const BorderRadius borderRadius20 = BorderRadius.all(Radius.circular(radius20));
  static const BorderRadius borderRadius24 = BorderRadius.all(Radius.circular(radius24));
  static const BorderRadius borderRadius32 = BorderRadius.all(Radius.circular(radius32));
  static const BorderRadius borderRadius50 = BorderRadius.all(Radius.circular(radius50));

  // Shadow helpers
  static List<BoxShadow> get shadowSmall => [
    BoxShadow(
      color: Colors.black.withOpacity(0.1),
      blurRadius: shadowBlurRadius,
      spreadRadius: shadowSpreadRadius,
      offset: const Offset(0, shadowOffsetY),
    ),
  ];

  static List<BoxShadow> get shadowMedium => [
    BoxShadow(
      color: Colors.black.withOpacity(0.15),
      blurRadius: shadowBlurRadius * 2,
      spreadRadius: shadowSpreadRadius,
      offset: const Offset(0, shadowOffsetY * 2),
    ),
  ];

  static List<BoxShadow> get shadowLarge => [
    BoxShadow(
      color: Colors.black.withOpacity(0.2),
      blurRadius: shadowBlurRadius * 3,
      spreadRadius: shadowSpreadRadius,
      offset: const Offset(0, shadowOffsetY * 3),
    ),
  ];
}
