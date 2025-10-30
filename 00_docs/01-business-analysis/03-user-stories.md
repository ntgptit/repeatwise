# User Stories - RepeatWise MVP

## Introduction

This document lists user stories for the RepeatWise MVP, organized by epic. Each story uses the standard format:

- As a [role]
- I want [goal]
- So that [reason]

Each story includes Priority (MoSCoW) and Story Points (relative effort).

---

## Epic 1: User Management

### US-1.1: Register an account

As a new user, I want to sign up with email, optional username, and password, so that I can use the app to study.
Priority: Must Have — Story Points: 3

### US-1.2: Log in

As a registered user, I want to log in with my username or email and password, so that I can access my study data.
Priority: Must Have — Story Points: 3

### US-1.3: Auto refresh token

As a logged-in user, I want my session to refresh automatically when the access token expires, so that my study flow is not interrupted.
Priority: Must Have — Story Points: 5

### US-1.4: Log out (current device)

As a logged-in user, I want to log out on the current device, so that I can secure my account.
Priority: Must Have — Story Points: 2

### US-1.5: Log out from all devices

As a user, I want to log out from all devices, so that I can secure my account if I suspect compromise.
Priority: Should Have — Story Points: 3

### US-1.6: Update profile

As a user, I want to update my name, username (if not set during registration), timezone, language, and theme, so that the app matches my preferences and locale.
Priority: Must Have — Story Points: 3

### US-1.7: Change password

As a user, I want to change my password, so that I can improve my account security.
Priority: Should Have — Story Points: 2

---

## Epic 2: Folder Management

### US-2.1: Create a new folder

As a user, I want to create a folder at any level (up to depth 10), so that I can organize study materials hierarchically.
Priority: Must Have — Story Points: 5

### US-2.2: Rename a folder

As a user, I want to rename a folder, so that the name matches its contents.
Priority: Must Have — Story Points: 2

### US-2.3: Delete a folder

As a user, I want to delete a folder (soft delete) with confirmation, so that I can clean up unused folders and recover if deleted by mistake.
Priority: Must Have — Story Points: 3

### US-2.4: Move a folder

As a user, I want to move a folder within the tree, so that I can reorganize my hierarchy.
Priority: Must Have — Story Points: 5

### US-2.5: Copy a folder (sync/async)

As a user, I want to copy a folder (including sub-folders and decks), so that I can duplicate structures efficiently.
Priority: Should Have — Story Points: 8

### US-2.6: View folder statistics (recursive)

As a user, I want to view total decks/cards and due cards for a folder (including sub-folders), so that I see overall progress.
Priority: Must Have — Story Points: 3

---

## Epic 3: Deck Management

### US-3.1: Create a deck

As a user, I want to create a deck in a folder or at root, so that I can group related cards.
Priority: Must Have — Story Points: 2

### US-3.2: Update a deck

As a user, I want to rename a deck or change its description, so that it stays accurate.
Priority: Must Have — Story Points: 2

### US-3.3: Delete a deck

As a user, I want to delete a deck (soft delete) with confirmation, so that I can remove unused material.
Priority: Must Have — Story Points: 2

### US-3.4: Move a deck

As a user, I want to move a deck between folders or to root, so that I can reorganize content.
Priority: Must Have — Story Points: 2

### US-3.5: Copy a deck (sync/async)

As a user, I want to copy a deck (including cards), so that I can reuse content across folders.
Priority: Should Have — Story Points: 5

---

## Epic 4: Card Management

### US-4.1: Create a card

As a user, I want to create a card with Front and Back text, so that I can add study material quickly.
Priority: Must Have — Story Points: 2

### US-4.2: Update a card

As a user, I want to edit a card’s Front/Back, so that I can refine content.
Priority: Must Have — Story Points: 2

### US-4.3: Delete a card

As a user, I want to delete a card (soft delete), so that I can remove irrelevant items.
Priority: Must Have — Story Points: 2

### US-4.4: Import cards

As a user, I want to import cards from CSV/XLSX with validation, so that I can bulk-create content.
Priority: Must Have — Story Points: 8

### US-4.5: Export cards

As a user, I want to export cards to CSV/XLSX, so that I can back up or share content.
Priority: Must Have — Story Points: 5

---

## Epic 5: SRS Review

### US-5.1: Start a review session

As a user, I want to start a review session for due cards, so that I study at the right time.
Priority: Must Have — Story Points: 5

### US-5.2: Rate a card

As a user, I want to rate a card AGAIN/HARD/GOOD/EASY, so that the system schedules the next review appropriately.
Priority: Must Have — Story Points: 5

### US-5.3: Undo last review

As a user, I want to undo the last rating, so that I can correct mistakes.
Priority: Should Have — Story Points: 3

### US-5.4: Skip current card

As a user, I want to skip the current card, so that I can postpone it temporarily without changing SRS.
Priority: Should Have — Story Points: 2

### US-5.5: Edit card during review

As a user, I want to edit the current card while reviewing, so that I can fix errors on the spot.
Priority: Should Have — Story Points: 3

### US-5.6: Configure SRS settings

As a user, I want to configure total boxes, daily limits, and review order, so that review fits my pace.
Priority: Must Have — Story Points: 3

---

## Epic 6: Study Modes

### US-6.1: Cram mode (deck)

As a user, I want to cram a deck (ignoring due dates), so that I can revise quickly.
Priority: Should Have — Story Points: 5

### US-6.2: Cram mode (folder)

As a user, I want to cram a folder (including sub-folders), so that I can revise a broader scope quickly.
Priority: Should Have — Story Points: 5

### US-6.3: Random mode

As a user, I want to review due cards in random order, so that I can test my knowledge.
Priority: Should Have — Story Points: 5

---

## Epic 7: Statistics & Analytics

### US-7.1: View streak counter

As a user, I want to see my consecutive study days, so that I stay motivated.
Priority: Should Have — Story Points: 3

### US-7.2: View cards reviewed today

As a user, I want to see how many cards I reviewed today, so that I can track progress.
Priority: Should Have — Story Points: 2

### US-7.3: View box distribution

As a user, I want to see a chart of cards by SRS box, so that I understand my knowledge retention.
Priority: Should Have — Story Points: 5

### US-7.4: View deck statistics

As a user, I want to see total/due/new cards per deck and last studied date, so that I can focus my efforts.
Priority: Should Have — Story Points: 3

### US-7.5: View folder statistics (recursive)

As a user, I want to see total/due/new cards for a folder (including sub-folders), so that I understand overall status.
Priority: Should Have — Story Points: 5

---

## Epic 8: UI/UX Enhancements

### US-8.1: Theme switching (Light/Dark/System)

As a user, I want to switch between Light, Dark, and System themes, so that the app is comfortable in any lighting.
Priority: Must Have — Story Points: 5

### US-8.2: Language switching (VI/EN)

As a user, I want to switch between Vietnamese and English, so that I can use the app in my preferred language.
Priority: Must Have — Story Points: 5

### US-8.3: Responsive design

As a user, I want the app to work well on mobile, tablet, and desktop, so that I can study anywhere.
Priority: Must Have — Story Points: 8

---

## Priority Legend

- Must Have: Core functionality required for MVP
- Should Have: Important; include if time permits
- Could Have: Nice to have; post‑MVP
- Won’t Have: Out of MVP scope

## Story Points Guide

- 1–2: Very simple (< 4 hours)
- 3: Simple (4–8 hours)
- 5: Medium (1–2 days)
- 8: Complex (3–5 days)
- 13: Very complex (1–2 weeks; should be broken down)
