import 'package:flutter/material.dart';

import '../theme/app_color_scheme.dart';

extension ColorExtensions on Color {
  /// Creates a variant of the color with custom transparency
  Color withValues({int? red, int? green, int? blue, double? alpha}) {
    return Color.fromRGBO(
      red ?? (r * 255.0).round() & 0xff,
      green ?? (g * 255.0).round() & 0xff,
      blue ?? (b * 255.0).round() & 0xff,
      alpha ?? a,
    );
  }

  /// Creates a lighter version of the color
  Color lighten([double amount = 0.1]) {
    assert(amount >= 0 && amount <= 1);

    final hsl = HSLColor.fromColor(this);
    final lightness = (hsl.lightness + amount).clamp(0.0, 1.0);

    return hsl.withLightness(lightness).toColor();
  }

  /// Creates a darker version of the color
  Color darken([double amount = 0.1]) {
    assert(amount >= 0 && amount <= 1);

    final hsl = HSLColor.fromColor(this);
    final lightness = (hsl.lightness - amount).clamp(0.0, 1.0);

    return hsl.withLightness(lightness).toColor();
  }

  /// Creates a color variant with a different saturation level
  Color withSaturation(double saturation) {
    assert(saturation >= 0 && saturation <= 1);

    final hsl = HSLColor.fromColor(this);
    return hsl.withSaturation(saturation).toColor();
  }

  /// Increases the saturation of the color
  Color saturate([double amount = 0.1]) {
    assert(amount >= 0 && amount <= 1);

    final hsl = HSLColor.fromColor(this);
    final saturation = (hsl.saturation + amount).clamp(0.0, 1.0);

    return hsl.withSaturation(saturation).toColor();
  }

  /// Decreases the saturation of the color
  Color desaturate([double amount = 0.1]) {
    assert(amount >= 0 && amount <= 1);

    final hsl = HSLColor.fromColor(this);
    final saturation = (hsl.saturation - amount).clamp(0.0, 1.0);

    return hsl.withSaturation(saturation).toColor();
  }

  /// Kiểm tra độ tương phản với màu khác
  bool hasGoodContrastWith(Color background) {
    // Tính toán độ tương phản theo WCAG 2.0
    final foregroundLuminance = computeLuminance();
    final backgroundLuminance = background.computeLuminance();

    final ratio = (foregroundLuminance > backgroundLuminance)
        ? (foregroundLuminance + 0.05) / (backgroundLuminance + 0.05)
        : (backgroundLuminance + 0.05) / (foregroundLuminance + 0.05);

    return ratio >= 4.5; // Ngưỡng độ tương phản tối thiểu là 4.5:1 theo WCAG
  }

  /// Đảm bảo tạo ra màu có độ tương phản tốt với màu nền
  Color ensureContrastWith(Color background) {
    if (hasGoodContrastWith(background)) {
      return this;
    }

    // Điều chỉnh độ sáng hoặc tối dần cho đến khi đạt độ tương phản tốt
    Color result = this;
    final backgroundLuminance = background.computeLuminance();

    if (backgroundLuminance > 0.5) {
      // Nền sáng, cần làm tối màu chữ
      double darkenAmount = 0.1;
      while (darkenAmount <= 0.9 && !result.hasGoodContrastWith(background)) {
        result = darken(darkenAmount);
        darkenAmount += 0.1;
      }
    } else {
      // Nền tối, cần làm sáng màu chữ
      double lightenAmount = 0.1;
      while (lightenAmount <= 0.9 && !result.hasGoodContrastWith(background)) {
        result = lighten(lightenAmount);
        lightenAmount += 0.1;
      }
    }

    return result;
  }
}

extension CustomColorScheme on ColorScheme {
  /// Success color - Uses Google's green shade
  Color get success => successGreen; // Defined in app_color_scheme.dart

  /// Text/icon color on success background
  Color get onSuccess =>
      brightness == Brightness.light ? Colors.white : Colors.black;

  /// Container color for success
  Color get successContainer => brightness == Brightness.light
      ? const Color(0xFFE0F2E3) // Light green container
      : const Color(0xFF1A4D33); // Dark green container

  /// Text/icon color on success container
  Color get onSuccessContainer => brightness == Brightness.light
      ? const Color(0xFF00472A) // Dark green text for light mode
      : const Color(0xFFB8E5C7); // Light green text for dark mode

  /// Warning color - Uses Google's yellow/orange shade
  Color get warning => warningOrange; // Defined in app_color_scheme.dart

  /// Text/icon color on warning background
  Color get onWarning =>
      brightness == Brightness.light ? Colors.black : Colors.white;

  /// Container color for warning
  Color get warningContainer => brightness == Brightness.light
      ? const Color(0xFFFFF0D3) // Light orange container
      : const Color(0xFF4D3200); // Dark orange container

  /// Text/icon color on warning container
  Color get onWarningContainer => brightness == Brightness.light
      ? const Color(0xFF3D2900) // Dark orange text for light mode
      : const Color(0xFFFFD599); // Light orange text for dark mode

  /// Info color - Uses blue shade from geminiLightColorScheme
  Color get info => brightness == Brightness.light
      ? lightColorScheme
            .primary // Light blue (from gemini)
      : darkColorScheme.primary; // Dark blue (from gemini)

  /// Text/icon color on info background
  Color get onInfo =>
      brightness == Brightness.light ? Colors.white : Colors.black;

  /// Container color for info
  Color get infoContainer => brightness == Brightness.light
      ? lightColorScheme.primaryContainer
      : darkColorScheme.primaryContainer;

  /// Text/icon color on info container
  Color get onInfoContainer => brightness == Brightness.light
      ? lightColorScheme.onPrimaryContainer
      : darkColorScheme.onPrimaryContainer;

  /// Nâng cao: Định nghĩa thêm các màu hỗn hợp
  Color get primarySurface => brightness == Brightness.light
      ? surfaceContainer.withValues(alpha: 0.95).saturate(0.05)
      : surfaceContainer.withValues(alpha: 0.95).lighten(0.05);

  /// Màu dành cho các thành phần nhấn mạnh phụ
  Color get accentSurface => brightness == Brightness.light
      ? secondaryContainer.withValues(alpha: 0.9)
      : secondaryContainer.withValues(alpha: 0.8).lighten(0.05);

  /// Màu dành cho các highlight dễ nhận biết
  Color get highlightColor => brightness == Brightness.light
      ? tertiaryContainer.withValues(alpha: 0.9).saturate(0.1)
      : tertiaryContainer.withValues(alpha: 0.8).saturate(0.1);
}
