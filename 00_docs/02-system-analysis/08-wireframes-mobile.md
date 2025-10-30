# Mobile Application Wireframes (MVP)

This document provides detailed wireframes for the RepeatWise mobile application (React Native), covering all major screens and user flows.

## Navigation Structure

```
┌─────────────────────┐
│  Header (App Bar)   │
├─────────────────────┤
│                     │
│   Main Content      │
│                     │
│                     │
│                     │
│                     │
│                     │
├─────────────────────┤
│ Bottom Navigation   │
│ [Home] [Folders]    │
│ [Review] [Stats]    │
└─────────────────────┘
```

## 1. Authentication Screens

### 1.1 Registration Screen

```
┌─────────────────────┐
│  ← Back             │
├─────────────────────┤
│                     │
│   RepeatWise Logo   │
│                     │
│  Create Account     │
│                     │
│  Email *            │
│  ┌───────────────┐  │
│  │ user@exam... │  │
│  └───────────────┘  │
│                     │
│  Username (optional)│
│  ┌───────────────┐  │
│  │               │  │
│  └───────────────┘  │
│  3-30 chars         │
│                     │
│  Password *         │
│  ┌───────────────┐  │
│  │ ●●●●●●●●     │  │
│  └───────────────┘  │
│  Min 8 characters   │
│                     │
│  Confirm Password *  │
│  ┌───────────────┐  │
│  │ ●●●●●●●●     │  │
│  └───────────────┘  │
│                     │
│  Name (optional)    │
│  ┌───────────────┐  │
│  │               │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │   Register    │  │
│  └───────────────┘  │
│                     │
│  Already have      │
│  account? Login    │
│                     │
└─────────────────────┘
```

**Key Elements**:
- Scrollable form
- Required field indicators (*)
- Password visibility toggle
- Validation messages inline
- Link to login screen

### 1.2 Login Screen

```
┌─────────────────────┐
│                     │
├─────────────────────┤
│                     │
│   RepeatWise Logo   │
│                     │
│  Login              │
│                     │
│  Username or Email  │
│  ┌───────────────┐  │
│  │ user@exam... │  │
│  └───────────────┘  │
│                     │
│  Password           │
│  ┌───────────────┐  │
│  │ ●●●●●●●●     │  │
│  └───────────────┘  │
│  👁 Show            │
│                     │
│  ☐ Remember me      │
│                     │
│  ┌───────────────┐  │
│  │     Login     │  │
│  └───────────────┘  │
│                     │
│  Forgot Password?   │
│                     │
│  Don't have account?│
│  Register           │
│                     │
└─────────────────────┘
```

**Key Elements**:
- Username/Email field
- Password field with visibility toggle
- Remember me checkbox
- Forgot password link
- Register link

---

## 2. Home / Dashboard Screen

```
┌─────────────────────┐
│ ☰ RepeatWise  [🔍] │
├─────────────────────┤
│                     │
│  Dashboard          │
│                     │
│  ┌───────────────┐  │
│  │    120        │  │
│  │ Total Cards   │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │     45        │  │
│  │  Due Cards    │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │      7        │  │
│  │ Streak Days 🔥│  │
│  └───────────────┘  │
│                     │
│  Quick Actions      │
│  ┌───────────────┐  │
│  │ ▶ Start Review│  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │  ➕ New Deck  │  │
│  └───────────────┘  │
│                     │
│  Recent Activity    │
│  ┌───────────────┐  │
│  │ Reviewed 45    │  │
│  │ cards today   │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ Created "Vocab"│  │
│  │ yesterday     │  │
│  └───────────────┘  │
│                     │
├─────────────────────┤
│ [🏠] [📁] [📚] [📊]│
└─────────────────────┘
```

**Key Elements**:
- Header with menu and search
- Statistics cards (swipeable)
- Quick action buttons
- Recent activity list
- Bottom navigation bar

---

## 3. Folder Management

### 3.1 Folder List / Tree View

```
┌─────────────────────┐
│ ← Back  Folders  [⋮]│
├─────────────────────┤
│                     │
│  [🔍 Search]        │
│                     │
│  ┌───────────────┐  │
│  │ 📁 Root       │  │
│  │   15 decks    │  │
│  │   1,250 cards │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ 📁 Languages  │  │
│  │   8 decks     │  │
│  │   650 cards   │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ 📁 Science    │  │
│  │   5 decks     │  │
│  │   450 cards   │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ 📁 Math       │  │
│  │   2 decks     │  │
│  │   150 cards   │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │   ➕ New      │  │
│  │   Folder      │  │
│  └───────────────┘  │
│                     │
├─────────────────────┤
│ [🏠] [📁] [📚] [📊]│
└─────────────────────┘
```

**Key Elements**:
- Folder list with statistics
- Tap to expand/collapse
- Long press for context menu
- Floating action button for new folder
- Search bar

### 3.2 Folder Detail Screen

```
┌─────────────────────┐
│ ← Back  IELTS Prep  │
├─────────────────────┤
│                     │
│  Statistics         │
│  ┌───────────────┐  │
│  │ Total Decks:  │  │
│  │     15        │  │
│  └───────────────┘  │
│  ┌───────────────┐  │
│  │ Total Cards:  │  │
│  │   1,250       │  │
│  └───────────────┘  │
│  ┌───────────────┐  │
│  │ Due Cards:    │  │
│  │     45        │  │
│  └───────────────┘  │
│                     │
│  Actions            │
│  ┌───────────────┐  │
│  │ ▶ Start Review│  │
│  └───────────────┘  │
│  ┌───────────────┐  │
│  │ ➕ New Folder │  │
│  └───────────────┘  │
│  ┌───────────────┐  │
│  │ ➕ New Deck   │  │
│  └───────────────┘  │
│                     │
│  Decks (15)         │
│  ┌───────────────┐  │
│  │ 📚 Vocabulary │  │
│  │   120 cards   │  │
│  │   15 due      │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ 📚 Grammar    │  │
│  │   85 cards    │  │
│  │   10 due      │  │
│  └───────────────┘  │
│                     │
│  ... (scrollable)   │
│                     │
├─────────────────────┤
│ [🏠] [📁] [📚] [📊]│
└─────────────────────┘
```

**Key Elements**:
- Folder statistics cards
- Action buttons
- Deck list within folder
- Swipe actions (future)

### 3.3 Create Folder Screen

```
┌─────────────────────┐
│ ← Cancel  New Folder│
├─────────────────────┤
│                     │
│  Name *             │
│  ┌───────────────┐  │
│  │               │  │
│  └───────────────┘  │
│                     │
│  Description        │
│  ┌───────────────┐  │
│  │               │  │
│  │               │  │
│  │               │  │
│  └───────────────┘  │
│                     │
│  Parent Folder      │
│  ┌───────────────┐  │
│  │ Root       ▼  │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │    Create     │  │
│  └───────────────┘  │
│                     │
└─────────────────────┘
```

**Key Elements**:
- Form fields
- Parent folder picker
- Create button
- Validation messages

---

## 4. Deck Management

### 4.1 Deck List Screen

```
┌─────────────────────┐
│ ☰ Decks  [🔍] [➕]  │
├─────────────────────┤
│                     │
│  [Filter ▼] [Sort ▼]│
│                     │
│  ┌───────────────┐  │
│  │ 📚 Vocabulary │  │
│  │   120 cards   │  │
│  │   15 due      │  │
│  │   Last: 2h ago│  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ 📚 Grammar    │  │
│  │   85 cards    │  │
│  │   10 due      │  │
│  │   Last: 1d ago│  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ 📚 Reading    │  │
│  │   95 cards    │  │
│  │   20 due      │  │
│  │   Last: 5h ago│  │
│  └───────────────┘  │
│                     │
│  ... (scrollable)   │
│                     │
├─────────────────────┤
│ [🏠] [📁] [📚] [📊]│
└─────────────────────┘
```

**Key Elements**:
- Search and filter controls
- Deck cards with statistics
- Tap to open deck
- Long press for actions
- Floating action button

### 4.2 Deck Detail Screen

```
┌─────────────────────┐
│ ← Back  Vocabulary  │
│        [⋮]          │
├─────────────────────┤
│                     │
│  ┌───────────────┐  │
│  │ 120 cards     │  │
│  │ 15 due        │  │
│  │ 5 new         │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ ▶ Start Review│  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ ➕ Add Card   │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ 📥 Import     │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ 📤 Export     │  │
│  └───────────────┘  │
│                     │
│  Cards (120)        │
│  [Due ▼] [Sort ▼]  │
│                     │
│  ┌───────────────┐  │
│  │ Front: What is│  │
│  │ "hello"?      │  │
│  │               │  │
│  │ Back: "Xin... │  │
│  │ Box: 3        │  │
│  │ Due: Jan 22   │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ Front: What is│  │
│  │ "goodbye"?   │  │
│  │               │  │
│  │ Back: "Tạm... │  │
│  │ Box: 1        │  │
│  │ Due: Today    │  │
│  └───────────────┘  │
│                     │
│  ... (scrollable)   │
│                     │
├─────────────────────┤
│ [🏠] [📁] [📚] [📊]│
└─────────────────────┘
```

**Key Elements**:
- Deck statistics
- Action buttons
- Card list with filters
- Swipe to delete (future)

---

## 5. Card Management

### 5.1 Create/Edit Card Screen

```
┌─────────────────────┐
│ ← Cancel  New Card  │
├─────────────────────┤
│                     │
│  Front *            │
│  ┌───────────────┐  │
│  │               │  │
│  │               │  │
│  │               │  │
│  │               │  │
│  └───────────────┘  │
│  0/5000 characters  │
│                     │
│  Back *             │
│  ┌───────────────┐  │
│  │               │  │
│  │               │  │
│  │               │  │
│  │               │  │
│  └───────────────┘  │
│  0/5000 characters  │
│                     │
│  ┌───────────────┐  │
│  │     Save      │  │
│  └───────────────┘  │
│                     │
└─────────────────────┘
```

**Key Elements**:
- Large text areas
- Character counter
- Save button
- Keyboard auto-focus

### 5.2 Import Cards Screen

```
┌─────────────────────┐
│ ← Back  Import Cards│
├─────────────────────┤
│                     │
│  Step 1 of 3        │
│                     │
│  Upload File        │
│                     │
│  ┌───────────────┐  │
│  │               │  │
│  │   📄 Select   │  │
│  │     File      │  │
│  │               │  │
│  └───────────────┘  │
│                     │
│  Supports:          │
│  CSV, XLSX          │
│  Max: 50MB          │
│  10,000 rows       │
│                     │
│  ┌───────────────┐  │
│  │     Next      │  │
│  └───────────────┘  │
│                     │
└─────────────────────┘
```

**Key Elements**:
- File picker button
- File format information
- Multi-step wizard
- Progress indicator

---

## 6. Review Session

### 6.1 Review Session Screen (Front)

```
┌─────────────────────┐
│ ← Exit  Review      │
├─────────────────────┤
│                     │
│  15/120 (12%)       │
│  ┌───────────────┐  │
│  │████░░░░░░░░░░ │  │
│  └───────────────┘  │
│                     │
│                     │
│  ┌───────────────┐  │
│  │               │  │
│  │               │  │
│  │               │  │
│  │    Front      │  │
│  │               │  │
│  │               │  │
│  │               │  │
│  │               │  │
│  │               │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ Show Answer   │  │
│  └───────────────┘  │
│                     │
│  [Skip] [Undo]      │
│                     │
├─────────────────────┤
│ [🏠] [📁] [📚] [📊]│
└─────────────────────┘
```

**Key Elements**:
- Progress bar
- Large card display area
- Show answer button
- Skip and Undo buttons

### 6.2 Review Session Screen (Back with Rating)

```
┌─────────────────────┐
│ ← Exit  Review      │
├─────────────────────┤
│                     │
│  15/120 (12%)       │
│  ┌───────────────┐  │
│  │████░░░░░░░░░░ │  │
│  └───────────────┘  │
│                     │
│                     │
│  ┌───────────────┐  │
│  │               │  │
│  │    Front      │  │
│  │               │  │
│  │ What is       │  │
│  │ "hello"?      │  │
│  │               │  │
│  │ ───────────── │  │
│  │               │  │
│  │    Back       │  │
│  │               │  │
│  │ "Xin chào"    │  │
│  │               │  │
│  └───────────────┘  │
│                     │
│  Rating:            │
│  ┌───────────────┐  │
│  │ [Again] [Hard]│  │
│  │ [Good] [Easy] │  │
│  └───────────────┘  │
│                     │
│  [Skip] [Undo]      │
│                     │
├─────────────────────┤
│ [🏠] [📁] [📚] [📊]│
└─────────────────────┘
```

**Key Elements**:
- Card flip animation
- Rating buttons (4 options)
- Large touch targets
- Visual feedback on tap

### 6.3 Review Session Complete

```
┌─────────────────────┐
│                     │
├─────────────────────┤
│                     │
│      🎉            │
│                     │
│  Session Complete!  │
│                     │
│  ┌───────────────┐  │
│  │ Reviewed:     │  │
│  │   120 cards   │  │
│  │               │  │
│  │ Time:         │  │
│  │   25 minutes  │  │
│  │               │  │
│  │ Streak:       │  │
│  │   7 days 🔥   │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ Review Again  │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │ Back to Home  │  │
│  └───────────────┘  │
│                     │
└─────────────────────┘
```

**Key Elements**:
- Celebration message
- Session statistics
- Action buttons
- Motivation elements

---

## 7. Settings

### 7.1 Settings Screen

```
┌─────────────────────┐
│ ← Back  Settings    │
├─────────────────────┤
│                     │
│  Profile            │
│  ┌───────────────┐  │
│  │ Name, Email   │  │
│  │ >             │  │
│  └───────────────┘  │
│                     │
│  Account            │
│  ┌───────────────┐  │
│  │ Change Password│  │
│  │ >             │  │
│  └───────────────┘  │
│                     │
│  SRS Settings       │
│  ┌───────────────┐  │
│  │ Configure SRS │  │
│  │ >             │  │
│  └───────────────┘  │
│                     │
│  Preferences        │
│  ┌───────────────┐  │
│  │ Language      │  │
│  │ EN >          │  │
│  └───────────────┘  │
│  ┌───────────────┐  │
│  │ Theme         │  │
│  │ Dark >        │  │
│  └───────────────┘  │
│                     │
│  Account Actions    │
│  ┌───────────────┐  │
│  │ Logout        │  │
│  └───────────────┘  │
│  ┌───────────────┐  │
│  │ Logout All    │  │
│  └───────────────┘  │
│                     │
└─────────────────────┘
```

**Key Elements**:
- Settings sections
- Navigation items
- Account actions
- Current values displayed

### 7.2 SRS Settings Screen

```
┌─────────────────────┐
│ ← Back  SRS Settings│
├─────────────────────┤
│                     │
│  Total Boxes        │
│  ┌───────────────┐  │
│  │     7        │  │
│  │    [─] [+]    │  │
│  └───────────────┘  │
│                     │
│  Review Order       │
│  ┌───────────────┐  │
│  │ ● Due Date   │  │
│  │ ○ Random     │  │
│  │ ○ Box Order  │  │
│  └───────────────┘  │
│                     │
│  Daily Limits       │
│  ┌───────────────┐  │
│  │ New Cards/Day │  │
│  │ [20] [─] [+]  │  │
│  └───────────────┘  │
│  ┌───────────────┐  │
│  │ Max Reviews   │  │
│  │ [200] [─] [+] │  │
│  └───────────────┘  │
│                     │
│  Forgotten Card     │
│  ┌───────────────┐  │
│  │ ● Move to Box 1│  │
│  │ ○ Move Down N │  │
│  │ ○ Repeat      │  │
│  └───────────────┘  │
│                     │
│  ┌───────────────┐  │
│  │     Save      │  │
│  └───────────────┘  │
│                     │
└─────────────────────┘
```

**Key Elements**:
- Number pickers
- Radio buttons
- Sliders for limits
- Save button

---

## 8. Statistics

### 8.1 Statistics Screen

```
┌─────────────────────┐
│ ← Back  Statistics  │
├─────────────────────┤
│                     │
│  Overview           │
│  ┌───────────────┐  │
│  │ Streak: 7 🔥  │  │
│  └───────────────┘  │
│  ┌───────────────┐  │
│  │ Reviewed: 45  │  │
│  │ Today        │  │
│  └───────────────┘  │
│                     │
│  Box Distribution   │
│  ┌───────────────┐  │
│  │ [Bar Chart]   │  │
│  │               │  │
│  │ Box 1: 150    │  │
│  │ Box 2: 80     │  │
│  │ Box 3: 45     │  │
│  │ ...           │  │
│  └───────────────┘  │
│                     │
│  Review History     │
│  ┌───────────────┐  │
│  │ [Line Chart]  │  │
│  │ Last 30 days  │  │
│  └───────────────┘  │
│                     │
│  Total Stats        │
│  ┌───────────────┐  │
│  │ Cards: 1,250  │  │
│  │ Decks: 15     │  │
│  │ Folders: 8    │  │
│  └───────────────┘  │
│                     │
├─────────────────────┤
│ [🏠] [📁] [📚] [📊]│
└─────────────────────┘
```

**Key Elements**:
- Statistics cards
- Charts (bar, line)
- Scrollable content
- Time period selector

---

## Navigation Patterns

### Bottom Tab Navigation

```
┌─────────────────────┐
│                     │
│   Main Content      │
│                     │
│                     │
├─────────────────────┤
│ [🏠] [📁] [📚] [📊]│
│ Home Folders Decks │
│      Stats          │
└─────────────────────┘
```

### Stack Navigation

```
Screen 1 → Screen 2 → Screen 3
  ← Back      ← Back    ← Back
```

### Modal Navigation

```
Screen (Modal)
  [X Close]
  Content
  [Action]
```

---

## Common UI Components

### App Bar

```
┌─────────────────────┐
│ [←] Title      [⋮]  │
└─────────────────────┘
```

### Cards

```
┌───────────────┐
│               │
│   Card Title  │
│   Content     │
│               │
└───────────────┘
```

### Buttons

- Primary: `┌───────────────┐\n│  Primary Action │\n└───────────────┘`
- Secondary: `┌───────────────┐\n│  Secondary     │\n└───────────────┘`
- Text: `[Text Link]`

### Input Fields

```
┌───────────────┐
│ Label         │
│ [____________]│
│ Hint text     │
└───────────────┘
```

### Lists

```
┌───────────────┐
│ Item 1      > │
├───────────────┤
│ Item 2      > │
├───────────────┤
│ Item 3      > │
└───────────────┘
```

---

## Gestures

### Swipe Actions
- **Swipe Left**: Delete/Archive
- **Swipe Right**: Favorite/Mark
- **Pull to Refresh**: Refresh list

### Tap Actions
- **Single Tap**: Open/Select
- **Long Press**: Context menu
- **Double Tap**: Quick action

### Pinch/Zoom
- **Pinch**: Zoom card content (future)

---

## Platform-Specific Considerations

### iOS
- Native navigation patterns
- iOS-style modals
- Safe area insets
- Haptic feedback

### Android
- Material Design components
- Android-style navigation
- Back button handling
- Material ripple effects

### Common
- Bottom navigation (primary)
- Stack navigation (secondary)
- Modal overlays
- Pull-to-refresh

---

## Screen Flow Diagram

```
Login/Register
    ↓
Home/Dashboard
    ↓
├─→ Folders → Folder Detail → Decks → Deck Detail → Cards
│
├─→ Decks → Deck Detail → Cards → Create/Edit Card
│
├─→ Review Session → Complete
│
└─→ Settings → Profile/SRS Settings
```

---

## Notes

- All screens are designed for mobile-first approach
- Touch targets minimum 44x44 points
- Text size minimum 14pt for readability
- Dark mode support throughout
- Accessibility labels for screen readers
- Loading states and error handling
- Empty states with helpful messages
- Pull-to-refresh on list screens
- Infinite scroll for long lists

