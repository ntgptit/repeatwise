# RepeatWise Widgets

This directory contains all reusable widgets for the RepeatWise Flutter application, organized by category and following Material 3 design standards.

## Organization

Widgets are organized into the following categories:

### 📁 Basic Widgets (`basic/`)
Core UI components for basic interactions:
- `RepeatWiseLoadingWidget` - Loading indicators with customizable messages
- `RepeatWiseErrorWidget` - Error states with retry functionality
- `RepeatWiseEmptyStateWidget` - Empty state displays with action buttons

### 📁 Feedback Widgets (`feedback/`)
User input and interaction components:
- `RepeatWiseButton` - Primary, secondary, and destructive buttons with loading states
- `RepeatWiseTextField` - Text input fields with validation support
- `RepeatWiseSearchField` - Search input with clear functionality

### 📁 Layout Widgets (`layout/`)
Structural and layout components:
- `RepeatWiseCard` - Material 3 cards with tap support
- `RepeatWiseListTile` - List items with leading/trailing content
- `RepeatWiseSpacing` - Consistent spacing utilities
- `RepeatWiseDivider` - Visual separators
- `RepeatWiseResponsiveLayout` - Responsive layout wrapper

### 📁 Navigation Widgets (`navigation/`)
Navigation and modal components:
- `RepeatWiseBottomSheet` - Bottom sheet dialogs
- `RepeatWiseDialog` - Modal dialogs
- `RepeatWiseConfirmationDialog` - Confirmation dialogs with actions

### 📁 Data Display Widgets (`data_display/`)
Data visualization components:
- `RepeatWiseChip` - Filter and action chips
- `RepeatWiseBadge` - Notification badges
- `RepeatWiseAvatar` - User avatars with fallback initials

### 📁 App-Specific Widgets (`app_specific/`)
Domain-specific components for RepeatWise:
- `RepeatWiseProgressIndicator` - Learning progress indicators
- `RepeatWiseSetCard` - Learning set display cards
- `RepeatWiseCycleCard` - Learning cycle display cards
- `RepeatWiseReminderCard` - Reminder schedule cards
- `RepeatWiseStreakCounter` - Learning streak displays
- `RepeatWiseStudyTimer` - Study session timer
- `RepeatWiseQuickActionButton` - Quick action buttons
- `RepeatWiseStatisticsCard` - Statistics display cards

## Usage

### Importing Widgets

```dart
// Import all widgets
import 'package:repeatwise/core/widgets/widgets.dart';

// Or import specific categories
import 'package:repeatwise/core/widgets/basic/basic.dart';
import 'package:repeatwise/core/widgets/feedback/feedback.dart';
import 'package:repeatwise/core/widgets/layout/layout.dart';
import 'package:repeatwise/core/widgets/navigation/navigation.dart';
import 'package:repeatwise/core/widgets/data_display/data_display.dart';
import 'package:repeatwise/core/widgets/app_specific/app_specific.dart';
```

### Example Usage

```dart
// Basic widgets
RepeatWiseLoadingWidget(message: 'Loading...');
RepeatWiseErrorWidget(message: 'Error occurred', onRetry: () {});

// Feedback widgets
RepeatWiseButton(text: 'Save', onPressed: () {});
RepeatWiseTextField(label: 'Email', hint: 'Enter email');

// Layout widgets
RepeatWiseCard(
  onTap: () {},
  child: Text('Card content'),
);

// App-specific widgets
RepeatWiseSetCard(set: mySet, onTap: () {});
RepeatWiseStatisticsCard(
  title: 'Active Sets',
  value: '12',
  icon: Icons.list,
);
```

## Design Principles

### Material 3 Compliance
All widgets follow Material 3 design standards:
- Use theme colors from `Theme.of(context).colorScheme`
- Support light and dark themes automatically
- Follow Material 3 spacing and typography guidelines
- Use appropriate elevation and surface colors

### Accessibility
- All widgets support screen readers
- Proper semantic labels and descriptions
- Keyboard navigation support where applicable
- High contrast support through theme colors

### Responsive Design
- Widgets adapt to different screen sizes
- Use `RepeatWiseResponsiveLayout` for complex layouts
- Support for tablet and desktop layouts

## Adding New Widgets

### 1. Choose the Right Category
- **Basic**: Core UI states (loading, error, empty)
- **Feedback**: User input and interactions
- **Layout**: Structural components
- **Navigation**: Modals and navigation elements
- **Data Display**: Data visualization
- **App-Specific**: Domain-specific components

### 2. Follow Naming Convention
- Use `RepeatWise` prefix for all widgets
- Use descriptive, clear names
- Follow camelCase naming

### 3. Implementation Guidelines
```dart
class RepeatWiseNewWidget extends StatelessWidget {
  final String title;
  final VoidCallback? onTap;
  
  const RepeatWiseNewWidget({
    super.key,
    required this.title,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return Container(
      // Use theme colors
      color: theme.colorScheme.surface,
      child: Text(
        title,
        style: theme.textTheme.bodyMedium,
      ),
    );
  }
}
```

### 4. Add to Barrel File
Update the appropriate barrel file (e.g., `basic/basic.dart`):
```dart
export 'repeatwise_new_widget.dart';
```

### 5. Update Documentation
- Add widget to this README
- Include usage examples
- Document all parameters

## Testing

### Widget Testing
```dart
testWidgets('RepeatWiseButton shows loading state', (tester) async {
  await tester.pumpWidget(
    MaterialApp(
      home: RepeatWiseButton(
        text: 'Save',
        isLoading: true,
        onPressed: () {},
      ),
    ),
  );
  
  expect(find.byType(CircularProgressIndicator), findsOneWidget);
});
```

### Visual Testing
Use the `WidgetExamplesPage` to visually test all widgets:
```dart
// Navigate to widget examples
Navigator.push(
  context,
  MaterialPageRoute(builder: (context) => const WidgetExamplesPage()),
);
```

## Best Practices

### 1. Theme Integration
Always use theme colors and styles:
```dart
// Good
color: theme.colorScheme.primary

// Avoid
color: Colors.blue
```

### 2. Consistent Spacing
Use the spacing constants:
```dart
// Good
const SizedBox(height: 16)

// Avoid
const SizedBox(height: 20)
```

### 3. Proper Callbacks
Provide meaningful callback names:
```dart
// Good
onTap: () => debugPrint('Card tapped')

// Avoid
onTap: () {}
```

### 4. Documentation
Include comprehensive documentation for all widgets:
```dart
/// RepeatWise widget for displaying user avatars
/// 
/// Supports both image URLs and fallback initials.
/// Automatically adapts to theme colors.
class RepeatWiseAvatar extends StatelessWidget {
  // ...
}
```

## Migration Guide

### From Old Widget Names
If you're migrating from the old widget names, update your imports:

```dart
// Old
import 'common_widgets.dart';
CommonButton(...)

// New
import 'widgets.dart';
RepeatWiseButton(...)
```

### From Single File Organization
If you were using the old single-file organization:

```dart
// Old
import 'common_widgets.dart';
import 'app_specific_widgets.dart';

// New
import 'widgets.dart';
// All widgets are now available through the main barrel file
```

## Contributing

When adding new widgets:

1. **Follow the naming convention**: `RepeatWise[WidgetName]`
2. **Organize by category**: Place in the appropriate folder
3. **Update barrel files**: Export from the category barrel file
4. **Add examples**: Include in `WidgetExamplesPage`
5. **Update documentation**: Add to this README
6. **Test thoroughly**: Include widget tests
7. **Follow Material 3**: Ensure design compliance

## Examples

See `widget_examples.dart` for comprehensive usage examples of all widgets.
