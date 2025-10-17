# Web Wireframes - RepeatWise MVP

## Mục đích
Wireframes này mô tả UI/UX cho React web app (desktop, tablet, mobile responsive).
Sử dụng ASCII art + annotations để AI có thể hiểu và implement UI.

## 1. Responsive Breakpoints

- **Desktop**: 1920x1080 (sidebar always visible)
- **Tablet**: 768px (sidebar collapsible overlay)
- **Mobile**: 375px (bottom nav, hamburger menu)

---

## 2. Layout Components

### 2.1 App Shell (Desktop 1920px)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ Header (h:64px)                                                     [Actions]│
│ [🔄 RepeatWise]          Search...      🌙Theme  🔔Notif  [👤 Profile ▼]    │
├───────────┬─────────────────────────────────────────────────────────────────┤
│ Sidebar   │                                                                  │
│ (w:260px) │                   Main Content Area                             │
│           │                   (Flex-grow)                                    │
│ [🏠 Home] │                                                                  │
│           │                                                                  │
│ Folders:  │                                                                  │
│ ├📁Eng    │                                                                  │
│ │├📂IELTS │                                                                  │
│ │└📂Biz   │                                                                  │
│ ├📁Math   │                                                                  │
│ └📁Code   │                                                                  │
│           │                                                                  │
│ [📊Stats] │                                                                  │
│ [⚙️ Set]  │                                                                  │
│           │                                                                  │
│           │                                                                  │
│           │                                                                  │
└───────────┴─────────────────────────────────────────────────────────────────┘

Elements:
- Header: Fixed top, shadow-sm, bg-white dark:bg-gray-900
- Sidebar: Fixed left, scrollable, collapsible on tablet
- Main: Padding 24px, max-width container
```

### 2.2 Responsive Layout (Tablet 768px)

```
┌──────────────────────────────────────────────────────────┐
│ [☰] RepeatWise   Search...   🌙  🔔  [Profile ▼]        │ h:64px
├──────────────────────────────────────────────────────────┤
│                                                          │
│                  Main Content                            │
│                  (Full width)                            │
│                                                          │
│                                                          │
│                                                          │
│                                                          │
│                                                          │
└──────────────────────────────────────────────────────────┘

Sidebar: Overlay (slide from left), backdrop blur
Trigger: Hamburger button (☰)
```

### 2.3 Mobile Layout (375px)

```
┌────────────────────────┐
│ [☰] RepeatWise    [🔍] │ h:56px
├────────────────────────┤
│                        │
│    Main Content        │
│    (Full width)        │
│                        │
│                        │
│                        │
│                        │
│                        │
├────────────────────────┤
│ [🏠] [📖] [📊] [⚙️]   │ h:56px (Bottom Nav)
└────────────────────────┘

Bottom Nav items:
- Home (Folders)
- Review (Due cards)
- Stats
- Settings
```

---

## 3. Authentication Pages

### 3.1 Login Page (Centered)

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                  │
│                       ┌────────────────────┐                    │
│                       │                    │                    │
│                       │  🔄 RepeatWise     │ Logo (centered)    │
│                       │  Spaced Repetition │ Tagline            │
│                       │                    │                    │
│                       │                    │                    │
│            ┌──────────────────────────────────────┐             │
│            │ Sign in to your account              │             │
│            │                                      │             │
│            │ Email address                        │             │
│            │ ┌──────────────────────────────────┐ │             │
│            │ │ you@example.com                  │ │             │
│            │ └──────────────────────────────────┘ │             │
│            │                                      │             │
│            │ Password                             │             │
│            │ ┌──────────────────────────────────┐ │             │
│            │ │ ••••••••••••••••••        [👁️]   │ │ Toggle     │
│            │ └──────────────────────────────────┘ │             │
│            │                                      │             │
│            │ ☑️ Remember me    Forgot password?   │             │
│            │                                      │             │
│            │ ┌──────────────────────────────────┐ │             │
│            │ │      Sign in                     │ │ Primary btn│
│            │ └──────────────────────────────────┘ │             │
│            │                                      │             │
│            │ ────────── or ──────────             │             │
│            │                                      │             │
│            │ Don't have an account?               │             │
│            │ [Create account]                     │ Link       │
│            │                                      │             │
│            └──────────────────────────────────────┘             │
│                                                                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

Interactions:
- Email: Validates on blur (format check)
- Password: Toggle visibility with eye icon
- Remember me: Stores refresh token
- Submit: POST /api/auth/login
- Success: Redirect to /folders
- Error: Show inline error below form
```

### 3.2 Register Page (Similar layout)

```
[Similar to Login, with additional fields:]

- Full Name (required, max 100 chars)
- Email (required, unique check)
- Password (min 8 chars, strength indicator)
- Confirm Password (must match)
- [x] I agree to Terms & Privacy Policy
- [Create account] button
- Already have account? [Sign in]
```

---

## 4. Main Pages

### 4.1 Dashboard/Folders Page (Desktop)

```
┌──────────┬──────────────────────────────────────────────────────────────┐
│ Sidebar  │ Breadcrumb: Home                           [🔍] [+ New Folder]│
│          ├──────────────────────────────────────────────────────────────┤
│ [🏠Home] │                                                               │
│          │ My Folders (5)            View: [Grid ✓] [List]  Sort: [Name▼]│
│ Folders: │                                                               │
│ ├📁Eng   │ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │
│ │├📂IELTS│ │ ┌───┐   │ │ ┌───┐   │ │ ┌───┐   │ │ ┌───┐   │           │
│ │└📂Biz  │ │ │📁 │   │ │ │📁 │   │ │ │📁 │   │ │ │📁 │   │           │
│ ├📁Math  │ │ └───┘   │ │ └───┘   │ │ └───┘   │ │ └───┘   │           │
│ └📁Code  │ │ English │ │ Math    │ │ Code    │ │ Science │           │
│          │ │         │ │         │ │         │ │         │           │
│ ───────  │ │ 5 decks │ │ 3 decks │ │ 8 decks │ │ 2 decks │           │
│          │ │120 cards│ │45 cards │ │200 cards│ │30 cards │           │
│ [📊Stats]│ │         │ │         │ │         │ │         │           │
│          │ │ 25 due  │ │ 10 due  │ │ 32 due  │ │ 5 due   │           │
│ [⚙️ Set] │ │         │ │         │ │         │ │         │           │
│          │ │ [Open]  │ │ [Open]  │ │ [Open]  │ │ [Open]  │           │
│          │ └─────────┘ └─────────┘ └─────────┘ └─────────┘           │
│          │                                                               │
│          │ ──────────────────────────────────────────────────────────   │
│          │                                                               │
│          │ Recent Activity                                               │
│          │                                                               │
│          │ ┌────────────────────────────────────────────────┐           │
│          │ │ 📂 IELTS Vocabulary              120 cards     │           │
│          │ │    45 due • Last review: 2 hours ago           │  [Study] │
│          │ ├────────────────────────────────────────────────┤           │
│          │ │ 📂 Java Design Patterns          80 cards      │           │
│          │ │    12 due • Last review: 1 day ago             │  [Study] │
│          │ ├────────────────────────────────────────────────┤           │
│          │ │ 📂 Spanish Verbs                 150 cards     │           │
│          │ │    0 due • Last review: 3 days ago             │  [Browse]│
│          │ └────────────────────────────────────────────────┘           │
│          │                                                               │
└──────────┴──────────────────────────────────────────────────────────────┘

Card Hover Effect:
- Shadow elevation increase
- Subtle scale (1.02)
- Highlight border (primary color)

Card Click:
- Navigate to FolderDetailScreen
- Show loading state (skeleton)

Actions Menu (hover):
- [...] button appears top-right
- Dropdown: Rename, Move, Copy, Delete
```

### 4.2 Folder Detail Page

```
┌──────────┬──────────────────────────────────────────────────────────────┐
│ Sidebar  │ Breadcrumb: Home > English > IELTS         [⋮ Actions] [✕]  │
│          ├──────────────────────────────────────────────────────────────┤
│ (Active: │                                                               │
│ IELTS)   │ 📁 IELTS Preparation                                         │
│          │                                                               │
│          │ ┌─────────────────────────────────────────────────────────┐  │
│          │ │ 📊 Folder Statistics                                    │  │
│          │ │                                                         │  │
│          │ │ Total Cards: 250    Due Today: 45    New: 20          │  │
│          │ │                                                         │  │
│          │ │ Progress: ████████░░ 80% mastered                      │  │
│          │ │                                                         │  │
│          │ │ Box Distribution:                                       │  │
│          │ │ [1:30] [2:45] [3:50] [4:40] [5:35] [6:25] [7:25]      │  │
│          │ └─────────────────────────────────────────────────────────┘  │
│          │                                                               │
│          │ Subfolders (3)                             [+ New Subfolder] │
│          │                                                               │
│          │ ┌─────────┐ ┌─────────┐ ┌─────────┐                         │
│          │ │ 📁      │ │ 📁      │ │ 📁      │                         │
│          │ │Vocabulary│ │Grammar  │ │Speaking │                         │
│          │ │         │ │         │ │         │                         │
│          │ │ 2 decks │ │ 1 deck  │ │ 1 deck  │                         │
│          │ │100 cards│ │50 cards │ │100 cards│                         │
│          │ │         │ │         │ │         │                         │
│          │ │ [Open]  │ │ [Open]  │ │ [Open]  │                         │
│          │ └─────────┘ └─────────┘ └─────────┘                         │
│          │                                                               │
│          │ ──────────────────────────────────────────────────────────   │
│          │                                                               │
│          │ Decks (0 directly in this folder)          [+ New Deck]      │
│          │                                                               │
│          │ ┌───────────────────────────────────────────────────────┐   │
│          │ │ No decks in this folder                               │   │
│          │ │ Create a deck or organize them in subfolders above    │   │
│          │ └───────────────────────────────────────────────────────┘   │
│          │                                                               │
└──────────┴──────────────────────────────────────────────────────────────┘

Actions Menu ([⋮]):
- Rename folder
- Move folder
- Copy folder (shows progress if >50 items)
- Delete folder (confirmation dialog)
- View statistics

Breadcrumb:
- Clickable links
- Current folder highlighted
- Hover: Underline
```

### 4.3 Deck Detail Page

```
┌──────────┬──────────────────────────────────────────────────────────────┐
│          │ Breadcrumb: Home>Eng>IELTS>Vocab         [⋮] [Import] [✕]   │
│          ├──────────────────────────────────────────────────────────────┤
│          │                                                               │
│          │ 📂 Academic Words                                            │
│          │                                                               │
│          │ ┌─────────────────────────────────────────────────────────┐  │
│          │ │ 📊 Deck Statistics                                      │  │
│          │ │                                                         │  │
│          │ │ Total: 120 cards  │  Due: 25  │  New: 10  │  Box Avg: 3│  │
│          │ │                                                         │  │
│          │ │ Last Studied: 2 hours ago  │  Study Time: 45 min      │  │
│          │ │                                                         │  │
│          │ │ Box Distribution:                                       │  │
│          │ │ ┌─┐ ┌─┐ ┌─┐ ┌─┐ ┌─┐ ┌─┐ ┌─┐                           │  │
│          │ │ │█│ │█│ │█│ │█│ │█│ │█│ │█│  (Bar chart)              │  │
│          │ │ │█│ │█│ │█│ │█│ │█│ │ │ │ │                           │  │
│          │ │ │█│ │█│ │█│ │█│ │ │ │ │ │ │                           │  │
│          │ │ 1:20 2:30 3:25 4:20 5:15 6:7 7:3                        │  │
│          │ └─────────────────────────────────────────────────────────┘  │
│          │                                                               │
│          │ ┌──────────────┐ ┌───────────┐ ┌───────────┐               │
│          │ │ 🎯 Start     │ │ 📝 Cram   │ │ 🎲 Random │               │
│          │ │   Review     │ │   Mode    │ │   Review  │               │
│          │ └──────────────┘ └───────────┘ └───────────┘               │
│          │                                                               │
│          │ ──────────────────────────────────────────────────────────   │
│          │                                                               │
│          │ Cards (120)                   [🔍 Search...] [Export] [+Add] │
│          │                                                               │
│          │ ┌──────────────────────────────────────────────────┐  [✏️] │
│          │ │ Front: Abandon                                    │  [🗑️] │
│          │ │ Back: To give up completely                       │        │
│          │ │ Box: 3  │  Due: Tomorrow  │  Reviews: 5           │        │
│          │ ├──────────────────────────────────────────────────┤  [✏️] │
│          │ │ Front: Aberrant                                   │  [🗑️] │
│          │ │ Back: Departing from the usual or normal type     │        │
│          │ │ Box: 2  │  Due: Today  │  Reviews: 3              │        │
│          │ ├──────────────────────────────────────────────────┤  [✏️] │
│          │ │ Front: Abhor                                      │  [🗑️] │
│          │ │ Back: To regard with disgust and hatred           │        │
│          │ │ Box: 1  │  Due: Today  │  Reviews: 1              │        │
│          │ └──────────────────────────────────────────────────┘        │
│          │                                                               │
│          │ Showing 1-10 of 120    [1] [2] [3] ... [12] →               │
│          │                                                               │
└──────────┴──────────────────────────────────────────────────────────────┘

Card Row Hover:
- Background highlight
- Action buttons visible ([✏️] [🗑️])

Inline Edit:
- Click [✏️] → Editable inputs
- [Save] [Cancel] buttons
- Auto-focus on front field

Delete:
- Click [🗑️] → Confirmation dialog
- "Delete card 'Abandon'?"
- [Cancel] [Delete]
```

### 4.4 Review Session Page (Fullscreen)

**Question State:**
```
┌─────────────────────────────────────────────────────────────────────┐
│ [← Exit]  Review: Academic Words      Progress: 15/45  🔥 Streak: 7│
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│                                                                      │
│                    ┌──────────────────────────┐                     │
│                    │                          │                     │
│                    │                          │                     │
│                    │                          │  Card (flippable)   │
│                    │       ABANDON            │                     │
│                    │                          │                     │
│                    │                          │                     │
│                    │                          │                     │
│                    │   (Click to reveal)      │  Hint               │
│                    │                          │                     │
│                    └──────────────────────────┘                     │
│                                                                      │
│                 Deck: Academic Words  │  Box: 3  │  Review #5       │
│                                                                      │
│                                                                      │
│                      ┌───────────────────┐                          │
│                      │  Show Answer      │  Primary button          │
│                      └───────────────────┘                          │
│                                                                      │
│                [Space] to reveal  │  [←] Undo  │  [→] Skip          │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘

Card Animation:
- Hover: Lift shadow, scale 1.02
- Click: Flip animation 300ms
- Reveal: Fade in back content
```

**Answer State:**
```
┌─────────────────────────────────────────────────────────────────────┐
│ [← Exit]  Review: Academic Words      Progress: 15/45  🔥 Streak: 7│
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│                    ┌──────────────────────────┐                     │
│                    │       ABANDON            │  Front (small)      │
│                    │         ───              │                     │
│                    │                          │                     │
│                    │   To give up completely  │  Back (emphasized)  │
│                    │                          │                     │
│                    └──────────────────────────┘                     │
│                                                                      │
│                How well did you remember this card?                 │
│                                                                      │
│     ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│     │  Again   │ │   Hard   │ │   Good   │ │   Easy   │          │
│     │  <1 min  │ │  <6 min  │ │  3 days  │ │  12 days │          │
│     │          │ │          │ │          │ │          │          │
│     │   [1]    │ │   [2]    │ │   [3]    │ │   [4]    │  Hotkeys│
│     └──────────┘ └──────────┘ └──────────┘ └──────────┘          │
│                                                                      │
│           [↶ Undo]  │  [⏭️ Skip]  │  [✏️ Edit Card]                 │
│                                                                      │
│                [1-4] to rate  │  [←] Undo  │  [→] Skip              │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘

Button Hover Effects:
- Again: Red highlight
- Hard: Orange highlight
- Good: Green highlight
- Easy: Blue highlight

After Rating:
- Fade out current card
- Slide in next card from right
- Update progress bar
- Show "Next card" loading indicator 200ms
```

---

## 5. Modal Dialogs

### 5.1 Create Folder Dialog

```
┌─────────────────────────────────────────────────────┐
│ Create New Folder                             [✕]   │
├─────────────────────────────────────────────────────┤
│                                                      │
│  Folder Name *                                       │
│  ┌─────────────────────────────────────────────┐    │
│  │ My New Folder                               │    │
│  └─────────────────────────────────────────────┘    │
│  Max 100 characters                                  │
│                                                      │
│  Description (optional)                              │
│  ┌─────────────────────────────────────────────┐    │
│  │                                             │    │
│  │                                             │    │
│  │                                             │    │
│  └─────────────────────────────────────────────┘    │
│  Max 500 characters                                  │
│                                                      │
│  Parent Folder                                       │
│  ┌─────────────────────────────────────────────┐    │
│  │ Select parent...                        [▼] │    │
│  └─────────────────────────────────────────────┘    │
│  Leave empty to create at root level                │
│                                                      │
│             [Cancel]           [Create Folder]       │
│                                                      │
└─────────────────────────────────────────────────────┘

Validation:
- Name: Required, max 100 chars, trim whitespace
- Description: Optional, max 500 chars
- Parent: Optional, must exist, depth check (<10)

Error Display:
- Below field, red text
- "Folder name is required"
- "Name already exists in parent"
- "Maximum depth exceeded"
```

### 5.2 Import Cards Dialog

**Step 1: File Upload**
```
┌─────────────────────────────────────────────────────┐
│ Import Cards to: Academic Words               [✕]   │
├─────────────────────────────────────────────────────┤
│                                                      │
│  Step 1: Upload File                                 │
│                                                      │
│  ┌─────────────────────────────────────────────┐    │
│  │                                             │    │
│  │     📄 Drag & drop CSV/Excel file here      │    │
│  │                   or                        │    │
│  │            [Browse Files]                   │    │
│  │                                             │    │
│  │   Supported formats: .csv, .xlsx            │    │
│  │   Max size: 50 MB, 10,000 rows              │    │
│  │                                             │    │
│  └─────────────────────────────────────────────┘    │
│                                                      │
│  [Download Template] to see format example           │
│                                                      │
│                          [Cancel]                    │
│                                                      │
└─────────────────────────────────────────────────────┘

Drag Over State:
- Border highlight (blue)
- Background tint
- Icon scale up
```

**Step 2: Preview & Validate**
```
┌─────────────────────────────────────────────────────┐
│ Import Cards to: Academic Words               [✕]   │
├─────────────────────────────────────────────────────┤
│                                                      │
│  Step 2: Review & Import                             │
│                                                      │
│  📄 vocabulary.csv (2.3 MB)                  [Remove]│
│                                                      │
│  ✓ 1,234 valid rows                                  │
│  ⚠️  12 warnings (will be imported)                  │
│  ✕ 3 errors (will be skipped)                       │
│                                                      │
│  Preview (first 10 rows):                            │
│  ┌───────────────┬─────────────────────┬──────┐     │
│  │ Front         │ Back                │ Stat │     │
│  ├───────────────┼─────────────────────┼──────┤     │
│  │ Abandon       │ Give up completely  │  ✓   │     │
│  │ Aberrant      │ Abnormal            │  ✓   │     │
│  │ (empty)       │ Missing front text  │  ✕   │     │
│  │ Very long...  │ Text exceeds 5000.. │  ⚠️   │     │
│  │ Duplicate     │ Already exists      │  ⚠️   │     │
│  └───────────────┴─────────────────────┴──────┘     │
│                                                      │
│  [View all errors]                                   │
│                                                      │
│  Import Options:                                     │
│  ☑️ Skip duplicates (don't import existing cards)    │
│  ☐ Replace existing cards (overwrite duplicates)    │
│                                                      │
│           [Cancel]            [Import 1,231 Cards]   │
│                                                      │
└─────────────────────────────────────────────────────┘

Status Icons:
- ✓ Valid (green)
- ⚠️  Warning (yellow)
- ✕ Error (red)

Import Progress:
- Show progress bar
- "Importing... 50% (615/1231)"
- Cancel button active
```

**Step 3: Success**
```
┌─────────────────────────────────────────────────────┐
│ Import Complete                               [✕]   │
├─────────────────────────────────────────────────────┤
│                                                      │
│              ✓ Import Successful                     │
│                                                      │
│  Successfully imported 1,231 cards                   │
│  Skipped 3 rows with errors                          │
│  Skipped 12 duplicates                               │
│                                                      │
│  📂 Academic Words now has 1,351 total cards         │
│                                                      │
│  [Download Error Report]    [View Deck]      [Close] │
│                                                      │
└─────────────────────────────────────────────────────┘
```

### 5.3 Copy Folder Dialog (Async Job)

**Initial Dialog:**
```
┌─────────────────────────────────────────────────────┐
│ Copy Folder: IELTS Preparation               [✕]   │
├─────────────────────────────────────────────────────┤
│                                                      │
│  This folder contains:                               │
│  • 3 subfolders                                      │
│  • 5 decks                                           │
│  • 250 cards                                         │
│  • Total: 258 items                                  │
│                                                      │
│  ⚠️  Large folder (>50 items)                        │
│  Copy will run in background. You'll be notified    │
│  when complete.                                      │
│                                                      │
│  Destination:                                        │
│  ┌─────────────────────────────────────────────┐    │
│  │ Select destination folder...            [▼] │    │
│  └─────────────────────────────────────────────┘    │
│  (Leave empty to copy to root)                       │
│                                                      │
│  Options:                                            │
│  ☑️ Copy all decks and cards                         │
│  ☑️ Copy folder structure                            │
│                                                      │
│           [Cancel]                  [Start Copy]     │
│                                                      │
└─────────────────────────────────────────────────────┘
```

**Job Started (Toast Notification):**
```
┌─────────────────────────────────────────┐
│ ℹ️  Copy job started                    │
│ Copying "IELTS Preparation"...          │
│ You'll be notified when complete.       │
│                                         │
│ [View Status]              [Dismiss]    │
└─────────────────────────────────────────┘

Toast: Auto-dismiss 5s, click [View Status] → Job status page
```

**Job Status Page:**
```
┌──────────┬──────────────────────────────────────────────────────────────┐
│          │ Breadcrumb: Jobs                                  [Refresh]  │
│          ├──────────────────────────────────────────────────────────────┤
│          │                                                               │
│          │ Copy Job #abc123                                             │
│          │                                                               │
│          │ Status: ⏳ In Progress (85% complete)                         │
│          │                                                               │
│          │ ████████████████░░ 85%                                       │
│          │                                                               │
│          │ Details:                                                      │
│          │ • Source: IELTS Preparation                                  │
│          │ • Destination: Root                                          │
│          │ • Started: 2 minutes ago                                     │
│          │ • Items copied: 220/258                                      │
│          │ • Estimated time remaining: ~30 seconds                      │
│          │                                                               │
│          │ [Cancel Job]                                                 │
│          │                                                               │
└──────────┴──────────────────────────────────────────────────────────────┘

Auto-refresh: Every 2 seconds until complete
```

**Job Complete (Notification):**
```
┌─────────────────────────────────────────┐
│ ✓ Copy complete!                        │
│ "IELTS Preparation" copied successfully │
│ 258 items copied in 2 min 15 sec        │
│                                         │
│ [View Folder]              [Dismiss]    │
└─────────────────────────────────────────┘
```

---

## 6. Statistics Page

```
┌──────────┬──────────────────────────────────────────────────────────────┐
│ Sidebar  │ Statistics Overview                         Period: [Week ▼] │
│          ├──────────────────────────────────────────────────────────────┤
│          │                                                               │
│ [📊Stats]│ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐            │
│          │ │ 🔥 Streak   │ │ 📚 Total    │ │ ⏱️  Study   │            │
│ [⚙️ Set] │ │   7 days    │ │ 550 cards   │ │  5.2 hours  │            │
│          │ └─────────────┘ └─────────────┘ └─────────────┘            │
│          │                                                               │
│          │ Today's Activity                                              │
│          │ ┌───────────────────────────────────────────────────────┐   │
│          │ │ ✓ 45 cards reviewed                                   │   │
│          │ │ 🆕 20 new cards learned                               │   │
│          │ │ ⏱️  25 minutes spent                                  │   │
│          │ │ 🎯 85% average accuracy                               │   │
│          │ └───────────────────────────────────────────────────────┘   │
│          │                                                               │
│          │ Box Distribution (All Decks)                                 │
│          │ ┌───────────────────────────────────────────────────────┐   │
│          │ │                                                       │   │
│          │ │ 150│                ┌──┐                             │   │
│          │ │    │                │  │                             │   │
│          │ │ 120│           ┌──┐ │  │                             │   │
│          │ │    │      ┌──┐ │  │ │  │                             │   │
│          │ │  90│      │  │ │  │ │  │ ┌──┐                       │   │
│          │ │    │      │  │ │  │ │  │ │  │                       │   │
│          │ │  60│ ┌──┐ │  │ │  │ │  │ │  │ ┌──┐                 │   │
│          │ │    │ │  │ │  │ │  │ │  │ │  │ │  │ ┌──┐           │   │
│          │ │  30│ │  │ │  │ │  │ │  │ │  │ │  │ │  │           │   │
│          │ │    ├─┴──┴─┴──┴─┴──┴─┴──┴─┴──┴─┴──┴─┴──┴───────────│   │
│          │ │     1    2    3    4    5    6    7      Box        │   │
│          │ │                                                       │   │
│          │ │ Cards: 120  150  100  80   50   30   20             │   │
│          │ └───────────────────────────────────────────────────────┘   │
│          │                                                               │
│          │ Weekly Activity                                               │
│          │ ┌───────────────────────────────────────────────────────┐   │
│          │ │     Reviews                                           │   │
│          │ │ 100│     ●                                            │   │
│          │ │    │       ●                                          │   │
│          │ │  75│         ●   ●                                    │   │
│          │ │    │             ●                                    │   │
│          │ │  50│               ● ●                               │   │
│          │ │    │                   ●                              │   │
│          │ │  25├───────────────────────────                      │   │
│          │ │     Mon Tue Wed Thu Fri Sat Sun                      │   │
│          │ └───────────────────────────────────────────────────────┘   │
│          │                                                               │
└──────────┴──────────────────────────────────────────────────────────────┘

Charts:
- Box distribution: Bar chart (Recharts or Chart.js)
- Weekly activity: Line chart
- Interactive tooltips on hover
- Responsive sizing
```

---

## 7. Settings Page

```
┌──────────┬──────────────────────────────────────────────────────────────┐
│ Sidebar  │ Settings                                                      │
│          ├──────────────────────────────────────────────────────────────┤
│          │                                                               │
│ [⚙️ Set] │ ┌─────────────────────────────────────────────────────────┐ │
│          │ │ Profile                                                 │ │
│          │ │                                                         │ │
│          │ │ Name                                                    │ │
│          │ │ ┌─────────────────────────────────────────────────────┐ │ │
│          │ │ │ John Doe                                            │ │ │
│          │ │ └─────────────────────────────────────────────────────┘ │ │
│          │ │                                                         │ │
│          │ │ Email                                                   │ │
│          │ │ ┌─────────────────────────────────────────────────────┐ │ │
│          │ │ │ john@example.com                                    │ │ │
│          │ │ └─────────────────────────────────────────────────────┘ │ │
│          │ │                                                         │ │
│          │ │ Timezone                                                │ │
│          │ │ ┌─────────────────────────────────────────────────────┐ │ │
│          │ │ │ (UTC+07:00) Asia/Ho_Chi_Minh                   [▼] │ │ │
│          │ │ └─────────────────────────────────────────────────────┘ │ │
│          │ │                                                         │ │
│          │ │                      [Save Changes]                     │ │
│          │ └─────────────────────────────────────────────────────────┘ │
│          │                                                               │
│          │ ┌─────────────────────────────────────────────────────────┐ │
│          │ │ SRS Settings                                            │ │
│          │ │                                                         │ │
│          │ │ Review Order                                            │ │
│          │ │ ○ Ascending (Box 1 → 7)                                │ │
│          │ │ ○ Descending (Box 7 → 1)                               │ │
│          │ │ ● Random (Shuffle)                                     │ │
│          │ │                                                         │ │
│          │ │ When you forget a card (Again rating):                  │ │
│          │ │ ● Move to Box 1 (Restart from beginning)               │ │
│          │ │ ○ Move down N boxes:  [2 ▼]                            │ │
│          │ │ ○ Stay in current box                                  │ │
│          │ │                                                         │ │
│          │ │ Daily Limits                                            │ │
│          │ │ New cards per day:  [━━━━━━░░░░] 20                    │ │
│          │ │ Max reviews/day:    [━━━━━━━━━━] 200                   │ │
│          │ │                                                         │ │
│          │ │                      [Save Changes]                     │ │
│          │ └─────────────────────────────────────────────────────────┘ │
│          │                                                               │
│          │ ┌─────────────────────────────────────────────────────────┐ │
│          │ │ Notifications                                           │ │
│          │ │                                                         │ │
│          │ │ ☑️ Daily reminder                                        │ │
│          │ │ Time:  [09:00 ▼]  (your timezone)                      │ │
│          │ │                                                         │ │
│          │ │ ☑️ Review completed notifications                        │ │
│          │ │                                                         │ │
│          │ │                      [Save Changes]                     │ │
│          │ └─────────────────────────────────────────────────────────┘ │
│          │                                                               │
│          │ ┌─────────────────────────────────────────────────────────┐ │
│          │ │ Appearance                                              │ │
│          │ │                                                         │ │
│          │ │ Theme                                                   │ │
│          │ │ ○ Light  ● Dark  ○ System default                      │ │
│          │ │                                                         │ │
│          │ │ Language                                                │ │
│          │ │ [Vietnamese (Tiếng Việt)                           ▼] │ │
│          │ │                                                         │ │
│          │ └─────────────────────────────────────────────────────────┘ │
│          │                                                               │
│          │ ┌─────────────────────────────────────────────────────────┐ │
│          │ │ Account                                                 │ │
│          │ │                                                         │ │
│          │ │ [Change Password]                                       │ │
│          │ │                                                         │ │
│          │ │ [Logout]                                                │ │
│          │ │                                                         │ │
│          │ └─────────────────────────────────────────────────────────┘ │
│          │                                                               │
└──────────┴──────────────────────────────────────────────────────────────┘

Form Validation:
- Real-time validation on blur
- Success indicators (green checkmark)
- Error messages below fields
- Disabled submit until valid
```

---

## 8. Component States

### 8.1 Loading States

**Skeleton Loaders (Folders Page):**
```
┌─────────┐ ┌─────────┐ ┌─────────┐
│ ░░░░░   │ │ ░░░░░   │ │ ░░░░░   │
│ ░░░░    │ │ ░░░░    │ │ ░░░░    │
│         │ │         │ │         │
│ ░░░░░░  │ │ ░░░░░░  │ │ ░░░░░░  │
│ ░░░░░░  │ │ ░░░░░░  │ │ ░░░░░░  │
└─────────┘ └─────────┘ └─────────┘

Shimmer animation: Gradient moves left→right
```

**Spinner (Review Session):**
```
      ⏳ Loading next card...
```

### 8.2 Empty States

**No Folders:**
```
┌─────────────────────────────────────────┐
│                                         │
│            📂                           │
│      No folders yet                     │
│                                         │
│  Create your first folder to organize   │
│  your flashcards                        │
│                                         │
│      [+ Create First Folder]            │
│                                         │
└─────────────────────────────────────────┘
```

**No Due Cards:**
```
┌─────────────────────────────────────────┐
│                                         │
│            ✓                            │
│   You're all caught up!                 │
│                                         │
│  No cards due for review today          │
│  Come back tomorrow or try Cram mode    │
│                                         │
│      [Browse Decks]  [Cram Mode]        │
│                                         │
└─────────────────────────────────────────┘
```

### 8.3 Error States

**Network Error:**
```
┌─────────────────────────────────────────┐
│                                         │
│            ⚠️                            │
│   Connection Error                      │
│                                         │
│  Unable to connect to server            │
│  Please check your internet connection  │
│                                         │
│      [Retry]         [Go Offline]       │
│                                         │
└─────────────────────────────────────────┘
```

**404 Not Found:**
```
┌─────────────────────────────────────────┐
│                                         │
│            🔍                            │
│   Folder Not Found                      │
│                                         │
│  This folder may have been deleted      │
│  or you don't have permission to view   │
│                                         │
│      [← Go Back]   [Go to Home]         │
│                                         │
└─────────────────────────────────────────┘
```

---

## 9. Interactions & Animations

### 9.1 Hover States
- **Cards**: Shadow elevation, scale 1.02, border highlight
- **Buttons**: Background darken 10%, cursor pointer
- **Links**: Underline, color darken
- **Icons**: Scale 1.1, color change

### 9.2 Click/Tap Effects
- **Buttons**: Scale down 0.98, ripple effect
- **Cards**: Press effect, quick feedback
- **Checkboxes**: Checkmark animation

### 9.3 Transitions
- **Page transitions**: Fade in 200ms
- **Modal open**: Scale up + fade (150ms ease-out)
- **Modal close**: Scale down + fade (150ms ease-in)
- **Toast notifications**: Slide in from top-right
- **Sidebar toggle**: Slide 300ms ease-in-out

### 9.4 Flashcard Flip Animation
```
Duration: 300ms
Easing: ease-in-out

Front → Back:
1. RotateY 0° → 90° (150ms)
2. Swap content
3. RotateY 90° → 180° (150ms)

3D effect:
- Perspective: 1000px
- BackfaceVisibility: hidden
- TransformStyle: preserve-3d
```

### 9.5 Copy Job Progress
```
Update interval: 2s polling

Progress bar animation:
- Width transition 500ms ease-out
- Color: blue → green when complete
- Pulse animation on active
```

---

## 10. Responsive Behavior

### Desktop (>1024px)
- Sidebar always visible (260px fixed)
- Content max-width: 1200px centered
- Grid: 4 columns for folder cards
- Modals: 600px width centered

### Tablet (768px - 1024px)
- Sidebar: Collapsible overlay
- Content: Full width with padding
- Grid: 2-3 columns adaptive
- Modals: 80% width

### Mobile (<768px)
- Bottom navigation
- Sidebar: Slide-over drawer
- Content: Full width, 16px padding
- Grid: 1-2 columns
- Modals: Fullscreen on small devices

---

## 11. Accessibility

### Keyboard Navigation
- Tab order logical
- Focus indicators visible
- Skip to content link
- Escape closes modals
- Arrow keys in card list
- Hotkeys: [1-4] for rating, [Space] reveal, [←] undo, [→] skip

### Screen Readers
- ARIA labels on all interactive elements
- ARIA live regions for dynamic content
- Semantic HTML (nav, main, aside, article)
- Alt text for icons
- Form field labels properly associated

### Color Contrast
- WCAG AA compliance (4.5:1 text)
- Focus indicators 3:1 contrast
- Non-color indicators (icons + text)

---

## 12. Dark Mode

### Color Scheme
```
Light Mode:
- Background: #ffffff
- Surface: #f5f5f5
- Text: #1a1a1a
- Border: #e0e0e0

Dark Mode:
- Background: #1a1a1a
- Surface: #2d2d2d
- Text: #ffffff
- Border: #404040

Transition: 200ms ease
Storage: localStorage 'theme'
System preference detection: prefers-color-scheme
```

### Toggle Implementation
```
Header: Sun/Moon icon toggle
Settings: Radio buttons (Light / Dark / System)
Apply: Add/remove 'dark' class on <html>
```

---

**Version**: 1.0
**Last Updated**: 2025-01-12
**Status**: Complete - Ready for Implementation
**Total Wireframes**: 25+ screens with all states
