# UC-015: Import Cards from CSV/Excel

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-015 |
| **Use Case Name** | Import Cards from CSV/Excel File |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | File Processing Service |
| **Priority** | High (P0) - Core Feature |
| **Complexity** | High |
| **Status** | MVP |

## 2. Brief Description

User imports flashcards in bulk from CSV or Excel file (.xlsx). System validates the file format, previews data, and batch inserts cards into selected deck with progress tracking.

## 3. Preconditions

- User is logged in
- User has created at least one deck
- User has CSV/Excel file ready (Front, Back columns)
- File size ≤ 50MB
- Row count ≤ 10,000 rows

## 4. Postconditions

**Success**:
- Cards imported into deck
- CardBoxPosition records created for each card
- Import summary displayed (success count, errors)
- User can immediately review imported cards

**Failure**:
- No cards imported (atomic transaction)
- Error report displayed
- File can be corrected and re-imported

## 5. Main Success Scenario

### Step 1: Access Import Feature
**Actor**: User navigates to deck "IELTS Vocabulary" and clicks "Import Cards"

**System**:
- Shows import dialog with:
  - File upload area (drag & drop or browse)
  - Template download link
  - Format instructions
  - Supported formats: CSV, XLSX

### Step 2: Download Template (Optional)
**Actor**: User clicks "Download Template"

**System**:
- Generates template file with:
  - Headers: "Front", "Back"
  - 3 sample rows:
    ```
    Front,Back
    ubiquitous,existing or being everywhere
    ephemeral,lasting for a very short time
    serendipity,finding valuable things by chance
    ```
  - Instructions sheet (for Excel)

**Actor**: User downloads template, fills with 1000 vocabulary words

### Step 3: Upload File
**Actor**: User drags and drops `ielts_vocabulary.xlsx` file or browses to select it

**System**:
1. Validates file:
   - File size: 2.5MB < 50MB ✓
   - File extension: .xlsx ✓
   - MIME type: application/vnd.openxmlformats ✓
2. Shows file info:
   - Name: ielts_vocabulary.xlsx
   - Size: 2.5 MB
   - Status: Uploading... (progress bar)
3. Uploads file to server
4. Server parses file using Apache POI

### Step 4: Validate and Preview Data
**System**:
1. Parses Excel file:
   - Reads first sheet
   - Checks for "Front" and "Back" columns
   - Row count: 1000 rows
2. Validates each row:
   - Skip empty rows (10 empty rows found)
   - Check Front and Back not empty
   - Check length ≤ 5000 characters
   - Check for duplicates in file
   - Check for existing duplicates in deck (optional)
3. Generates validation report:
   - Total rows: 1000
   - Valid rows: 985
   - Empty rows: 10 (skipped)
   - Invalid rows: 5 (missing front or back)
   - Duplicate rows: 0
4. Shows preview table (first 10 valid rows):
   ```
   | # | Front | Back | Status |
   |---|-------|------|--------|
   | 1 | ubiquitous | existing everywhere | ✓ Valid |
   | 2 | ephemeral | lasting short time | ✓ Valid |
   | 3 | serendipity | finding by chance | ✓ Valid |
   ...
   | 10| meticulous | showing great attention | ✓ Valid |
   ```
5. Shows error details for invalid rows:
   ```
   Row 15: Missing 'Back' field
   Row 47: Missing 'Front' field
   Row 203: Front text exceeds 5000 characters
   Row 508: Missing 'Back' field
   Row 891: Missing 'Front' field
   ```

### Step 5: Review and Confirm Import
**Actor**: User reviews preview and validation report

**System**: Shows import summary:
- ✓ 985 cards ready to import
- ⚠ 5 cards will be skipped (errors)
- ⚠ 10 empty rows will be ignored
- Option to download error report (CSV)

**Actor**: User clicks "Import 985 Cards" button

### Step 6: Execute Batch Import
**System**:
1. Shows progress modal:
   - "Importing cards..." (spinner)
   - Progress bar: 0% → 100%
   - "Processing: 100 / 985 cards" (updates every 100 cards)
2. Batch processing (1000 cards per transaction):
   - INSERT INTO cards (id, deck_id, front, back) VALUES ...
   - INSERT INTO card_box_position for each card:
     - current_box = 1
     - interval_days = 1
     - due_date = tomorrow
     - review_count = 0
3. Progress updates:
   - 10% (100 cards)
   - 20% (200 cards)
   - ...
   - 100% (985 cards)
4. Completes in 8 seconds

### Step 7: Display Import Results
**System**:
- Shows success modal:
  ```
  ✓ Import Successful!

  985 cards imported successfully
  5 cards skipped due to errors
  10 empty rows ignored

  [Download Error Report] [Start Reviewing] [Close]
  ```
- Updates deck card count: 985 cards
- Updates folder stats (invalidate cache)
- Logs event: "Imported 985 cards into deck {deck_id}"

**Actor**: User clicks "Start Reviewing" to immediately review new cards

## 6. Alternative Flows

### A1: Invalid File Format
**Trigger**: User uploads .doc file instead of CSV/Excel (Step 3)

**Flow**:
1. System checks file extension
2. Extension not in [.csv, .xlsx]
3. System shows error: "Unsupported file format. Please upload CSV or Excel (.xlsx) file."
4. System highlights accepted formats
5. User selects correct file

**Return to**: Step 3

---

### A2: File Too Large
**Trigger**: User uploads 60MB file (Step 3)

**Flow**:
1. System checks file size: 60MB > 50MB
2. System shows error: "File size exceeds 50MB limit. Please split into smaller files."
3. System suggests: "Try importing in batches of 5,000 cards each"
4. User splits file into 2 smaller files

**End Use Case** (user will import twice)

---

### A3: Too Many Rows
**Trigger**: File contains 15,000 rows (Step 4)

**Flow**:
1. System counts rows: 15,000 > 10,000
2. System shows error: "File contains 15,000 rows. Maximum allowed: 10,000 rows."
3. System suggests: "Please split file or import first 10,000 rows"
4. System offers option: "Import first 10,000 rows only"
5. User can:
   - Split file and import in batches
   - Import first 10,000 rows only

**Return to**: Step 3 or End

---

### A4: Missing Required Columns
**Trigger**: File doesn't have "Front" or "Back" column (Step 4)

**Flow**:
1. System parses header row
2. Required columns not found
3. System shows error: "Missing required columns: 'Front' and 'Back'"
4. System shows found columns: "Word, Definition"
5. System suggests: "Please rename columns to 'Front' and 'Back' or download template"
6. User downloads template and reformats file

**Return to**: Step 3

---

### A5: All Rows Invalid
**Trigger**: All rows have errors (Step 4)

**Flow**:
1. System validates all rows
2. 0 valid rows found
3. System shows error: "No valid cards found in file"
4. System shows error report with all issues
5. System offers: "Download error report to fix issues"
6. User corrects file

**Return to**: Step 3

---

### A6: Duplicate Cards Detected
**Trigger**: Some cards already exist in deck (Step 4)

**Flow**:
1. System checks for duplicates by comparing Front text
2. Found 50 duplicates
3. System shows warning: "50 cards already exist in this deck"
4. System offers options:
   - Skip duplicates (import 935 new cards only)
   - Replace existing (update Back text)
   - Import all (create duplicates)
5. User selects "Skip duplicates"
6. System imports 935 cards, skips 50

**Continue to**: Step 6

---

### A7: Import Timeout
**Trigger**: Import takes > 2 minutes (Step 6)

**Flow**:
1. System processing exceeds timeout (2 min)
2. System cancels import transaction (rollback)
3. System shows error: "Import timed out. File may be too large or complex."
4. System suggests: "Try splitting file into smaller batches (< 5,000 cards)"
5. System logs timeout error for investigation

**End Use Case**

---

### A8: Database Error During Import
**Trigger**: Database connection lost mid-import (Step 6)

**Flow**:
1. System encounters error at card 500/985
2. System rolls back transaction (no partial import)
3. System shows error: "Import failed due to technical error. No cards were imported."
4. System logs error with stack trace
5. System offers: "Please try again or contact support if problem persists"
6. User can retry

**End Use Case**

## 7. Special Requirements

### Performance
- File upload: Stream processing, no size limit on client
- Parsing: Stream parsing, not load entire file to memory
- Validation: Process in chunks (100 rows at a time)
- Import: Batch insert 1000 cards per transaction
- Progress updates: Every 100 cards or every 500ms
- Target time:
  - 1,000 cards: < 10 seconds
  - 5,000 cards: < 30 seconds
  - 10,000 cards: < 60 seconds

### File Format Support
- **CSV**:
  - UTF-8 encoding
  - Comma delimiter
  - Optional header row
  - Quoted fields supported
- **Excel (.xlsx)**:
  - Microsoft Excel 2007+ format
  - First sheet only
  - First row as header
  - Support merged cells (Future)

### Validation Rules
- **Required**: Front and Back columns
- **Front/Back**: 1-5000 characters, not empty
- **Empty rows**: Silently skipped
- **Duplicates**: Warn user, offer options
- **Invalid chars**: No restrictions (accept unicode)

## 8. Business Rules

### BR-020: Import Limits
- File size: Max 50MB
- Row count: Max 10,000 rows per import
- Card count per deck: Max 10,000 cards total
- Concurrent imports: Max 5 per user

### BR-021: Duplicate Handling
- Duplicate check: Compare Front text (case-insensitive)
- Options:
  - Skip (default): Don't import duplicates
  - Replace: Update Back text of existing card
  - Import All: Create duplicate cards
- User chooses option in Step 4

### BR-022: Validation Errors
- Missing Front or Back: Row skipped
- Empty row: Silently skipped
- Invalid length: Row skipped with error
- Invalid format: Row skipped with error
- All errors reported in downloadable CSV

### BR-023: Atomic Import
- All-or-nothing transaction (except skipped rows)
- If error occurs, rollback entire import
- No partial imports (prevents data inconsistency)

## 9. Data Requirements

### Input File Format (CSV)
```csv
Front,Back
ubiquitous,existing or being everywhere at the same time
ephemeral,lasting for a very short time
```

### Input File Format (Excel)
| Front | Back |
|-------|------|
| ubiquitous | existing or being everywhere |
| ephemeral | lasting for a very short time |

### Database Changes
```sql
-- Batch insert cards
INSERT INTO cards (id, deck_id, front, back, created_at, updated_at)
VALUES
  (uuid1, deck_id, 'ubiquitous', 'existing everywhere', NOW(), NOW()),
  (uuid2, deck_id, 'ephemeral', 'lasting short time', NOW(), NOW()),
  ...
  (uuid1000, deck_id, 'word1000', 'definition1000', NOW(), NOW());

-- Batch insert box positions
INSERT INTO card_box_position (id, card_id, user_id, current_box, interval_days, due_date, review_count)
VALUES
  (uuid1, card1_id, user_id, 1, 1, CURRENT_DATE + 1, 0),
  (uuid2, card2_id, user_id, 1, 1, CURRENT_DATE + 1, 0),
  ...
  (uuid1000, card1000_id, user_id, 1, 1, CURRENT_DATE + 1, 0);

-- Invalidate folder stats cache
UPDATE folder_stats SET last_computed_at = NULL
WHERE folder_id IN (SELECT folder_id FROM decks WHERE id = ?);
```

## 10. UI Mockup

### Import Dialog - Step 1 (Upload)
```
┌──────────────────────────────────────────────┐
│  Import Cards into "IELTS Vocabulary"    × │
├──────────────────────────────────────────────┤
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │                                      │   │
│  │    📁 Drag & drop file here          │   │
│  │         or click to browse           │   │
│  │                                      │   │
│  │   Supported: CSV, Excel (.xlsx)      │   │
│  │   Max size: 50MB, Max rows: 10,000   │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  [📥 Download Template (CSV)]                │
│  [📥 Download Template (Excel)]              │
│                                              │
│  Format Requirements:                        │
│  • Two columns: "Front" and "Back"           │
│  • First row should be headers               │
│  • Empty rows will be skipped                │
│                                              │
│           [Cancel]                           │
└──────────────────────────────────────────────┘
```

### Import Dialog - Step 4 (Preview)
```
┌──────────────────────────────────────────────┐
│  Review Import - ielts_vocabulary.xlsx    × │
├──────────────────────────────────────────────┤
│  Validation Summary                          │
│  ✓ 985 valid cards                           │
│  ⚠ 5 invalid rows (will be skipped)          │
│  ⚠ 10 empty rows (will be ignored)           │
│                                              │
│  Preview (first 10 cards):                   │
│  ┌──────────────────────────────────────┐   │
│  │ # │ Front       │ Back         │ ✓   │   │
│  │ 1 │ ubiquitous  │ existing...  │ ✓   │   │
│  │ 2 │ ephemeral   │ lasting...   │ ✓   │   │
│  │ 3 │ serendipity │ finding...   │ ✓   │   │
│  │...│ ...         │ ...          │ ... │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ⚠ Errors Found:                             │
│  • Row 15: Missing 'Back' field              │
│  • Row 47: Missing 'Front' field             │
│  [Download Full Error Report (CSV)]          │
│                                              │
│    [Cancel]  [Import 985 Cards →]            │
└──────────────────────────────────────────────┘
```

### Import Progress
```
┌──────────────────────────────────────────────┐
│  Importing Cards...                          │
├──────────────────────────────────────────────┤
│                                              │
│  ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░ 60%        │
│                                              │
│  Processing: 600 / 985 cards                 │
│  Estimated time remaining: 4 seconds         │
│                                              │
└──────────────────────────────────────────────┘
```

## 11. Testing Scenarios

### Happy Path
1. Upload valid CSV with 1000 cards
2. All rows valid
3. Preview shows correct data
4. Import completes successfully in < 10 seconds
5. 1000 cards appear in deck

### Edge Cases
1. File with exactly 10,000 rows (should succeed)
2. File with 10,001 rows (should fail)
3. File exactly 50MB (should succeed)
4. Unicode characters in Front/Back (should work)
5. Empty rows scattered throughout file (should skip)
6. Duplicate Front text (should warn)

### Error Cases
1. .doc file → Format error
2. 60MB file → Size error
3. Missing "Front" column → Column error
4. All rows invalid → No valid cards error
5. Network timeout during upload → Retry option
6. Database error → Rollback, error message

## 12. Performance Benchmarks

| Cards | Target Time | Max Time |
|-------|-------------|----------|
| 100 | < 2s | 5s |
| 1,000 | < 10s | 20s |
| 5,000 | < 30s | 60s |
| 10,000 | < 60s | 120s |

## 13. Related Use Cases

- **UC-016**: Export Cards to CSV/Excel
- **UC-014**: Create Card Manually
- **UC-019**: Review Cards with SRS

## 14. Acceptance Criteria

- [ ] User can upload CSV and Excel files
- [ ] File size limit (50MB) enforced
- [ ] Row limit (10,000) enforced
- [ ] Template download works (CSV and Excel)
- [ ] Validation detects all error types
- [ ] Preview shows first 10 valid rows
- [ ] Error report downloadable
- [ ] Duplicate detection works
- [ ] Progress bar updates every 100 cards
- [ ] Batch insert completes in target time
- [ ] Import is atomic (rollback on error)
- [ ] Folder stats invalidated after import

---

**Version**: 1.0
**Last Updated**: 2025-01
