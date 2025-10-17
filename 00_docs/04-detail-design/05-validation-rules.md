# Validation Rules - RepeatWise MVP

## Document Overview

This document provides comprehensive validation rules for RepeatWise MVP, organized into three levels: Field-Level (syntax), Entity-Level (structure), and Business Logic (semantics).

**Purpose**: Define all validation rules in pseudo-code format suitable for implementation across frontend and backend systems.

**Target Audience**: Backend developers, frontend developers, QA engineers

**Related Documents**:
- [Entity Specifications](./01-entity-specifications.md) - Database field constraints
- [API Request/Response Specs](./02-api-request-response-specs.md) - API validation requirements
- [Business Logic Flows](./03-business-logic-flows.md) - Business rule implementations
- [Error Handling Specs](./06-error-handling-specs.md) - Error codes and responses

---

## Table of Contents

1. [Field-Level Validations](#1-field-level-validations)
2. [Entity-Level Validations](#2-entity-level-validations)
3. [Business Logic Validations](#3-business-logic-validations)
4. [Validation Execution Order](#4-validation-execution-order)
5. [Error Response Format](#5-error-response-format)

---

## 1. Field-Level Validations

Field-level validations check individual field syntax, format, and constraints.

### 1.1 User Entity

#### Field: email
```
Field: email
Type: String
Constraints: NOT NULL, UNIQUE, valid email format
Maximum Length: 255 characters

Validation Logic:
  IF email IS NULL OR TRIM(email) IS EMPTY THEN
    ERROR "Email is required"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  IF LENGTH(email) > 255 THEN
    ERROR "Email cannot exceed 255 characters"
    ERROR_CODE: FIELD_TOO_LONG
    HTTP_STATUS: 400
  END IF

  emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"
  IF NOT MATCHES(email, emailRegex) THEN
    ERROR "Invalid email format"
    ERROR_CODE: INVALID_FORMAT
    HTTP_STATUS: 400
  END IF

  // Normalize email to lowercase for uniqueness check
  email = LOWERCASE(TRIM(email))
```

#### Field: password (at registration)
```
Field: password
Type: String (plain text during validation, hashed for storage)
Constraints: NOT NULL, minimum 8 characters
Maximum Length: 128 characters

Validation Logic:
  IF password IS NULL OR TRIM(password) IS EMPTY THEN
    ERROR "Password is required"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  // Note: Do NOT trim password (preserve whitespace)
  IF LENGTH(password) < 8 THEN
    ERROR "Password must be at least 8 characters"
    ERROR_CODE: FIELD_TOO_SHORT
    HTTP_STATUS: 400
  END IF

  IF LENGTH(password) > 128 THEN
    ERROR "Password cannot exceed 128 characters"
    ERROR_CODE: FIELD_TOO_LONG
    HTTP_STATUS: 400
  END IF
```

#### Field: fullName
```
Field: fullName
Type: String
Constraints: NOT NULL, trimmed
Maximum Length: 100 characters

Validation Logic:
  IF fullName IS NULL THEN
    ERROR "Name is required"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  fullName = TRIM(fullName)

  IF fullName IS EMPTY THEN
    ERROR "Name cannot be empty or whitespace only"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  IF LENGTH(fullName) > 100 THEN
    ERROR "Name cannot exceed 100 characters"
    ERROR_CODE: FIELD_TOO_LONG
    HTTP_STATUS: 400
  END IF
```

#### Field: timezone
```
Field: timezone
Type: String
Constraints: NOT NULL, valid timezone identifier
Default: 'Asia/Ho_Chi_Minh'

Validation Logic:
  IF timezone IS NULL OR TRIM(timezone) IS EMPTY THEN
    timezone = 'Asia/Ho_Chi_Minh'  // Set default
  END IF

  // Validate against IANA timezone database
  IF NOT isValidTimezone(timezone) THEN
    ERROR "Invalid timezone identifier"
    ERROR_CODE: INVALID_FORMAT
    HTTP_STATUS: 400
  END IF
```

#### Field: language
```
Field: language
Type: Enum
Constraints: NOT NULL, must be valid enum value
Valid Values: VI, EN
Default: VI

Validation Logic:
  IF language IS NULL THEN
    language = 'VI'  // Set default
  END IF

  validLanguages = ['VI', 'EN']
  IF language NOT IN validLanguages THEN
    ERROR "Invalid language. Must be one of: VI, EN"
    ERROR_CODE: INVALID_ENUM
    HTTP_STATUS: 400
  END IF
```

#### Field: theme
```
Field: theme
Type: Enum
Constraints: NOT NULL, must be valid enum value
Valid Values: LIGHT, DARK, SYSTEM
Default: SYSTEM

Validation Logic:
  IF theme IS NULL THEN
    theme = 'SYSTEM'  // Set default
  END IF

  validThemes = ['LIGHT', 'DARK', 'SYSTEM']
  IF theme NOT IN validThemes THEN
    ERROR "Invalid theme. Must be one of: LIGHT, DARK, SYSTEM"
    ERROR_CODE: INVALID_ENUM
    HTTP_STATUS: 400
  END IF
```

---

### 1.2 Folder Entity

#### Field: name
```
Field: name
Type: String
Constraints: NOT NULL, trimmed, unique within parent
Maximum Length: 100 characters

Validation Logic:
  IF name IS NULL THEN
    ERROR "Folder name is required"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  name = TRIM(name)

  IF name IS EMPTY THEN
    ERROR "Folder name cannot be empty or whitespace only"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  IF LENGTH(name) > 100 THEN
    ERROR "Folder name cannot exceed 100 characters"
    ERROR_CODE: FIELD_TOO_LONG
    HTTP_STATUS: 400
  END IF
```

#### Field: description
```
Field: description
Type: String
Constraints: nullable
Maximum Length: 500 characters

Validation Logic:
  IF description IS NOT NULL THEN
    IF LENGTH(description) > 500 THEN
      ERROR "Folder description cannot exceed 500 characters"
      ERROR_CODE: FIELD_TOO_LONG
      HTTP_STATUS: 400
    END IF
  END IF
```

#### Field: parentFolderId
```
Field: parentFolderId
Type: UUID
Constraints: nullable (null = root level), must reference existing folder

Validation Logic:
  IF parentFolderId IS NOT NULL THEN
    uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    IF NOT MATCHES(parentFolderId, uuidRegex) THEN
      ERROR "Parent folder ID must be a valid UUID"
      ERROR_CODE: INVALID_FORMAT
      HTTP_STATUS: 400
    END IF
  END IF
  // Note: Existence check happens at entity level
```

#### Field: depth
```
Field: depth
Type: Integer
Constraints: NOT NULL, range 0 to 10
Auto-calculated: depth = parent.depth + 1 (or 0 if root)

Validation Logic:
  IF depth IS NULL THEN
    ERROR "Folder depth is required"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  IF depth < 0 OR depth > 10 THEN
    ERROR "Folder depth must be between 0 and 10"
    ERROR_CODE: INVALID_RANGE
    HTTP_STATUS: 400
  END IF
```

---

### 1.3 Deck Entity

#### Field: name
```
Field: name
Type: String
Constraints: NOT NULL, trimmed, unique within parent folder
Maximum Length: 100 characters

Validation Logic:
  IF name IS NULL THEN
    ERROR "Deck name is required"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  name = TRIM(name)

  IF name IS EMPTY THEN
    ERROR "Deck name cannot be empty or whitespace only"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  IF LENGTH(name) > 100 THEN
    ERROR "Deck name cannot exceed 100 characters"
    ERROR_CODE: FIELD_TOO_LONG
    HTTP_STATUS: 400
  END IF
```

#### Field: description
```
Field: description
Type: String
Constraints: nullable
Maximum Length: 500 characters

Validation Logic:
  IF description IS NOT NULL THEN
    IF LENGTH(description) > 500 THEN
      ERROR "Deck description cannot exceed 500 characters"
      ERROR_CODE: FIELD_TOO_LONG
      HTTP_STATUS: 400
    END IF
  END IF
```

#### Field: folderId
```
Field: folderId
Type: UUID
Constraints: nullable (null = root level), must reference existing folder

Validation Logic:
  IF folderId IS NOT NULL THEN
    uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    IF NOT MATCHES(folderId, uuidRegex) THEN
      ERROR "Folder ID must be a valid UUID"
      ERROR_CODE: INVALID_FORMAT
      HTTP_STATUS: 400
    END IF
  END IF
  // Note: Existence check happens at entity level
```

---

### 1.4 Card Entity

#### Field: front
```
Field: front
Type: String (plain text in MVP)
Constraints: NOT NULL, trimmed, not empty
Maximum Length: 5000 characters

Validation Logic:
  IF front IS NULL THEN
    ERROR "Card front is required"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  front = TRIM(front)

  IF front IS EMPTY THEN
    ERROR "Card front cannot be empty or whitespace only"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  IF LENGTH(front) > 5000 THEN
    ERROR "Card front cannot exceed 5000 characters"
    ERROR_CODE: FIELD_TOO_LONG
    HTTP_STATUS: 400
  END IF
```

#### Field: back
```
Field: back
Type: String (plain text in MVP)
Constraints: NOT NULL, trimmed, not empty
Maximum Length: 5000 characters

Validation Logic:
  IF back IS NULL THEN
    ERROR "Card back is required"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  back = TRIM(back)

  IF back IS EMPTY THEN
    ERROR "Card back cannot be empty or whitespace only"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  IF LENGTH(back) > 5000 THEN
    ERROR "Card back cannot exceed 5000 characters"
    ERROR_CODE: FIELD_TOO_LONG
    HTTP_STATUS: 400
  END IF
```

---

### 1.5 Review Entity

#### Field: cardId
```
Field: cardId
Type: UUID
Constraints: NOT NULL, must reference existing card

Validation Logic:
  IF cardId IS NULL THEN
    ERROR "Card ID is required"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
  IF NOT MATCHES(cardId, uuidRegex) THEN
    ERROR "Card ID must be a valid UUID"
    ERROR_CODE: INVALID_FORMAT
    HTTP_STATUS: 400
  END IF
  // Note: Existence check happens at entity level
```

#### Field: rating
```
Field: rating
Type: Enum
Constraints: NOT NULL, must be valid rating
Valid Values: AGAIN, HARD, GOOD, EASY

Validation Logic:
  IF rating IS NULL THEN
    ERROR "Rating is required"
    ERROR_CODE: FIELD_REQUIRED
    HTTP_STATUS: 400
  END IF

  validRatings = ['AGAIN', 'HARD', 'GOOD', 'EASY']
  IF rating NOT IN validRatings THEN
    ERROR "Invalid rating. Must be one of: AGAIN, HARD, GOOD, EASY"
    ERROR_CODE: INVALID_ENUM
    HTTP_STATUS: 400
  END IF
```

---

### 1.6 SRS Settings Entity

#### Field: reviewOrder
```
Field: reviewOrder
Type: Enum
Constraints: NOT NULL, must be valid review order
Valid Values: ASCENDING, DESCENDING, RANDOM
Default: RANDOM

Validation Logic:
  IF reviewOrder IS NULL THEN
    reviewOrder = 'RANDOM'  // Set default
  END IF

  validOrders = ['ASCENDING', 'DESCENDING', 'RANDOM']
  IF reviewOrder NOT IN validOrders THEN
    ERROR "Invalid review order. Must be one of: ASCENDING, DESCENDING, RANDOM"
    ERROR_CODE: INVALID_ENUM
    HTTP_STATUS: 400
  END IF
```

#### Field: notificationEnabled
```
Field: notificationEnabled
Type: Boolean
Constraints: NOT NULL
Default: true

Validation Logic:
  IF notificationEnabled IS NULL THEN
    notificationEnabled = TRUE  // Set default
  END IF

  IF notificationEnabled IS NOT BOOLEAN THEN
    ERROR "Notification enabled must be true or false"
    ERROR_CODE: INVALID_FORMAT
    HTTP_STATUS: 400
  END IF
```

#### Field: notificationTime
```
Field: notificationTime
Type: Time
Constraints: NOT NULL, valid time format (HH:MM)
Default: '09:00'

Validation Logic:
  IF notificationTime IS NULL THEN
    notificationTime = '09:00'  // Set default
  END IF

  timeRegex = "^([01]\\d|2[0-3]):([0-5]\\d)$"
  IF NOT MATCHES(notificationTime, timeRegex) THEN
    ERROR "Invalid time format. Use HH:MM (e.g., 09:00)"
    ERROR_CODE: INVALID_FORMAT
    HTTP_STATUS: 400
  END IF
```

#### Field: forgottenCardAction
```
Field: forgottenCardAction
Type: Enum
Constraints: NOT NULL, must be valid action
Valid Values: MOVE_TO_BOX_1, MOVE_DOWN_N_BOXES, STAY_IN_BOX
Default: MOVE_TO_BOX_1

Validation Logic:
  IF forgottenCardAction IS NULL THEN
    forgottenCardAction = 'MOVE_TO_BOX_1'  // Set default
  END IF

  validActions = ['MOVE_TO_BOX_1', 'MOVE_DOWN_N_BOXES', 'STAY_IN_BOX']
  IF forgottenCardAction NOT IN validActions THEN
    ERROR "Invalid forgotten card action"
    ERROR_CODE: INVALID_ENUM
    HTTP_STATUS: 400
  END IF
```

#### Field: moveDownBoxes
```
Field: moveDownBoxes
Type: Integer
Constraints: required if forgottenCardAction = MOVE_DOWN_N_BOXES, range 1-3
Default: 1

Validation Logic:
  IF forgottenCardAction = 'MOVE_DOWN_N_BOXES' THEN
    IF moveDownBoxes IS NULL THEN
      ERROR "Move down boxes is required when using MOVE_DOWN_N_BOXES action"
      ERROR_CODE: FIELD_REQUIRED
      HTTP_STATUS: 400
    END IF

    IF moveDownBoxes < 1 OR moveDownBoxes > 3 THEN
      ERROR "Move down boxes must be between 1 and 3"
      ERROR_CODE: INVALID_RANGE
      HTTP_STATUS: 400
    END IF
  END IF
```

#### Field: newCardsPerDay
```
Field: newCardsPerDay
Type: Integer
Constraints: NOT NULL, range 1-100
Default: 20

Validation Logic:
  IF newCardsPerDay IS NULL THEN
    newCardsPerDay = 20  // Set default
  END IF

  IF newCardsPerDay < 1 OR newCardsPerDay > 100 THEN
    ERROR "New cards per day must be between 1 and 100"
    ERROR_CODE: INVALID_RANGE
    HTTP_STATUS: 400
  END IF
```

#### Field: maxReviewsPerDay
```
Field: maxReviewsPerDay
Type: Integer
Constraints: NOT NULL, range 1-500
Default: 200

Validation Logic:
  IF maxReviewsPerDay IS NULL THEN
    maxReviewsPerDay = 200  // Set default
  END IF

  IF maxReviewsPerDay < 1 OR maxReviewsPerDay > 500 THEN
    ERROR "Max reviews per day must be between 1 and 500"
    ERROR_CODE: INVALID_RANGE
    HTTP_STATUS: 400
  END IF
```

---

## 2. Entity-Level Validations

Entity-level validations check cross-field constraints and structural integrity.

### 2.1 User Entity

#### Validation: Email Uniqueness
```
Entity: User
Validation: Email must be unique across all users
Applies to: Registration, Profile Update (email change)

Validation Logic:
  FUNCTION validateEmailUniqueness(email, currentUserId):
    normalizedEmail = LOWERCASE(TRIM(email))

    existingUser = userRepository.findByEmail(normalizedEmail)

    IF existingUser IS NOT NULL THEN
      // For update: allow if it's the same user
      IF currentUserId IS NOT NULL AND existingUser.id = currentUserId THEN
        RETURN VALID
      END IF

      ERROR "Email already registered"
      ERROR_CODE: DUPLICATE_RESOURCE
      HTTP_STATUS: 409
    END IF

    RETURN VALID
  END FUNCTION
```

---

### 2.2 RefreshToken Entity

#### Validation: Token Not Expired and Not Revoked
```
Entity: RefreshToken
Validation: Token must not be expired AND not revoked
Applies to: Token refresh, Token validation

Validation Logic:
  FUNCTION validateRefreshToken(token):
    IF token IS NULL THEN
      ERROR "Refresh token is required"
      ERROR_CODE: FIELD_REQUIRED
      HTTP_STATUS: 401
    END IF

    IF token.expiresAt < NOW() THEN
      ERROR "Refresh token expired"
      ERROR_CODE: TOKEN_EXPIRED
      HTTP_STATUS: 401
    END IF

    IF token.revokedAt IS NOT NULL THEN
      ERROR "Refresh token revoked"
      ERROR_CODE: TOKEN_REVOKED
      HTTP_STATUS: 401
    END IF

    RETURN VALID
  END FUNCTION
```

---

### 2.3 Folder Entity

#### Validation: Name Unique Within Parent
```
Entity: Folder
Validation: Folder name must be unique within same parent (per user)
Applies to: Create folder, Rename folder, Move folder

Validation Logic:
  FUNCTION validateFolderNameUniqueness(userId, parentFolderId, name, currentFolderId):
    normalizedName = TRIM(name)

    existingFolder = folderRepository.findByUserIdAndParentFolderIdAndNameAndDeletedAtIsNull(
      userId,
      parentFolderId,
      normalizedName
    )

    IF existingFolder IS NOT NULL THEN
      // For update: allow if it's the same folder
      IF currentFolderId IS NOT NULL AND existingFolder.id = currentFolderId THEN
        RETURN VALID
      END IF

      ERROR "A folder with this name already exists in this location"
      ERROR_CODE: DUPLICATE_NAME
      HTTP_STATUS: 409
    END IF

    RETURN VALID
  END FUNCTION
```

#### Validation: Parent Folder Exists
```
Entity: Folder
Validation: Parent folder must exist and belong to user
Applies to: Create folder, Move folder

Validation Logic:
  FUNCTION validateParentFolder(userId, parentFolderId):
    IF parentFolderId IS NULL THEN
      RETURN VALID  // Root level folder
    END IF

    parentFolder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(
      parentFolderId,
      userId
    )

    IF parentFolder IS NULL THEN
      ERROR "Parent folder not found"
      ERROR_CODE: NOT_FOUND
      HTTP_STATUS: 404
    END IF

    RETURN VALID
  END FUNCTION
```

---

### 2.4 Deck Entity

#### Validation: Name Unique Within Folder
```
Entity: Deck
Validation: Deck name must be unique within same folder (per user)
Applies to: Create deck, Rename deck, Move deck

Validation Logic:
  FUNCTION validateDeckNameUniqueness(userId, folderId, name, currentDeckId):
    normalizedName = TRIM(name)

    existingDeck = deckRepository.findByUserIdAndFolderIdAndNameAndDeletedAtIsNull(
      userId,
      folderId,
      normalizedName
    )

    IF existingDeck IS NOT NULL THEN
      // For update: allow if it's the same deck
      IF currentDeckId IS NOT NULL AND existingDeck.id = currentDeckId THEN
        RETURN VALID
      END IF

      ERROR "A deck with this name already exists in this folder"
      ERROR_CODE: DUPLICATE_NAME
      HTTP_STATUS: 409
    END IF

    RETURN VALID
  END FUNCTION
```

---

### 2.5 Card Entity

#### Validation: Deck Exists and Belongs to User
```
Entity: Card
Validation: Deck must exist and belong to authenticated user
Applies to: Create card, Import cards

Validation Logic:
  FUNCTION validateCardDeck(userId, deckId):
    IF deckId IS NULL THEN
      ERROR "Deck ID is required"
      ERROR_CODE: FIELD_REQUIRED
      HTTP_STATUS: 400
    END IF

    deck = deckRepository.findByIdAndUserIdAndDeletedAtIsNull(
      deckId,
      userId
    )

    IF deck IS NULL THEN
      ERROR "Deck not found"
      ERROR_CODE: NOT_FOUND
      HTTP_STATUS: 404
    END IF

    RETURN VALID
  END FUNCTION
```

---

## 3. Business Logic Validations

Business logic validations enforce domain rules and complex constraints.

### BR-001: Max Folder Depth (10 Levels)

```
Business Rule: BR-001
Name: Max Folder Depth
Description: Folder tree maximum 10 levels (depth 0-9)
Applies to: Create folder, Move folder

Validation Logic:
  FUNCTION validateFolderDepth(parentFolder, action, folder):
    /**
     * Validates folder depth constraint
     *
     * @param parentFolder: Parent folder entity (null if root)
     * @param action: "CREATE" or "MOVE"
     * @param folder: Folder to be created/moved (only for MOVE)
     * @return void (throws exception if invalid)
     */

    // Calculate new depth
    IF parentFolder IS NULL THEN
      newDepth = 0  // Root level
    ELSE
      newDepth = parentFolder.depth + 1
    END IF

    // For MOVE: include descendant depth
    IF action = "MOVE" THEN
      maxSubDepth = getMaxDescendantDepth(folder)
      subFolderDepth = maxSubDepth - folder.depth
      newDepth = newDepth + subFolderDepth
    END IF

    // Check constraint
    IF newDepth > 9 THEN
      ERROR "Cannot exceed 10 folder levels (depth 0-9)"
      ERROR_CODE: MAX_DEPTH_EXCEEDED
      HTTP_STATUS: 422
      DETAIL: {
        currentDepth: newDepth,
        maxDepth: 9,
        action: action
      }
    END IF

    RETURN VALID
  END FUNCTION

  FUNCTION getMaxDescendantDepth(folder):
    /**
     * Recursively find maximum depth among all descendants
     */
    maxDepth = folder.depth

    descendants = folderRepository.findDescendants(folder.id)

    FOR EACH descendant IN descendants DO
      IF descendant.depth > maxDepth THEN
        maxDepth = descendant.depth
      END IF
    END FOR

    RETURN maxDepth
  END FUNCTION
```

**Error Code**: MAX_DEPTH_EXCEEDED
**HTTP Status**: 422 Unprocessable Entity

---

### BR-002: Folder Name Unique in Parent

```
Business Rule: BR-002
Name: Folder Name Unique in Parent
Description: Folder name must be unique within same parent folder (per user)
Applies to: Create folder, Rename folder, Move folder

Validation Logic:
  FUNCTION validateFolderNameUniquenessInParent(userId, parentFolderId, name, currentFolderId):
    /**
     * Ensures no duplicate folder names within same parent
     * Case-insensitive comparison
     *
     * @param userId: Current user ID
     * @param parentFolderId: Parent folder ID (null = root)
     * @param name: Folder name to validate
     * @param currentFolderId: ID of folder being updated (null for create)
     */

    normalizedName = TRIM(LOWERCASE(name))

    // Query for existing folder with same name in same parent
    existingFolder = folderRepository.findByUserIdAndParentFolderIdAndNameIgnoreCaseAndDeletedAtIsNull(
      userId,
      parentFolderId,
      normalizedName
    )

    IF existingFolder IS NOT NULL THEN
      // Allow if updating same folder
      IF currentFolderId IS NOT NULL AND existingFolder.id = currentFolderId THEN
        RETURN VALID
      END IF

      ERROR "A folder with this name already exists in this location"
      ERROR_CODE: DUPLICATE_NAME
      HTTP_STATUS: 409
    END IF

    RETURN VALID
  END FUNCTION
```

**Error Code**: DUPLICATE_NAME
**HTTP Status**: 409 Conflict

---

### BR-003: Circular Reference Prevention (Move Folder)

```
Business Rule: BR-003
Name: Circular Reference Prevention
Description: Cannot move folder into itself or its descendants
Applies to: Move folder

Validation Logic:
  FUNCTION validateNoCircularReference(sourceFolder, targetParentFolder):
    /**
     * Prevents moving folder into itself or any of its descendants
     * Uses materialized path for efficient checking
     *
     * @param sourceFolder: Folder being moved
     * @param targetParentFolder: New parent folder (null = root)
     */

    IF targetParentFolder IS NULL THEN
      RETURN VALID  // Moving to root is always safe
    END IF

    // Check if moving to itself
    IF sourceFolder.id = targetParentFolder.id THEN
      ERROR "Cannot move folder into itself"
      ERROR_CODE: CIRCULAR_REFERENCE
      HTTP_STATUS: 409
    END IF

    // Check if target is a descendant of source
    // Using materialized path: descendant.path STARTS WITH source.path
    IF targetParentFolder.path STARTS WITH sourceFolder.path THEN
      ERROR "Cannot move folder into its own descendant"
      ERROR_CODE: CIRCULAR_REFERENCE
      HTTP_STATUS: 409
    END IF

    RETURN VALID
  END FUNCTION
```

**Error Code**: CIRCULAR_REFERENCE
**HTTP Status**: 409 Conflict

---

### BR-004: Copy Folder Size Limit (<=500 Items)

```
Business Rule: BR-004
Name: Copy Folder Size Limit
Description: Max 500 total items (folders + decks + cards) for copy operation
Applies to: Copy folder

Validation Logic:
  FUNCTION validateFolderCopySize(sourceFolder, copyDecks):
    /**
     * Validates folder size doesn't exceed copy limit
     *
     * @param sourceFolder: Folder to be copied
     * @param copyDecks: Boolean, whether to include decks/cards
     * @return CopySizeInfo with counts and threshold
     */

    // Count all items recursively
    totalItems = countFolderItems(sourceFolder, copyDecks)

    // Define thresholds
    SYNC_THRESHOLD = 50   // Synchronous copy
    ASYNC_THRESHOLD = 500  // Asynchronous copy

    IF totalItems.total > ASYNC_THRESHOLD THEN
      ERROR "Folder exceeds maximum copy limit"
      ERROR_CODE: FOLDER_TOO_LARGE
      HTTP_STATUS: 422
      DETAIL: {
        totalItems: totalItems.total,
        folders: totalItems.folders,
        decks: totalItems.decks,
        cards: totalItems.cards,
        maxAllowed: ASYNC_THRESHOLD
      }
    END IF

    // Return copy strategy
    IF totalItems.total <= SYNC_THRESHOLD THEN
      RETURN { strategy: "SYNC", totalItems: totalItems }
    ELSE
      RETURN { strategy: "ASYNC", totalItems: totalItems }
    END IF
  END FUNCTION

  FUNCTION countFolderItems(folder, includeDecksAndCards):
    /**
     * Recursively count all items in folder tree
     */
    counts = {
      folders: 1,  // Count self
      decks: 0,
      cards: 0,
      total: 1
    }

    // Count decks and cards in this folder
    IF includeDecksAndCards THEN
      decks = deckRepository.findByFolderIdAndDeletedAtIsNull(folder.id)
      counts.decks = LENGTH(decks)
      counts.total = counts.total + counts.decks

      FOR EACH deck IN decks DO
        cardCount = cardRepository.countByDeckIdAndDeletedAtIsNull(deck.id)
        counts.cards = counts.cards + cardCount
        counts.total = counts.total + cardCount
      END FOR
    END IF

    // Recursively count sub-folders
    subFolders = folderRepository.findByParentFolderIdAndDeletedAtIsNull(folder.id)
    FOR EACH subFolder IN subFolders DO
      subCounts = countFolderItems(subFolder, includeDecksAndCards)
      counts.folders = counts.folders + subCounts.folders
      counts.decks = counts.decks + subCounts.decks
      counts.cards = counts.cards + subCounts.cards
      counts.total = counts.total + subCounts.total
    END FOR

    RETURN counts
  END FUNCTION
```

**Error Code**: FOLDER_TOO_LARGE
**HTTP Status**: 422 Unprocessable Entity

---

### BR-005: Copy Deck Size Limit (<=10,000 Cards)

```
Business Rule: BR-005
Name: Copy Deck Size Limit
Description: Max 10,000 cards per deck for copy operation
Applies to: Copy deck

Validation Logic:
  FUNCTION validateDeckCopySize(sourceDeck):
    /**
     * Validates deck size doesn't exceed copy limit
     *
     * @param sourceDeck: Deck to be copied
     * @return CopyStrategy (SYNC or ASYNC)
     */

    cardCount = cardRepository.countByDeckIdAndDeletedAtIsNull(sourceDeck.id)

    // Define thresholds
    SYNC_THRESHOLD = 1000   // Synchronous copy
    ASYNC_THRESHOLD = 10000  // Asynchronous copy

    IF cardCount > ASYNC_THRESHOLD THEN
      ERROR "Deck exceeds maximum copy limit"
      ERROR_CODE: DECK_TOO_LARGE
      HTTP_STATUS: 422
      DETAIL: {
        deckId: sourceDeck.id,
        deckName: sourceDeck.name,
        cardCount: cardCount,
        maxAllowed: ASYNC_THRESHOLD
      }
    END IF

    // Return copy strategy
    IF cardCount <= SYNC_THRESHOLD THEN
      RETURN "SYNC"
    ELSE
      RETURN "ASYNC"
    END IF
  END FUNCTION
```

**Error Code**: DECK_TOO_LARGE
**HTTP Status**: 422 Unprocessable Entity

---

### BR-006: Import File Limits (<=10,000 Rows, <=50MB)

```
Business Rule: BR-006
Name: Import File Limits
Description: Max 10,000 rows and 50MB file size for card import
Applies to: Import cards from CSV/Excel

Validation Logic:
  FUNCTION validateImportFile(file):
    /**
     * Validates file size and row count before import
     *
     * @param file: Uploaded file object
     * @return FileValidationResult
     */

    MAX_FILE_SIZE = 50 * 1024 * 1024  // 50MB in bytes
    MAX_ROWS = 10000

    // Validate file size
    IF file.size > MAX_FILE_SIZE THEN
      ERROR "File size exceeds 50MB limit"
      ERROR_CODE: FILE_TOO_LARGE
      HTTP_STATUS: 400
      DETAIL: {
        fileSize: file.size,
        maxSize: MAX_FILE_SIZE,
        fileSizeMB: ROUND(file.size / 1024 / 1024, 2)
      }
    END IF

    // Validate file type
    allowedTypes = ['text/csv', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet']
    allowedExtensions = ['.csv', '.xlsx']

    IF file.mimeType NOT IN allowedTypes AND file.extension NOT IN allowedExtensions THEN
      ERROR "Invalid file type. Only CSV and Excel (.xlsx) are supported"
      ERROR_CODE: INVALID_FILE_TYPE
      HTTP_STATUS: 400
    END IF

    // Count rows (quick pre-validation)
    rowCount = countFileRows(file)

    IF rowCount > MAX_ROWS THEN
      ERROR "File contains too many rows"
      ERROR_CODE: ROW_LIMIT_EXCEEDED
      HTTP_STATUS: 422
      DETAIL: {
        rowCount: rowCount,
        maxRows: MAX_ROWS,
        suggestion: "Please split file into smaller batches"
      }
    END IF

    RETURN VALID
  END FUNCTION

  FUNCTION validateImportRow(row, rowNumber):
    /**
     * Validates individual row during import
     *
     * @param row: Row data { front, back }
     * @param rowNumber: Row number in file (for error reporting)
     */

    errors = []

    // Validate front field
    IF row.front IS NULL OR TRIM(row.front) IS EMPTY THEN
      errors.ADD({
        row: rowNumber,
        field: "front",
        error: "Front field is empty or whitespace only"
      })
    ELSE IF LENGTH(row.front) > 5000 THEN
      errors.ADD({
        row: rowNumber,
        field: "front",
        error: "Front field exceeds 5000 characters"
      })
    END IF

    // Validate back field
    IF row.back IS NULL OR TRIM(row.back) IS EMPTY THEN
      errors.ADD({
        row: rowNumber,
        field: "back",
        error: "Back field is empty or whitespace only"
      })
    ELSE IF LENGTH(row.back) > 5000 THEN
      errors.ADD({
        row: rowNumber,
        field: "back",
        error: "Back field exceeds 5000 characters"
      })
    END IF

    IF errors IS NOT EMPTY THEN
      RETURN { valid: FALSE, errors: errors }
    END IF

    RETURN { valid: TRUE, errors: [] }
  END FUNCTION
```

**Error Code**: FILE_TOO_LARGE, INVALID_FILE_TYPE, ROW_LIMIT_EXCEEDED
**HTTP Status**: 400 Bad Request / 422 Unprocessable Entity

---

### BR-007: Daily Review Limits (Configurable)

```
Business Rule: BR-007
Name: Daily Review Limits
Description: Max reviews per day configurable (1-500)
Applies to: Fetch due cards for review

Validation Logic:
  FUNCTION applyDailyReviewLimit(userId, requestedLimit):
    /**
     * Applies user's daily review limit setting
     *
     * @param userId: User ID
     * @param requestedLimit: Limit from request parameter (optional)
     * @return Effective limit to use
     */

    // Get user's SRS settings
    srsSettings = srsSettingsRepository.findByUserId(userId)

    maxReviewsPerDay = srsSettings.maxReviewsPerDay

    // Check if user has reached daily limit
    todayReviewCount = reviewLogRepository.countByUserIdAndReviewedAtToday(userId)

    IF todayReviewCount >= maxReviewsPerDay THEN
      ERROR "Daily review limit reached"
      ERROR_CODE: DAILY_LIMIT_EXCEEDED
      HTTP_STATUS: 422
      DETAIL: {
        reviewsToday: todayReviewCount,
        dailyLimit: maxReviewsPerDay,
        message: "You've completed all reviews for today. Come back tomorrow!"
      }
    END IF

    // Calculate remaining reviews for today
    remainingReviews = maxReviewsPerDay - todayReviewCount

    // Apply limit
    IF requestedLimit IS NULL THEN
      effectiveLimit = MIN(remainingReviews, 100)  // Default 100
    ELSE
      effectiveLimit = MIN(requestedLimit, remainingReviews)
    END IF

    RETURN effectiveLimit
  END FUNCTION
```

**Error Code**: DAILY_LIMIT_EXCEEDED
**HTTP Status**: 422 Unprocessable Entity

---

### BR-008: Daily New Card Limits (Configurable)

```
Business Rule: BR-008
Name: Daily New Card Limits
Description: Max new cards per day configurable (1-100)
Applies to: Fetch new cards for review

Validation Logic:
  FUNCTION applyDailyNewCardLimit(userId, requestedLimit):
    /**
     * Applies user's daily new card limit setting
     *
     * @param userId: User ID
     * @param requestedLimit: Limit from request (optional)
     * @return Effective limit to use
     */

    // Get user's SRS settings
    srsSettings = srsSettingsRepository.findByUserId(userId)

    newCardsPerDay = srsSettings.newCardsPerDay

    // Count new cards reviewed today
    todayNewCardCount = reviewLogRepository.countNewCardsByUserIdAndReviewedAtToday(userId)

    IF todayNewCardCount >= newCardsPerDay THEN
      // Don't throw error, just return 0
      RETURN 0
    END IF

    // Calculate remaining new cards for today
    remainingNewCards = newCardsPerDay - todayNewCardCount

    // Apply limit
    IF requestedLimit IS NULL THEN
      effectiveLimit = MIN(remainingNewCards, 20)  // Default 20
    ELSE
      effectiveLimit = MIN(requestedLimit, remainingNewCards)
    END IF

    RETURN effectiveLimit
  END FUNCTION
```

**Error Code**: (No error - silently returns 0 when limit reached)
**HTTP Status**: 200 OK (with 0 cards)

---

### BR-009: Password Strength (Min 8 Chars)

```
Business Rule: BR-009
Name: Password Strength
Description: Password must be at least 8 characters
Applies to: User registration, Password change

Validation Logic:
  FUNCTION validatePasswordStrength(password):
    /**
     * Validates password meets minimum strength requirements
     * MVP: Only length requirement (8+ chars)
     * Future: Complexity requirements (uppercase, numbers, symbols)
     *
     * @param password: Plain text password
     */

    MIN_LENGTH = 8
    MAX_LENGTH = 128

    IF password IS NULL OR password IS EMPTY THEN
      ERROR "Password is required"
      ERROR_CODE: FIELD_REQUIRED
      HTTP_STATUS: 400
    END IF

    // Do NOT trim password (preserve whitespace)

    IF LENGTH(password) < MIN_LENGTH THEN
      ERROR "Password must be at least 8 characters"
      ERROR_CODE: FIELD_TOO_SHORT
      HTTP_STATUS: 400
    END IF

    IF LENGTH(password) > MAX_LENGTH THEN
      ERROR "Password cannot exceed 128 characters"
      ERROR_CODE: FIELD_TOO_LONG
      HTTP_STATUS: 400
    END IF

    RETURN VALID
  END FUNCTION
```

**Error Code**: FIELD_TOO_SHORT, FIELD_TOO_LONG
**HTTP Status**: 400 Bad Request

---

### BR-010: Email Uniqueness

```
Business Rule: BR-010
Name: Email Uniqueness
Description: Email must be unique across all users (case-insensitive)
Applies to: User registration

Validation Logic:
  FUNCTION validateEmailUniqueness(email):
    /**
     * Ensures email is not already registered
     * Case-insensitive comparison
     *
     * @param email: Email address to validate
     */

    normalizedEmail = LOWERCASE(TRIM(email))

    existingUser = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(normalizedEmail)

    IF existingUser IS NOT NULL THEN
      ERROR "Email already registered"
      ERROR_CODE: EMAIL_ALREADY_EXISTS
      HTTP_STATUS: 409
    END IF

    RETURN VALID
  END FUNCTION
```

**Error Code**: EMAIL_ALREADY_EXISTS
**HTTP Status**: 409 Conflict

---

## 4. Validation Execution Order

Validations execute in a specific order to optimize performance and provide clear error messages.

### 4.1 Validation Sequence

```
FUNCTION executeValidations(request, context):
  /**
   * Master validation orchestrator
   * Executes validations in order, fails fast on first error
   *
   * @param request: Request DTO to validate
   * @param context: Validation context (userId, existing entities, etc.)
   */

  // LEVEL 1: Field Syntax (Format, Length)
  // - Fast, no database queries
  // - Validates individual field format and constraints
  // - Fails fast if basic syntax is invalid

  TRY:
    validateFieldSyntax(request)
  CATCH ValidationException e:
    THROW e  // Stop immediately
  END TRY

  // LEVEL 2: Entity Structure (Cross-field)
  // - Validates relationships between fields
  // - May include simple database lookups (by ID)
  // - Ensures referential integrity

  TRY:
    validateEntityStructure(request, context)
  CATCH ValidationException e:
    THROW e  // Stop immediately
  END TRY

  // LEVEL 3: Business Semantics (Domain Rules)
  // - Complex business rules
  // - May include expensive queries (counting, recursion)
  // - Enforces domain constraints

  TRY:
    validateBusinessRules(request, context)
  CATCH ValidationException e:
    THROW e  // Stop immediately
  END TRY

  // LEVEL 4: Database Constraints (Unique, FK)
  // - Final validation happens at database level
  // - Catch unique constraint violations, FK violations
  // - Convert to friendly error messages

  TRY:
    saveToDatabase(request)
  CATCH DatabaseException e:
    convertDatabaseError(e)
    THROW ValidationException
  END TRY

  RETURN SUCCESS
END FUNCTION
```

### 4.2 Example: Create Folder Validation Order

```
FUNCTION validateCreateFolder(request, userId):
  /**
   * Example: Validation order for creating a folder
   */

  // LEVEL 1: Field Syntax
  validateField_FolderName(request.name)            // NOT NULL, length, format
  validateField_FolderDescription(request.description)  // length (if present)
  validateField_ParentFolderId(request.parentFolderId)  // UUID format (if present)

  // LEVEL 2: Entity Structure
  parentFolder = validateEntity_ParentFolderExists(userId, request.parentFolderId)

  // LEVEL 3: Business Semantics
  validateBusinessRule_MaxDepth(parentFolder, "CREATE", null)
  validateBusinessRule_FolderNameUniqueness(userId, request.parentFolderId, request.name, null)

  // LEVEL 4: Database Constraints
  // Handled automatically by database unique constraints

  RETURN VALID
END FUNCTION
```

---

## 5. Error Response Format

All validation errors follow a consistent response format for frontend handling.

### 5.1 Single Field Error

```json
{
  "error": "FIELD_REQUIRED",
  "message": "Email is required",
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/auth/register",
  "traceId": "abc-123-def-456"
}
```

### 5.2 Multiple Field Errors

```json
{
  "error": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [
    {
      "field": "name",
      "code": "FIELD_REQUIRED",
      "message": "Folder name is required"
    },
    {
      "field": "description",
      "code": "FIELD_TOO_LONG",
      "message": "Description cannot exceed 500 characters"
    }
  ],
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/folders",
  "traceId": "abc-123-def-456"
}
```

### 5.3 Business Rule Violation

```json
{
  "error": "MAX_DEPTH_EXCEEDED",
  "message": "Cannot exceed 10 folder levels (depth 0-9)",
  "details": {
    "currentDepth": 10,
    "maxDepth": 9,
    "action": "CREATE"
  },
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/folders",
  "traceId": "abc-123-def-456"
}
```

### 5.4 Import Validation Errors

```json
{
  "successCount": 950,
  "errorCount": 50,
  "duplicateCount": 20,
  "totalRows": 1020,
  "errors": [
    {
      "row": 15,
      "field": "front",
      "error": "Front field is empty or whitespace only"
    },
    {
      "row": 42,
      "field": "back",
      "error": "Back field exceeds 5000 characters"
    }
  ],
  "message": "Import completed with 950 successes, 20 duplicates skipped, 50 errors"
}
```

---

## Summary

This document provides comprehensive validation rules for RepeatWise MVP:

- **Field-level**: 30+ field validations across 6 entities (User, Folder, Deck, Card, Review, SRS Settings)
- **Entity-level**: 5 cross-field validations
- **Business logic**: 10 complex business rules (BR-001 to BR-010)
- **Error codes**: 60+ unique error codes
- **Error format**: Standardized JSON response format

All validation rules are written in detailed pseudo-code suitable for implementation in any programming language.

---

**Document Version**: 1.0
**Last Updated**: 2025-01-10
**Status**: Ready for Implementation

**Related Documents**:
- [Entity Specifications](./01-entity-specifications.md) - Entity field definitions
- [Business Logic Flows](./03-business-logic-flows.md) - Service layer logic
- [API Request/Response Specs](./02-api-request-response-specs.md) - API validation requirements
- [Error Handling Specs](./06-error-handling-specs.md) - Error codes and responses
