# Use Cases - RepeatWise MVP

## Use Case List

### Authentication & User Management
- ✅ **UC-001**: User Registration - User creates new account
- ✅ **UC-002**: User Login - User logs into application
- ✅ **UC-003**: User Logout - User logs out
- ✅ **UC-004**: User Profile Management - User updates profile settings

### Folder Management
- ✅ **UC-005**: Create Folder Hierarchy - User creates nested folders
- ✅ **UC-006**: Rename Folder - User renames existing folder
- ✅ **UC-007**: Move Folder - User moves folder to different location
- ✅ **UC-008**: Copy Folder - User copies folder with all contents (sync/async)
- ✅ **UC-009**: Delete Folder - User deletes folder (soft delete)
- ✅ **UC-010**: View Folder Statistics - User views folder stats (recursive)

### Deck Management
- ✅ **UC-011**: Create Deck - User creates new deck in folder
- ✅ **UC-012**: Move Deck - User moves deck between folders
- ✅ **UC-013**: Copy Deck - User copies deck (sync/async for large decks)
- ✅ **UC-014**: Delete Deck - User deletes deck (soft delete)

### Card Management
- ✅ **UC-015**: Import Cards from File - User imports cards from CSV/Excel
- ✅ **UC-016**: Export Cards to File - User exports cards to CSV/Excel
- ✅ **UC-017**: Create/Edit Card - User creates or edits flashcard
- ✅ **UC-018**: Delete Card - User deletes card (soft delete)

### Review & Study
- ✅ **UC-019**: Review Cards with SRS - User reviews due cards with spaced repetition
- ✅ **UC-020**: Cram Mode Review - User reviews all cards without schedule impact
- ✅ **UC-021**: Random Mode Review - User reviews random selection of cards
- ✅ **UC-022**: Configure SRS Settings - User configures SRS preferences
- ✅ **UC-023**: View Statistics - User views learning statistics
- ✅ **UC-024**: Manage Notifications - User configures notification settings

## Status Legend
- ✅ **Completed**: Use case documentation written
- 📝 **Pending**: Use case needs to be documented
- ⏳ **In Progress**: Use case being documented

## Use Case Template Structure

Each use case follows this structure:
1. **Use Case Information** - ID, name, actors, priority, complexity
2. **Brief Description** - High-level summary
3. **Preconditions** - Required state before use case
4. **Postconditions** - System state after use case
5. **Main Success Scenario** - Happy path flow
6. **Alternative Flows** - Variations of main scenario
7. **Exception Flows** - Error handling and recovery
8. **Special Requirements** - Performance, security, usability, accessibility
9. **Business Rules** - Domain rules and constraints
10. **Data Requirements** - Input/output data, DB changes
11. **UI Mockup Notes** - Visual layout descriptions
12. **Testing Scenarios** - Happy path, edge cases, error cases
13. **Related Use Cases** - Connections to other use cases
14. **Notes & Assumptions** - Additional context
15. **Acceptance Criteria** - Definition of done

## Priority Classification

### P0 - Critical (MVP Core)
- UC-001, UC-002, UC-005, UC-015, UC-019
- Must have for MVP launch

### P1 - High (MVP Essential)
- UC-003, UC-004, UC-006, UC-007, UC-009, UC-011, UC-014, UC-017, UC-022
- Essential features for complete user experience

### P2 - Medium (MVP Nice-to-Have)
- UC-008, UC-010, UC-012, UC-013, UC-016, UC-018, UC-020, UC-021, UC-023, UC-024
- Important but can be added in later iterations

## Use Case Dependencies

```
UC-001 (Register) → UC-002 (Login) → UC-003 (Logout)
                         ↓
                    UC-004 (Profile)
                         ↓
          ┌──────────────┴───────────────┐
          ↓                              ↓
     UC-005 (Create Folder)        UC-022 (SRS Settings)
          ↓                              ↓
    UC-006, UC-007,                UC-019 (Review)
    UC-008, UC-009                       ↓
          ↓                        UC-020, UC-021
     UC-010 (Stats)                      ↓
          ↓                         UC-023 (Stats)
     UC-011 (Create Deck)                ↓
          ↓                        UC-024 (Notifications)
    UC-012, UC-013, UC-014
          ↓
  UC-015, UC-016 (Import/Export)
          ↓
    UC-017, UC-018
```

## Implementation Order (Development Phases)

### Phase 1: Backend Foundation (Week 1-2)
1. UC-001: User Registration
2. UC-002: User Login
3. UC-005: Create Folder Hierarchy
4. UC-011: Create Deck
5. UC-017: Create/Edit Card

### Phase 2: SRS Algorithm (Week 3-4)
1. UC-022: Configure SRS Settings
2. UC-019: Review Cards with SRS
3. UC-020: Cram Mode Review
4. UC-021: Random Mode Review

### Phase 3: Folder & Deck Management (Week 5)
1. UC-006: Rename Folder
2. UC-007: Move Folder
3. UC-009: Delete Folder
4. UC-012: Move Deck
5. UC-014: Delete Deck

### Phase 4: Advanced Features (Week 6)
1. UC-008: Copy Folder (async)
2. UC-013: Copy Deck (async)
3. UC-015: Import Cards from File
4. UC-016: Export Cards to File

### Phase 5: Statistics & UX Polish (Week 7-8)
1. UC-010: View Folder Statistics
2. UC-023: View Statistics
3. UC-024: Manage Notifications
4. UC-003: User Logout
5. UC-004: User Profile Management
6. UC-018: Delete Card

## Related Documentation

- [System Specification](../system-spec.md)
- [Domain Model](../domain-model.md)
- [Data Dictionary](../data-dictionary.md)
- [Non-Functional Requirements](../nfr.md)
- [API Endpoints](../../03-technical-design/api-spec.md)
- [Database Schema](../../03-technical-design/database-schema.md)

---

**Version**: 1.0
**Last Updated**: 2025-01
