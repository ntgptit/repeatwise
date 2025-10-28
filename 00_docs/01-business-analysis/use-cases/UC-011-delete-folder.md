# UC-011: Delete Folder

## 1. Brief Description

User deletes a folder and its entire subtree. Deletion uses soft delete to allow recovery within a grace period.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Folder Service

## 3. Preconditions

- User is authenticated
- Target folder exists and belongs to the user

## 4. Postconditions

### Success Postconditions

- Folder and all descendants marked as soft-deleted (`deleted_at` set)
- UI updates to remove the subtree from active view

### Failure Postconditions

- No deletion performed
- Error displayed

## 5. Main Success Scenario (Basic Flow)

1. User selects a folder and clicks “Delete”
2. System shows confirmation dialog: "Delete this folder and all contents?"
3. User confirms
4. System performs soft delete for the folder and all descendants in a transaction
5. System returns 200 OK with success message
6. UI removes the subtree from active list

## 6. Alternative Flows

### 6a. User Cancels Deletion

Trigger: Step 3

1. User cancels the confirmation dialog
2. No changes made

### 6b. Folder Not Found / Forbidden

1. Folder id invalid or belongs to another user
2. Return 404/403

### 6c. Permanent Cleanup

1. Background job permanently deletes records after 30 days (outside this use case)

## 7. Special Requirements

- Confirm dialog must indicate that sub-folders and decks will also be deleted
- Provide restore option in Trash (future use case)

## 8. Business Rules / Constraints

- BR-DEL-01: Soft delete using `deleted_at`
- BR-DEL-02: Permanent cleanup after 30 days

## 9. Frequency of Occurrence

- Infrequent

## 10. Open Issues

- Trash/restore flow defined in future scope

## 11. Related Use Cases

- UC-009: Move Folder
- UC-010: Copy Folder

## 12. Business Rules References

- BR-DEL-01..02

## 13. UI Mockup Notes

- Confirmation dialog lists number of decks/cards (if available)

## 14. API Endpoint

```
DELETE /api/folders/{folderId}
```

Success (200):

```json
{ "message": "Folder deleted (soft delete)" }
```

Errors:

- 404 not found
- 403 forbidden

## 15. Test Cases

- TC-011-001: Delete simple folder -> success
- TC-011-002: Delete deep subtree -> success
- TC-011-003: Cancel at confirm -> no change
- TC-011-004: Not found -> 404
