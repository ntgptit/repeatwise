# Web Application Wireframes (MVP)

This document provides detailed wireframes for the RepeatWise web application, covering all major screens and user flows.

## Navigation Structure

```
┌─────────────────────────────────────────────────────────┐
│ Header: Logo | Navigation | User Menu | Theme Toggle    │
├─────────────────────────────────────────────────────────┤
│ Sidebar (Folders Tree) │ Main Content Area              │
│                         │                               │
│                         │                               │
└─────────────────────────────────────────────────────────┘
```

## 1. Authentication Screens

### 1.1 Registration Page

```
┌─────────────────────────────────────────────────────────┐
│                    RepeatWise Logo                      │
│                                                         │
│              ┌───────────────────────┐                 │
│              │   Create Account      │                 │
│              ├───────────────────────┤                 │
│              │                       │                 │
│              │ Email: [___________]  │                 │
│              │                       │                 │
│              │ Username (optional):  │                 │
│              │ [___________]         │                 │
│              │ 3-30 chars, a-z0-9_-  │                 │
│              │                       │                 │
│              │ Password: [________]  │                 │
│              │ Min 8 characters      │                 │
│              │                       │                 │
│              │ Confirm Password:     │                 │
│              │ [________]            │                 │
│              │                       │                 │
│              │ Name (optional):      │                 │
│              │ [___________]         │                 │
│              │                       │                 │
│              │ [  Register  ]        │                 │
│              │                       │                 │
│              │ Already have account? │                 │
│              │ [Login]               │                 │
│              └───────────────────────┘                 │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**Key Elements**:
- Email field (required, validated)
- Username field (optional, format validation)
- Password fields (min 8 chars, match validation)
- Name field (optional)
- Link to login page
- Form validation messages inline

### 1.2 Login Page

```
┌─────────────────────────────────────────────────────────┐
│                    RepeatWise Logo                      │
│                                                         │
│              ┌───────────────────────┐                 │
│              │       Login           │                 │
│              ├───────────────────────┤                 │
│              │                       │                 │
│              │ Username or Email:    │                 │
│              │ [___________________]  │                 │
│              │                       │                 │
│              │ Password:             │                 │
│              │ [___________________]  │                 │
│              │ [ ] Remember me       │                 │
│              │                       │                 │
│              │ [  Login  ]           │                 │
│              │                       │                 │
│              │ [Forgot Password?]    │                 │
│              │                       │                 │
│              │ Don't have account?   │                 │
│              │ [Register]             │                 │
│              └───────────────────────┘                 │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**Key Elements**:
- Username/Email field (auto-detects format)
- Password field
- Remember me checkbox
- Login button
- Links to register and forgot password

---

## 2. Dashboard / Home Screen

```
┌─────────────────────────────────────────────────────────┐
│ ☰ Logo  RepeatWise    [Search]  [User] [Theme] [Logout]│
├──────────┬──────────────────────────────────────────────┤
│ Folders  │ Dashboard                                     │
│          │                                               │
│ 📁 Root  │ ┌──────────┐ ┌──────────┐ ┌──────────┐     │
│   ├─ 📁  │ │  120     │ │   45     │ │    7     │     │
│   │  └─  │ │  Cards   │ │   Due    │ │  Streak  │     │
│   │  📁  │ │  Total   │ │  Cards   │ │  Days    │     │
│   │      │ └──────────┘ └──────────┘ └──────────┘     │
│   ├─ 📁  │                                               │
│   └─ 📁  │ ┌─────────────────────────────────────┐     │
│          │ │  Quick Actions                       │     │
│ 📚 Decks │ ├─────────────────────────────────────┤     │
│          │ │ [Start Review] [Create Deck]        │     │
│          │ │ [Import Cards] [View Statistics]     │     │
│          │ └─────────────────────────────────────┘     │
│          │                                               │
│          │ ┌─────────────────────────────────────┐     │
│          │ │  Recent Activity                     │     │
│          │ ├─────────────────────────────────────┤     │
│          │ │ • Reviewed 45 cards today            │     │
│          │ │ • Created "Vocab Deck" yesterday     │     │
│          │ │ • Imported 120 cards 2 days ago      │     │
│          │ └─────────────────────────────────────┘     │
│          │                                               │
│          │ ┌─────────────────────────────────────┐     │
│          │ │  Box Distribution                   │     │
│          │ │  [Chart: Box 1-7 with counts]       │     │
│          │ └─────────────────────────────────────┘     │
└──────────┴──────────────────────────────────────────────┘
```

**Key Elements**:
- Left sidebar: Folder tree navigation
- Header: Logo, search, user menu, theme toggle
- Dashboard cards: Total cards, due cards, streak
- Quick actions: Start review, create deck, import, stats
- Recent activity feed
- Box distribution chart

---

## 3. Folder Management

### 3.1 Folder Tree View

```
┌─────────────────────────────────────────────────────────┐
│ ☰ Logo  [Search]  [User] [Theme]                       │
├──────────┬──────────────────────────────────────────────┤
│ Folders  │ Folder: IELTS Preparation                    │
│          │                                               │
│ 📁 Root  │ ┌─────────────────────────────────────┐     │
│   ├─ 📁  │ │ Stats:                              │     │
│   │  └─  │ │ • Total Decks: 15                  │     │
│   │  📁  │ │ • Total Cards: 1,250               │     │
│   │      │ │ • Due Cards: 45                    │     │
│   │      │ │ • New Cards: 12                    │     │
│   │      │ └─────────────────────────────────────┘     │
│   ├─ 📁  │                                               │
│   │  └─  │ ┌─────────────────────────────────────┐     │
│   │  📁  │ │ Actions:                             │     │
│   │      │ │ [Create Folder] [Create Deck]        │     │
│   │      │ │ [Rename] [Move] [Copy] [Delete]     │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   └─ 📁  │ ┌─────────────────────────────────────┐     │
│          │ │ Decks (15)                          │     │
│ 📚 Decks │ ├─────────────────────────────────────┤     │
│          │ │ [🔍 Filter] [Sort: Name ▼]          │     │
│          │ │                                     │     │
│          │ │ ┌─────────────────────────────┐   │     │
│          │ │ │ Vocabulary Deck             │   │     │
│          │ │ │ 120 cards • 15 due          │   │     │
│          │ │ │ [Open] [Edit] [Delete]     │   │     │
│          │ │ └─────────────────────────────┘   │     │
│          │ │                                     │     │
│          │ │ ┌─────────────────────────────┐   │     │
│          │ │ │ Grammar Deck                │   │     │
│          │ │ │ 85 cards • 10 due           │   │     │
│          │ │ │ [Open] [Edit] [Delete]     │   │     │
│          │ │ └─────────────────────────────┘   │     │
│          │ │                                     │     │
│          │ │ ... (paginated)                    │     │
│          │ └─────────────────────────────────────┘     │
└──────────┴──────────────────────────────────────────────┘
```

**Key Elements**:
- Left sidebar: Expandable folder tree
- Breadcrumb navigation
- Folder statistics panel
- Action buttons (Create, Rename, Move, Copy, Delete)
- Deck list with pagination

### 3.2 Create Folder Modal

```
┌─────────────────────────────────────────────────────────┐
│              ┌───────────────────────────┐              │
│              │    Create New Folder       │              │
│              ├───────────────────────────┤              │
│              │                           │              │
│              │ Name: *                   │              │
│              │ [___________________]     │              │
│              │ Max 100 characters         │              │
│              │                           │              │
│              │ Description (optional):    │              │
│              │ [___________________]     │              │
│              │ [___________________]     │              │
│              │ Max 500 characters         │              │
│              │                           │              │
│              │ Parent Folder:            │              │
│              │ [Root ▼]                  │              │
│              │                           │              │
│              │ [Cancel]      [Create]    │              │
│              └───────────────────────────┘              │
└─────────────────────────────────────────────────────────┘
```

**Key Elements**:
- Name field (required, validated)
- Description field (optional)
- Parent folder selector (dropdown)
- Validation messages
- Cancel and Create buttons

### 3.3 Move Folder Modal

```
┌─────────────────────────────────────────────────────────┐
│              ┌───────────────────────────┐              │
│              │    Move Folder             │              │
│              ├───────────────────────────┤              │
│              │                           │              │
│              │ Moving: "IELTS Prep"      │              │
│              │                           │              │
│              │ Select Destination:       │              │
│              │ ┌─────────────────────┐  │              │
│              │ │ 📁 Root              │  │              │
│              │ │   ├─ 📁 Languages    │  │              │
│              │ │   ├─ 📁 Science      │  │              │
│              │ │   └─ 📁 Mathematics │  │              │
│              │ └─────────────────────┘  │              │
│              │                           │              │
│              │ ⚠ Cannot move into:      │              │
│              │   • Itself                │              │
│              │   • Its descendants       │              │
│              │                           │              │
│              │ [Cancel]      [Move]     │              │
│              └───────────────────────────┘              │
└─────────────────────────────────────────────────────────┘
```

**Key Elements**:
- Source folder display
- Destination folder tree selector
- Validation warnings
- Depth limit check (max 10)

---

## 4. Deck Management

### 4.1 Deck List View

```
┌─────────────────────────────────────────────────────────┐
│ ☰ Logo  [Search]  [User] [Theme]                       │
├──────────┬──────────────────────────────────────────────┤
│ Folders  │ Decks                                         │
│          │                                               │
│ 📁 Root  │ ┌─────────────────────────────────────┐     │
│   ├─ 📁  │ │ [🔍 Search] [Filter ▼] [Sort ▼]  │     │
│   │  └─  │ │ [New Deck] [Import] [Export]      │     │
│   │  📁  │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   ├─ 📁  │ ┌─────────────────────────────────────┐     │
│   │  └─  │ │ Vocabulary Deck                     │     │
│   │  📁  │ │ 120 cards • 15 due • Last: 2h ago │     │
│   │      │ │ [Open] [Edit] [Move] [Copy] [Del] │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   └─ 📁  │ ┌─────────────────────────────────────┐     │
│          │ │ Grammar Deck                        │     │
│ 📚 Decks │ │ 85 cards • 10 due • Last: 1d ago   │     │
│          │ │ [Open] [Edit] [Move] [Copy] [Del]  │     │
│          │ └─────────────────────────────────────┘     │
│          │                                               │
│          │ ┌─────────────────────────────────────┐     │
│          │ │ ... (paginated, 50 per page)        │     │
│          │ └─────────────────────────────────────┘     │
└──────────┴──────────────────────────────────────────────┘
```

**Key Elements**:
- Search and filter controls
- Deck cards with statistics
- Action buttons per deck
- Pagination

### 4.2 Deck Detail View

```
┌─────────────────────────────────────────────────────────┐
│ ☰ Logo  [Search]  [User] [Theme]                       │
├──────────┬──────────────────────────────────────────────┤
│ Folders  │ Deck: Vocabulary Deck                         │
│          │                                               │
│ 📁 Root  │ ┌─────────────────────────────────────┐     │
│   ├─ 📁  │ │ Description: English vocabulary    │     │
│   │  └─  │ │ Created: Jan 15, 2024               │     │
│   │  📁  │ │ Updated: Jan 20, 2024               │     │
│   │      │ │                                     │     │
│   │      │ │ Stats:                              │     │
│   │      │ │ • Total Cards: 120                  │     │
│   │      │ │ • Due Cards: 15                    │     │
│   │      │ │ • New Cards: 5                     │     │
│   │      │ └─────────────────────────────────────┘     │
│   ├─ 📁  │                                               │
│   │  └─  │ ┌─────────────────────────────────────┐     │
│   │  📁  │ │ Actions:                             │     │
│   │      │ │ [Start Review] [Add Card] [Import]  │     │
│   │      │ │ [Export] [Edit] [Move] [Copy]     │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   └─ 📁  │ ┌─────────────────────────────────────┐     │
│          │ │ Cards (120)                         │     │
│ 📚 Decks │ ├─────────────────────────────────────┤     │
│          │ │ [🔍 Search] [Filter: Due ▼] [Sort] │     │
│          │ │                                     │     │
│          │ │ ┌─────────────────────────────┐   │     │
│          │ │ │ Front: What is "hello"?     │   │     │
│          │ │ │ Back: "Xin chào"            │   │     │
│          │ │ │ Box: 3 • Due: Jan 22        │   │     │
│          │ │ │ [Edit] [Delete]              │   │     │
│          │ │ └─────────────────────────────┘   │     │
│          │ │                                     │     │
│          │ │ ┌─────────────────────────────┐   │     │
│          │ │ │ Front: What is "goodbye"?   │   │     │
│          │ │ │ Back: "Tạm biệt"            │   │     │
│          │ │ │ Box: 1 • Due: Today         │   │     │
│          │ │ │ [Edit] [Delete]              │   │     │
│          │ │ └─────────────────────────────┘   │     │
│          │ │                                     │     │
│          │ │ ... (paginated, 100 per page)      │     │
│          │ └─────────────────────────────────────┘     │
└──────────┴──────────────────────────────────────────────┘
```

**Key Elements**:
- Deck metadata display
- Statistics panel
- Action buttons
- Card list with search/filter
- Card cards with SRS state

---

## 5. Card Management

### 5.1 Create/Edit Card Modal

```
┌─────────────────────────────────────────────────────────┐
│              ┌───────────────────────────┐              │
│              │    Create New Card         │              │
│              ├───────────────────────────┤              │
│              │                           │              │
│              │ Front: *                   │              │
│              │ ┌───────────────────────┐ │              │
│              │ │                       │ │              │
│              │ │                       │ │              │
│              │ │                       │ │              │
│              │ │                       │ │              │
│              │ └───────────────────────┘ │              │
│              │ [0/5000 characters]        │              │
│              │                           │              │
│              │ Back: *                   │              │
│              │ ┌───────────────────────┐ │              │
│              │ │                       │ │              │
│              │ │                       │ │              │
│              │ │                       │ │              │
│              │ │                       │ │              │
│              │ └───────────────────────┘ │              │
│              │ [0/5000 characters]        │              │
│              │                           │              │
│              │ [Cancel]      [Create]    │              │
│              └───────────────────────────┘              │
└─────────────────────────────────────────────────────────┘
```

**Key Elements**:
- Front textarea (required, max 5000 chars)
- Back textarea (required, max 5000 chars)
- Character counter
- Validation messages

### 5.2 Import Cards Wizard

```
┌─────────────────────────────────────────────────────────┐
│              ┌───────────────────────────┐              │
│              │    Import Cards            │              │
│              │    Step 1 of 3             │              │
│              ├───────────────────────────┤              │
│              │                           │              │
│              │ 1. Upload File            │              │
│              │    ┌─────────────────┐   │              │
│              │    │   Drag & Drop   │   │              │
│              │    │   or Click      │   │              │
│              │    │   to Upload     │   │              │
│              │    └─────────────────┘   │              │
│              │    Supports: CSV, XLSX    │              │
│              │    Max: 50MB, 10,000 rows │              │
│              │                           │              │
│              │ [Cancel]      [Next >]   │              │
│              └───────────────────────────┘              │
│                                                         │
│              ┌───────────────────────────┐              │
│              │    Step 2: Map Columns    │              │
│              │    Column Mapping:        │              │
│              │                           │              │
│              │ Front: [Front ▼]         │              │
│              │ Back:  [Back ▼]         │              │
│              │                           │              │
│              │ Preview (first 5 rows):   │              │
│              │ [Table preview]           │              │
│              │                           │              │
│              │ [< Back]      [Next >]   │              │
│              └───────────────────────────┘              │
│                                                         │
│              ┌───────────────────────────┐              │
│              │    Step 3: Review & Import│              │
│              │                           │              │
│              │ File: cards.csv           │              │
│              │ Rows: 1,000               │              │
│              │                           │              │
│              │ Validation Results:       │              │
│              │ ✅ Valid: 950 rows         │              │
│              │ ⚠ Warnings: 30 rows      │              │
│              │ ❌ Errors: 20 rows        │              │
│              │                           │              │
│              │ [View Errors]             │              │
│              │                           │              │
│              │ [< Back]      [Import]   │              │
│              └───────────────────────────┘              │
└─────────────────────────────────────────────────────────┘
```

**Key Elements**:
- Multi-step wizard
- File upload with drag & drop
- Column mapping interface
- Validation preview
- Error reporting

---

## 6. Review Session

### 6.1 Review Session Screen

```
┌─────────────────────────────────────────────────────────┐
│ ☰ Logo  [User] [Theme]                                  │
├──────────────────────────────────────────────────────────┤
│                                                          │
│              Review Session                              │
│              ┌─────────────────────────────┐             │
│              │ Progress: 15/120 (12%)      │             │
│              │ [████░░░░░░░░░░░░░░░░]      │             │
│              └─────────────────────────────┘             │
│                                                          │
│              ┌─────────────────────────────┐             │
│              │                             │             │
│              │                             │             │
│              │    Front Side               │             │
│              │                             │             │
│              │    What is "hello"?         │             │
│              │                             │             │
│              │                             │             │
│              │                             │             │
│              │        [Show Answer]        │             │
│              │                             │             │
│              └─────────────────────────────┘             │
│                                                          │
│              ┌─────────────────────────────┐             │
│              │                             │             │
│              │                             │             │
│              │    Back Side                │             │
│              │                             │             │
│              │    "Xin chào"               │             │
│              │                             │             │
│              │                             │             │
│              │                             │             │
│              └─────────────────────────────┘             │
│                                                          │
│              ┌─────────────────────────────┐             │
│              │ Rating:                    │             │
│              │ [Again] [Hard] [Good] [Easy]│            │
│              │                             │             │
│              │ [Skip] [Undo] [Edit Card]  │             │
│              └─────────────────────────────┘             │
│                                                          │
│              Keyboard Shortcuts:                          │
│              1=Again, 2=Hard, 3=Good, 4=Easy            │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

**Key Elements**:
- Progress bar
- Card display (front/back reveal)
- Rating buttons (AGAIN/HARD/GOOD/EASY)
- Additional actions (Skip, Undo, Edit)
- Keyboard shortcuts hint

### 6.2 Review Session Complete

```
┌─────────────────────────────────────────────────────────┐
│              ┌───────────────────────────┐              │
│              │    Session Complete!      │              │
│              ├───────────────────────────┤              │
│              │                           │              │
│              │ 🎉 Great job!              │              │
│              │                           │              │
│              │ You reviewed:             │              │
│              │ • 120 cards               │              │
│              │ • Time: 25 minutes        │              │
│              │ • Streak: 7 days          │              │
│              │                           │              │
│              │ Box Distribution:         │              │
│              │ [Chart]                   │              │
│              │                           │              │
│              │ [Review Again] [Back to Dashboard]│      │
│              └───────────────────────────┘              │
└─────────────────────────────────────────────────────────┘
```

**Key Elements**:
- Completion message
- Session statistics
- Box distribution chart
- Actions to continue or exit

---

## 7. Settings

### 7.1 Profile Settings

```
┌─────────────────────────────────────────────────────────┐
│ ☰ Logo  [Search]  [User] [Theme]                       │
├──────────┬──────────────────────────────────────────────┤
│ Folders  │ Settings > Profile                            │
│          │                                               │
│ 📁 Root  │ ┌─────────────────────────────────────┐     │
│   ├─ 📁  │ │ Profile Information                 │     │
│   │  └─  │ ├─────────────────────────────────────┤     │
│   │  📁  │ │ Name:                               │     │
│   │      │ │ [John Doe________________]          │     │
│   │      │ │                                     │     │
│   │      │ │ Username:                           │     │
│   │      │ │ [john_doe123___________]            │     │
│   │      │ │ 3-30 chars, unique                 │     │
│   │      │ │                                     │     │
│   │      │ │ Timezone:                          │     │
│   │      │ │ [Asia/Ho_Chi_Minh ▼]              │     │
│   │      │ │                                     │     │
│   │      │ │ Language:                          │     │
│   │      │ │ [○] Vietnamese  [●] English       │     │
│   │      │ │                                     │     │
│   │      │ │ Theme:                             │     │
│   │      │ │ [○] Light  [●] Dark  [○] System  │     │
│   │      │ │                                     │     │
│   │      │ │ [Cancel]      [Save Changes]      │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   │      │ ┌─────────────────────────────────────┐     │
│   │      │ │ Password                            │     │
│   │      │ ├─────────────────────────────────────┤     │
│   │      │ │ Current Password:                   │     │
│   │      │ │ [___________________]                │     │
│   │      │ │                                     │     │
│   │      │ │ New Password:                       │     │
│   │      │ │ [___________________]                │     │
│   │      │ │ Min 8 characters                     │     │
│   │      │ │                                     │     │
│   │      │ │ Confirm New Password:               │     │
│   │      │ │ [___________________]                │     │
│   │      │ │                                     │     │
│   │      │ │ [Change Password]                   │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   │      │ ┌─────────────────────────────────────┐     │
│   │      │ │ Account Actions                     │     │
│   │      │ ├─────────────────────────────────────┤     │
│   │      │ │ [Logout]                            │     │
│   │      │ │ [Logout All Devices]                │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   └─ 📁  │                                               │
│          │                                               │
│ 📚 Decks │                                               │
│          │                                               │
└──────────┴──────────────────────────────────────────────┘
```

**Key Elements**:
- Profile form fields
- Password change section
- Account actions
- Form validation

### 7.2 SRS Settings

```
┌─────────────────────────────────────────────────────────┐
│ ☰ Logo  [Search]  [User] [Theme]                       │
├──────────┬──────────────────────────────────────────────┤
│ Folders  │ Settings > SRS                                │
│          │                                               │
│ 📁 Root  │ ┌─────────────────────────────────────┐     │
│   ├─ 📁  │ │ SRS Configuration                   │     │
│   │  └─  │ ├─────────────────────────────────────┤     │
│   │  📁  │ │ Total Boxes:                        │     │
│   │      │ │ [7] (3-10)                          │     │
│   │      │ │                                     │     │
│   │      │ │ Review Order:                       │     │
│   │      │ │ [●] Due Date (Oldest First)        │     │
│   │      │ │ [○] Random                         │     │
│   │      │ │ [○] Current Box (Lowest First)     │     │
│   │      │ │                                     │     │
│   │      │ │ Daily Limits:                      │     │
│   │      │ │ New Cards Per Day: [20] (1-500)    │     │
│   │      │ │ Max Reviews Per Day: [200] (1-1000)│     │
│   │      │ │                                     │     │
│   │      │ │ Forgotten Card Action:             │     │
│   │      │ │ [●] Move to Box 1                 │     │
│   │      │ │ [○] Move Down N Boxes             │     │
│   │      │ │ [○] Repeat in Session             │     │
│   │      │ │                                     │     │
│   │      │ │ Move Down Boxes (if selected):     │     │
│   │      │ │ [1] (1-3)                          │     │
│   │      │ │                                     │     │
│   │      │ │ Notifications:                     │     │
│   │      │ │ [✓] Enable daily notifications     │     │
│   │      │ │ Time: [09:00]                      │     │
│   │      │ │                                     │     │
│   │      │ │ [Cancel]      [Save Changes]      │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   │      │ ┌─────────────────────────────────────┐     │
│   │      │ │ Box Intervals (Default)             │     │
│   │      │ ├─────────────────────────────────────┤     │
│   │      │ │ Box 1: 1 day                        │     │
│   │      │ │ Box 2: 3 days                       │     │
│   │      │ │ Box 3: 7 days                       │     │
│   │      │ │ Box 4: 14 days                      │     │
│   │      │ │ Box 5: 30 days                      │     │
│   │      │ │ Box 6: 60 days                      │     │
│   │      │ │ Box 7: 120 days                     │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   └─ 📁  │                                               │
│          │                                               │
│ 📚 Decks │                                               │
│          │                                               │
└──────────┴──────────────────────────────────────────────┘
```

**Key Elements**:
- SRS configuration form
- Box intervals display
- Validation for ranges
- Radio buttons and checkboxes

---

## 8. Statistics

### 8.1 Statistics Dashboard

```
┌─────────────────────────────────────────────────────────┐
│ ☰ Logo  [Search]  [User] [Theme]                       │
├──────────┬──────────────────────────────────────────────┤
│ Folders  │ Statistics                                    │
│          │                                               │
│ 📁 Root  │ ┌─────────────────────────────────────┐     │
│   ├─ 📁  │ │ Overview                             │     │
│   │  └─  │ ├─────────────────────────────────────┤     │
│   │  📁  │ │ Streak: 7 days 🔥                   │     │
│   │      │ │                                     │     │
│   │      │ │ Cards Reviewed Today: 45            │     │
│   │      │ │ Total Cards: 1,250                  │     │
│   │      │ │ Total Decks: 15                     │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   ├─ 📁  │ ┌─────────────────────────────────────┐     │
│   │  └─  │ │ Box Distribution                   │     │
│   │  📁  │ │ [Bar Chart: Box 1-7 with counts]   │     │
│   │      │ │                                     │     │
│   │      │ │ Box 1: 150 cards                   │     │
│   │      │ │ Box 2: 80 cards                    │     │
│   │      │ │ ...                                │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   │      │ ┌─────────────────────────────────────┐     │
│   │      │ │ Review History (Last 30 Days)       │     │
│   │      │ │ [Line Chart: Reviews per day]       │     │
│   │      │ └─────────────────────────────────────┘     │
│   │      │                                               │
│   └─ 📁  │                                               │
│          │                                               │
│ 📚 Decks │                                               │
│          │                                               │
└──────────┴──────────────────────────────────────────────┘
```

**Key Elements**:
- Overview statistics
- Box distribution chart
- Review history chart
- Time range selector (future)

---

## Layout Patterns

### Common Header

```
┌─────────────────────────────────────────────────────────┐
│ [☰] RepeatWise Logo  [🔍 Search]  [User▼] [🌙] [Logout]│
└─────────────────────────────────────────────────────────┘
```

### Common Sidebar

```
┌──────────┐
│ Folders  │
│          │
│ 📁 Root  │
│   ├─ 📁  │
│   │  └─  │
│   │  📁  │
│   │      │
│   ├─ 📁  │
│   │  └─  │
│   │  📁  │
│   │      │
│   └─ 📁  │
│          │
│ 📚 Decks │
│          │
│ [Expand] │
└──────────┘
```

### Common Modal Pattern

```
┌─────────────────────────────────────────────────────────┐
│              ┌───────────────────────────┐              │
│              │    Modal Title             │              │
│              ├───────────────────────────┤              │
│              │                           │              │
│              │ Content area              │              │
│              │                           │              │
│              │                           │              │
│              │                           │              │
│              │ [Cancel]      [Action]    │              │
│              └───────────────────────────┘              │
└─────────────────────────────────────────────────────────┘
```

---

## Responsive Breakpoints

### Desktop (> 1024px)
- Full sidebar + main content
- Multi-column layouts
- Hover states

### Tablet (768px - 1024px)
- Collapsible sidebar
- Stacked layouts
- Touch-friendly buttons

### Mobile (< 768px)
- Bottom navigation
- Full-width content
- Hamburger menu
- Stacked cards

---

## UI Components Library

### Buttons
- Primary: `[Primary Action]`
- Secondary: `[Secondary]`
- Danger: `[Delete]`
- Text: `[Cancel]`

### Input Fields
- Text: `[___________]`
- Password: `[●●●●●●●●]`
- Textarea: `┌─────┐\n│     │\n└─────┘`
- Select: `[Option ▼]`

### Cards
- Container: `┌─────────────┐\n│  Content    │\n└─────────────┘`

### Indicators
- Progress: `[████░░░░░░]`
- Status: `✅ ⚠️ ❌`
- Icons: `📁 📚 📝 🔍`

---

## Notes

- All wireframes use ASCII art for simplicity
- Actual implementation will use React components (Shadcn/ui)
- Colors and styling follow design system (light/dark themes)
- All interactive elements should be keyboard accessible
- Responsive design adapts layout for different screen sizes

