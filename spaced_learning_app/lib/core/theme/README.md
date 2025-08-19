# Theme System Documentation

## Overview

The theme system provides a comprehensive design system for the Spaced Learning App, following Material 3 design principles with custom brand colors and typography.

## Architecture

### Core Components

- **AppTheme**: Main theme configuration with light and dark variants
- **AppColors**: Color scheme definitions and semantic color utilities
- **AppTypography**: Typography configuration with Google Fonts integration
- **AppDimens**: Design system dimensions and responsive utilities
- **ThemeHelper**: Utility methods for theme-aware operations
- **ThemeExtensions**: Extensions for semantic color mapping

### File Structure

```
lib/core/theme/
├── app_theme_data.dart      # Main theme configuration
├── app_color_scheme.dart    # Color scheme definitions
├── app_typography.dart      # Typography configuration
├── app_dimens.dart          # Design system dimensions
├── theme_extensions.dart    # Theme extensions
├── theme_helper.dart        # Theme utility methods
├── index.dart              # Export file
└── README.md               # This documentation
```

## Usage

### Basic Theme Usage

```dart
import 'package:spaced_learning_app/core/theme/index.dart';

// Get theme from context
final theme = Theme.of(context);
final colorScheme = theme.colorScheme;
final textTheme = theme.textTheme;

// Check theme mode
final isDark = ThemeHelper.isDarkMode(context);
final isLight = ThemeHelper.isLightMode(context);
```

### Color Usage

```dart
// Get theme-aware colors
final primaryColor = ThemeHelper.getPrimaryColor(context);
final surfaceColor = ThemeHelper.getSurfaceColor(context);
final textColor = ThemeHelper.getTextColor(context);

// Get semantic colors
final successColor = colorScheme.getStatColor('success');
final warningColor = colorScheme.getStatColor('warning');
final errorColor = colorScheme.getStatColor('error');
```

### Typography Usage

```dart
// Get text styles
final headlineStyle = AppTypography.getHeadlineStyle(textTheme);
final bodyStyle = AppTypography.getBodyStyle(textTheme);
final titleStyle = AppTypography.getTitleStyle(textTheme);

// Use in widgets
Text(
  'Hello World',
  style: textTheme.headlineLarge,
)
```

### Responsive Design

```dart
// Get responsive padding
final padding = ThemeHelper.getResponsivePadding(context);

// Get responsive font size
final fontSize = ThemeHelper.getResponsiveFontSize(context);

// Get responsive border radius
final radius = ThemeHelper.getResponsiveBorderRadius(context);
```

### Theme Switching

```dart
// Using Riverpod providers
final themeModeState = ref.watch(themeModeStateProvider.notifier);

// Toggle theme
await themeModeState.toggleTheme();

// Set specific theme
await themeModeState.setThemeMode(ThemeMode.dark);
```

## Design System

### Color Palette

#### Primary Colors
- **Light Primary**: `#65558f`
- **Dark Primary**: `#cfbdfe`
- **Primary Container**: `#e9ddff` (light) / `#4d3d75` (dark)

#### Secondary Colors
- **Light Secondary**: `#625b70`
- **Dark Secondary**: `#ccc2db`
- **Secondary Container**: `#e8def8` (light) / `#4a4458` (dark)

#### Semantic Colors
- **Success**: `#2e7d32`
- **Warning**: `#f57c00`
- **Error**: `#ba1a1a` (light) / `#ffb4ab` (dark)
- **Info**: `#1976d2`

### Typography

The app uses **Inter** font family from Google Fonts with the following hierarchy:

- **Display**: 57px, 45px, 36px
- **Headline**: 32px, 28px, 24px
- **Title**: 22px, 16px, 14px
- **Body**: 16px, 14px, 12px
- **Label**: 14px, 12px, 11px

### Spacing System

#### Padding & Margin
- **XXS**: 2px
- **XS**: 4px
- **S**: 8px
- **M**: 12px
- **L**: 16px
- **XL**: 20px
- **XXL**: 24px
- **XXXL**: 32px

#### Border Radius
- **XXS**: 2px
- **XS**: 4px
- **S**: 8px
- **M**: 12px
- **L**: 16px
- **XL**: 20px
- **XXL**: 24px
- **XXXL**: 32px
- **Circular**: 100px

### Breakpoints

- **XS**: 360px
- **S**: 480px
- **M**: 768px
- **L**: 1024px
- **XL**: 1440px

## Best Practices

### 1. Use Theme-Aware Colors

Always use theme-aware colors instead of hardcoded values:

```dart
// ✅ Good
color: ThemeHelper.getPrimaryColor(context)

// ❌ Bad
color: Colors.blue
```

### 2. Use Semantic Colors

Use semantic colors for different states:

```dart
// ✅ Good
color: colorScheme.getStatColor('success')

// ❌ Bad
color: Colors.green
```

### 3. Use Responsive Design

Always consider responsive design:

```dart
// ✅ Good
padding: ThemeHelper.getResponsivePadding(context)

// ❌ Bad
padding: EdgeInsets.all(16)
```

### 4. Use Typography System

Use the typography system for consistent text styling:

```dart
// ✅ Good
style: textTheme.headlineLarge

// ❌ Bad
style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold)
```

### 5. Use Design System Dimensions

Use predefined dimensions for consistency:

```dart
// ✅ Good
borderRadius: BorderRadius.circular(AppDimens.radiusM)

// ❌ Bad
borderRadius: BorderRadius.circular(12)
```

## Accessibility

The theme system supports accessibility features:

- **High Contrast**: Colors automatically adjust for high contrast mode
- **Text Scaling**: Typography scales with system text size
- **Color Blindness**: Semantic colors are designed for color blindness
- **Touch Targets**: Minimum touch target size of 48px

## Performance

### Theme Optimization

- Theme data is cached and reused
- Color schemes are computed once and cached
- Typography is pre-computed for both light and dark modes
- Responsive calculations are optimized

### Memory Management

- Theme providers are properly disposed
- Color schemes are immutable and shared
- Typography instances are reused

## Testing

### Theme Testing

```dart
testWidgets('Theme switching works correctly', (tester) async {
  await tester.pumpWidget(
    ProviderScope(
      child: MaterialApp(
        theme: AppTheme.light,
        darkTheme: AppTheme.dark,
        home: MyWidget(),
      ),
    ),
  );

  // Test light theme
  expect(ThemeHelper.isLightMode(tester.element(find.byType(MyWidget))), true);

  // Switch to dark theme
  await tester.tap(find.byIcon(Icons.brightness_6));
  await tester.pump();

  // Test dark theme
  expect(ThemeHelper.isDarkMode(tester.element(find.byType(MyWidget))), true);
});
```

## Migration Guide

### From Old Theme System

1. Replace direct color usage with `ThemeHelper` methods
2. Update typography usage to use `AppTypography`
3. Replace hardcoded dimensions with `AppDimens` constants
4. Use semantic colors instead of hardcoded color values
5. Implement responsive design using `ThemeHelper` methods

### Breaking Changes

- `primaryGradient` is now `AppColors.primaryGradient`
- `successGreen` is now `AppColors.successGreen`
- Direct color scheme access should use `ThemeHelper` methods
- Typography should use `AppTypography` methods

## Contributing

When contributing to the theme system:

1. Follow the existing naming conventions
2. Add proper documentation for new methods
3. Include accessibility considerations
4. Test with both light and dark themes
5. Ensure responsive design compatibility
6. Update this documentation

## Resources

- [Material 3 Design System](https://m3.material.io/)
- [Flutter Theme Documentation](https://docs.flutter.dev/ui/advanced/themes)
- [Google Fonts](https://fonts.google.com/)
- [FlexColorScheme](https://pub.dev/packages/flex_color_scheme)
