# UC-011: Create Deck

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-011 |
| **Use Case Name** | Create Deck |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | None |
| **Priority** | High (P0) |
| **Complexity** | Low |
| **Status** | MVP |

## 2. Brief Description

User creates a new flashcard deck within a folder to organize related flashcards. The deck serves as a container for cards and can be used for focused study sessions.

## 3. Preconditions

- User is logged in
- User has at least one folder (or creates deck at root level)
- User has permission to create decks
- System has available storage

## 4. Postconditions

**Success**:
- New deck created in database
- Deck appears in folder view
- Deck ready to receive cards
- User can immediately add cards

**Failure**:
- No deck created
- Error message displayed
- User remains on current view

## 5. Main Success Scenario

### Step 1: Navigate to Deck Creation
**Actor**: User clicks "New Deck" button in folder view or right-clicks folder

**System**:
- Shows deck creation dialog/modal
- Pre-selects current folder as parent
- Shows breadcrumb of parent path: "Home > English Learning > IELTS Preparation"
- Auto-focuses on name field

### Step 2: Enter Deck Information
**Actor**: User enters:
- Name: "Academic Vocabulary"
- Description: "High-frequency academic words for IELTS writing" (optional)
- Folder: "IELTS Preparation / Vocabulary" (selected via dropdown)

**System**:
- Validates name in real-time (not empty, ≤100 chars)
- Shows character counter: "18 / 100"
- Previews location: "📁 IELTS Preparation > Vocabulary > Academic Vocabulary"

### Step 3: Submit Deck Creation
**Actor**: User clicks "Create" button

**System**:
1. Validates input (server-side):
   - Name not empty and ≤100 characters
   - Description ≤500 characters (if provided)
   - Folder exists and belongs to user
   - Name unique within folder (case-sensitive)
2. Generates UUID for deck
3. Creates deck record in database:
```sql
INSERT INTO decks (id, user_id, folder_id, name, description)
VALUES (
    :deck_id,
    :user_id,
    :folder_id,
    'Academic Vocabulary',
    'High-frequency academic words for IELTS writing'
);
```
4. Creates deck_stats record (optional, for caching):
```sql
INSERT INTO deck_stats (deck_id, total_cards, due_cards, learned_cards)
VALUES (:deck_id, 0, 0, 0);
```
5. Logs event: "Deck created: 'Academic Vocabulary' in folder 'Vocabulary'"

### Step 4: Display New Deck
**System**:
- Inserts deck into folder view
- Shows deck card/tile:
  - Name: "Academic Vocabulary"
  - Description preview (first 100 chars)
  - Stats: "0 cards • 0 due"
  - Created date
- Highlights new deck
- Shows success toast: "Deck 'Academic Vocabulary' created"
- Auto-focuses on new deck for quick action

**Actor**: User sees new deck and can immediately:
- Add cards manually (UC-014)
- Import cards from file (UC-015)
- Configure deck settings (Future)
- Start study session (empty for now)

## 6. Alternative Flows

### A1: Duplicate Deck Name in Folder
**Trigger**: User enters name that already exists in same folder (Step 3)

**Flow**:
1. System checks existing decks in same folder
2. Found duplicate: "Academic Vocabulary" already exists
3. System shows error: "A deck with this name already exists in this folder"
4. System suggests: "Choose a different name or use the existing deck"
5. User can:
   - Change name to "Academic Vocabulary 2"
   - Cancel and use existing deck
   - Select different folder

**Return to**: Step 2

---

### A2: Create Deck at Root Level (No Folder)
**Trigger**: User creates deck without selecting folder (Step 2)

**Flow**:
1. User selects "None (Root Level)" as folder
2. System sets folder_id = NULL
3. System shows preview: "📁 Home > Academic Vocabulary"
4. System creates deck at root level:
```sql
INSERT INTO decks (id, user_id, folder_id, name, description)
VALUES (:deck_id, :user_id, NULL, 'Academic Vocabulary', :description);
```
5. Deck appears in root-level deck list

**Continue to**: Step 4

---

### A3: Empty Deck Name
**Trigger**: User submits without entering name (Step 2)

**Flow**:
1. System validates name is not empty
2. System shows error: "Deck name is required"
3. System focuses on name field
4. User enters name

**Return to**: Step 2

---

### A4: Name Too Long
**Trigger**: User enters > 100 characters (Step 2)

**Flow**:
1. User types long name (101+ characters)
2. System validates length
3. System shows error: "Deck name too long (101/100 characters)"
4. System prevents further typing OR truncates
5. Character counter shows red: "101 / 100"
6. User shortens name

**Return to**: Step 2

---

### A5: Description Too Long
**Trigger**: User enters > 500 characters in description (Step 2)

**Flow**:
1. User types long description (501+ characters)
2. System validates length
3. System shows warning: "Description too long (501/500 characters)"
4. System prevents further typing OR truncates
5. Character counter shows: "501 / 500" in red
6. User shortens description

**Return to**: Step 2

---

### A6: Parent Folder Deleted
**Trigger**: Folder was deleted while dialog open (Step 3)

**Flow**:
1. System tries to create deck in folder
2. Folder not found or soft-deleted
3. System shows error: "Selected folder no longer exists"
4. System suggests: "Please refresh and try again"
5. User closes dialog and refreshes view

**End Use Case**

---

### A7: Quick Create (Minimal Input)
**Trigger**: User wants to create deck quickly (Alternative Step 2)

**Flow**:
1. User enters only name: "Quick Deck"
2. User presses Enter (skips description)
3. System uses current folder as default
4. System creates deck with minimal info:
   - Name: "Quick Deck"
   - Description: NULL
   - Folder: Current folder
5. Deck created in < 100ms

**Continue to**: Step 4

## 7. Special Requirements

### Performance
- Deck creation completes in < 200ms
- Folder view updates immediately (optimistic UI)
- No refresh needed

### Usability
- Auto-focus on name field
- Enter key submits form
- Escape key cancels
- Show folder breadcrumb preview
- Remember last selected folder (session)
- Keyboard shortcut: Ctrl+Shift+D for new deck

### Validation
- Name: 1-100 characters, required
- Description: 0-500 characters, optional
- Name unique within same folder
- Folder must exist and belong to user

## 8. Business Rules

### BR-031: Deck Naming
- Name required, not empty
- Length: 1-100 characters
- Trim leading/trailing whitespace
- No special character restrictions (unlike folders)
- Case-sensitive (but warn on case-only differences)

### BR-032: Deck Organization
- Deck must belong to a folder OR be at root level
- folder_id can be NULL (root-level deck)
- Same deck name allowed in different folders
- Name must be unique within same folder

### BR-033: Deck Uniqueness
- Name unique within same folder (case-sensitive)
- Same name allowed in different folders
- Same name allowed at root vs. in folder

### BR-034: Initial State
- New deck has 0 cards
- deck_stats initialized to zeros (if used)
- No default cards created
- Ready to receive cards immediately

## 9. Data Requirements

### Input
- name: VARCHAR(100), required
- description: VARCHAR(500), optional
- folder_id: UUID, nullable

### Output
- Deck object: { id, name, description, folder_id, created_at }

### Database Changes
```sql
-- Create deck
INSERT INTO decks (id, user_id, folder_id, name, description, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    :user_id,
    :folder_id, -- Can be NULL
    :name,
    :description,
    NOW(),
    NOW()
);

-- Optional: Initialize deck_stats (for caching)
INSERT INTO deck_stats (deck_id, total_cards, due_cards, learned_cards)
VALUES (:deck_id, 0, 0, 0);
```

### Validation Query
```sql
-- Check name uniqueness within folder
SELECT COUNT(*) FROM decks
WHERE folder_id = :folder_id -- OR both are NULL for root
  AND name = :name
  AND user_id = :user_id
  AND deleted_at IS NULL;
```

## 10. UI Mockup

### Deck Creation Dialog
```
┌────────────────────────────────────────┐
│  Create New Deck                    × │
├────────────────────────────────────────┤
│                                        │
│  Folder Location                       │
│  📁 IELTS Preparation > Vocabulary     │
│  [Change Folder ▼]                     │
│                                        │
│  Deck Name *                           │
│  [Academic Vocabulary__________] 18/100│
│                                        │
│  Description (Optional)                │
│  [High-frequency academic words___]    │
│  [for IELTS writing_______________]    │
│  [________________________________]    │
│                                   0/500│
│                                        │
│  Preview:                              │
│  📁 IELTS Preparation > Vocabulary >   │
│     📂 Academic Vocabulary             │
│                                        │
│         [Cancel]  [Create Deck]        │
└────────────────────────────────────────┘
```

### Folder View After Creation
```
┌────────────────────────────────────────┐
│  📁 Vocabulary                         │
│  3 decks • 250 cards • 45 due          │
├────────────────────────────────────────┤
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ 📂 Academic Vocabulary      New! │ │
│  │ High-frequency academic words... │ │
│  │ 0 cards • 0 due • Created today  │ │
│  └──────────────────────────────────┘ │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ 📂 Common Collocations           │ │
│  │ Frequently used word pairs       │ │
│  │ 120 cards • 20 due               │ │
│  └──────────────────────────────────┘ │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ 📂 Phrasal Verbs                 │ │
│  │ Essential phrasal verbs          │ │
│  │ 130 cards • 25 due               │ │
│  └──────────────────────────────────┘ │
│                                        │
│  [+ New Deck]                          │
└────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Create deck "Academic Vocabulary" in folder "Vocabulary"
2. Verify deck created in database
3. Verify deck appears in folder view
4. Verify deck has 0 cards
5. Verify deck ready to receive cards

### Alternative Flows
1. Create deck at root level (folder_id = NULL) → Should succeed
2. Create deck with description → Should save description
3. Create deck without description → Should save with NULL description
4. Quick create with Enter key → Should work same as clicking button

### Edge Cases
1. Create deck with name "Test", then try "test" in same folder (case-sensitive) → Should fail
2. Create deck with 100-character name → Should succeed
3. Create deck with 101-character name → Should fail
4. Create deck with 500-character description → Should succeed
5. Create deck with Unicode name "Học Tiếng Anh" → Should succeed
6. Create deck with same name in different folder → Should succeed

### Error Cases
1. Empty name → Error: "Deck name is required"
2. Duplicate name in same folder → Error with suggestion
3. Name too long (101 chars) → Error
4. Description too long (501 chars) → Error
5. Folder deleted during creation → Error: "Folder no longer exists"

## 12. Performance Benchmarks

| Operation | Target | Max |
|-----------|--------|-----|
| Create deck | < 100ms | 200ms |
| Uniqueness check | < 30ms | 50ms |
| Folder view update | Immediate | 100ms |

## 13. Related Use Cases

- **UC-005**: Create Folder Hierarchy
- **UC-014**: Create Card (add cards to deck)
- **UC-015**: Import Cards from File (bulk add cards)
- **UC-017**: Edit Deck
- **UC-018**: Delete Deck
- **UC-019**: Review Cards with SRS

## 14. Acceptance Criteria

- [ ] User can create deck in any folder
- [ ] User can create deck at root level
- [ ] Duplicate names in same folder prevented
- [ ] Name validation (1-100 chars) enforced
- [ ] Description optional (0-500 chars)
- [ ] Deck appears in folder view immediately
- [ ] Creation completes in < 200ms (p95)
- [ ] Character counters work for name and description
- [ ] Breadcrumb preview shows full path
- [ ] Auto-focus on name field
- [ ] Enter key submits form
- [ ] Escape key cancels
- [ ] Unicode names supported

---

**Version**: 1.0
**Last Updated**: 2025-01
