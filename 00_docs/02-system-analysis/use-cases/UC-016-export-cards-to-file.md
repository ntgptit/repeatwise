# UC-016: Export Cards to File

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-016 |
| **Use Case Name** | Export Cards to File |
| **Primary Actor** | Student (Learner) |
| **Secondary Actors** | File Export Service |
| **Priority** | High (P0) |
| **Complexity** | Medium |
| **Status** | MVP |

## 2. Brief Description

User exports flashcards from a deck to CSV or Excel (.xlsx) file for backup, sharing, or editing externally. The export includes card content and optionally SRS progress data.

## 3. Preconditions

- User is logged in
- User owns the deck to export
- Deck exists and is not deleted
- Deck has at least 1 card

## 4. Postconditions

**Success**:
- File generated with card data
- File downloaded to user's device
- Export logged for audit trail
- User can import file back later

**Failure**:
- No file generated
- Error message displayed
- User remains on current view

## 5. Main Success Scenario

### Step 1: Initiate Export
**Actor**: User opens deck "Academic Vocabulary" and clicks "Export" button

**System**:
- Shows export options dialog
- Displays deck information:
  - Name: "Academic Vocabulary"
  - Cards: 120 cards
  - Folder: "IELTS Preparation > Vocabulary"
- Shows format options and export settings

### Step 2: Configure Export Options
**Actor**: User selects:
- **Format**: CSV (Default) OR Excel (.xlsx)
- **Include SRS Data**: ☑ (checked)
  - If checked: Exports current_box, due_date, review_count, lapse_count
  - If unchecked: Exports only front/back (clean import to other apps)
- **Encoding**: UTF-8 (recommended) OR UTF-8 BOM (for Excel compatibility)

**System**:
- Shows preview of export columns:
  - **Basic**: Front, Back
  - **With SRS**: Front, Back, Current Box, Due Date, Review Count, Lapse Count
- Shows estimated file size: "~50 KB"

### Step 3: Generate Export File
**Actor**: User clicks "Export" button

**System**:
1. Queries deck cards with optional SRS data:
```sql
SELECT
    c.front,
    c.back,
    cbp.current_box,
    cbp.due_date,
    cbp.review_count,
    cbp.lapse_count
FROM cards c
LEFT JOIN card_box_position cbp ON c.id = cbp.card_id AND cbp.user_id = :user_id
WHERE c.deck_id = :deck_id
  AND c.deleted_at IS NULL
ORDER BY c.created_at ASC;
```

2. Generates file based on format:

**CSV Format**:
```csv
Front,Back,Current Box,Due Date,Review Count,Lapse Count
ubiquitous,"existing or being everywhere, especially at the same time; omnipresent",3,2025-10-15,5,1
pertinent,"relevant to a particular matter; applicable",2,2025-10-12,3,0
meticulous,"showing great attention to detail; very careful and precise",1,2025-10-10,1,0
```

**Excel Format (.xlsx)**:
- Sheet 1: "Cards"
  - Headers in bold
  - Auto-filter enabled
  - Column widths auto-sized
  - Date format: YYYY-MM-DD
- Sheet 2: "Metadata" (optional)
  - Deck Name, Export Date, Total Cards, etc.

3. Sets appropriate headers:
```http
Content-Type: text/csv; charset=utf-8
Content-Disposition: attachment; filename="academic-vocabulary-2025-10-09.csv"
```

4. Logs event: "Cards exported: Deck 'Academic Vocabulary', 120 cards, CSV format"

### Step 4: Download File
**System**:
- Triggers browser download (web) or saves to Downloads folder (mobile)
- Shows success toast: "Exported 120 cards to 'academic-vocabulary-2025-10-09.csv'"
- Closes export dialog

**Actor**: User receives file and can:
- Open in Excel/Google Sheets for review
- Edit cards externally
- Share with others
- Backup to cloud storage
- Import back later (UC-015)

## 6. Alternative Flows

### A1: Export Without SRS Data (Clean Export)
**Trigger**: User unchecks "Include SRS Data" (Step 2)

**Flow**:
1. User unchecks "Include SRS Data"
2. System updates preview:
   - Columns: Front, Back only
   - File size: "~30 KB"
3. User exports
4. System generates file with only Front and Back columns:
```csv
Front,Back
ubiquitous,"existing or being everywhere, especially at the same time; omnipresent"
pertinent,"relevant to a particular matter; applicable"
meticulous,"showing great attention to detail; very careful and precise"
```
5. File compatible with any flashcard app (Anki, Quizlet, etc.)

**Continue to**: Step 4

---

### A2: Export to Excel (.xlsx)
**Trigger**: User selects "Excel (.xlsx)" format (Step 2)

**Flow**:
1. User selects "Excel (.xlsx)"
2. System generates .xlsx file using Apache POI:
```java
Workbook workbook = new XSSFWorkbook();
Sheet sheet = workbook.createSheet("Cards");

// Create header row
Row headerRow = sheet.createRow(0);
headerRow.createCell(0).setCellValue("Front");
headerRow.createCell(1).setCellValue("Back");
// ... more columns

// Apply header style (bold)
CellStyle headerStyle = workbook.createCellStyle();
Font font = workbook.createFont();
font.setBold(true);
headerStyle.setFont(font);

// Create data rows
int rowNum = 1;
for (Card card : cards) {
    Row row = sheet.createRow(rowNum++);
    row.createCell(0).setCellValue(card.getFront());
    row.createCell(1).setCellValue(card.getBack());
    // ... more cells
}

// Auto-size columns
for (int i = 0; i < 6; i++) {
    sheet.autoSizeColumn(i);
}

// Write to output stream
workbook.write(outputStream);
```
3. File downloaded with .xlsx extension
4. Opens directly in Excel with proper formatting

**Continue to**: Step 4

---

### A3: Export Empty Deck
**Trigger**: Deck has 0 cards (Step 1)

**Flow**:
1. User tries to export deck with 0 cards
2. System shows warning: "This deck has no cards to export"
3. System offers options:
   - **Cancel**: Close dialog
   - **Export Template**: Download empty template with headers only
4. User selects "Export Template"
5. System generates file with headers only:
```csv
Front,Back,Current Box,Due Date,Review Count,Lapse Count
```
6. User can use template to create cards externally and import

**End Use Case**

---

### A4: Export Large Deck (> 10,000 cards)
**Trigger**: Deck has > 10,000 cards (Step 3)

**Flow**:
1. User exports deck with 15,000 cards
2. System shows warning: "This deck is large. Export may take up to 30 seconds."
3. User confirms
4. System generates file with progress indicator:
   - Shows progress: "Exporting... 5,000 / 15,000 cards"
   - Updates every 1,000 cards
5. File generated successfully
6. File size: ~5 MB

**Continue to**: Step 4

---

### A5: Export with Special Characters
**Trigger**: Cards contain special characters (Step 3)

**Flow**:
1. Card front contains: `He said, "Hello, world!"`
2. Card back contains: Line breaks, commas, quotes
3. System properly escapes CSV:
```csv
Front,Back
"He said, ""Hello, world!""","This is line 1
This is line 2
With commas, quotes, and ""special"" chars"
```
4. CSV standard (RFC 4180) followed:
   - Quotes escaped by doubling: `""`
   - Fields with quotes/commas/newlines wrapped in quotes
   - Line breaks preserved in quotes
5. File imports correctly without corruption

**Continue to**: Step 4

---

### A6: Export with UTF-8 BOM (Excel Compatibility)
**Trigger**: User selects "UTF-8 BOM" encoding (Step 2)

**Flow**:
1. User selects "UTF-8 BOM" for better Excel compatibility
2. System adds BOM (Byte Order Mark) at file start:
```java
// Add UTF-8 BOM
outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
// Then write CSV content
```
3. File opens correctly in Excel on Windows with Unicode characters
4. Vietnamese characters: "Học tiếng Anh" displayed correctly

**Continue to**: Step 4

---

### A7: Export Fails - File Write Error
**Trigger**: Cannot write file (Step 3)

**Flow**:
1. System generates CSV content
2. File write fails (disk full, permission error)
3. System catches exception
4. System shows error: "Export failed. Unable to create file."
5. System logs error with details
6. User can retry

**End Use Case**

## 7. Special Requirements

### Performance
- Export < 1,000 cards: < 2 seconds
- Export < 10,000 cards: < 10 seconds
- Export > 10,000 cards: Show progress indicator
- File generation should not block UI (async for large exports)

### File Format
- CSV: UTF-8 encoding by default
- CSV: RFC 4180 compliant (proper escaping)
- Excel: .xlsx format (not legacy .xls)
- Excel: Headers bold, auto-filter, auto-sized columns

### Data Integrity
- All special characters preserved
- Line breaks preserved
- Unicode characters supported
- No data loss or corruption

### Usability
- Filename includes deck name and date: `deck-name-YYYY-MM-DD.csv`
- Preview shows sample rows before export
- Clear option to include/exclude SRS data
- Download triggers automatically (no "Save As" dialog needed)

## 8. Business Rules

### BR-039: Export Columns
- **Basic Export**: Front, Back only
- **Full Export**: Front, Back, Current Box, Due Date, Review Count, Lapse Count
- Column order fixed (for consistency with import)
- Headers required (first row)

### BR-040: Export Scope
- Exports only cards in specified deck
- Soft-deleted cards excluded (deleted_at IS NULL)
- Cards sorted by created_at ASC (oldest first)
- All cards exported (no pagination)

### BR-041: File Naming
- Format: `{deck-name-slug}-{YYYY-MM-DD}.{ext}`
- Deck name slugified: lowercase, spaces → hyphens, special chars removed
- Example: "Academic Vocabulary" → "academic-vocabulary-2025-10-09.csv"
- Unique timestamp prevents overwrites

### BR-042: Encoding
- Default: UTF-8 without BOM
- Option: UTF-8 with BOM (for Excel on Windows)
- All Unicode characters supported
- No character replacement or transliteration

## 9. Data Requirements

### Input
- deck_id: UUID, required
- format: ENUM('CSV', 'XLSX'), default CSV
- include_srs_data: BOOLEAN, default TRUE
- encoding: ENUM('UTF8', 'UTF8_BOM'), default UTF8

### Output
- File: Binary stream (CSV or Excel)
- Filename: String
- Export summary: { total_cards, file_size, format }

### Export Query
```sql
-- Full export with SRS data
SELECT
    c.front,
    c.back,
    cbp.current_box,
    cbp.due_date,
    cbp.review_count,
    cbp.lapse_count
FROM cards c
LEFT JOIN card_box_position cbp ON c.id = cbp.card_id AND cbp.user_id = :user_id
WHERE c.deck_id = :deck_id
  AND c.deleted_at IS NULL
ORDER BY c.created_at ASC;

-- Basic export (front/back only)
SELECT
    c.front,
    c.back
FROM cards c
WHERE c.deck_id = :deck_id
  AND c.deleted_at IS NULL
ORDER BY c.created_at ASC;
```

## 10. UI Mockup

### Export Options Dialog
```
┌────────────────────────────────────────┐
│  Export Deck                        × │
├────────────────────────────────────────┤
│                                        │
│  Deck: 📂 Academic Vocabulary          │
│  Location: IELTS Preparation > Vocab  │
│  Cards: 120                            │
│                                        │
│  ─────────────────────────────────────│
│                                        │
│  Export Format:                        │
│  ◉ CSV (.csv)                          │
│  ○ Excel (.xlsx)                       │
│                                        │
│  Options:                              │
│  ☑ Include SRS data (box, due date)   │
│  ☐ UTF-8 BOM (for Excel on Windows)   │
│                                        │
│  Preview Columns:                      │
│  • Front                               │
│  • Back                                │
│  • Current Box                         │
│  • Due Date                            │
│  • Review Count                        │
│  • Lapse Count                         │
│                                        │
│  Filename:                             │
│  academic-vocabulary-2025-10-09.csv    │
│                                        │
│  Estimated size: ~50 KB                │
│                                        │
│         [Cancel]  [Export]             │
└────────────────────────────────────────┘
```

### Export Success Toast
```
┌────────────────────────────────────────┐
│  ✓ Export Complete                     │
│                                        │
│  📥 academic-vocabulary-2025-10-09.csv │
│  120 cards exported                    │
│                                        │
│  File saved to Downloads folder        │
│                                        │
│  [Open File]  [Dismiss]                │
└────────────────────────────────────────┘
```

### Sample CSV Output (Full Export)
```csv
Front,Back,Current Box,Due Date,Review Count,Lapse Count
ubiquitous,"existing or being everywhere, especially at the same time; omnipresent",3,2025-10-15,5,1
pertinent,"relevant to a particular matter; applicable",2,2025-10-12,3,0
meticulous,"showing great attention to detail; very careful and precise",1,2025-10-10,1,0
pragmatic,"dealing with things sensibly and realistically",4,2025-10-20,7,0
articulate,"having or showing the ability to speak fluently and coherently",2,2025-10-13,4,1
comprehensive,"complete; including all or nearly all elements or aspects",5,2025-11-05,12,2
```

### Sample CSV Output (Basic Export)
```csv
Front,Back
ubiquitous,"existing or being everywhere, especially at the same time; omnipresent"
pertinent,"relevant to a particular matter; applicable"
meticulous,"showing great attention to detail; very careful and precise"
pragmatic,"dealing with things sensibly and realistically"
articulate,"having or showing the ability to speak fluently and coherently"
comprehensive,"complete; including all or nearly all elements or aspects"
```

## 11. Testing Scenarios

### Happy Path - CSV Export
1. Export deck with 120 cards to CSV
2. Include SRS data
3. Verify file downloads
4. Open in Excel, verify all data correct
5. Verify special characters preserved

### Happy Path - Excel Export
1. Export deck with 120 cards to Excel
2. Include SRS data
3. Verify .xlsx file downloads
4. Open in Excel, verify formatting (bold headers, auto-filter)
5. Verify columns auto-sized

### Alternative Flows
1. Export without SRS data → Only Front/Back columns
2. Export empty deck → Template with headers only
3. Export with UTF-8 BOM → Opens correctly in Excel on Windows

### Edge Cases
1. Export 1 card → Should succeed
2. Export 10,000 cards → Show progress, ~30s
3. Export with quotes: `"Hello"` → Properly escaped: `"""Hello"""`
4. Export with newlines in card → Preserved in quotes
5. Export with Unicode: "学习" → UTF-8 encoded correctly
6. Export with commas in content → Quoted properly

### Error Cases
1. Export deleted deck → Error: "Deck not found"
2. Export with 0 cards → Warning, offer template
3. File write error → Error message, retry option

## 12. Performance Benchmarks

| Operation | Cards | Target | Max |
|-----------|-------|--------|-----|
| Export to CSV | 100 | < 1s | 2s |
| Export to CSV | 1,000 | < 3s | 5s |
| Export to CSV | 10,000 | < 10s | 20s |
| Export to Excel | 100 | < 2s | 3s |
| Export to Excel | 1,000 | < 5s | 10s |

## 13. Related Use Cases

- **UC-015**: Import Cards from File (reverse operation)
- **UC-013**: Create Deck
- **UC-014**: Create Card
- **UC-019**: Review Cards with SRS

## 14. Acceptance Criteria

- [ ] User can export deck to CSV format
- [ ] User can export deck to Excel (.xlsx) format
- [ ] User can choose to include/exclude SRS data
- [ ] Filename includes deck name and date
- [ ] CSV is RFC 4180 compliant (proper escaping)
- [ ] Excel file has bold headers and auto-filter
- [ ] Special characters preserved (quotes, commas, newlines)
- [ ] Unicode characters supported (UTF-8)
- [ ] UTF-8 BOM option available for Excel compatibility
- [ ] Export completes in < 5s for 1,000 cards
- [ ] Progress shown for large exports (> 10,000 cards)
- [ ] Soft-deleted cards excluded from export
- [ ] Export logged for audit trail
- [ ] File downloads automatically

---

**Version**: 1.0
**Last Updated**: 2025-01
