# Navigation Flow - RepeatWise

## 1. App Structure Overview

### 1.1 Main Navigation Tabs
```
┌─────────────────────────────────────────────────────────────┐
│                    RepeatWise App                           │
├─────────────────────────────────────────────────────────────┤
│ [🏠 Home] [📚 Sets] [📊 Stats] [⚙️ Settings] [👤 Profile]│
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Screen Hierarchy
```
App Entry
├── Authentication
│   ├── Login
│   ├── Register
│   └── Forgot Password
├── Main App
│   ├── Home Dashboard
│   ├── Sets Management
│   │   ├── Set List
│   │   ├── Set Details
│   │   ├── Create Set
│   │   └── Edit Set
│   ├── Review Session
│   │   ├── Review Interface
│   │   ├── Score Input
│   │   └── Review Complete
│   ├── Statistics
│   │   ├── Overview Dashboard
│   │   ├── Set Analytics
│   │   └── Performance Trends
│   ├── Settings
│   │   ├── Profile Settings
│   │   ├── Notification Preferences
│   │   ├── Learning Preferences
│   │   └── System Settings
│   └── Profile
│       ├── User Profile
│       ├── Learning History
│       └── Achievement Badges
```

## 2. User Journey Maps

### 2.1 New User Onboarding Journey
```
1. App Launch → Welcome Screen
2. Welcome Screen → Registration
3. Registration → Email Verification
4. Email Verification → Onboarding Tutorial
5. Onboarding Tutorial → Create First Set
6. Create First Set → Home Dashboard
```

### 2.2 Daily Learning Journey
```
1. Notification → Review Session
2. Review Session → Score Input
3. Score Input → Review Complete
4. Review Complete → Home Dashboard
5. Home Dashboard → Set Details (optional)
6. Set Details → Statistics (optional)
```

### 2.3 Set Management Journey
```
1. Home Dashboard → Sets Tab
2. Sets Tab → Set List
3. Set List → Create New Set
4. Create New Set → Set Details
5. Set Details → Edit Set (optional)
6. Edit Set → Set Details
```

## 3. Screen-by-Screen Navigation

### 3.1 Authentication Flow

#### Login Screen
- **Entry Point**: App launch, logout
- **Navigation Options**:
  - Login → Home Dashboard
  - Register → Registration Screen
  - Forgot Password → Password Reset Screen
- **Back Navigation**: None (app entry point)

#### Registration Screen
- **Entry Point**: Login screen "Register" button
- **Navigation Options**:
  - Register → Email Verification
  - Back to Login → Login Screen
- **Form Validation**: Real-time validation with error messages

#### Password Reset Screen
- **Entry Point**: Login screen "Forgot Password" button
- **Navigation Options**:
  - Submit → Email Sent Confirmation
  - Back to Login → Login Screen
- **Email Confirmation**: Separate screen with resend option

### 3.2 Home Dashboard

#### Dashboard Overview
- **Entry Point**: App launch (authenticated), tab navigation
- **Content Sections**:
  - Today's Reviews (priority cards)
  - Recent Sets (quick access)
  - Learning Progress (summary)
  - Quick Actions (create set, view stats)
- **Navigation Options**:
  - Review Card → Review Session
  - Set Card → Set Details
  - Create Set → Create Set Form
  - View Stats → Statistics Dashboard

#### Today's Reviews Section
- **Card Layout**: Horizontal scrollable cards
- **Card Content**:
  - Set name and description
  - Current cycle and review number
  - Due time
  - Progress indicator
- **Card Actions**:
  - Tap → Start Review
  - Swipe left → Skip Review
  - Long press → Reschedule

### 3.3 Sets Management

#### Set List Screen
- **Entry Point**: Sets tab, navigation menu
- **List Layout**: Vertical scrollable list
- **List Items**:
  - Set name and description
  - Current status (learning, mastered, paused)
  - Progress indicator
  - Last review date
- **List Actions**:
  - Tap → Set Details
  - Swipe right → Quick actions (edit, delete)
  - Pull to refresh → Refresh data
- **Floating Action Button**: Create New Set

#### Set Details Screen
- **Entry Point**: Set list item tap, notification
- **Content Sections**:
  - Set information header
  - Current progress
  - Learning history
  - Performance analytics
  - Action buttons
- **Navigation Options**:
  - Start Review → Review Session
  - Edit Set → Edit Set Form
  - View Statistics → Set Analytics
  - Back → Set List

#### Create/Edit Set Screen
- **Entry Point**: FAB, edit button
- **Form Sections**:
  - Basic information (name, description)
  - Content input (text area)
  - Tags and categories
  - Learning preferences
- **Navigation Options**:
  - Save → Set Details
  - Cancel → Previous screen
  - Preview → Preview modal

### 3.4 Review Session Flow

#### Review Session Screen
- **Entry Point**: Notification, dashboard card, set details
- **Content Layout**:
  - Set information header
  - Content display area
  - Progress indicator
  - Action buttons
- **Navigation Options**:
  - Start Review → Score Input
  - Skip Review → Skip Confirmation
  - Back → Previous screen

#### Score Input Screen
- **Entry Point**: Review session start
- **Input Methods**:
  - Slider (0-100%)
  - Number input
  - Quick buttons (0%, 25%, 50%, 75%, 100%)
- **Additional Inputs**:
  - Notes (optional)
  - Difficulty rating
- **Navigation Options**:
  - Submit → Review Complete
  - Back → Review Session

#### Review Complete Screen
- **Entry Point**: Score submission
- **Content**:
  - Completion confirmation
  - Next review schedule
  - Progress update
  - Motivational message
- **Navigation Options**:
  - Continue → Home Dashboard
  - Next Review → Next Review Session
  - View Progress → Set Details

### 3.5 Statistics Dashboard

#### Statistics Overview
- **Entry Point**: Stats tab, navigation menu
- **Dashboard Sections**:
  - Overview cards (total sets, active sets, mastered sets)
  - Performance charts
  - Recent activity
  - Learning insights
- **Navigation Options**:
  - Chart tap → Detailed analytics
  - Set card → Set details
  - Export → Export options

#### Detailed Analytics
- **Entry Point**: Chart tap, "View Details" button
- **Analytics Types**:
  - Performance over time
  - Set comparisons
  - Learning patterns
  - Achievement tracking
- **Navigation Options**:
  - Back → Statistics Overview
  - Export → Export modal
  - Filter → Filter options

### 3.6 Settings & Profile

#### Settings Screen
- **Entry Point**: Settings tab, profile menu
- **Settings Categories**:
  - Profile settings
  - Notification preferences
  - Learning preferences
  - System settings
  - Privacy & security
- **Navigation Options**:
  - Setting item → Setting detail
  - Back → Previous screen

#### Profile Screen
- **Entry Point**: Profile tab, settings menu
- **Profile Sections**:
  - User information
  - Learning statistics
  - Achievement badges
  - Account settings
- **Navigation Options**:
  - Edit Profile → Edit form
  - View History → Learning history
  - Back → Previous screen

## 4. Navigation Patterns

### 4.1 Tab Navigation
- **Primary Tabs**: Home, Sets, Stats, Settings, Profile
- **Tab Behavior**: 
  - Always visible at bottom (mobile)
  - Side navigation (tablet/desktop)
  - Badge indicators for notifications
- **Tab States**: Active, inactive, disabled

### 4.2 Stack Navigation
- **Screen Stack**: Maintains navigation history
- **Back Button**: Always available (except root screens)
- **Back Behavior**: Return to previous screen in stack
- **Deep Linking**: Direct navigation to specific screens

### 4.3 Modal Navigation
- **Modal Types**: 
  - Full screen modals (forms, complex interactions)
  - Partial screen modals (confirmations, quick actions)
  - Bottom sheets (options, filters)
- **Modal Behavior**:
  - Overlay current screen
  - Backdrop tap to dismiss
  - Swipe to dismiss (bottom sheets)

### 4.4 Gesture Navigation
- **Swipe Gestures**:
  - Swipe left/right → Navigate between tabs
  - Swipe back → Go back (iOS)
  - Pull to refresh → Refresh data
  - Long press → Context menu
- **Touch Targets**: Minimum 44px for all interactive elements

## 5. State Management

### 5.1 Loading States
- **Initial Load**: Splash screen → Loading indicator
- **Data Loading**: Skeleton screens, loading spinners
- **Action Loading**: Button loading states, progress indicators
- **Error States**: Error messages with retry options

### 5.2 Offline States
- **Offline Detection**: Network status indicator
- **Offline Behavior**: 
  - Cache data for offline viewing
  - Queue actions for sync when online
  - Show offline indicators
- **Sync Status**: Visual indicators for sync progress

### 5.3 Error Handling
- **Error Types**:
  - Network errors
  - Validation errors
  - Server errors
  - Permission errors
- **Error Recovery**:
  - Retry mechanisms
  - Fallback content
  - User guidance
  - Support contact

## 6. Accessibility Navigation

### 6.1 Keyboard Navigation
- **Tab Order**: Logical tab sequence through all interactive elements
- **Focus Management**: Clear focus indicators
- **Keyboard Shortcuts**: Common actions (Ctrl+S, Ctrl+Z, etc.)
- **Skip Links**: Skip to main content, skip navigation

### 6.2 Screen Reader Support
- **Semantic Structure**: Proper heading hierarchy
- **ARIA Labels**: Descriptive labels for all interactive elements
- **Live Regions**: Dynamic content updates
- **Landmarks**: Navigation, main, complementary regions

### 6.3 Motion and Animation
- **Reduced Motion**: Respect user preferences
- **Animation Duration**: Configurable timing
- **Focus Indicators**: High contrast focus rings
- **Color Contrast**: WCAG AA compliance

## 7. Performance Considerations

### 7.1 Navigation Performance
- **Screen Transitions**: Smooth 60fps animations
- **Data Loading**: Lazy loading, pagination
- **Image Optimization**: Compressed images, lazy loading
- **Code Splitting**: Load only necessary components

### 7.2 Memory Management
- **Screen Caching**: Cache frequently accessed screens
- **Data Caching**: Cache API responses
- **Memory Cleanup**: Dispose unused resources
- **Background Processing**: Handle heavy operations in background

## 8. Implementation Guidelines

### 8.1 Navigation Implementation
```dart
// Example navigation structure
class AppRouter {
  static const String home = '/';
  static const String sets = '/sets';
  static const String setDetails = '/sets/:id';
  static const String createSet = '/sets/create';
  static const String review = '/review/:id';
  static const String stats = '/stats';
  static const String settings = '/settings';
  static const String profile = '/profile';
}
```

### 8.2 State Management
```dart
// Example state management
class NavigationState {
  final String currentRoute;
  final List<String> navigationHistory;
  final Map<String, dynamic> routeParams;
  
  // Navigation actions
  void navigateTo(String route, {Map<String, dynamic> params});
  void goBack();
  void clearHistory();
}
```

### 8.3 Deep Linking
```dart
// Example deep link handling
class DeepLinkHandler {
  void handleDeepLink(Uri uri) {
    // Parse URI and navigate accordingly
    switch (uri.path) {
      case '/sets':
        navigateToSetList();
        break;
      case '/review':
        navigateToReview(uri.queryParameters['id']);
        break;
    }
  }
}
``` 
