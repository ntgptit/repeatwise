# Requirements Summary - RepeatWise MVP

## Overview

This document summarizes the functional and non-functional requirements for the RepeatWise MVP.

## Functional Requirements

### FR-1: User Management

**FR-1.1: User Registration**

- Users can register with email and password.
- Validation: email must be unique; password length >= 8 characters.
- Do not auto‑login after successful registration.

**FR-1.2: User Authentication**

- Login with email + password.
- JWT access token (15 minutes) and refresh token (7 days).
- Refresh token stored in HTTP‑only cookie and rotated on refresh.

**FR-1.3: User Profile**

- Update profile: name, timezone, language (VI/EN), theme (Light/Dark/System).
- Change password.
- Logout (current device) and Logout all devices.

### FR-2: Folder Management

**FR-2.1: Folder CRUD**

- Create folder at any level (max depth = 10).
- Rename folder.
- Delete folder (soft delete).
- View folder details.

**FR-2.2: Folder Hierarchy**

- Hierarchical structure with nesting (max 10 levels).
- Parent–child relationship.
- Materialized path for fast ancestor/descendant queries.
- Breadcrumb navigation.

**FR-2.3: Folder Operations**

- Move folder to a new location.
  - Validate resulting depth <= 10.
  - Cannot move into itself or its descendants.
- Copy folder (deep copy).
  - Sync: <= 50 items (folders + decks).
  - Async: 51–500 items with progress tracking.
  - Hard limit: > 500 items is rejected.

**FR-2.4: Folder Statistics**

- Total decks (recursive).
- Total cards (recursive).
- Due cards (recursive).
- New cards.
- Last modified date.

### FR-3: Deck Management

**FR-3.1: Deck CRUD**

- Create deck (in a folder or at root level).
- View deck details.
- Update deck (name, description).
- Delete deck (soft delete).

**FR-3.2: Deck Operations**

- Move deck between folders.
- Copy deck.
  - Sync: <= 1,000 cards.
  - Async: 1,001–10,000 cards.
  - Hard limit: > 10,000 cards is rejected.

**FR-3.3: Deck Metadata**

- Name, description.
- Parent folder.
- Card count, due cards count.
- Last studied date.
- Created/updated timestamps.

### FR-4: Card Management

**FR-4.1: Card CRUD**

- Create card with front/back text.
- View card details.
- Update card content.
- Delete card (soft delete).

**FR-4.2: Card Validation**

- Front and Back are required.
- Max 5,000 characters each.
- Plain text only (MVP).

**FR-4.3: Import/Export**

- Import from CSV/Excel.
  - Max 10,000 rows per file.
  - Required columns: Front, Back.
  - Validation with preview and per‑row error reporting.
  - Progress bar for large files.
- Export to CSV/Excel.
  - Columns: Front, Back, Created Date, Review Count, Current Box.
  - Scope: All cards or Due cards only.
- Downloadable template file.

### FR-5: Spaced Repetition System

**FR-5.1: Box System**

- 7 boxes with default intervals:
  - Box 1: 1 day
  - Box 2: 3 days
  - Box 3: 7 days
  - Box 4: 14 days
  - Box 5: 30 days
  - Box 6: 60 days
  - Box 7: 120 days

**FR-5.2: Review Session**

- Fetch due cards by due_date <= today; order by due_date ASC, current_box ASC (configurable).
- Batch size capped (e.g., 200 per request) with client prefetching.
- Respect daily limits: new_cards_per_day, max_reviews_per_day.
- Rating options (AGAIN/HARD/GOOD/EASY) update SRS state.

## Non‑Functional Requirements

### NFR-1: Performance

- P95 API response time < 300 ms for typical requests.
- Folder tree load < 300 ms for common depths.
- Async operations for large copies/imports/exports.

### NFR-2: Security

- Password hashing using bcrypt (cost factor 12).
- HTTPS for all endpoints.
- JWT with refresh token rotation.
- Input validation and authorization checks on all endpoints.

### NFR-3: Reliability

- Uptime target 99.9%.
- Daily database backups; retention 30 days.
- Transactional integrity for critical operations (move/copy/delete).

### NFR-4: UX & Accessibility

- Responsive design for mobile, tablet, desktop.
- Light/Dark/System themes with smooth transitions; preference persisted.
- Localized strings (EN/VI) where applicable.

### NFR-5: Maintainability

- Unit test coverage >= 70%.
- Clean code principles (SOLID, DRY, KISS) and mandatory code reviews.
- OpenAPI for API documentation; architecture docs where relevant.

### NFR-6: Compatibility

- Browsers: Chrome/Firefox/Safari/Edge (latest 2 versions).
- Mobile: iOS 13+, Android 8.0+.
- Database: PostgreSQL 15+.

## Constraints

### Technical Constraints

- Backend: Java 17, Spring Boot 3.
- Database: PostgreSQL only.
- Web: React + TypeScript.
- Mobile: React Native.

### Business Constraints

- MVP timeline: 3–4 months.
- Initial team size: 1 developer.
- No budget for premium tools (MVP).
- Personal‑use focus (no collaboration features).

### Scope Constraints

- Plain text cards only; no images/audio in MVP.
- No social features; no offline mode.
- Email/password only (no OAuth) in MVP.

## Assumptions

- Stable internet connection.
- Modern browsers (last 2 versions).
- Basic technical literacy.
- Typical user total cards < 10,000; folder depth < 5 on average.
- Peak concurrent users < 100 (MVP).

## Dependencies

- PostgreSQL availability.
- Java 17 runtime; Node.js for web build; React Native toolchain.
- Git for version control.

## Success Criteria

### MVP Launch

- All core features functional; zero critical bugs.
- P95 response time < 500 ms.
- Optional mobile builds available for beta (TestFlight/Play Store).

### Post‑Launch (1 month)

- User retention > 50%.
- Average rating > 4.0/5.
- Daily active users > 100.
- Zero data loss incidents.

### Long‑term (6 months)

- User retention > 40%.
- Average rating > 4.5/5.
- Daily active users > 1,000.
- Feature request pipeline prioritized.

## Out of Scope (Future)

- Rich text, media cards (images/audio), cloze, MCQ, type‑in.
- Tags/categories, shared/public folders, community decks.
- Drag & drop UI, advanced analytics, gamification.
- Offline mode, OAuth login, third‑party APIs.
