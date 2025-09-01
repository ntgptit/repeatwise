# Wireframes - RepeatWise

This document provides low-fidelity wireframes for the RepeatWise mobile application. The goal is to map business requirements and detailed use cases into tangible screens. Each wireframe references relevant use cases (UC) and sequence diagrams (SD) from the system analysis.

---

## 1. Authentication Flow

### 1.1 Login Screen
- **Use Cases:** UC-002 User Login, UC-004 Password Reset
- **Sequence Diagrams:** `architecture/sequence-diagrams/user-authentication-sequences.md`
- **Layout:**
```text
+-----------------------------------+
|          RepeatWise Logo          |
|-----------------------------------|
| Email Input                       |
| Password Input                    |
| [ Login ]                         |
|                                   |
| Forgot Password?  Register        |
+-----------------------------------+
```
- **Components & Interactions:**
  - Tapping **Login** sends credentials to the authentication API and shows a loading spinner.
  - **Forgot Password?** opens the reset form; **Register** opens account creation.
- **Validation & Errors:**
  - Email and password are mandatory. Invalid credentials trigger an inline error message.
  - Network issues display a retry toast.
- **Navigation:**
  - Successful login → Home Dashboard.
  - Links for **Forgot Password?** and **Register** route to their respective screens.
- **Business Rules Reference:**
  - User must be authenticated before accessing any set data (Business Spec §2).

### 1.2 Registration Screen
- **Use Cases:** UC-001 User Registration
- **Sequence Diagrams:** `architecture/sequence-diagrams/user-authentication-sequences.md`
- **Layout:**
```text
+-----------------------------------+
|          Create Account           |
|-----------------------------------|
| Name Input                        |
| Email Input                       |
| Password Input                    |
| Confirm Password                  |
| Timezone Dropdown                 |
| Default Reminder Time Picker      |
| [ Register ]                      |
| Back to Login                     |
+-----------------------------------+
```
- **Components & Interactions:**
  - Selecting a timezone and default reminder time seeds the reminder schedule.
  - **Register** creates the account via API and persists preferences.
- **Validation & Errors:**
  - All fields are required; password must be at least 8 characters.
  - Duplicate email addresses prompt an error with link to login.
- **Navigation:**
  - Successful registration → Home Dashboard with welcome message.
  - **Back to Login** returns to the login screen.
- **Business Rules Reference:**
  - Reminder defaults comply with daily limit rules (Business Spec §7).

### 1.3 Password Reset Screen
- **Use Cases:** UC-004 Password Reset
- **Sequence Diagrams:** `architecture/sequence-diagrams/user-authentication-sequences.md`
- **Layout:**
```text
+-----------------------------------+
|        Reset Your Password        |
|-----------------------------------|
| Email Input                       |
| [ Send Reset Link ]               |
| Back to Login                     |
+-----------------------------------+
```
- **Components & Interactions:**
  - User enters email and taps **Send Reset Link** to request a password email.
- **Validation & Errors:**
  - Email is required; unknown addresses show a generic success message for security.
- **Navigation:**
  - After submitting, app displays confirmation and link back to login.
- **Business Rules Reference:**
  - Reset flow must not expose whether an email exists (Security guideline).

---

## 2. Home Dashboard
- **Use Cases:** UC-015 Receive Reminder, UC-019 View Learning Statistics
- **Sequence Diagrams:** `architecture/sequence-diagrams/learning-cycle-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| Greeting & Quick Stats                    |
|-------------------------------------------|
| Today's Reviews (horizontal cards)        |
| ---------------------------------------- |
| | Set A | | Set B | | Set C |            |
| ---------------------------------------- |
|                                           |
| Recent Sets (vertical list)               |
| - Set A                                  |
| - Set B                                  |
| - Set C                                  |
|                                           |
| Quick Actions: [Create Set] [View Stats]  |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Horizontal cards show up to three due sets based on reminder scheduling.
  - Vertical list displays recent sets with progress indicators.
  - Quick action buttons open set creation or statistics overview.
- **Business Rules Reference:**
  - Daily reminders limited to three sets to avoid overload (Business Spec §6).
  - Statistics summarize overall learning performance (Business Spec §4).
- **Navigation:**
  - Tapping a set card → Review Session or Set Details depending on status.
  - **Create Set** → Set Creation form; **View Stats** → Statistics Dashboard.

---

## 3. Set Management

### 3.1 Set List Screen
- **Use Cases:** UC-008 View Set List, UC-005 Create New Set
- **Sequence Diagrams:** `architecture/sequence-diagrams/set-management-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| Sets                                      |
|-------------------------------------------|
| [ Search Bar ]                            |
|-------------------------------------------|
| • Set A  [progress bar] [Edit]            |
| • Set B  [progress bar] [Edit]            |
| • Set C  [progress bar] [Edit]            |
|-------------------------------------------|
| [ + Create New Set ] (floating button)    |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Search bar filters sets by name or category.
  - Each list item shows progress status and shortcut to edit.
  - Floating button creates a new set.
- **Business Rules Reference:**
  - Users can manage unlimited sets (Business Spec §2).
- **Navigation:**
  - Selecting a set opens Set Details.
  - Floating button routes to Create/Edit Set screen.

### 3.2 Set Details Screen
- **Use Cases:** UC-009 View Set Details, UC-010 Start Learning Cycle
- **Sequence Diagrams:** `architecture/sequence-diagrams/learning-set-management-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| Set Title                                 |
|-------------------------------------------|
| Description & Metadata                    |
| Progress Bar                              |
| Current Cycle: X/5                        |
| Upcoming Review: <time>                   |
|-------------------------------------------|
| Learning History (list)                   |
| Performance Analytics                     |
|-------------------------------------------|
| [ Start Review ] [ Edit Set ] [ Stats ]   |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Progress bar reflects current cycle (1–5) and percentage complete.
  - Analytics section links to detailed charts.
- **Business Rules Reference:**
  - Upcoming review dates calculated using SRS algorithm (Business Spec §3 & §5).
- **Navigation:**
  - **Start Review** launches review session.
  - **Edit Set** opens set editor.
  - **Stats** shows set-specific analytics.

### 3.3 Create/Edit Set Screen
- **Use Cases:** UC-005 Create New Set, UC-006 Edit Set Information
- **Sequence Diagrams:** `architecture/sequence-diagrams/set-management-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| Set Information                            |
|-------------------------------------------|
| Name Input                                 |
| Description Textarea                       |
| Category Dropdown                          |
| Vocabulary Count Input                     |
| Difficulty Selector                        |
|-------------------------------------------|
| [ Save ]    [ Cancel ]                     |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Category dropdown provides predefined options (`vocabulary`, `grammar`, etc.).
  - Difficulty selector sets initial SRS parameters.
- **Validation & Errors:**
  - Name and word count are mandatory; word count must be > 0.
- **Business Rules Reference:**
  - Set metadata aligns with specification for set entity (Business Spec §2).
- **Navigation:**
  - **Save** persists changes and returns to Set List.
  - **Cancel** discards edits and returns to previous screen.

---

## 4. Review Session Flow

### 4.1 Review Screen
- **Use Cases:** UC-011 Perform Review Session, UC-013 Skip Review Session
- **Sequence Diagrams:** `architecture/sequence-diagrams/learning-cycle-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| Set Title & Cycle Indicator               |
|-------------------------------------------|
| Content Display Area                      |
| (flashcard text / notes)                  |
|-------------------------------------------|
| [ Start Review ]  [ Skip ]                |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Content area flips between prompt and answer when tapped.
  - **Start Review** begins timing; **Skip** triggers skip reason dialog.
- **Business Rules Reference:**
  - Each review contributes to a 5-step cycle (Business Spec §3).
- **Navigation:**
  - **Start Review** → Score Input Screen after response.
  - **Skip** → Score Input Screen with status `skipped`.

### 4.2 Score Input Screen
- **Use Cases:** UC-012 Input Score
- **Sequence Diagrams:** `architecture/sequence-diagrams/learning-cycle-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| Score Your Recall                         |
|-------------------------------------------|
| Slider 0% -------------------------------- 100% |
| Quick Buttons: [0%] [25%] [50%] [75%] [100%] |
| Notes Textarea (optional)                 |
| Difficulty Rating (1-5 stars)             |
|-------------------------------------------|
| [ Submit ] [ Back ]                       |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Slider captures score between 0–100%; quick buttons set common values.
  - Optional note and difficulty rating enrich analytics.
- **Validation & Errors:**
  - Score is mandatory; user cannot proceed without a value.
- **Business Rules Reference:**
  - Scores drive scheduling of next reviews (Business Spec §4 & §5).
- **Navigation:**
  - **Submit** → Review Complete Screen; **Back** returns to previous item.

### 4.3 Review Complete Screen
- **Use Cases:** UC-014 Complete Cycle
- **Sequence Diagrams:** `architecture/sequence-diagrams/learning-cycle-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| 🎉 Review Complete!                       |
|-------------------------------------------|
| Score: XX%                                |
| Next Review: <date/time>                  |
|-------------------------------------------|
| [ Back to Dashboard ] [ View Set ]        |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Displays score and next scheduled review date.
- **Business Rules Reference:**
  - Next review date calculated using algorithm in Business Spec §5.
- **Navigation:**
  - **Back to Dashboard** returns to home; **View Set** opens Set Details.

---

## 5. Statistics & Analytics

### 5.1 Overview Dashboard
- **Use Cases:** UC-019 View Learning Statistics
- **Sequence Diagrams:** `architecture/sequence-diagrams/statistics-analytics-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| Learning Overview                         |
|-------------------------------------------|
| Total Sets   Mastered Sets   Active Sets  |
|-------------------------------------------|
| Progress Chart (line)                     |
| Recent Activity List                      |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Charts are interactive, allowing filter by date range.
  - Recent activity items link to corresponding sets or reviews.
- **Business Rules Reference:**
  - Statistics aggregate scores and cycles across all sets (Business Spec §4).
- **Navigation:**
  - Tapping chart points opens detailed set analytics.

### 5.2 Set Analytics Screen
- **Use Cases:** UC-020 View Set Progress, UC-021 View Performance Trends
- **Sequence Diagrams:** `architecture/sequence-diagrams/statistics-analytics-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| Set Name Analytics                        |
|-------------------------------------------|
| Cycle Progress Chart                      |
| Score Trend Graph                         |
| Review History Table                      |
|-------------------------------------------|
| [ Export Data ]                           |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Review history table sortable by date, score, or status.
- **Business Rules Reference:**
  - Export follows data portability guidelines; analytics leverage review history (Business Spec §4).
- **Navigation:**
  - **Export Data** generates CSV/PDF for sharing.

---

## 6. Settings & Profile

### 6.1 Settings Screen
- **Use Cases:** UC-003 User Profile Management, UC-016 Reschedule Reminder
- **Sequence Diagrams:** `architecture/sequence-diagrams/reminder-management-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| Settings                                  |
|-------------------------------------------|
| Profile Settings                          |
| Notification Preferences                  |
| Learning Preferences                      |
| System Settings                           |
|-------------------------------------------|
| [ Save Changes ]                          |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Profile section updates name, timezone, and reminder time.
  - Notification preferences toggle email/push alerts.
  - Learning preferences adjust default difficulty and daily limit.
- **Business Rules Reference:**
  - Reminder rescheduling obeys daily set cap (Business Spec §6).
- **Navigation:**
  - **Save Changes** persists settings; leaving screen without saving prompts confirmation.

### 6.2 Profile Screen
- **Use Cases:** UC-003 User Profile Management
- **Sequence Diagrams:** `architecture/sequence-diagrams/user-authentication-sequences.md`
- **Layout:**
```text
+-------------------------------------------+
| User Avatar & Name                        |
|-------------------------------------------|
| Email                                     |
| Learning Streak                           |
| Achievement Badges                        |
|-------------------------------------------|
| [ Edit Profile ] [ Logout ]               |
+-------------------------------------------+
```
- **Components & Interactions:**
  - Avatar editable via photo picker; badges tap to show criteria.
- **Business Rules Reference:**
  - Logout revokes authentication token (Business Spec §2).
- **Navigation:**
  - **Edit Profile** opens settings; **Logout** returns to login screen.

---

These wireframes serve as a baseline for UI/UX implementation. Detailed interactions and edge cases should be validated against the corresponding use cases and sequence diagrams.
