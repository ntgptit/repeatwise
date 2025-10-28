# Use Cases Index - RepeatWise MVP

## Introduction

This document is an index (catalog) of all use cases for the RepeatWise MVP. Each use case is described in a separate file under the `use-cases/` directory.

## Use Case Structure

Each use case follows a standard template:

1. Brief Description
2. Actors
3. Preconditions
4. Postconditions
5. Main Success Scenario (Basic Flow)
6. Alternative Flows
7. Special Requirements
8. Frequency of Occurrence
9. Open Issues
10. Related Use Cases
11. Business Rules References
12. API Endpoint and Test Cases (where applicable)

---

## Epic 1: User Management

### Authentication & Authorization

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-001](use-cases/UC-001-user-registration.md) | User Registration | Must Have | Low | Not Started |
| [UC-002](use-cases/UC-002-user-login.md) | User Login | Must Have | Low | Not Started |
| [UC-003](use-cases/UC-003-refresh-token.md) | Refresh Access Token | Must Have | Medium | Not Started |
| [UC-004](use-cases/UC-004-user-logout.md) | User Logout | Must Have | Low | Not Started |

### Profile Management

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-005](use-cases/UC-005-update-user-profile.md) | Update User Profile | Must Have | Low | Not Started |
| [UC-006](use-cases/UC-006-change-password.md) | Change Password | Should Have | Low | Not Started |

---

## Epic 2: Folder Management

### Folder CRUD Operations

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-007](use-cases/UC-007-create-folder.md) | Create Folder | Must Have | Medium | Not Started |
| [UC-008](use-cases/UC-008-rename-folder.md) | Rename Folder | Must Have | Low | Not Started |
| [UC-009](use-cases/UC-009-move-folder.md) | Move Folder | Must Have | High | Not Started |
| [UC-010](use-cases/UC-010-copy-folder.md) | Copy Folder (Sync & Async) | Should Have | High | Not Started |
| [UC-011](use-cases/UC-011-delete-folder.md) | Delete Folder | Must Have | Medium | Not Started |

### Folder Statistics & Navigation

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-012](use-cases/UC-012-view-folder-statistics.md) | View Folder Statistics | Must Have | Medium | Not Started |

---

## Epic 3: Deck Management

### Deck CRUD Operations

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-013](use-cases/UC-013-create-deck.md) | Create Deck | Must Have | Low | Not Started |
| [UC-014](use-cases/UC-014-update-deck.md) | Update Deck | Must Have | Low | Not Started |
| [UC-015](use-cases/UC-015-move-deck.md) | Move Deck | Must Have | Low | Not Started |
| [UC-016](use-cases/UC-016-copy-deck.md) | Copy Deck (Sync & Async) | Should Have | High | Not Started |
| [UC-017](use-cases/UC-017-delete-deck.md) | Delete Deck | Must Have | Low | Not Started |

---

## Epic 4: Card Management

### Card CRUD Operations

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-018](use-cases/UC-018-create-card.md) | Create Card | Must Have | Low | Not Started |
| [UC-019](use-cases/UC-019-update-card.md) | Update Card | Must Have | Low | Not Started |
| [UC-020](use-cases/UC-020-delete-card.md) | Delete Card | Must Have | Low | Not Started |

### Bulk Operations

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-021](use-cases/UC-021-import-cards.md) | Import Cards from CSV/Excel | Must Have | High | Not Started |
| [UC-022](use-cases/UC-022-export-cards.md) | Export Cards to CSV/Excel | Must Have | Medium | Not Started |

---

## Epic 5: Spaced Repetition System

### Review Session

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-023](use-cases/UC-023-review-cards-srs.md) | Review Cards (SRS Mode) | Must Have | High | Not Started |
| [UC-024](use-cases/UC-024-rate-card.md) | Rate Card During Review | Must Have | High | Not Started |
| [UC-025](use-cases/UC-025-undo-review.md) | Undo Last Review | Should Have | Medium | Not Started |
| [UC-026](use-cases/UC-026-skip-card.md) | Skip Card | Should Have | Low | Not Started |
| [UC-027](use-cases/UC-027-edit-card-during-review.md) | Edit Card During Review | Should Have | Medium | Not Started |

### SRS Configuration

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-028](use-cases/UC-028-configure-srs-settings.md) | Configure SRS Settings | Must Have | Medium | Not Started |

---

## Epic 6: Study Modes

### Alternative Study Modes

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-029](use-cases/UC-029-cram-mode.md) | Cram Mode | Should Have | Medium | Not Started |
| [UC-030](use-cases/UC-030-random-mode.md) | Random Mode | Should Have | Low | Not Started |

---

## Epic 7: Statistics

| ID | Use Case | Priority | Complexity | Status |
|----|----------|----------|------------|--------|
| [UC-031](use-cases/UC-031-view-user-statistics.md) | View User Statistics | Should Have | Low | Not Started |
| [UC-032](use-cases/UC-032-view-box-distribution.md) | View Box Distribution | Should Have | Low | Not Started |

---

## Priorities (MoSCoW)

### Must Have (MVP)

- Registration, Login, Refresh Token (UC-001..UC-003)
- Folder/Deck/Card basic CRUD (UC-007, UC-008, UC-011, UC-013, UC-014, UC-017, UC-018, UC-019, UC-020)
- Review + Rate (UC-023, UC-024)
- Import/Export (UC-021, UC-022)

### Should Have (MVP if time permits)

- Change Password (UC-006)
- Copy Folder/Deck (UC-010, UC-016) - Async mode
- Undo/Skip/Edit during Review (UC-025..UC-027)
- Study Modes (UC-029, UC-030)
- Statistics (UC-031, UC-032)

### Could Have (Post-MVP)

- Advanced filtering and search
- Bulk delete operations
- Card templates
- Advanced analytics and charts

---

## Use Case Dependencies

### Critical Path

1. UC-001, UC-002 — Authentication foundation
2. UC-003 — Token refresh for seamless UX
3. UC-007, UC-013, UC-018 — Create content hierarchy
4. UC-021 — Enable bulk content creation
5. UC-023, UC-024 — Core learning functionality

### Secondary Dependencies

- UC-010 depends on UC-007 (Copy Folder requires Create Folder)
- UC-016 depends on UC-013 (Copy Deck requires Create Deck)
- UC-025/UC-026/UC-027 depend on UC-023 (Review session)
- UC-012 depends on UC-007/UC-013/UC-018 (Statistics need content)

---

## Implementation Phases

### Phase 1: Foundation (Week 1–2)

- UC-001, UC-002, UC-003, UC-004, UC-005

### Phase 2: Content Structure (Week 3–4)

- UC-007, UC-008, UC-011, UC-013, UC-014, UC-017, UC-018, UC-019, UC-020

### Phase 3: SRS Core (Week 5–6)

- UC-023, UC-024, UC-028, UC-021, UC-022

### Phase 4: Advanced Ops (Week 7–8)

- UC-009, UC-010, UC-015, UC-016, UC-012, UC-025, UC-026, UC-027

### Phase 5: Study Modes & Analytics (Week 9–10)

- UC-029, UC-030, UC-031, UC-032

---

## Related Documents

- [Requirements Summary](01-requirements-summary.md)
- [User Stories](03-user-stories.md)
- [Business Rules](04-business-rules.md)
- [Acceptance Criteria](05-acceptance-criteria.md)

---

## Notes

- Each use case file follows a standard template for easy reading and maintenance.
- Priority and Complexity are based on business value and effort.
- Status lifecycle: Not Started → In Progress → In Review → Completed.
