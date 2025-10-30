# UC-010: Copy Folder

## 1. Brief Description

User creates a deep copy of a folder including all its sub-folders and decks to a new destination. Large copy operations (> 50 items) run asynchronously as background jobs with progress tracking.

## 2. Actors

- **Primary Actor:** Authenticated User
- **Secondary Actor:** Folder Service, Job Runner/Queue

## 3. Preconditions

- User is authenticated with valid access token
- Source folder exists and belongs to user
- Destination folder exists (or root selected) and belongs to user
- Source folder is not soft-deleted
- User can access folder management interface

## 4. Postconditions

### Success Postconditions

- New folder subtree created at destination with independent copies
- All copied folders have new UUIDs (not shared with source)
- Copied folders maintain same hierarchical structure as source
- For sync copy (<= 50 items): response returns new root folder immediately
- For async copy (51-500 items): job_id returned, notification on completion
- User can modify copied folders independently from source
- Success message displayed

### Failure Postconditions

- No folders created
- If async job fails, partial work rolled back (best effort)
- Error message displayed
- User remains on current page

## 5. Main Success Scenario (Basic Flow)

1. User views folder tree
2. User selects folder to copy (e.g., "IELTS Preparation")
3. User initiates copy action:
   - Right-clicks folder and selects "Copy"
   - Clicks three-dot menu and selects "Copy"
4. System displays Copy Folder modal with:
   - Source folder name and item count
   - Destination folder picker
   - Copy mode indicator (sync/async)
5. User selects destination folder (e.g., "Backup" folder)
6. User optionally modifies new folder name (default: "IELTS Preparation (copy)")
7. User clicks "Copy" button
8. System counts total items in source subtree (folders + decks)
9. System determines copy mode:
   - <= 50 items: synchronous copy
   - 51-500 items: asynchronous background job
   - > 500 items: reject with error
10. **For sync mode (<= 50 items):**
    - System starts database transaction
    - System recursively copies folder structure:
      - Create new folder with new UUID
      - Copy all folder metadata (name, description)
      - Set correct parent_id, path, depth at destination
      - Recursively copy all children
    - System commits transaction
    - System returns 200 OK with new folder info
    - UI adds new folder to tree at destination
    - Success message: "Folder copied successfully"
11. **For async mode (51-500 items):**
    - System enqueues background copy job
    - System returns 202 Accepted with job_id
    - UI displays progress indicator with job status
    - Background worker processes copy job:
      - Copies folders in batches
      - Updates job progress (items_processed/total_items)
    - On completion, system sends notification
    - UI refreshes tree to show new folder
    - Success message: "Folder copied successfully"
12. Use case ends (success)

## 6. Alternative Flows

### 6a. Folder Too Large (> 500 items)

**Trigger:** Step 9 - Total items > 500

1. System counts items: 501 folders and decks
2. System returns 400 Bad Request:

   ```json
   {
     "error": "Folder too large",
     "message": "Folder too large to copy (max 500 items). This folder has 501 items."
   }
   ```

3. UI displays error message
4. User must copy smaller folders or use export/import (future)
5. Use case ends (failure)

### 6b. Destination Not Found

**Trigger:** Step 10/11 - Destination folder doesn't exist

1. User selects destination
2. Before copy starts, destination folder deleted by another session
3. System returns 404 Not Found:

   ```json
   {
     "error": "Destination not found",
     "message": "Destination folder does not exist"
   }
   ```

4. UI displays error message
5. Use case ends (failure)

### 6c. Name Conflict at Destination

**Trigger:** Step 10/11 - Folder with same name exists at destination

1. System detects folder "IELTS Preparation" already exists at destination
2. System applies naming policy: append " (copy)" or " (copy 2)", etc.
3. New folder created with name "IELTS Preparation (copy)"
4. Copy proceeds successfully
5. UI shows success with actual name used

**Alternative:** Return 400 and ask user to provide different name

### 6d. Max Depth Exceeded After Copy

**Trigger:** Step 10/11 - Copying would exceed depth limit

1. Source folder depth 3 with subtree extending to depth 8 (5 levels deep)
2. Destination at depth 6
3. After copy: subtree would be depth 6+5 = 11 (exceeds limit 10)
4. System returns 400 Bad Request:

   ```json
   {
     "error": "Max depth exceeded",
     "message": "Copying this folder would exceed maximum depth (10 levels)"
   }
   ```

5. UI displays error message
6. User must choose shallower destination
7. Use case ends (failure)

### 6e. Async Job Failure

**Trigger:** Step 11 - Background job encounters error

1. Job starts processing copy
2. Database error occurs (connection lost, disk full, etc.)
3. Job marks status as FAILED
4. System attempts to rollback partial copies (best effort)
5. System sends failure notification to user
6. UI displays error: "Copy failed. Please try again later."
7. Use case ends (failure)

### 6f. User Cancels Copy (Async)

**Trigger:** During step 11 - User cancels running job

1. User clicks "Cancel" on progress indicator
2. Client sends cancel request to job service
3. Job service marks job as CANCELLED
4. Worker stops processing at next checkpoint
5. Partial copies may remain (user can delete manually)
6. UI displays info: "Copy cancelled"
7. Use case ends (cancelled)

### 6g. Session Expired During Copy

**Trigger:** Step 10/11 - Access token expired

1. User initiates copy
2. Token expired
3. Backend returns 401 Unauthorized
4. Client refreshes token automatically (UC-003)
5. Client retries copy request
6. Copy proceeds successfully
7. Continue to step 12 (Main Flow)

### 6h. Network Error

**Trigger:** Step 10/11 - Network failure

1. Client sends copy request
2. Network error (no connection, timeout)
3. Request fails
4. UI displays error: "Network error. Please try again."
5. User can retry
6. Use case ends (failure)

### 6i. Database Transaction Failure (Sync)

**Trigger:** Step 10 - Database error during sync copy

1. System starts transaction
2. Partially copies some folders
3. Database error occurs
4. Transaction rolled back
5. No folders created (atomic operation)
6. System returns 500 Internal Server Error
7. UI displays error: "Failed to copy folder. Please try again."
8. Use case ends (failure)

## 7. Special Requirements

### 7.1 Performance

- Sync copy (<= 50 items): Response time < 5 seconds
- Async copy (51-500 items): Job completion < 5 minutes
- Progress updates: Every 10% or 10 items processed
- Use batch processing for async copies (10-20 items per batch)

### 7.2 Data Integrity

- Sync copy: Atomic transaction (all or nothing)
- Async copy: Best-effort rollback on failure
- All copied items get new UUIDs (independent from source)
- Maintain hierarchical relationships in copied structure

### 7.3 Naming Policy

- Default: Append " (copy)" to folder name
- If exists: Append " (copy 2)", " (copy 3)", etc.
- User can override name before copying
- Preserve child folder names (only root renamed)

### 7.4 Progress Tracking

- Async jobs: Track items_processed and total_items
- Real-time progress updates via WebSocket or polling
- Notification on completion (in-app or email)
- Job status: QUEUED, RUNNING, COMPLETED, FAILED, CANCELLED

## 8. Technology and Data Variations

### 8.1 Recursive Copy Algorithm

```typescript
async function copyFolder(
  sourceFolderId: string,
  destinationParentId: string | null,
  userId: string
): Promise<string> {
  // Get source folder
  const source = await getFolderById(sourceFolderId);

  // Create new folder at destination
  const newFolder = await createFolder({
    userId,
    name: generateUniqueName(source.name, destinationParentId),
    description: source.description,
    parentId: destinationParentId
  });

  // Copy all children recursively
  const children = await getChildFolders(sourceFolderId);
  for (const child of children) {
    await copyFolder(child.id, newFolder.id, userId);
  }

  // Copy all decks in this folder
  await copyDecks(sourceFolderId, newFolder.id, userId);

  return newFolder.id;
}
```

### 8.2 Async Job Structure

```typescript
interface CopyJob {
  id: string;
  userId: string;
  sourceFolderId: string;
  destinationFolderId: string | null;
  status: 'QUEUED' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  totalItems: number;
  itemsProcessed: number;
  createdAt: Date;
  startedAt?: Date;
  completedAt?: Date;
  error?: string;
}
```

### 8.3 Name Conflict Resolution

```typescript
function generateUniqueName(
  baseName: string,
  destinationParentId: string | null
): string {
  let name = `${baseName} (copy)`;
  let counter = 2;

  while (nameExists(name, destinationParentId)) {
    name = `${baseName} (copy ${counter})`;
    counter++;
  }

  return name;
}
```

## 9. Frequency of Occurrence

- Expected: 5-15 folder copies per day (MVP phase)
- Peak: 30-50 folder copies per day (post-launch)
- Per user: 0-5 folder copies per month (occasional backup/reorganization)

## 10. Open Issues

- **Conflict resolution:** Let user choose rename strategy (append/replace/skip)
- **Selective copy:** Copy only specific subfolders (future)
- **Cross-user copy:** Share folders between users (future)
- **Copy with filters:** Copy only folders matching criteria (future)

## 11. Related Use Cases

- [UC-007: Create Folder](UC-007-create-folder.md) - Create new folders during copy
- [UC-009: Move Folder](UC-009-move-folder.md) - Move vs copy folder
- [UC-011: Delete Folder](UC-011-delete-folder.md) - Delete copied folders
- [UC-016: Copy Deck](UC-016-copy-deck.md) - Copy decks within folders

## 12. Business Rules References

- **BR-COPY-01:** Sync copy if <= 50 items
- **BR-COPY-02:** Async copy if 51-500 items
- **BR-COPY-03:** Reject if > 500 items
- **BR-FOLD-01:** Max depth = 10 (applies to copied structure)

## 13. UI Mockup Notes

### Copy Modal

```
┌─────────────────────────────────────────┐
│ Copy Folder                          [X]│
├─────────────────────────────────────────┤
│                                         │
│ Source: IELTS Preparation (45 items)   │
│                                         │
│ New name:                               │
│ ┌─────────────────────────────────┐    │
│ │ IELTS Preparation (copy)        │    │
│ └─────────────────────────────────┘    │
│                                         │
│ Destination:                            │
│ ┌─────────────────────────────────┐    │
│ │ ● Root                          │    │
│ │ ○ Backup                        │ ← Select
│ │ ○ Archive                       │    │
│ └─────────────────────────────────┘    │
│                                         │
│ ⓘ This will copy 45 items (sync mode)  │
│                                         │
│                    [Cancel]  [Copy Here]│
└─────────────────────────────────────────┘
```

### Async Progress

```
┌─────────────────────────────────────────┐
│ Copying "IELTS Preparation"...          │
│                                         │
│ [████████████░░░░░░░░] 60% (120/200)   │
│                                         │
│ Estimated time: 2 minutes               │
│                                         │
│                              [Cancel]   │
└─────────────────────────────────────────┘
```

## 14. API Endpoint

```http
POST /api/folders/{folderId}/copy
```

**Request Body:**

```json
{
  "destinationFolderId": "uuid-backup",
  "newName": "IELTS Preparation (copy)",
  "renamePolicy": "appendCopySuffix"
}
```

**Success Response (200 OK - Sync):**

```json
{
  "id": "uuid-new",
  "name": "IELTS Preparation (copy)",
  "parentId": "uuid-backup",
  "path": "|uuid-backup|uuid-new|",
  "depth": 2,
  "itemsCopied": 45,
  "mode": "sync"
}
```

**Success Response (202 Accepted - Async):**

```json
{
  "jobId": "job-uuid",
  "status": "QUEUED",
  "totalItems": 200,
  "mode": "async",
  "message": "Copy job started. You will be notified when complete."
}
```

**Job Status Endpoint:**

```http
GET /api/jobs/{jobId}
```

**Job Status Response:**

```json
{
  "id": "job-uuid",
  "status": "RUNNING",
  "totalItems": 200,
  "itemsProcessed": 120,
  "progress": 60,
  "estimatedTimeRemaining": 120
}
```

**Error Responses:**

400 Bad Request - Too large:

```json
{
  "error": "Folder too large",
  "message": "Folder too large to copy (max 500 items). This folder has 501 items."
}
```

400 Bad Request - Max depth:

```json
{
  "error": "Max depth exceeded",
  "message": "Copying this folder would exceed maximum depth (10 levels)"
}
```

404 Not Found:

```json
{
  "error": "Not found",
  "message": "Source or destination folder does not exist"
}
```

## 15. Test Cases

### TC-010-001: Sync Copy Small Folder (< 50 items)

- **Given:** User has folder with 10 items
- **When:** User copies to destination
- **Then:** Folder copied immediately, new folder appears at destination

### TC-010-002: Async Copy Large Folder (51-500 items)

- **Given:** User has folder with 200 items
- **When:** User initiates copy
- **Then:** Job created, progress tracked, notification on completion

### TC-010-003: Reject Too Large Folder (> 500 items)

- **Given:** User has folder with 501 items
- **When:** User attempts to copy
- **Then:** 400 error with message "Folder too large to copy"

### TC-010-004: Name Conflict Resolution

- **Given:** Destination has folder "IELTS Preparation"
- **When:** User copies folder with same name
- **Then:** New folder created as "IELTS Preparation (copy)"

### TC-010-005: Max Depth Exceeded

- **Given:** Copying would result in depth 11
- **When:** User attempts copy
- **Then:** 400 error with depth exceeded message

### TC-010-006: Copy Maintains Hierarchy

- **Given:** Source has structure A > B > C
- **When:** User copies A
- **Then:** Destination has same structure A(copy) > B > C

### TC-010-007: Copy Empty Folder

- **Given:** User has empty folder with no children
- **When:** User copies folder
- **Then:** Empty folder copied successfully

### TC-010-008: Cancel Async Copy

- **Given:** Async copy job is running
- **When:** User clicks Cancel
- **Then:** Job cancelled, partial copies may remain

### TC-010-009: Async Job Failure

- **Given:** Async copy job encounters database error
- **When:** Job is processing
- **Then:** Job marked as FAILED, notification sent to user

### TC-010-010: Session Expired During Copy

- **Given:** User's token expires
- **When:** User initiates copy
- **Then:** Token refreshed, copy succeeds

## 16. Database Operations

### Copy Folder Transaction (Sync)

```sql
BEGIN TRANSACTION;

-- Create new root folder
INSERT INTO folders (id, user_id, name, description, parent_id, path, depth)
VALUES (new_uuid, user_id, 'IELTS (copy)', description, dest_parent_id, new_path, new_depth);

-- Copy children recursively (application layer handles recursion)
-- Each child INSERT statement...

COMMIT;
```

### Job Status Tracking

```sql
CREATE TABLE copy_jobs (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  source_folder_id UUID NOT NULL,
  destination_folder_id UUID,
  status VARCHAR(20) NOT NULL,
  total_items INT NOT NULL,
  items_processed INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  started_at TIMESTAMP,
  completed_at TIMESTAMP,
  error TEXT
);
```

## 17. Future Enhancements

- **Selective copy:** Choose specific subfolders to copy
- **Copy with filters:** Copy only folders matching criteria
- **Copy across users:** Share/copy folders to other users
- **Incremental copy:** Copy only new/modified items
- **Copy templates:** Save and reuse copy configurations
- **Batch copy:** Copy multiple folders in single operation
- **Copy preview:** Show what will be copied before confirming
- **Smart naming:** AI-suggested names based on context
