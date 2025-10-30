# Acceptance Criteria - RepeatWise MVP

## Introduction

This document lists acceptance criteria (Given/When/Then) for key user stories in the RepeatWise MVP. Scenarios are written in Gherkin style.

---

## AC-1: User Management

### AC-1.1: Registration (US-1.1)

Scenario: Successful registration

```gherkin
Given I am on the registration page
And the email "user@example.com" does not exist
When I enter email "user@example.com"
And I optionally enter username "john_doe123"
And I enter password "Password123"
And I enter confirm password "Password123"
And I click "Register"
Then my account is created
And I see "Registration successful. Please login."
And I am redirected to the Login page
```

Scenario: Successful registration without username

```gherkin
Given I am on the registration page
And the email "user@example.com" does not exist
When I enter email "user@example.com"
And I leave username empty
And I enter password "Password123"
And I enter confirm password "Password123"
And I click "Register"
Then my account is created without username
And I see "Registration successful. Please login."
And I am redirected to the Login page
```

Scenario: Username already exists

```gherkin
Given I am on the registration page
And the username "john_doe123" already exists
When I enter username "john_doe123"
And I submit the form
Then I see error "Username already exists"
And my account is not created
```

Scenario: Invalid username format

```gherkin
Given I am on the registration page
When I enter username "ab" (too short)
Or I enter username "user@name" (contains @)
Or I enter username "user name" (contains space)
Then I see "Username must be 3-30 characters, alphanumeric + underscore/hyphen only"
And the "Register" button is disabled
```

Scenario: Email already exists

```gherkin
Given I am on the registration page
And the email "existing@example.com" already exists
When I submit the form
Then I see error "Email already exists"
And my account is not created
```

Scenario: Weak password

```gherkin
Given I am on the registration page
When I enter password "short"
Then I see "Password must be at least 8 characters"
And the "Register" button is disabled
```

Scenario: Invalid email format

```gherkin
Given I am on the registration page
When I enter email "invalid-email"
Then I see "Invalid email format"
And the "Register" button is disabled
```

### AC-1.2: Login (US-1.2)

Scenario: Successful login with email

```gherkin
Given a user exists with email "user@example.com" and password "Password123"
And I am on the login page
When I enter "user@example.com" and "Password123"
And I click "Login"
Then I am logged in
And the refresh token is set in an HTTP-only cookie
And I am redirected to the Dashboard
```

Scenario: Successful login with username

```gherkin
Given a user exists with username "john_doe123" and password "Password123"
And I am on the login page
When I enter "john_doe123" and "Password123"
And I click "Login"
Then I am logged in
And the refresh token is set in an HTTP-only cookie
And I am redirected to the Dashboard
```

Scenario: Wrong password

```gherkin
Given a user exists with username "john_doe123" or email "user@example.com"
When I enter password "WrongPassword"
Then I see "Invalid username/email or password"
```

Scenario: Username/Email not found

```gherkin
Given the username "nonexistent" or email "nonexistent@example.com" does not exist
When I try to login
Then I see "Invalid username/email or password"
```

Scenario: Case sensitive username

```gherkin
Given a user exists with username "JohnDoe" and password "Password123"
When I enter "johndoe" and "Password123"
Then I see "Invalid username/email or password"
```

### AC-1.3: Auto refresh token (US-1.3)

Scenario: Refresh when access token expired

```gherkin
Given I am logged in
And my access token has expired
And my refresh token is still valid
When I call any authenticated API
Then the client refreshes the token
And a new access token is used to retry the API
And I am not interrupted
```

Scenario: Refresh token expired

```gherkin
Given my access token has expired
And my refresh token has expired
When I call an authenticated API
Then the refresh request returns 401
And I am logged out
```

### AC-1.4: Logout (US-1.4)

Scenario: Logout current device

```gherkin
Given I am logged in
When I click "Logout"
Then my session is terminated on this device
And the refresh token cookie is cleared
```

### AC-1.5: Logout all devices (US-1.5)

Scenario: Global logout

```gherkin
Given I am logged in on multiple devices
When I click "Logout all devices"
Then all refresh tokens for my account are revoked
And all devices require re-login
```

### AC-1.6: Update profile (US-1.6)

Scenario: Update name/timezone/language/theme

```gherkin
Given I open Settings > Profile
When I update valid values for name, timezone, language, and theme
And I click "Save"
Then the changes are persisted
And I see a success message
```

### AC-1.7: Change password (US-1.7)

Scenario: Successful password change

```gherkin
Given I am logged in
When I enter my current password and a new valid password
Then my password is updated
And all refresh tokens are revoked
And I am redirected to Login
```

---

## AC-2: Folder Management

### AC-2.1: Create folder (US-2.1)

Scenario: Create at depth <= 10

```gherkin
Given I am viewing a folder at depth 9
When I create a child folder
Then the folder is created at depth 10
```

Scenario: Exceed max depth

```gherkin
Given the parent folder is at depth 10
When I try to create a child folder
Then I see "Maximum folder depth (10 levels) exceeded"
```

### AC-2.2: Rename folder (US-2.2)

Scenario: Duplicate name in same parent

```gherkin
Given two sibling folders exist
When I rename a folder to an existing sibling name
Then I see "Folder name already exists in this location"
```

### AC-2.3: Delete folder (US-2.3)

Scenario: Soft delete with confirmation

```gherkin
Given a folder contains sub-folders and decks
When I click "Delete" and confirm
Then the folder subtree is soft-deleted
And it is removed from active views
```

### AC-2.4: Move folder (US-2.4)

Scenario: Prevent cycles and depth overflow

```gherkin
Given a folder A with descendant B
When I try to move A into B
Then I see an error preventing the move
```

### AC-2.5: Copy folder (US-2.5)

Scenario: Sync copy (<= 50 items)

```gherkin
Given a folder with 20 items total
When I copy it
Then a new subtree is created synchronously
```

Scenario: Async copy (51–500 items)

```gherkin
Given a folder with 200 items total
When I copy it
Then I receive a job id and progress updates
And a success notification on completion
```

### AC-2.6: View folder statistics (US-2.6)

Scenario: Recursive counts

```gherkin
Given a folder with sub-folders and decks
When I open the statistics panel
Then I see total decks/cards and due cards (recursive)
```

---

## AC-3: Deck Management

### AC-3.1: Create/Update/Delete deck (US-3.1..3.3)

Scenario: Unique name within folder

```gherkin
Given a folder contains a deck named "Vocabulary"
When I create another deck with the same name in that folder
Then I see "Deck name already exists in this location"
```

### AC-3.2: Move deck (US-3.4)

Scenario: Move to another folder

```gherkin
Given a deck in Folder A
When I move it to Folder B
Then it appears under Folder B
```

### AC-3.3: Copy deck (US-3.5)

Scenario: Sync copy (<= 1,000 cards)

```gherkin
Given a deck with 500 cards
When I copy it
Then a new deck is created synchronously
```

Scenario: Async copy (1,001–10,000 cards)

```gherkin
Given a deck with 5,000 cards
When I copy it
Then I receive a job id and progress updates
```

---

## AC-4: Card Management

### AC-4.1: Create/Update/Delete card (US-4.1..4.3)

Scenario: Front/Back validation

```gherkin
Given I am creating a card
When Front or Back is empty or over 5,000 characters
Then I see a validation error
```

### AC-4.2: Import cards (US-4.4)

Scenario: Valid CSV import

```gherkin
Given a CSV file with 1,000 rows and columns Front, Back
When I import the file
Then 1,000 cards are created
And I see a summary of successes and failures
```

Scenario: Invalid format

```gherkin
Given a file without the required columns
When I import the file
Then I see an error describing the missing columns
```

### AC-4.3: Export cards (US-4.5)

Scenario: Export due cards only

```gherkin
Given a deck with due and non-due cards
When I export with scope = DUE_ONLY
Then the file contains only due cards
```

---

## AC-5: SRS Review

### AC-5.1: Start review (US-5.1)

Scenario: No due cards

```gherkin
Given there are no due cards in the selected scope
When I start a review session
Then I see "No cards to review today"
```

### AC-5.2: Rate card (US-5.2)

Scenario: Update SRS state

```gherkin
Given I am reviewing a card
When I rate it GOOD
Then the card moves up one box (capped at max)
And its due date is updated
```

### AC-5.3: Undo last review (US-5.3)

Scenario: Undo within allowed window

```gherkin
Given I rated the last card by mistake
When I click "Undo" within the allowed time
Then the card’s SRS state is restored
And the session queue is rolled back accordingly
```

### AC-5.4: Skip card (US-5.4)

Scenario: Skip without SRS change

```gherkin
Given I want to postpone the current card
When I click "Skip"
Then the card moves to the end of the queue
And its SRS fields do not change
```

### AC-5.5: Edit during review (US-5.5)

Scenario: Save and continue

```gherkin
Given I open the edit panel during review
When I update Front/Back with valid text
Then the card is saved
And the review resumes with the updated content
```

### AC-5.6: Configure SRS (US-5.6)

Scenario: Validate ranges

```gherkin
Given I open SRS settings
When I set total boxes outside 3..10
Then I see a validation error
```

---

## AC-6: Study Modes

### AC-6.1: Cram mode (US-6.1/6.2)

Scenario: Ignore due dates

```gherkin
Given I start a cram session for a deck
When the queue is built
Then it includes cards regardless of due dates
```

### AC-6.2: Random mode (US-6.3)

Scenario: Random ordering of due cards

```gherkin
Given there are due cards in scope
When I start Random Mode
Then the first card is selected randomly
```

---

## AC-7: Statistics

### AC-7.1: Streak counter (US-7.1)

```gherkin
Given I studied today and yesterday
When I open Statistics
Then the streak counter increments appropriately
```

### AC-7.2: Reviews today (US-7.2)

```gherkin
Given I reviewed 120 cards today
When I open Statistics
Then I see "Reviews today: 120"
```

### AC-7.3: Box distribution (US-7.3)

```gherkin
Given counts exist for boxes 1..7
When I open Box Distribution
Then I see a chart with counts per box
```

---

## AC-8: UI/UX Enhancements

### AC-8.1: Theme switching (US-8.1)

```gherkin
Given I am using Light mode
When I switch to Dark and save
Then the UI switches smoothly and persists the preference
```

### AC-8.2: Language switching (US-8.2)

```gherkin
Given the UI is in English
When I switch language to Vietnamese and save
Then the UI text appears in Vietnamese
And the preference persists
```

### AC-8.3: Responsive design (US-8.3)

```gherkin
Given I access the app on a smartphone
Then the layout adapts for touch and small screens
```

---

## Summary

These acceptance criteria support testing (manual/automated), Definition of Done, and product acceptance for the MVP.
