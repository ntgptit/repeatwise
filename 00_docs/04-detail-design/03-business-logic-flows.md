# Business Logic Flows - RepeatWise MVP

## Document Information

| Attribute | Value |
|-----------|-------|
| **Document ID** | DD-003 |
| **Version** | 1.0 |
| **Last Updated** | 2025-01 |
| **Status** | Draft |

## Overview

This document describes the detailed business logic flows for all service layer methods in RepeatWise MVP. Each method is documented with:
- Input/Output parameters
- Pre-conditions and Post-conditions
- Step-by-step pseudo-code logic
- Transaction boundaries
- Domain events published
- Error scenarios

**Purpose**: This pseudo-code is detailed enough for AI to convert into Java/TypeScript implementations.

---

## Table of Contents

1. [AuthService](#1-authservice)
2. [FolderService](#2-folderservice)
3. [DeckService](#3-deckservice)
4. [CardService](#4-cardservice)
5. [ReviewService](#5-reviewservice)
6. [SrsService](#6-srsservice)
7. [StatsService](#7-statsservice)
8. [ImportExportService](#8-importexportservice)

---

## 1. AuthService

### Method: register(request)

**Purpose**: Register new user with email and password

**Input**:
- request: RegisterRequest
  - email: String (email format, unique)
  - password: String (min 8 chars)
  - name: String (1-100 chars)
  - timezone: String (default: UTC)
  - language: Enum (VI/EN, default: VI)

**Output**:
- UserResponse (user object without password)

**Pre-conditions**:
- Email not already registered
- Password meets strength requirements (min 8 chars)

**Pseudo-code**:
```
FUNCTION register(request):
  // Transaction begins

  // Step 1: Validate email uniqueness
  existingUser = userRepository.findByEmail(request.email)
  IF existingUser IS NOT NULL THEN
    THROW DuplicateResourceException("Email already registered")
  END IF

  // Step 2: Validate password strength
  IF LENGTH(request.password) < 8 THEN
    THROW ValidationException("Password must be at least 8 characters")
  END IF

  // Step 3: Hash password
  passwordHash = bcrypt.hash(request.password, COST_FACTOR=12)

  // Step 4: Create user entity
  user = NEW User()
  user.id = generateUUID()
  user.email = LOWERCASE(TRIM(request.email))
  user.passwordHash = passwordHash
  user.name = TRIM(request.name)
  user.timezone = request.timezone OR "UTC"
  user.language = request.language OR Language.VI
  user.theme = Theme.SYSTEM
  user.createdAt = NOW()
  user.updatedAt = NOW()

  // Step 5: Save to database
  savedUser = userRepository.save(user)

  // Step 6: Initialize user stats
  userStats = NEW UserStats()
  userStats.userId = savedUser.id
  userStats.totalCardsLearned = 0
  userStats.streakDays = 0
  userStats.lastStudyDate = NULL
  userStats.totalStudyTimeMinutes = 0
  userStatsRepository.save(userStats)

  // Step 7: Initialize SRS settings (defaults)
  srsSettings = NEW SrsSettings()
  srsSettings.userId = savedUser.id
  srsSettings.totalBoxes = 7
  srsSettings.reviewOrder = ReviewOrder.RANDOM
  srsSettings.notificationEnabled = TRUE
  srsSettings.notificationTime = TIME("09:00")
  srsSettings.forgottenCardAction = ForgottenCardAction.MOVE_TO_BOX_1
  srsSettings.moveDownBoxes = 1
  srsSettings.newCardsPerDay = 20
  srsSettings.maxReviewsPerDay = 200
  srsSettingsRepository.save(srsSettings)

  // Transaction commits

  // Step 8: Log event (async, after commit)
  LOG_INFO("User registered: " + savedUser.email)

  // Step 9: Map to response DTO
  response = userMapper.toResponse(savedUser)

  RETURN response
END FUNCTION
```

**Post-conditions**:
- User created in database with hashed password
- UserStats initialized with zeros
- SrsSettings initialized with defaults
- User can login immediately

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After save user stats and SRS settings
- ROLLBACK: On any validation or database error

**Error Scenarios**:
- Email already exists → 409 DuplicateResourceException
- Invalid email format → 400 ValidationException
- Password too short → 400 ValidationException
- Database error → 500 InternalServerError

---

### Method: login(request)

**Purpose**: Authenticate user and issue access + refresh tokens

**Input**:
- request: LoginRequest
  - email: String
  - password: String

**Output**:
- AuthResponse
  - accessToken: String (JWT, 15 min expiry)
  - expiresIn: Integer (900 seconds)
  - user: UserResponse
  - refreshToken: String (7 days expiry, will be set as HTTP-only cookie)

**Pre-conditions**:
- User exists with given email
- Password matches hashed password in database

**Pseudo-code**:
```
FUNCTION login(request):
  // NO explicit transaction needed (read-only + single writes)

  // Step 1: Find user by email
  user = userRepository.findByEmail(LOWERCASE(TRIM(request.email)))
  IF user IS NULL THEN
    THROW InvalidCredentialsException("Invalid email or password")
  END IF

  // Step 2: Verify password
  passwordMatches = bcrypt.verify(request.password, user.passwordHash)
  IF NOT passwordMatches THEN
    // Log failed login attempt
    LOG_WARN("Failed login attempt for: " + request.email)
    THROW InvalidCredentialsException("Invalid email or password")
  END IF

  // Step 3: Generate access token (JWT)
  accessTokenPayload = {
    sub: user.id,
    email: user.email,
    name: user.name,
    iat: NOW(),
    exp: NOW() + 15_MINUTES
  }
  accessToken = jwtProvider.generateToken(accessTokenPayload, JWT_SECRET)

  // Step 4: Generate refresh token
  refreshTokenValue = generateSecureRandomString(64) // Cryptographically secure
  refreshTokenHash = bcrypt.hash(refreshTokenValue, COST_FACTOR=12)

  // Step 5: Save refresh token to database
  refreshToken = NEW RefreshToken()
  refreshToken.id = generateUUID()
  refreshToken.userId = user.id
  refreshToken.tokenHash = refreshTokenHash
  refreshToken.expiresAt = NOW() + 7_DAYS
  refreshToken.revokedAt = NULL
  refreshToken.createdAt = NOW()
  refreshTokenRepository.save(refreshToken)

  // Step 6: Log successful login
  LOG_INFO("User logged in: " + user.email)

  // Step 7: Update last login timestamp (optional, async)
  user.lastLoginAt = NOW()
  userRepository.save(user)

  // Step 8: Map to response DTO
  response = NEW AuthResponse()
  response.accessToken = accessToken
  response.expiresIn = 900 // seconds
  response.refreshToken = refreshTokenValue // Will be set as HTTP-only cookie
  response.user = userMapper.toResponse(user)

  RETURN response
END FUNCTION
```

**Post-conditions**:
- Access token generated (15 min expiry)
- Refresh token created and stored in database
- User last login timestamp updated
- Refresh token set as HTTP-only cookie in controller

**Transaction Scope**:
- Implicit transactions for each repository save (Spring default)
- No explicit @Transactional needed (independent operations)

**Error Scenarios**:
- User not found → 401 InvalidCredentialsException
- Password mismatch → 401 InvalidCredentialsException
- JWT generation error → 500 InternalServerError
- Database error → 500 InternalServerError

---

### Method: refreshToken(refreshTokenValue)

**Purpose**: Validate refresh token and issue new access + refresh tokens (rotation)

**Input**:
- refreshTokenValue: String (from HTTP-only cookie)

**Output**:
- RefreshTokenResponse
  - accessToken: String (new JWT, 15 min expiry)
  - expiresIn: Integer (900 seconds)
  - refreshToken: String (new refresh token, rotated)

**Pre-conditions**:
- Refresh token exists in database
- Refresh token not expired
- Refresh token not revoked

**Pseudo-code**:
```
FUNCTION refreshToken(refreshTokenValue):
  // Transaction begins

  // Step 1: Validate input
  IF refreshTokenValue IS NULL OR refreshTokenValue IS EMPTY THEN
    THROW InvalidCredentialsException("Refresh token is required")
  END IF

  // Step 2: Find all refresh tokens for hash matching
  // NOTE: Cannot query by hash directly (bcrypt hashes are different each time)
  // Strategy: Find recent tokens for this user (if we had user context)
  // For MVP: Find all non-revoked, non-expired tokens and verify hash
  candidateTokens = refreshTokenRepository.findValidTokens(NOW())

  matchedToken = NULL
  FOR EACH token IN candidateTokens DO
    IF bcrypt.verify(refreshTokenValue, token.tokenHash) THEN
      matchedToken = token
      BREAK
    END IF
  END FOR

  IF matchedToken IS NULL THEN
    THROW InvalidCredentialsException("Invalid or expired refresh token")
  END IF

  // Step 3: Check expiration
  IF matchedToken.expiresAt < NOW() THEN
    THROW InvalidCredentialsException("Refresh token expired")
  END IF

  // Step 4: Check revocation
  IF matchedToken.revokedAt IS NOT NULL THEN
    THROW InvalidCredentialsException("Refresh token has been revoked")
  END IF

  // Step 5: Revoke old refresh token (one-time use)
  matchedToken.revokedAt = NOW()
  refreshTokenRepository.save(matchedToken)

  // Step 6: Get user
  user = userRepository.findById(matchedToken.userId)
  IF user IS NULL THEN
    THROW ResourceNotFoundException("User not found")
  END IF

  // Step 7: Generate new access token
  accessTokenPayload = {
    sub: user.id,
    email: user.email,
    name: user.name,
    iat: NOW(),
    exp: NOW() + 15_MINUTES
  }
  newAccessToken = jwtProvider.generateToken(accessTokenPayload, JWT_SECRET)

  // Step 8: Generate new refresh token (rotation)
  newRefreshTokenValue = generateSecureRandomString(64)
  newRefreshTokenHash = bcrypt.hash(newRefreshTokenValue, COST_FACTOR=12)

  newRefreshToken = NEW RefreshToken()
  newRefreshToken.id = generateUUID()
  newRefreshToken.userId = user.id
  newRefreshToken.tokenHash = newRefreshTokenHash
  newRefreshToken.expiresAt = NOW() + 7_DAYS
  newRefreshToken.revokedAt = NULL
  newRefreshToken.createdAt = NOW()
  refreshTokenRepository.save(newRefreshToken)

  // Transaction commits

  // Step 9: Log token refresh
  LOG_INFO("Token refreshed for user: " + user.email)

  // Step 10: Map to response DTO
  response = NEW RefreshTokenResponse()
  response.accessToken = newAccessToken
  response.expiresIn = 900
  response.refreshToken = newRefreshTokenValue

  RETURN response
END FUNCTION
```

**Post-conditions**:
- Old refresh token revoked (one-time use)
- New access token generated
- New refresh token generated and stored
- Token rotation completed

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After revoking old token and saving new token
- ROLLBACK: On any validation or database error

**Error Scenarios**:
- Invalid refresh token → 401 InvalidCredentialsException
- Expired refresh token → 401 InvalidCredentialsException
- Revoked refresh token → 401 InvalidCredentialsException
- User not found → 404 ResourceNotFoundException
- Database error → 500 InternalServerError

**Performance Optimization Note**:
The token matching strategy (looping through candidates) is not optimal for large user bases. Future improvement: Store token family ID or user ID indexed column for faster lookup.

---

### Method: logout(refreshTokenValue, userId)

**Purpose**: Revoke refresh token for current session

**Input**:
- refreshTokenValue: String (from HTTP-only cookie)
- userId: UUID (from access token claims)

**Output**:
- void (204 No Content)

**Pre-conditions**:
- User is authenticated
- Refresh token exists

**Pseudo-code**:
```
FUNCTION logout(refreshTokenValue, userId):
  // Transaction begins

  // Step 1: Find and verify refresh token (similar to refresh)
  candidateTokens = refreshTokenRepository.findByUserIdAndNotRevoked(userId, NOW())

  matchedToken = NULL
  FOR EACH token IN candidateTokens DO
    IF bcrypt.verify(refreshTokenValue, token.tokenHash) THEN
      matchedToken = token
      BREAK
    END IF
  END FOR

  // Step 2: Revoke token if found
  IF matchedToken IS NOT NULL THEN
    matchedToken.revokedAt = NOW()
    refreshTokenRepository.save(matchedToken)
  END IF
  // If not found, silently succeed (idempotent logout)

  // Transaction commits

  // Step 3: Log logout
  LOG_INFO("User logged out: userId=" + userId)

  RETURN // void
END FUNCTION
```

**Post-conditions**:
- Refresh token revoked (if found)
- Access token still valid until expiry (client should discard)
- User must login again to get new tokens

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After revoke token
- ROLLBACK: On database error

**Error Scenarios**:
- Database error → 500 InternalServerError
- Token not found → Silent success (idempotent)

---

## 2. FolderService

### Method: createFolder(request, userId)

**Purpose**: Create a new folder in the folder tree hierarchy

**Input**:
- request: CreateFolderRequest
  - name: String (1-100 chars, required)
  - description: String (max 500 chars, optional)
  - parentFolderId: UUID (nullable, NULL = root level)
- userId: UUID (current authenticated user)

**Output**:
- FolderResponse

**Pre-conditions**:
- User is authenticated
- Parent folder (if specified) exists and belongs to user
- Parent folder depth < 9 (to allow child at depth 10)
- Name is unique within parent folder

**Pseudo-code**:
```
FUNCTION createFolder(request, userId):
  // Transaction begins

  // Step 1: Validate and load parent folder
  parent = NULL
  IF request.parentFolderId IS NOT NULL THEN
    parent = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(
      request.parentFolderId, userId
    )
    IF parent IS NULL THEN
      THROW ResourceNotFoundException("Parent folder not found")
    END IF

    // Check depth constraint
    IF parent.depth >= 9 THEN
      THROW MaxDepthExceededException("Cannot create folder: maximum depth (10 levels) reached")
    END IF
  END IF

  // Step 2: Check name uniqueness within parent
  existingFolder = folderRepository.findByUserIdAndParentFolderIdAndNameAndDeletedAtIsNull(
    userId, request.parentFolderId, TRIM(request.name)
  )
  IF existingFolder IS NOT NULL THEN
    THROW DuplicateResourceException("A folder with this name already exists in this location")
  END IF

  // Step 3: Create folder entity
  folder = NEW Folder()
  folder.id = generateUUID()
  folder.userId = userId
  folder.parentFolder = parent
  folder.name = TRIM(request.name)
  folder.description = request.description

  // Step 4: Calculate depth
  IF parent IS NULL THEN
    folder.depth = 0
  ELSE
    folder.depth = parent.depth + 1
  END IF

  // Step 5: Calculate materialized path
  IF parent IS NULL THEN
    folder.path = "/" + folder.id
  ELSE
    folder.path = parent.path + "/" + folder.id
  END IF

  // Step 6: Set audit fields
  folder.createdAt = NOW()
  folder.updatedAt = NOW()
  folder.deletedAt = NULL

  // Step 7: Save to database
  savedFolder = folderRepository.save(folder)

  // Step 8: Initialize folder stats (cache)
  folderStats = NEW FolderStats()
  folderStats.folderId = savedFolder.id
  folderStats.userId = userId
  folderStats.totalCardsCount = 0
  folderStats.dueCardsCount = 0
  folderStats.newCardsCount = 0
  folderStats.matureCardsCount = 0
  folderStats.lastComputedAt = NOW()
  folderStatsRepository.save(folderStats)

  // Step 9: Invalidate parent folder stats (if exists)
  IF parent IS NOT NULL THEN
    folderStatsRepository.invalidate(parent.id) // Set lastComputedAt = NULL
  END IF

  // Transaction commits

  // Step 10: Publish domain event (async, after commit)
  publishEvent(FolderCreatedEvent(
    folderId=savedFolder.id,
    userId=userId,
    parentFolderId=parent?.id,
    timestamp=NOW()
  ))

  // Step 11: Log event
  LOG_INFO("Folder created: " + savedFolder.name + " by user: " + userId)

  // Step 12: Map to response DTO
  response = folderMapper.toResponse(savedFolder)

  RETURN response
END FUNCTION
```

**Post-conditions**:
- Folder created in database with correct path and depth
- FolderStats initialized with zeros
- Parent folder stats cache invalidated
- FolderCreatedEvent published for listeners
- Folder appears in tree view immediately

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After saving folder and folder stats
- ROLLBACK: On any validation or database error

**Domain Events Published**:
- FolderCreatedEvent (handled by FolderStatsInvalidationListener)

**Error Scenarios**:
- Parent folder not found → 404 ResourceNotFoundException
- Parent folder deleted → 404 ResourceNotFoundException
- Max depth exceeded → 422 MaxDepthExceededException
- Duplicate name → 409 DuplicateResourceException
- Name validation fails → 400 ValidationException
- Database error → 500 InternalServerError

---

### Method: moveFolder(folderId, newParentId, userId)

**Purpose**: Move folder to a different parent (change parent_folder_id)

**Input**:
- folderId: UUID (folder to move)
- newParentId: UUID (nullable, NULL = move to root)
- userId: UUID (current authenticated user)

**Output**:
- FolderResponse

**Pre-conditions**:
- User owns the folder
- Folder exists and not deleted
- New parent exists (if specified) and belongs to user
- Move does not create circular reference
- Resulting depth for all descendants <= 10

**Pseudo-code**:
```
FUNCTION moveFolder(folderId, newParentId, userId):
  // Transaction begins

  // Step 1: Load folder to move
  folder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(folderId, userId)
  IF folder IS NULL THEN
    THROW ResourceNotFoundException("Folder not found")
  END IF

  // Step 2: Validate not moving to itself
  IF folderId == newParentId THEN
    THROW CircularReferenceException("Cannot move folder into itself")
  END IF

  // Step 3: Load new parent (if specified)
  newParent = NULL
  IF newParentId IS NOT NULL THEN
    newParent = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(newParentId, userId)
    IF newParent IS NULL THEN
      THROW ResourceNotFoundException("Destination folder not found")
    END IF

    // Step 4: Check circular reference (new parent is descendant of folder)
    IF newParent.path STARTS_WITH folder.path THEN
      THROW CircularReferenceException("Cannot move folder into its own descendant")
    END IF
  END IF

  // Step 5: Calculate new depth
  newDepth = 0
  IF newParent IS NOT NULL THEN
    newDepth = newParent.depth + 1
  END IF

  // Step 6: Find all descendants to check max depth
  descendants = folderRepository.findDescendants(userId, folder.path + "/")

  currentMaxDepth = folder.depth
  FOR EACH desc IN descendants DO
    IF desc.depth > currentMaxDepth THEN
      currentMaxDepth = desc.depth
    END IF
  END FOR

  depthDelta = newDepth - folder.depth
  newMaxDepth = currentMaxDepth + depthDelta

  IF newMaxDepth > 10 THEN
    THROW MaxDepthExceededException(
      "Cannot move: would exceed maximum depth. Current max: " + currentMaxDepth +
      ", New max would be: " + newMaxDepth + " (limit: 10)"
    )
  END IF

  // Step 7: Store old parent for stats invalidation
  oldParent = folder.parentFolder

  // Step 8: Update folder
  folder.parentFolder = newParent
  folder.depth = newDepth

  // Step 9: Recalculate path
  IF newParent IS NULL THEN
    folder.path = "/" + folder.id
  ELSE
    folder.path = newParent.path + "/" + folder.id
  END IF

  folder.updatedAt = NOW()
  folderRepository.save(folder)

  // Step 10: Update all descendants' paths and depths (recursive)
  FOR EACH desc IN descendants DO
    // Calculate new path (replace old prefix)
    oldPrefix = folder.path + "/"
    desc.path = REPLACE(desc.path, oldPrefix, folder.path + "/")

    // Update depth
    desc.depth = desc.depth + depthDelta
    desc.updatedAt = NOW()
    folderRepository.save(desc)
  END FOR

  // Step 11: Invalidate folder stats for old and new parent chains
  IF oldParent IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(oldParent.id)
  END IF
  IF newParent IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(newParent.id)
  END IF

  // Transaction commits

  // Step 12: Publish domain event (async)
  publishEvent(FolderMovedEvent(
    folderId=folder.id,
    oldParentId=oldParent?.id,
    newParentId=newParent?.id,
    userId=userId,
    timestamp=NOW()
  ))

  // Step 13: Log event
  LOG_INFO("Folder moved: " + folder.name + " to parent: " + newParent?.name)

  // Step 14: Map to response
  response = folderMapper.toResponse(folder)

  RETURN response
END FUNCTION
```

**Post-conditions**:
- Folder's parent_folder_id updated
- Folder's path and depth recalculated
- All descendant folders' paths and depths updated
- Old and new parent stats invalidated
- FolderMovedEvent published

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After updating folder and all descendants
- ROLLBACK: On any validation or database error

**Performance Considerations**:
- For large folder trees (>100 descendants), this can be slow
- Consider async operation with job tracking for very large moves
- Database indexes on path column critical for performance

**Domain Events Published**:
- FolderMovedEvent

**Error Scenarios**:
- Folder not found → 404 ResourceNotFoundException
- Destination not found → 404 ResourceNotFoundException
- Circular reference → 422 CircularReferenceException
- Max depth exceeded → 422 MaxDepthExceededException
- Database error → 500 InternalServerError

---

### Method: copyFolder(folderId, destinationParentId, newName, userId)

**Purpose**: Create a deep copy of folder including all sub-folders, decks, and cards (async for large folders)

**Input**:
- folderId: UUID (source folder to copy)
- destinationParentId: UUID (nullable, NULL = root)
- newName: String (name for copied folder)
- userId: UUID (current authenticated user)

**Output**:
- CopyJobResponse
  - jobId: UUID (for async operations)
  - status: JobStatus (SYNC_COMPLETED / ASYNC_PROCESSING)
  - folderId: UUID (if sync completed immediately)

**Pre-conditions**:
- User owns source folder
- Destination parent exists and belongs to user (if specified)
- Total items (folders + decks) <= 500 (hard limit)
- Resulting depth <= 10

**Pseudo-code**:
```
FUNCTION copyFolder(folderId, destinationParentId, newName, userId):
  // NO transaction yet (decision point for sync vs async)

  // Step 1: Load source folder
  sourceFolder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(folderId, userId)
  IF sourceFolder IS NULL THEN
    THROW ResourceNotFoundException("Source folder not found")
  END IF

  // Step 2: Load destination parent (if specified)
  destinationParent = NULL
  IF destinationParentId IS NOT NULL THEN
    destinationParent = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(
      destinationParentId, userId
    )
    IF destinationParent IS NULL THEN
      THROW ResourceNotFoundException("Destination folder not found")
    END IF
  END IF

  // Step 3: Validate depth constraint
  newDepth = 0
  IF destinationParent IS NOT NULL THEN
    newDepth = destinationParent.depth + 1
  END IF

  maxDescendantDepth = calculateMaxDescendantDepth(sourceFolder)
  depthDelta = maxDescendantDepth - sourceFolder.depth
  newMaxDepth = newDepth + depthDelta

  IF newMaxDepth > 10 THEN
    THROW MaxDepthExceededException(
      "Cannot copy: would exceed maximum depth (limit: 10)"
    )
  END IF

  // Step 4: Count total items to determine sync vs async
  totalItems = countTotalItems(sourceFolder) // Recursive count

  IF totalItems > 500 THEN
    THROW FolderTooLargeException(
      "Folder too large to copy (max 500 items). Total: " + totalItems
    )
  END IF

  // Step 5: Auto-generate unique name if duplicate
  finalName = newName
  counter = 1
  WHILE folderRepository.existsByParentAndName(destinationParentId, finalName, userId) DO
    counter = counter + 1
    finalName = newName + " (Copy " + counter + ")"
  END WHILE

  // Step 6: Decide sync vs async
  IF totalItems <= 50 THEN
    // SYNC COPY (small folder)
    // Transaction begins here

    copiedFolder = copyFolderRecursive(
      sourceFolder, destinationParent, finalName, userId
    )

    // Transaction commits

    LOG_INFO("Folder copied synchronously: " + totalItems + " items")

    RETURN CopyJobResponse(
      jobId=NULL,
      status=JobStatus.SYNC_COMPLETED,
      folderId=copiedFolder.id
    )
  ELSE
    // ASYNC COPY (large folder)
    // Create background job

    job = NEW CopyJob()
    job.id = generateUUID()
    job.userId = userId
    job.sourceFolderId = folderId
    job.destinationParentId = destinationParentId
    job.newName = finalName
    job.status = JobStatus.PENDING
    job.totalItems = totalItems
    job.processedItems = 0
    job.resultFolderId = NULL
    job.errorMessage = NULL
    job.createdAt = NOW()
    copyJobRepository.save(job)

    // Schedule async execution
    @Async
    executeCopyFolderAsync(job)

    LOG_INFO("Folder copy scheduled (async): " + totalItems + " items, jobId=" + job.id)

    RETURN CopyJobResponse(
      jobId=job.id,
      status=JobStatus.ASYNC_PROCESSING,
      folderId=NULL
    )
  END IF
END FUNCTION
```

**Helper Function: copyFolderRecursive(source, destParent, name, userId)**
```
FUNCTION copyFolderRecursive(source, destParent, name, userId):
  // Step 1: Create new folder
  copy = NEW Folder()
  copy.id = generateUUID()
  copy.userId = userId
  copy.parentFolder = destParent
  copy.name = name
  copy.description = source.description

  IF destParent IS NULL THEN
    copy.depth = 0
    copy.path = "/" + copy.id
  ELSE
    copy.depth = destParent.depth + 1
    copy.path = destParent.path + "/" + copy.id
  END IF

  copy.createdAt = NOW()
  copy.updatedAt = NOW()
  folderRepository.save(copy)

  // Step 2: Initialize folder stats
  folderStats = NEW FolderStats()
  folderStats.folderId = copy.id
  folderStats.userId = userId
  folderStats.totalCardsCount = 0
  folderStats.dueCardsCount = 0
  folderStats.newCardsCount = 0
  folderStats.matureCardsCount = 0
  folderStats.lastComputedAt = NULL // Will be computed later
  folderStatsRepository.save(folderStats)

  // Step 3: Copy all sub-folders (recursive)
  subFolders = folderRepository.findByParentFolderIdAndDeletedAtIsNull(source.id)
  FOR EACH subFolder IN subFolders DO
    copyFolderRecursive(subFolder, copy, subFolder.name, userId)
  END FOR

  // Step 4: Copy all decks in this folder
  decks = deckRepository.findByFolderIdAndDeletedAtIsNull(source.id)
  FOR EACH deck IN decks DO
    deckCopy = copyDeck(deck, copy, deck.name, userId)
  END FOR

  RETURN copy
END FUNCTION
```

**Helper Function: copyDeck(sourceDeck, destFolder, name, userId)**
```
FUNCTION copyDeck(sourceDeck, destFolder, name, userId):
  // Step 1: Create deck copy
  deckCopy = NEW Deck()
  deckCopy.id = generateUUID()
  deckCopy.userId = userId
  deckCopy.folder = destFolder
  deckCopy.name = name
  deckCopy.description = sourceDeck.description
  deckCopy.createdAt = NOW()
  deckCopy.updatedAt = NOW()
  deckRepository.save(deckCopy)

  // Step 2: Copy all cards (batch insert for performance)
  cards = cardRepository.findByDeckIdAndDeletedAtIsNull(sourceDeck.id)

  cardBatch = []
  FOR EACH card IN cards DO
    cardCopy = NEW Card()
    cardCopy.id = generateUUID()
    cardCopy.deckId = deckCopy.id
    cardCopy.front = card.front
    cardCopy.back = card.back
    cardCopy.createdAt = NOW()
    cardCopy.updatedAt = NOW()
    cardBatch.ADD(cardCopy)

    // Batch insert every 1000 cards
    IF SIZE(cardBatch) >= 1000 THEN
      cardRepository.batchInsert(cardBatch)
      cardBatch = []
    END IF
  END FOR

  // Insert remaining
  IF SIZE(cardBatch) > 0 THEN
    cardRepository.batchInsert(cardBatch)
  END IF

  // Step 3: Initialize SRS positions for all copied cards (Box 1)
  copiedCards = cardRepository.findByDeckIdAndDeletedAtIsNull(deckCopy.id)

  positionBatch = []
  FOR EACH cardCopy IN copiedCards DO
    position = NEW CardBoxPosition()
    position.id = generateUUID()
    position.cardId = cardCopy.id
    position.userId = userId
    position.currentBox = 1
    position.intervalDays = 1
    position.dueDate = CURRENT_DATE() + 1 // Tomorrow
    position.lastReviewedAt = NULL
    position.reviewCount = 0
    position.lapseCount = 0
    position.easeFactor = 2.5 // Default
    position.createdAt = NOW()
    position.updatedAt = NOW()
    positionBatch.ADD(position)

    IF SIZE(positionBatch) >= 1000 THEN
      cardBoxPositionRepository.batchInsert(positionBatch)
      positionBatch = []
    END IF
  END FOR

  IF SIZE(positionBatch) > 0 THEN
    cardBoxPositionRepository.batchInsert(positionBatch)
  END IF

  RETURN deckCopy
END FUNCTION
```

**Async Execution Function:**
```
@Async
FUNCTION executeCopyFolderAsync(job):
  TRY
    // Transaction begins

    job.status = JobStatus.RUNNING
    job.startedAt = NOW()
    copyJobRepository.save(job)

    // Load source and destination
    sourceFolder = folderRepository.findById(job.sourceFolderId)
    destinationParent = NULL
    IF job.destinationParentId IS NOT NULL THEN
      destinationParent = folderRepository.findById(job.destinationParentId)
    END IF

    // Execute copy with progress tracking
    copiedFolder = copyFolderRecursiveWithProgress(
      sourceFolder, destinationParent, job.newName, job.userId, job
    )

    // Update job status
    job.status = JobStatus.COMPLETED
    job.completedAt = NOW()
    job.resultFolderId = copiedFolder.id
    copyJobRepository.save(job)

    // Transaction commits

    // Send notification to user
    notificationService.sendCopyCompletedNotification(
      job.userId, job.newName, job.totalItems
    )

    LOG_INFO("Async folder copy completed: jobId=" + job.id)

  CATCH Exception e
    // Rollback transaction

    job.status = JobStatus.FAILED
    job.completedAt = NOW()
    job.errorMessage = e.getMessage()
    copyJobRepository.save(job)

    LOG_ERROR("Async folder copy failed: jobId=" + job.id + ", error=" + e.getMessage())

    // Send error notification
    notificationService.sendCopyFailedNotification(job.userId, job.newName)
  END TRY
END FUNCTION
```

**Post-conditions**:
- **Sync (<= 50 items)**: Folder copied immediately, returns new folder ID
- **Async (51-500 items)**: Job created, returns job ID for status tracking
- All sub-folders, decks, and cards deep copied with new UUIDs
- All copied cards reset to Box 1 (fresh start)
- Destination parent stats invalidated
- Notification sent on async completion

**Transaction Scope**:
- **Sync**: Single transaction for entire copy
- **Async**: Separate transaction in background thread

**Performance Considerations**:
- Batch insert for cards and card positions (1000 per batch)
- Flush and clear entity manager every 1000 entities to avoid memory issues
- Progress tracking updates every 100 items processed

**Domain Events Published**:
- FolderCopiedEvent (on completion)

**Error Scenarios**:
- Source folder not found → 404 ResourceNotFoundException
- Destination not found → 404 ResourceNotFoundException
- Max depth exceeded → 422 MaxDepthExceededException
- Folder too large (>500 items) → 422 FolderTooLargeException
- Database error → 500 InternalServerError
- Async job timeout (>5 min) → Job status FAILED, notification sent

---

### Method: deleteFolder(folderId, userId)

**Purpose**: Soft delete folder and all its contents (sub-folders, decks, cards)

**Input**:
- folderId: UUID
- userId: UUID

**Output**:
- void (204 No Content)

**Pre-conditions**:
- User owns the folder
- Folder exists and not already deleted

**Pseudo-code**:
```
FUNCTION deleteFolder(folderId, userId):
  // Transaction begins

  // Step 1: Load folder
  folder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(folderId, userId)
  IF folder IS NULL THEN
    THROW ResourceNotFoundException("Folder not found")
  END IF

  // Step 2: Soft delete folder
  folder.deletedAt = NOW()
  folder.updatedAt = NOW()
  folderRepository.save(folder)

  // Step 3: Soft delete all descendants (recursive)
  descendants = folderRepository.findDescendants(userId, folder.path + "/")
  FOR EACH desc IN descendants DO
    desc.deletedAt = NOW()
    desc.updatedAt = NOW()
    folderRepository.save(desc)
  END FOR

  // Step 4: Soft delete all decks in folder and descendants
  allFolderIds = [folder.id] + descendants.map(d => d.id)
  decks = deckRepository.findByFolderIdInAndDeletedAtIsNull(allFolderIds)
  FOR EACH deck IN decks DO
    deck.deletedAt = NOW()
    deck.updatedAt = NOW()
    deckRepository.save(deck)

    // Step 5: Soft delete all cards in deck
    cards = cardRepository.findByDeckIdAndDeletedAtIsNull(deck.id)
    FOR EACH card IN cards DO
      card.deletedAt = NOW()
      card.updatedAt = NOW()
      cardRepository.save(card)
    END FOR
  END FOR

  // Step 6: Invalidate parent folder stats
  IF folder.parentFolder IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(folder.parentFolder.id)
  END IF

  // Transaction commits

  // Step 7: Publish domain event (async)
  publishEvent(FolderDeletedEvent(
    folderId=folder.id,
    userId=userId,
    itemsDeleted={
      folders: 1 + SIZE(descendants),
      decks: SIZE(decks),
      cards: SUM of cards in all decks
    },
    timestamp=NOW()
  ))

  // Step 8: Log event
  LOG_INFO("Folder soft deleted: " + folder.name + " by user: " + userId)

  RETURN // void
END FUNCTION
```

**Post-conditions**:
- Folder marked as deleted (deletedAt set)
- All sub-folders marked as deleted
- All decks in folder marked as deleted
- All cards in decks marked as deleted
- Parent folder stats invalidated
- FolderDeletedEvent published

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After all deletions
- ROLLBACK: On database error

**Soft Delete Strategy**:
- Items not physically removed from database
- Queries filter by `deleted_at IS NULL`
- Allows restore functionality (future feature)
- Cleanup job can permanently delete after 30 days (future)

**Domain Events Published**:
- FolderDeletedEvent

**Error Scenarios**:
- Folder not found → 404 ResourceNotFoundException
- Folder already deleted → 404 ResourceNotFoundException
- Database error → 500 InternalServerError

---

### Method: getFolderStats(folderId, userId)

**Purpose**: Calculate and return folder statistics (recursive, includes all sub-folders)

**Input**:
- folderId: UUID
- userId: UUID

**Output**:
- FolderStatsResponse
  - folderId: UUID
  - totalCardsCount: Integer (recursive)
  - dueCardsCount: Integer (recursive)
  - newCardsCount: Integer (recursive)
  - matureCardsCount: Integer (recursive)
  - lastComputedAt: Timestamp

**Pre-conditions**:
- User owns the folder
- Folder exists and not deleted

**Pseudo-code**:
```
FUNCTION getFolderStats(folderId, userId):
  // NO explicit transaction (read-only)

  // Step 1: Load folder
  folder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(folderId, userId)
  IF folder IS NULL THEN
    THROW ResourceNotFoundException("Folder not found")
  END IF

  // Step 2: Check if cached stats are fresh (< 5 minutes old)
  cachedStats = folderStatsRepository.findByFolderIdAndUserId(folderId, userId)

  IF cachedStats IS NOT NULL AND cachedStats.lastComputedAt IS NOT NULL THEN
    cacheAge = NOW() - cachedStats.lastComputedAt
    IF cacheAge < 5_MINUTES THEN
      // Return cached stats
      LOG_DEBUG("Returning cached folder stats: folderId=" + folderId)
      RETURN folderStatsMapper.toResponse(cachedStats)
    END IF
  END IF

  // Step 3: Recalculate stats using Visitor pattern
  statsVisitor = NEW FolderStatsVisitor()
  freshStats = statsVisitor.visit(folder) // Recursive calculation

  // Step 4: Update cache
  IF cachedStats IS NULL THEN
    cachedStats = NEW FolderStats()
    cachedStats.folderId = folderId
    cachedStats.userId = userId
  END IF

  cachedStats.totalCardsCount = freshStats.totalCardsCount
  cachedStats.dueCardsCount = freshStats.dueCardsCount
  cachedStats.newCardsCount = freshStats.newCardsCount
  cachedStats.matureCardsCount = freshStats.matureCardsCount
  cachedStats.lastComputedAt = NOW()
  folderStatsRepository.save(cachedStats)

  // Step 5: Map to response
  response = folderStatsMapper.toResponse(cachedStats)

  RETURN response
END FUNCTION
```

**Visitor Pattern Implementation: FolderStatsVisitor.visit(folder)**
```
FUNCTION visit(folder):
  stats = NEW FolderStats()
  stats.folderId = folder.id

  totalCards = 0
  dueCards = 0
  newCards = 0
  matureCards = 0

  // Step 1: Count cards in this folder's decks
  decks = deckRepository.findByFolderIdAndDeletedAtIsNull(folder.id)
  FOR EACH deck IN decks DO
    cards = cardRepository.findByDeckIdAndDeletedAtIsNull(deck.id)
    totalCards = totalCards + SIZE(cards)

    FOR EACH card IN cards DO
      position = cardBoxPositionRepository.findByCardIdAndUserId(card.id, folder.userId)
      IF position IS NOT NULL THEN
        IF position.dueDate <= CURRENT_DATE() THEN
          dueCards = dueCards + 1
        END IF
        IF position.currentBox == 1 AND position.reviewCount == 0 THEN
          newCards = newCards + 1
        END IF
        IF position.currentBox >= 5 THEN
          matureCards = matureCards + 1
        END IF
      END IF
    END FOR
  END FOR

  // Step 2: Recursively visit sub-folders
  subFolders = folderRepository.findByParentFolderIdAndDeletedAtIsNull(folder.id)
  FOR EACH subFolder IN subFolders DO
    subStats = visit(subFolder) // Recursive call
    totalCards = totalCards + subStats.totalCardsCount
    dueCards = dueCards + subStats.dueCardsCount
    newCards = newCards + subStats.newCardsCount
    matureCards = matureCards + subStats.matureCardsCount
  END FOR

  stats.totalCardsCount = totalCards
  stats.dueCardsCount = dueCards
  stats.newCardsCount = newCards
  stats.matureCardsCount = matureCards

  RETURN stats
END FUNCTION
```

**Post-conditions**:
- Fresh stats calculated and returned
- Stats cache updated with timestamp
- Query optimized with eager loading where possible

**Transaction Scope**:
- Implicit read-only transaction

**Performance Considerations**:
- Cached stats used if < 5 minutes old
- Visitor pattern allows recursive traversal
- For very large folders (>1000 cards), consider async recalculation
- Denormalized folder_stats table improves read performance

**Error Scenarios**:
- Folder not found → 404 ResourceNotFoundException
- Database error → 500 InternalServerError

---

## 3. DeckService

### Method: createDeck(request, userId)

**Purpose**: Create a new flashcard deck in a folder or at root level

**Input**:
- request: CreateDeckRequest
  - name: String (1-100 chars, required)
  - description: String (max 500 chars, optional)
  - folderId: UUID (nullable, NULL = root level)
- userId: UUID

**Output**:
- DeckResponse

**Pre-conditions**:
- User is authenticated
- Folder (if specified) exists and belongs to user
- Name is unique within folder

**Pseudo-code**:
```
FUNCTION createDeck(request, userId):
  // Transaction begins

  // Step 1: Validate and load folder (if specified)
  folder = NULL
  IF request.folderId IS NOT NULL THEN
    folder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(request.folderId, userId)
    IF folder IS NULL THEN
      THROW ResourceNotFoundException("Folder not found")
    END IF
  END IF

  // Step 2: Check name uniqueness within folder/root
  existingDeck = deckRepository.findByUserIdAndFolderIdAndNameAndDeletedAtIsNull(
    userId, request.folderId, TRIM(request.name)
  )
  IF existingDeck IS NOT NULL THEN
    THROW DuplicateResourceException("A deck with this name already exists in this location")
  END IF

  // Step 3: Create deck entity
  deck = NEW Deck()
  deck.id = generateUUID()
  deck.userId = userId
  deck.folder = folder
  deck.name = TRIM(request.name)
  deck.description = request.description
  deck.createdAt = NOW()
  deck.updatedAt = NOW()
  deck.deletedAt = NULL

  // Step 4: Save to database
  savedDeck = deckRepository.save(deck)

  // Step 5: Invalidate folder stats (if in folder)
  IF folder IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(folder.id)
  END IF

  // Transaction commits

  // Step 6: Publish domain event (async)
  publishEvent(DeckCreatedEvent(
    deckId=savedDeck.id,
    userId=userId,
    folderId=folder?.id,
    timestamp=NOW()
  ))

  // Step 7: Log event
  LOG_INFO("Deck created: " + savedDeck.name + " by user: " + userId)

  // Step 8: Map to response
  response = deckMapper.toResponse(savedDeck)

  RETURN response
END FUNCTION
```

**Post-conditions**:
- Deck created in database
- Folder stats invalidated (if in folder)
- DeckCreatedEvent published
- Deck appears in folder/root listing

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After saving deck
- ROLLBACK: On validation or database error

**Domain Events Published**:
- DeckCreatedEvent

**Error Scenarios**:
- Folder not found → 404 ResourceNotFoundException
- Duplicate name → 409 DuplicateResourceException
- Validation error → 400 ValidationException
- Database error → 500 InternalServerError

---

### Method: moveDeck(deckId, newFolderId, userId)

**Purpose**: Move deck to a different folder

**Input**:
- deckId: UUID
- newFolderId: UUID (nullable, NULL = move to root)
- userId: UUID

**Output**:
- DeckResponse

**Pre-conditions**:
- User owns the deck
- Deck exists and not deleted
- New folder exists (if specified) and belongs to user

**Pseudo-code**:
```
FUNCTION moveDeck(deckId, newFolderId, userId):
  // Transaction begins

  // Step 1: Load deck
  deck = deckRepository.findByIdAndUserIdAndDeletedAtIsNull(deckId, userId)
  IF deck IS NULL THEN
    THROW ResourceNotFoundException("Deck not found")
  END IF

  // Step 2: Load new folder (if specified)
  newFolder = NULL
  IF newFolderId IS NOT NULL THEN
    newFolder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(newFolderId, userId)
    IF newFolder IS NULL THEN
      THROW ResourceNotFoundException("Destination folder not found")
    END IF
  END IF

  // Step 3: Store old folder for stats invalidation
  oldFolder = deck.folder

  // Step 4: Update deck
  deck.folder = newFolder
  deck.updatedAt = NOW()
  deckRepository.save(deck)

  // Step 5: Invalidate old and new folder stats
  IF oldFolder IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(oldFolder.id)
  END IF
  IF newFolder IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(newFolder.id)
  END IF

  // Transaction commits

  // Step 6: Publish domain event (async)
  publishEvent(DeckMovedEvent(
    deckId=deck.id,
    oldFolderId=oldFolder?.id,
    newFolderId=newFolder?.id,
    userId=userId,
    timestamp=NOW()
  ))

  // Step 7: Log event
  LOG_INFO("Deck moved: " + deck.name + " to folder: " + newFolder?.name)

  // Step 8: Map to response
  response = deckMapper.toResponse(deck)

  RETURN response
END FUNCTION
```

**Post-conditions**:
- Deck's folder_id updated
- Old and new folder stats invalidated
- DeckMovedEvent published

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After updating deck
- ROLLBACK: On validation or database error

**Domain Events Published**:
- DeckMovedEvent

**Error Scenarios**:
- Deck not found → 404 ResourceNotFoundException
- Destination not found → 404 ResourceNotFoundException
- Database error → 500 InternalServerError

---

### Method: copyDeck(deckId, destinationFolderId, newName, userId)

**Purpose**: Create a copy of deck with all cards (async for large decks)

**Input**:
- deckId: UUID (source deck)
- destinationFolderId: UUID (nullable, NULL = root)
- newName: String
- userId: UUID

**Output**:
- CopyJobResponse
  - jobId: UUID (for async operations)
  - status: JobStatus (SYNC_COMPLETED / ASYNC_PROCESSING)
  - deckId: UUID (if sync completed)

**Pre-conditions**:
- User owns source deck
- Destination folder exists (if specified) and belongs to user
- Card count <= 10,000 (hard limit)

**Pseudo-code**:
```
FUNCTION copyDeck(deckId, destinationFolderId, newName, userId):
  // Step 1: Load source deck
  sourceDeck = deckRepository.findByIdAndUserIdAndDeletedAtIsNull(deckId, userId)
  IF sourceDeck IS NULL THEN
    THROW ResourceNotFoundException("Source deck not found")
  END IF

  // Step 2: Load destination folder (if specified)
  destinationFolder = NULL
  IF destinationFolderId IS NOT NULL THEN
    destinationFolder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(
      destinationFolderId, userId
    )
    IF destinationFolder IS NULL THEN
      THROW ResourceNotFoundException("Destination folder not found")
    END IF
  END IF

  // Step 3: Count cards in source deck
  cardCount = cardRepository.countByDeckIdAndDeletedAtIsNull(sourceDeck.id)

  IF cardCount > 10000 THEN
    THROW DeckTooLargeException(
      "Deck too large to copy (max 10,000 cards). Total: " + cardCount
    )
  END IF

  // Step 4: Auto-generate unique name if duplicate
  finalName = newName
  counter = 1
  WHILE deckRepository.existsByFolderAndName(destinationFolderId, finalName, userId) DO
    counter = counter + 1
    finalName = newName + " (Copy " + counter + ")"
  END WHILE

  // Step 5: Decide sync vs async
  IF cardCount <= 1000 THEN
    // SYNC COPY (small deck)
    // Transaction begins

    deckCopy = copyDeckSync(sourceDeck, destinationFolder, finalName, userId)

    // Transaction commits

    LOG_INFO("Deck copied synchronously: " + cardCount + " cards")

    RETURN CopyJobResponse(
      jobId=NULL,
      status=JobStatus.SYNC_COMPLETED,
      deckId=deckCopy.id
    )
  ELSE
    // ASYNC COPY (large deck)
    job = NEW CopyJob()
    job.id = generateUUID()
    job.userId = userId
    job.type = JobType.DECK_COPY
    job.sourceDeckId = deckId
    job.destinationFolderId = destinationFolderId
    job.newName = finalName
    job.status = JobStatus.PENDING
    job.totalItems = cardCount
    job.processedItems = 0
    job.createdAt = NOW()
    copyJobRepository.save(job)

    @Async
    executeCopyDeckAsync(job)

    LOG_INFO("Deck copy scheduled (async): " + cardCount + " cards, jobId=" + job.id)

    RETURN CopyJobResponse(
      jobId=job.id,
      status=JobStatus.ASYNC_PROCESSING,
      deckId=NULL
    )
  END IF
END FUNCTION
```

**Helper Function: copyDeckSync(sourceDeck, destFolder, name, userId)**
```
FUNCTION copyDeckSync(sourceDeck, destFolder, name, userId):
  // Step 1: Create deck copy
  deckCopy = NEW Deck()
  deckCopy.id = generateUUID()
  deckCopy.userId = userId
  deckCopy.folder = destFolder
  deckCopy.name = name
  deckCopy.description = sourceDeck.description
  deckCopy.createdAt = NOW()
  deckCopy.updatedAt = NOW()
  deckRepository.save(deckCopy)

  // Step 2: Batch copy cards
  cards = cardRepository.findByDeckIdAndDeletedAtIsNull(sourceDeck.id)

  cardBatch = []
  FOR EACH card IN cards DO
    cardCopy = NEW Card()
    cardCopy.id = generateUUID()
    cardCopy.deckId = deckCopy.id
    cardCopy.front = card.front
    cardCopy.back = card.back
    cardCopy.createdAt = NOW()
    cardCopy.updatedAt = NOW()
    cardBatch.ADD(cardCopy)

    IF SIZE(cardBatch) >= 1000 THEN
      cardRepository.batchInsert(cardBatch)
      cardBatch = []
    END IF
  END FOR

  IF SIZE(cardBatch) > 0 THEN
    cardRepository.batchInsert(cardBatch)
  END IF

  // Step 3: Initialize SRS positions (Box 1)
  copiedCards = cardRepository.findByDeckIdAndDeletedAtIsNull(deckCopy.id)

  positionBatch = []
  FOR EACH cardCopy IN copiedCards DO
    position = NEW CardBoxPosition()
    position.id = generateUUID()
    position.cardId = cardCopy.id
    position.userId = userId
    position.currentBox = 1
    position.intervalDays = 1
    position.dueDate = CURRENT_DATE() + 1
    position.lastReviewedAt = NULL
    position.reviewCount = 0
    position.lapseCount = 0
    position.easeFactor = 2.5
    position.createdAt = NOW()
    position.updatedAt = NOW()
    positionBatch.ADD(position)

    IF SIZE(positionBatch) >= 1000 THEN
      cardBoxPositionRepository.batchInsert(positionBatch)
      positionBatch = []
    END IF
  END FOR

  IF SIZE(positionBatch) > 0 THEN
    cardBoxPositionRepository.batchInsert(positionBatch)
  END IF

  // Step 4: Invalidate folder stats
  IF destFolder IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(destFolder.id)
  END IF

  RETURN deckCopy
END FUNCTION
```

**Post-conditions**:
- Deck copied with all cards
- All cards reset to Box 1
- Destination folder stats invalidated
- Notification sent for async operations

**Transaction Scope**:
- **Sync**: Single transaction for entire copy
- **Async**: Separate transaction in background thread

**Domain Events Published**:
- DeckCopiedEvent

**Error Scenarios**:
- Source deck not found → 404 ResourceNotFoundException
- Destination not found → 404 ResourceNotFoundException
- Deck too large (>10,000 cards) → 422 DeckTooLargeException
- Database error → 500 InternalServerError

---

### Method: deleteDeck(deckId, userId)

**Purpose**: Soft delete deck and all its cards

**Input**:
- deckId: UUID
- userId: UUID

**Output**:
- void (204 No Content)

**Pre-conditions**:
- User owns the deck
- Deck exists and not already deleted

**Pseudo-code**:
```
FUNCTION deleteDeck(deckId, userId):
  // Transaction begins

  // Step 1: Load deck
  deck = deckRepository.findByIdAndUserIdAndDeletedAtIsNull(deckId, userId)
  IF deck IS NULL THEN
    THROW ResourceNotFoundException("Deck not found")
  END IF

  // Step 2: Soft delete deck
  deck.deletedAt = NOW()
  deck.updatedAt = NOW()
  deckRepository.save(deck)

  // Step 3: Soft delete all cards in deck
  cards = cardRepository.findByDeckIdAndDeletedAtIsNull(deck.id)
  FOR EACH card IN cards DO
    card.deletedAt = NOW()
    card.updatedAt = NOW()
    cardRepository.save(card)
  END FOR

  // Step 4: Invalidate folder stats
  IF deck.folder IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(deck.folder.id)
  END IF

  // Transaction commits

  // Step 5: Publish domain event (async)
  publishEvent(DeckDeletedEvent(
    deckId=deck.id,
    userId=userId,
    cardsDeleted=SIZE(cards),
    timestamp=NOW()
  ))

  // Step 6: Log event
  LOG_INFO("Deck soft deleted: " + deck.name + " with " + SIZE(cards) + " cards")

  RETURN // void
END FUNCTION
```

**Post-conditions**:
- Deck marked as deleted
- All cards marked as deleted
- Folder stats invalidated
- DeckDeletedEvent published

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After all deletions
- ROLLBACK: On database error

**Domain Events Published**:
- DeckDeletedEvent

**Error Scenarios**:
- Deck not found → 404 ResourceNotFoundException
- Database error → 500 InternalServerError

---

## 4. CardService

### Method: createCard(deckId, request, userId)

**Purpose**: Create a new flashcard in a deck

**Input**:
- deckId: UUID
- request: CreateCardRequest
  - front: String (1-5000 chars, required)
  - back: String (1-5000 chars, required)
- userId: UUID

**Output**:
- CardResponse

**Pre-conditions**:
- User owns the deck
- Deck exists and not deleted
- Front and back are not empty

**Pseudo-code**:
```
FUNCTION createCard(deckId, request, userId):
  // Transaction begins

  // Step 1: Load and validate deck
  deck = deckRepository.findByIdAndUserIdAndDeletedAtIsNull(deckId, userId)
  IF deck IS NULL THEN
    THROW ResourceNotFoundException("Deck not found")
  END IF

  // Step 2: Validate input
  IF TRIM(request.front) IS EMPTY THEN
    THROW ValidationException("Front text is required")
  END IF
  IF TRIM(request.back) IS EMPTY THEN
    THROW ValidationException("Back text is required")
  END IF
  IF LENGTH(request.front) > 5000 THEN
    THROW ValidationException("Front text exceeds 5000 characters")
  END IF
  IF LENGTH(request.back) > 5000 THEN
    THROW ValidationException("Back text exceeds 5000 characters")
  END IF

  // Step 3: Create card entity
  card = NEW Card()
  card.id = generateUUID()
  card.deckId = deck.id
  card.front = TRIM(request.front)
  card.back = TRIM(request.back)
  card.createdAt = NOW()
  card.updatedAt = NOW()
  card.deletedAt = NULL

  // Step 4: Save to database
  savedCard = cardRepository.save(card)

  // Step 5: Initialize SRS position (Box 1, new card)
  position = NEW CardBoxPosition()
  position.id = generateUUID()
  position.cardId = savedCard.id
  position.userId = userId
  position.currentBox = 1
  position.intervalDays = 1
  position.dueDate = CURRENT_DATE() + 1 // Due tomorrow
  position.lastReviewedAt = NULL
  position.reviewCount = 0
  position.lapseCount = 0
  position.easeFactor = 2.5 // Default ease
  position.createdAt = NOW()
  position.updatedAt = NOW()
  cardBoxPositionRepository.save(position)

  // Step 6: Invalidate folder stats (if deck in folder)
  IF deck.folder IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(deck.folder.id)
  END IF

  // Transaction commits

  // Step 7: Publish domain event (async)
  publishEvent(CardCreatedEvent(
    cardId=savedCard.id,
    deckId=deck.id,
    userId=userId,
    timestamp=NOW()
  ))

  // Step 8: Log event
  LOG_INFO("Card created in deck: " + deck.name)

  // Step 9: Map to response
  response = cardMapper.toResponse(savedCard)

  RETURN response
END FUNCTION
```

**Post-conditions**:
- Card created in database
- CardBoxPosition initialized (Box 1, due tomorrow)
- Folder stats invalidated
- CardCreatedEvent published

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After saving card and position
- ROLLBACK: On validation or database error

**Domain Events Published**:
- CardCreatedEvent

**Error Scenarios**:
- Deck not found → 404 ResourceNotFoundException
- Empty front/back → 400 ValidationException
- Text too long → 400 ValidationException
- Database error → 500 InternalServerError

---

### Method: updateCard(cardId, request, userId)

**Purpose**: Update card front and back text

**Input**:
- cardId: UUID
- request: UpdateCardRequest
  - front: String (1-5000 chars)
  - back: String (1-5000 chars)
- userId: UUID

**Output**:
- CardResponse

**Pre-conditions**:
- User owns the card (through deck)
- Card exists and not deleted

**Pseudo-code**:
```
FUNCTION updateCard(cardId, request, userId):
  // Transaction begins

  // Step 1: Load card with deck (for authorization)
  card = cardRepository.findByIdWithDeck(cardId)
  IF card IS NULL OR card.deletedAt IS NOT NULL THEN
    THROW ResourceNotFoundException("Card not found")
  END IF

  // Step 2: Verify ownership
  IF card.deck.userId != userId THEN
    THROW ForbiddenException("You don't have permission to update this card")
  END IF

  // Step 3: Validate input
  IF TRIM(request.front) IS EMPTY THEN
    THROW ValidationException("Front text is required")
  END IF
  IF TRIM(request.back) IS EMPTY THEN
    THROW ValidationException("Back text is required")
  END IF
  IF LENGTH(request.front) > 5000 OR LENGTH(request.back) > 5000 THEN
    THROW ValidationException("Text exceeds 5000 characters")
  END IF

  // Step 4: Update card
  card.front = TRIM(request.front)
  card.back = TRIM(request.back)
  card.updatedAt = NOW()
  cardRepository.save(card)

  // Transaction commits

  // Step 5: Publish domain event (async)
  publishEvent(CardUpdatedEvent(
    cardId=card.id,
    deckId=card.deck.id,
    userId=userId,
    timestamp=NOW()
  ))

  // Step 6: Log event
  LOG_INFO("Card updated: cardId=" + cardId)

  // Step 7: Map to response
  response = cardMapper.toResponse(card)

  RETURN response
END FUNCTION
```

**Post-conditions**:
- Card front and back updated
- updatedAt timestamp refreshed
- CardUpdatedEvent published

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After saving card
- ROLLBACK: On validation or database error

**Domain Events Published**:
- CardUpdatedEvent

**Error Scenarios**:
- Card not found → 404 ResourceNotFoundException
- Forbidden access → 403 ForbiddenException
- Validation error → 400 ValidationException
- Database error → 500 InternalServerError

---

### Method: deleteCard(cardId, userId)

**Purpose**: Soft delete card

**Input**:
- cardId: UUID
- userId: UUID

**Output**:
- void (204 No Content)

**Pre-conditions**:
- User owns the card
- Card exists and not deleted

**Pseudo-code**:
```
FUNCTION deleteCard(cardId, userId):
  // Transaction begins

  // Step 1: Load card with deck (for authorization)
  card = cardRepository.findByIdWithDeck(cardId)
  IF card IS NULL OR card.deletedAt IS NOT NULL THEN
    THROW ResourceNotFoundException("Card not found")
  END IF

  // Step 2: Verify ownership
  IF card.deck.userId != userId THEN
    THROW ForbiddenException("You don't have permission to delete this card")
  END IF

  // Step 3: Soft delete card
  card.deletedAt = NOW()
  card.updatedAt = NOW()
  cardRepository.save(card)

  // Step 4: Invalidate folder stats (if deck in folder)
  IF card.deck.folder IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(card.deck.folder.id)
  END IF

  // Transaction commits

  // Step 5: Publish domain event (async)
  publishEvent(CardDeletedEvent(
    cardId=card.id,
    deckId=card.deck.id,
    userId=userId,
    timestamp=NOW()
  ))

  // Step 6: Log event
  LOG_INFO("Card soft deleted: cardId=" + cardId)

  RETURN // void
END FUNCTION
```

**Post-conditions**:
- Card marked as deleted
- Folder stats invalidated
- CardDeletedEvent published

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After deletion
- ROLLBACK: On database error

**Domain Events Published**:
- CardDeletedEvent

**Error Scenarios**:
- Card not found → 404 ResourceNotFoundException
- Forbidden access → 403 ForbiddenException
- Database error → 500 InternalServerError

---

## 5. ReviewService

### Method: getDueCards(userId, scope, scopeId, reviewOrder, limit)

**Purpose**: Get cards due for review based on SRS schedule

**Input**:
- userId: UUID
- scope: Enum (ALL / FOLDER / DECK)
- scopeId: UUID (required if scope = FOLDER or DECK)
- reviewOrder: Enum (ASCENDING / DESCENDING / RANDOM)
- limit: Integer (default 200, max 200)

**Output**:
- ReviewSessionResponse
  - cards: List<CardReviewDto>
  - totalCount: Integer
  - sessionId: UUID (for tracking)

**Pre-conditions**:
- User is authenticated
- Scope folder/deck exists and belongs to user (if specified)
- Daily review limit not exceeded

**Pseudo-code**:
```
FUNCTION getDueCards(userId, scope, scopeId, reviewOrder, limit):
  // NO explicit transaction (read-only query)

  // Step 1: Check daily review limit
  srsSettings = srsSettingsRepository.findByUserId(userId)
  todayReviewCount = reviewLogRepository.countByUserIdAndDate(userId, CURRENT_DATE())

  IF todayReviewCount >= srsSettings.maxReviewsPerDay THEN
    // User can override in UI, but warn
    LOG_WARN("User reached daily review limit: userId=" + userId)
  END IF

  // Step 2: Build query based on scope
  dueCards = []

  SWITCH scope:
    CASE ALL:
      // Get all due cards for user
      dueCards = cardBoxPositionRepository.findDueCards(
        userId=userId,
        dueDate=CURRENT_DATE(),
        limit=limit
      )

    CASE FOLDER:
      // Validate folder
      folder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(scopeId, userId)
      IF folder IS NULL THEN
        THROW ResourceNotFoundException("Folder not found")
      END IF

      // Get all due cards in folder and descendants
      dueCards = cardBoxPositionRepository.findDueCardsInFolderTree(
        userId=userId,
        folderPath=folder.path,
        dueDate=CURRENT_DATE(),
        limit=limit
      )

    CASE DECK:
      // Validate deck
      deck = deckRepository.findByIdAndUserIdAndDeletedAtIsNull(scopeId, userId)
      IF deck IS NULL THEN
        THROW ResourceNotFoundException("Deck not found")
      END IF

      // Get due cards in specific deck
      dueCards = cardBoxPositionRepository.findDueCardsInDeck(
        userId=userId,
        deckId=scopeId,
        dueDate=CURRENT_DATE(),
        limit=limit
      )
  END SWITCH

  // Step 3: Apply review order strategy
  reviewStrategy = reviewStrategyFactory.getStrategy(reviewOrder)
  orderedCards = reviewStrategy.order(dueCards)

  // Step 4: Create review session (for tracking)
  sessionId = generateUUID()
  // Session stored in memory or session store (optional for MVP)

  // Step 5: Map to response
  response = NEW ReviewSessionResponse()
  response.sessionId = sessionId
  response.cards = cardMapper.toReviewDtoList(orderedCards)
  response.totalCount = SIZE(orderedCards)

  RETURN response
END FUNCTION
```

**Review Order Strategies:**
```
INTERFACE ReviewOrderStrategy:
  FUNCTION order(cards): List<Card>
END INTERFACE

CLASS AscendingReviewStrategy IMPLEMENTS ReviewOrderStrategy:
  FUNCTION order(cards):
    RETURN SORT(cards, BY currentBox ASC, dueDate ASC)
  END FUNCTION
END CLASS

CLASS DescendingReviewStrategy IMPLEMENTS ReviewOrderStrategy:
  FUNCTION order(cards):
    RETURN SORT(cards, BY currentBox DESC, dueDate ASC)
  END FUNCTION
END CLASS

CLASS RandomReviewStrategy IMPLEMENTS ReviewOrderStrategy:
  FUNCTION order(cards):
    RETURN SHUFFLE(cards)
  END FUNCTION
END CLASS
```

**Critical Database Query (findDueCards):**
```sql
-- Uses composite index: idx_card_box_user_due (user_id, due_date, current_box)
SELECT c.id, c.front, c.back, c.deck_id,
       cbp.current_box, cbp.interval_days, cbp.due_date,
       d.name as deck_name, d.folder_id
FROM card_box_position cbp
JOIN cards c ON c.id = cbp.card_id AND c.deleted_at IS NULL
JOIN decks d ON d.id = c.deck_id AND d.deleted_at IS NULL
WHERE cbp.user_id = :userId
  AND cbp.due_date <= :currentDate
ORDER BY cbp.due_date ASC, cbp.current_box ASC
LIMIT :limit;
```

**Post-conditions**:
- List of due cards returned
- Cards ordered by strategy
- Session tracked (optional)

**Transaction Scope**:
- Implicit read-only transaction

**Performance Optimization**:
- Use composite index on (user_id, due_date, current_box)
- Limit query to 200 cards max
- Batch fetch with JOINs to avoid N+1

**Error Scenarios**:
- Folder/Deck not found → 404 ResourceNotFoundException
- Database error → 500 InternalServerError

---

### Method: submitReview(cardId, rating, userId)

**Purpose**: Submit rating for a reviewed card and update SRS position

**Input**:
- cardId: UUID
- rating: Enum (AGAIN / HARD / GOOD / EASY)
- userId: UUID

**Output**:
- ReviewResultResponse
  - cardId: UUID
  - previousBox: Integer
  - newBox: Integer
  - newDueDate: LocalDate
  - nextInterval: Integer

**Pre-conditions**:
- User owns the card
- Card exists and not deleted
- Card is due (due_date <= today)

**Pseudo-code**:
```
FUNCTION submitReview(cardId, rating, userId):
  // Transaction begins

  // Step 1: Load card and current box position
  card = cardRepository.findByIdWithDeck(cardId)
  IF card IS NULL OR card.deletedAt IS NOT NULL THEN
    THROW ResourceNotFoundException("Card not found")
  END IF

  // Verify ownership
  IF card.deck.userId != userId THEN
    THROW ForbiddenException("You don't have permission to review this card")
  END IF

  position = cardBoxPositionRepository.findByCardIdAndUserId(cardId, userId)
  IF position IS NULL THEN
    THROW ResourceNotFoundException("Card position not found")
  END IF

  // Step 2: Load user SRS settings
  srsSettings = srsSettingsRepository.findByUserId(userId)

  // Step 3: Calculate new position using SRS algorithm
  previousBox = position.currentBox

  srsCalculation = srsService.calculateNextReview(
    currentBox=previousBox,
    rating=rating,
    forgottenCardAction=srsSettings.forgottenCardAction,
    moveDownBoxes=srsSettings.moveDownBoxes
  )

  newBox = srsCalculation.newBox
  newInterval = srsCalculation.intervalDays
  newDueDate = CURRENT_DATE() + newInterval

  // Step 4: Update card box position
  position.currentBox = newBox
  position.intervalDays = newInterval
  position.dueDate = newDueDate
  position.lastReviewedAt = NOW()
  position.reviewCount = position.reviewCount + 1

  IF rating == Rating.AGAIN THEN
    position.lapseCount = position.lapseCount + 1
  END IF

  position.updatedAt = NOW()
  cardBoxPositionRepository.save(position)

  // Step 5: Create review log
  reviewLog = NEW ReviewLog()
  reviewLog.id = generateUUID()
  reviewLog.cardId = cardId
  reviewLog.userId = userId
  reviewLog.rating = rating
  reviewLog.previousBox = previousBox
  reviewLog.newBox = newBox
  reviewLog.intervalDays = newInterval
  reviewLog.reviewedAt = NOW()
  reviewLogRepository.save(reviewLog)

  // Step 6: Update user stats (synchronous for streak)
  statsService.updateUserStats(userId, reviewedToday=TRUE)

  // Step 7: Invalidate folder stats (async, not critical)
  IF card.deck.folder IS NOT NULL THEN
    folderStatsRepository.invalidate(card.deck.folder.id)
  END IF

  // Transaction commits

  // Step 8: Publish domain event (async)
  publishEvent(CardReviewedEvent(
    cardId=cardId,
    userId=userId,
    rating=rating,
    previousBox=previousBox,
    newBox=newBox,
    timestamp=NOW()
  ))

  // Step 9: Log event
  LOG_INFO("Card reviewed: cardId=" + cardId + ", rating=" + rating +
           ", box: " + previousBox + " → " + newBox)

  // Step 10: Map to response
  response = NEW ReviewResultResponse()
  response.cardId = cardId
  response.previousBox = previousBox
  response.newBox = newBox
  response.newDueDate = newDueDate
  response.nextInterval = newInterval

  RETURN response
END FUNCTION
```

**Post-conditions**:
- CardBoxPosition updated with new box, interval, due date
- ReviewLog created
- User stats updated (streak, cards learned count)
- Folder stats invalidated
- CardReviewedEvent published

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After updating position, creating log, updating stats
- ROLLBACK: On database error

**Domain Events Published**:
- CardReviewedEvent (handled by UserStatsUpdateListener)

**Error Scenarios**:
- Card not found → 404 ResourceNotFoundException
- Forbidden access → 403 ForbiddenException
- Invalid rating → 400 ValidationException
- Database error → 500 InternalServerError

---

### Method: undoReview(userId)

**Purpose**: Undo the last review submission in current session

**Input**:
- userId: UUID

**Output**:
- UndoResultResponse
  - success: Boolean
  - cardId: UUID (card that was undone)

**Pre-conditions**:
- User has reviewed at least one card in current session
- Last review was within last 5 minutes (safety limit)

**Pseudo-code**:
```
FUNCTION undoReview(userId):
  // Transaction begins

  // Step 1: Find last review log for user
  lastReview = reviewLogRepository.findLastReviewByUserId(
    userId,
    withinMinutes=5
  )

  IF lastReview IS NULL THEN
    THROW ValidationException("No recent review to undo")
  END IF

  // Step 2: Load card box position
  position = cardBoxPositionRepository.findByCardIdAndUserId(
    lastReview.cardId, userId
  )

  IF position IS NULL THEN
    THROW ResourceNotFoundException("Card position not found")
  END IF

  // Step 3: Rollback position to previous state
  position.currentBox = lastReview.previousBox
  // Restore previous interval (estimate based on box)
  position.intervalDays = calculateIntervalForBox(lastReview.previousBox)
  position.dueDate = CURRENT_DATE() // Mark as due again
  position.reviewCount = MAX(0, position.reviewCount - 1)

  IF lastReview.rating == Rating.AGAIN THEN
    position.lapseCount = MAX(0, position.lapseCount - 1)
  END IF

  position.updatedAt = NOW()
  cardBoxPositionRepository.save(position)

  // Step 4: Delete review log
  reviewLogRepository.delete(lastReview)

  // Step 5: Update user stats (decrement reviewed count)
  // NOTE: Don't break streak, just decrement today's count
  userStats = userStatsRepository.findByUserId(userId)
  IF userStats IS NOT NULL THEN
    userStats.totalCardsLearned = MAX(0, userStats.totalCardsLearned - 1)
    userStatsRepository.save(userStats)
  END IF

  // Transaction commits

  // Step 6: Log event
  LOG_INFO("Review undone: cardId=" + lastReview.cardId + ", userId=" + userId)

  // Step 7: Map to response
  response = NEW UndoResultResponse()
  response.success = TRUE
  response.cardId = lastReview.cardId

  RETURN response
END FUNCTION
```

**Post-conditions**:
- CardBoxPosition reverted to previous box
- ReviewLog deleted
- User stats decremented
- Card marked as due again

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After rollback and deletions
- ROLLBACK: On database error

**Error Scenarios**:
- No recent review → 400 ValidationException
- Database error → 500 InternalServerError

---

### Method: skipCard(cardId, userId)

**Purpose**: Skip card in current session, postpone to end of queue

**Input**:
- cardId: UUID
- userId: UUID

**Output**:
- SkipResultResponse
  - success: Boolean
  - message: String

**Pre-conditions**:
- User owns the card
- Card is in current review session

**Pseudo-code**:
```
FUNCTION skipCard(cardId, userId):
  // NO transaction needed (simple state update)

  // Step 1: Verify card ownership
  card = cardRepository.findByIdWithDeck(cardId)
  IF card IS NULL OR card.deck.userId != userId THEN
    THROW ForbiddenException("You don't have permission to skip this card")
  END IF

  // Step 2: Add to skip queue in session (in-memory or session store)
  sessionService.addToSkipQueue(userId, cardId)

  // Step 3: Log event
  LOG_INFO("Card skipped: cardId=" + cardId + ", userId=" + userId)

  // Step 4: Map to response
  response = NEW SkipResultResponse()
  response.success = TRUE
  response.message = "Card skipped. You'll see it again at the end."

  RETURN response
END FUNCTION
```

**Post-conditions**:
- Card added to skip queue
- Will appear again at end of session

**Transaction Scope**:
- None (in-memory operation)

**Error Scenarios**:
- Card not found → 404 ResourceNotFoundException
- Forbidden access → 403 ForbiddenException

---

## 6. SrsService

### Method: calculateNextReview(currentBox, rating, forgottenCardAction, moveDownBoxes)

**Purpose**: Calculate next box position and interval based on rating and SRS settings

**Input**:
- currentBox: Integer (1-7)
- rating: Enum (AGAIN / HARD / GOOD / EASY)
- forgottenCardAction: Enum (MOVE_TO_BOX_1 / MOVE_DOWN_N_BOXES / STAY_IN_BOX)
- moveDownBoxes: Integer (1-3, used if MOVE_DOWN_N_BOXES)

**Output**:
- SrsCalculationResult
  - newBox: Integer (1-7)
  - intervalDays: Integer

**Pre-conditions**:
- currentBox is valid (1-7)
- rating is valid

**Pseudo-code**:
```
FUNCTION calculateNextReview(currentBox, rating, forgottenCardAction, moveDownBoxes):
  // Constants: Box intervals (fixed for MVP)
  BOX_INTERVALS = [1, 3, 7, 14, 30, 60, 120] // Days for boxes 1-7

  newBox = currentBox
  intervalDays = BOX_INTERVALS[currentBox - 1] // Default to current interval

  SWITCH rating:
    CASE AGAIN:
      // Apply forgotten card action strategy
      forgottenStrategy = forgottenCardStrategyFactory.getStrategy(forgottenCardAction)
      newBox = forgottenStrategy.calculateNewBox(currentBox, moveDownBoxes)
      intervalDays = BOX_INTERVALS[newBox - 1]

    CASE HARD:
      // Stay in same box, reduce interval by half
      newBox = currentBox
      intervalDays = BOX_INTERVALS[currentBox - 1] / 2
      IF intervalDays < 1 THEN
        intervalDays = 1
      END IF

    CASE GOOD:
      // Move to next box (if not already at max)
      IF currentBox < 7 THEN
        newBox = currentBox + 1
      ELSE
        newBox = 7 // Stay at max box
      END IF
      intervalDays = BOX_INTERVALS[newBox - 1]

    CASE EASY:
      // Move to next box with 4x interval multiplier
      IF currentBox < 7 THEN
        newBox = currentBox + 1
      ELSE
        newBox = 7
      END IF
      intervalDays = BOX_INTERVALS[newBox - 1] * 4
  END SWITCH

  result = NEW SrsCalculationResult()
  result.newBox = newBox
  result.intervalDays = intervalDays

  RETURN result
END FUNCTION
```

**Forgotten Card Action Strategies:**
```
INTERFACE ForgottenCardActionStrategy:
  FUNCTION calculateNewBox(currentBox, moveDownBoxes): Integer
END INTERFACE

CLASS MoveToBox1Strategy IMPLEMENTS ForgottenCardActionStrategy:
  FUNCTION calculateNewBox(currentBox, moveDownBoxes):
    RETURN 1 // Always reset to Box 1
  END FUNCTION
END CLASS

CLASS MoveDownNBoxesStrategy IMPLEMENTS ForgottenCardActionStrategy:
  FUNCTION calculateNewBox(currentBox, moveDownBoxes):
    newBox = currentBox - moveDownBoxes
    IF newBox < 1 THEN
      newBox = 1
    END IF
    RETURN newBox
  END FUNCTION
END CLASS

CLASS StayInBoxStrategy IMPLEMENTS ForgottenCardActionStrategy:
  FUNCTION calculateNewBox(currentBox, moveDownBoxes):
    RETURN currentBox // Stay in same box, interval reduced elsewhere
  END FUNCTION
END CLASS
```

**Post-conditions**:
- New box position calculated
- New interval calculated
- Strategy pattern applied for forgotten card actions

**Transaction Scope**:
- None (pure calculation, no database access)

**Error Scenarios**:
- Invalid box → 400 ValidationException
- Invalid rating → 400 ValidationException

---

### Method: getDailyLimits(userId)

**Purpose**: Get and check daily review limits for user

**Input**:
- userId: UUID

**Output**:
- DailyLimitsResponse
  - newCardsPerDay: Integer
  - maxReviewsPerDay: Integer
  - newCardsToday: Integer
  - reviewsToday: Integer
  - newCardsRemaining: Integer
  - reviewsRemaining: Integer

**Pre-conditions**:
- User is authenticated

**Pseudo-code**:
```
FUNCTION getDailyLimits(userId):
  // NO transaction (read-only)

  // Step 1: Load user SRS settings
  srsSettings = srsSettingsRepository.findByUserId(userId)

  // Step 2: Count today's activity
  newCardsToday = cardBoxPositionRepository.countNewCardsReviewedToday(
    userId, CURRENT_DATE()
  )

  reviewsToday = reviewLogRepository.countByUserIdAndDate(
    userId, CURRENT_DATE()
  )

  // Step 3: Calculate remaining
  newCardsRemaining = MAX(0, srsSettings.newCardsPerDay - newCardsToday)
  reviewsRemaining = MAX(0, srsSettings.maxReviewsPerDay - reviewsToday)

  // Step 4: Map to response
  response = NEW DailyLimitsResponse()
  response.newCardsPerDay = srsSettings.newCardsPerDay
  response.maxReviewsPerDay = srsSettings.maxReviewsPerDay
  response.newCardsToday = newCardsToday
  response.reviewsToday = reviewsToday
  response.newCardsRemaining = newCardsRemaining
  response.reviewsRemaining = reviewsRemaining

  RETURN response
END FUNCTION
```

**Post-conditions**:
- Daily limits returned with current progress

**Transaction Scope**:
- Implicit read-only transaction

**Error Scenarios**:
- Settings not found → Initialize with defaults
- Database error → 500 InternalServerError

---

## 7. StatsService

### Method: updateUserStats(userId, reviewedToday)

**Purpose**: Update user statistics after review session (streak, total cards learned)

**Input**:
- userId: UUID
- reviewedToday: Boolean (whether user reviewed cards today)

**Output**:
- void

**Pre-conditions**:
- User exists

**Pseudo-code**:
```
FUNCTION updateUserStats(userId, reviewedToday):
  // Transaction begins

  // Step 1: Load user stats
  userStats = userStatsRepository.findByUserId(userId)

  IF userStats IS NULL THEN
    // Initialize if doesn't exist
    userStats = NEW UserStats()
    userStats.userId = userId
    userStats.totalCardsLearned = 0
    userStats.streakDays = 0
    userStats.lastStudyDate = NULL
    userStats.totalStudyTimeMinutes = 0
  END IF

  // Step 2: Update total cards learned (incremented in submitReview)
  // Already updated, no change here

  // Step 3: Calculate streak
  today = CURRENT_DATE()

  IF userStats.lastStudyDate IS NULL THEN
    // First time studying
    userStats.streakDays = 1
    userStats.lastStudyDate = today

  ELSE IF userStats.lastStudyDate == today THEN
    // Already studied today, no change
    // streakDays stays the same

  ELSE IF userStats.lastStudyDate == today - 1 THEN
    // Studied yesterday, increment streak
    userStats.streakDays = userStats.streakDays + 1
    userStats.lastStudyDate = today

  ELSE
    // Broke streak, reset to 1
    userStats.streakDays = 1
    userStats.lastStudyDate = today
  END IF

  // Step 4: Update timestamp
  userStats.updatedAt = NOW()

  // Step 5: Save to database
  userStatsRepository.save(userStats)

  // Transaction commits

  // Step 6: Log event
  LOG_INFO("User stats updated: userId=" + userId + ", streak=" + userStats.streakDays)

  RETURN // void
END FUNCTION
```

**Post-conditions**:
- User streak calculated and updated
- Last study date updated
- Total cards learned incremented

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After save
- ROLLBACK: On database error

**Streak Calculation Logic**:
- First study: streak = 1
- Same day: no change
- Consecutive day: increment streak
- Gap > 1 day: reset to 1

**Error Scenarios**:
- User not found → Initialize new stats
- Database error → 500 InternalServerError

---

### Method: calculateFolderStats(folderId, userId)

**Purpose**: Recursively calculate folder statistics (total cards, due cards, etc.)

**Input**:
- folderId: UUID
- userId: UUID

**Output**:
- FolderStats (entity, also saved to cache table)

**Pre-conditions**:
- Folder exists and belongs to user

**Pseudo-code**:
```
FUNCTION calculateFolderStats(folderId, userId):
  // Transaction begins

  // Step 1: Load folder
  folder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(folderId, userId)
  IF folder IS NULL THEN
    THROW ResourceNotFoundException("Folder not found")
  END IF

  // Step 2: Use Visitor pattern to traverse tree
  statsVisitor = NEW FolderStatsVisitor()
  stats = statsVisitor.visit(folder) // Recursive calculation

  // Step 3: Save/update cache
  cachedStats = folderStatsRepository.findByFolderIdAndUserId(folderId, userId)

  IF cachedStats IS NULL THEN
    cachedStats = NEW FolderStats()
    cachedStats.folderId = folderId
    cachedStats.userId = userId
  END IF

  cachedStats.totalCardsCount = stats.totalCardsCount
  cachedStats.dueCardsCount = stats.dueCardsCount
  cachedStats.newCardsCount = stats.newCardsCount
  cachedStats.matureCardsCount = stats.matureCardsCount
  cachedStats.lastComputedAt = NOW()

  folderStatsRepository.save(cachedStats)

  // Transaction commits

  // Step 4: Log event
  LOG_INFO("Folder stats calculated: folderId=" + folderId +
           ", totalCards=" + stats.totalCardsCount)

  RETURN cachedStats
END FUNCTION
```

**Post-conditions**:
- Folder stats calculated and cached
- lastComputedAt timestamp updated

**Transaction Scope**:
- BEGIN: Start of method
- COMMIT: After save
- ROLLBACK: On database error

**Performance Considerations**:
- Cached stats used if < 5 minutes old
- Recalculation can be slow for large folder trees
- Consider scheduled background job for large folders

**Error Scenarios**:
- Folder not found → 404 ResourceNotFoundException
- Database error → 500 InternalServerError

---

### Method: getBoxDistribution(userId, scope, scopeId)

**Purpose**: Get distribution of cards across boxes (1-7) for visualization

**Input**:
- userId: UUID
- scope: Enum (ALL / FOLDER / DECK)
- scopeId: UUID (required if scope = FOLDER or DECK)

**Output**:
- BoxDistributionResponse
  - distribution: Map<Integer, Integer> (box → card count)
  - totalCards: Integer

**Pre-conditions**:
- User is authenticated
- Scope folder/deck exists and belongs to user (if specified)

**Pseudo-code**:
```
FUNCTION getBoxDistribution(userId, scope, scopeId):
  // NO transaction (read-only aggregate query)

  distribution = EMPTY_MAP // box → count

  SWITCH scope:
    CASE ALL:
      // Count cards per box for all user's cards
      distribution = cardBoxPositionRepository.countByUserIdGroupByBox(userId)

    CASE FOLDER:
      // Validate folder
      folder = folderRepository.findByIdAndUserIdAndDeletedAtIsNull(scopeId, userId)
      IF folder IS NULL THEN
        THROW ResourceNotFoundException("Folder not found")
      END IF

      // Count cards per box in folder tree
      distribution = cardBoxPositionRepository.countInFolderGroupByBox(
        userId, folder.path
      )

    CASE DECK:
      // Validate deck
      deck = deckRepository.findByIdAndUserIdAndDeletedAtIsNull(scopeId, userId)
      IF deck IS NULL THEN
        THROW ResourceNotFoundException("Deck not found")
      END IF

      // Count cards per box in deck
      distribution = cardBoxPositionRepository.countInDeckGroupByBox(
        userId, scopeId
      )
  END SWITCH

  // Calculate total
  totalCards = SUM(distribution.values())

  // Map to response
  response = NEW BoxDistributionResponse()
  response.distribution = distribution
  response.totalCards = totalCards

  RETURN response
END FUNCTION
```

**Database Query Example (countByUserIdGroupByBox):**
```sql
SELECT cbp.current_box, COUNT(*) as card_count
FROM card_box_position cbp
JOIN cards c ON c.id = cbp.card_id AND c.deleted_at IS NULL
WHERE cbp.user_id = :userId
GROUP BY cbp.current_box
ORDER BY cbp.current_box ASC;
```

**Post-conditions**:
- Distribution map returned with counts per box
- Total card count calculated

**Transaction Scope**:
- Implicit read-only transaction

**Error Scenarios**:
- Folder/Deck not found → 404 ResourceNotFoundException
- Database error → 500 InternalServerError

---

## 8. ImportExportService

### Method: importCards(deckId, file, userId)

**Purpose**: Import cards from CSV or Excel file in bulk

**Input**:
- deckId: UUID
- file: MultipartFile (CSV or XLSX)
- userId: UUID

**Output**:
- ImportResultResponse
  - totalRows: Integer
  - successCount: Integer
  - errorCount: Integer
  - errors: List<ImportError> (row number, error message)

**Pre-conditions**:
- User owns the deck
- File size <= 50MB
- Row count <= 10,000
- File format is CSV or XLSX

**Pseudo-code**:
```
FUNCTION importCards(deckId, file, userId):
  // Step 1: Validate deck
  deck = deckRepository.findByIdAndUserIdAndDeletedAtIsNull(deckId, userId)
  IF deck IS NULL THEN
    THROW ResourceNotFoundException("Deck not found")
  END IF

  // Step 2: Validate file
  IF file.size > 50_MB THEN
    THROW ValidationException("File size exceeds 50MB limit")
  END IF

  fileExtension = getFileExtension(file.originalFilename)
  IF fileExtension NOT IN ["csv", "xlsx"] THEN
    THROW ValidationException("Unsupported file format. Please upload CSV or Excel (.xlsx)")
  END IF

  // Step 3: Parse file
  rows = []
  errors = []

  IF fileExtension == "csv" THEN
    rows = parseCsv(file) // Using OpenCSV
  ELSE IF fileExtension == "xlsx" THEN
    rows = parseExcel(file) // Using Apache POI
  END IF

  IF SIZE(rows) > 10000 THEN
    THROW ValidationException("File contains too many rows (max 10,000). Total: " + SIZE(rows))
  END IF

  // Step 4: Validate and prepare cards
  validCards = []
  rowIndex = 1

  FOR EACH row IN rows DO
    rowIndex = rowIndex + 1

    // Skip empty rows
    IF row.front IS EMPTY AND row.back IS EMPTY THEN
      CONTINUE
    END IF

    // Validate front
    IF TRIM(row.front) IS EMPTY THEN
      errors.ADD(ImportError(
        row=rowIndex,
        message="Missing 'Front' field"
      ))
      CONTINUE
    END IF

    // Validate back
    IF TRIM(row.back) IS EMPTY THEN
      errors.ADD(ImportError(
        row=rowIndex,
        message="Missing 'Back' field"
      ))
      CONTINUE
    END IF

    // Validate length
    IF LENGTH(row.front) > 5000 THEN
      errors.ADD(ImportError(
        row=rowIndex,
        message="Front text exceeds 5000 characters"
      ))
      CONTINUE
    END IF

    IF LENGTH(row.back) > 5000 THEN
      errors.ADD(ImportError(
        row=rowIndex,
        message="Back text exceeds 5000 characters"
      ))
      CONTINUE
    END IF

    // Add to valid cards
    validCards.ADD({
      front: TRIM(row.front),
      back: TRIM(row.back)
    })
  END FOR

  // Step 5: Batch insert cards
  // Transaction begins here

  successCount = 0
  cardBatch = []

  FOR EACH validCard IN validCards DO
    card = NEW Card()
    card.id = generateUUID()
    card.deckId = deck.id
    card.front = validCard.front
    card.back = validCard.back
    card.createdAt = NOW()
    card.updatedAt = NOW()
    cardBatch.ADD(card)

    // Batch insert every 1000 cards
    IF SIZE(cardBatch) >= 1000 THEN
      cardRepository.batchInsert(cardBatch)
      successCount = successCount + SIZE(cardBatch)
      cardBatch = []

      // Flush and clear to avoid memory issues
      entityManager.flush()
      entityManager.clear()
    END IF
  END FOR

  // Insert remaining
  IF SIZE(cardBatch) > 0 THEN
    cardRepository.batchInsert(cardBatch)
    successCount = successCount + SIZE(cardBatch)
  END IF

  // Step 6: Initialize SRS positions for all imported cards
  importedCards = cardRepository.findByDeckIdAndCreatedAtAfter(
    deckId, transactionStartTime
  )

  positionBatch = []
  FOR EACH card IN importedCards DO
    position = NEW CardBoxPosition()
    position.id = generateUUID()
    position.cardId = card.id
    position.userId = userId
    position.currentBox = 1
    position.intervalDays = 1
    position.dueDate = CURRENT_DATE() + 1 // Due tomorrow
    position.lastReviewedAt = NULL
    position.reviewCount = 0
    position.lapseCount = 0
    position.easeFactor = 2.5
    position.createdAt = NOW()
    position.updatedAt = NOW()
    positionBatch.ADD(position)

    IF SIZE(positionBatch) >= 1000 THEN
      cardBoxPositionRepository.batchInsert(positionBatch)
      positionBatch = []

      entityManager.flush()
      entityManager.clear()
    END IF
  END FOR

  IF SIZE(positionBatch) > 0 THEN
    cardBoxPositionRepository.batchInsert(positionBatch)
  END IF

  // Step 7: Invalidate folder stats
  IF deck.folder IS NOT NULL THEN
    folderStatsRepository.invalidateAncestorChain(deck.folder.id)
  END IF

  // Transaction commits

  // Step 8: Publish domain event (async)
  publishEvent(CardsImportedEvent(
    deckId=deck.id,
    userId=userId,
    successCount=successCount,
    errorCount=SIZE(errors),
    timestamp=NOW()
  ))

  // Step 9: Log event
  LOG_INFO("Cards imported: deckId=" + deckId + ", success=" + successCount +
           ", errors=" + SIZE(errors))

  // Step 10: Map to response
  response = NEW ImportResultResponse()
  response.totalRows = SIZE(rows)
  response.successCount = successCount
  response.errorCount = SIZE(errors)
  response.errors = errors

  RETURN response
END FUNCTION
```

**Helper Function: parseCsv(file)**
```
FUNCTION parseCsv(file):
  reader = NEW CSVReader(file.inputStream, UTF8_ENCODING)

  // Read header
  header = reader.readNext()
  IF "Front" NOT IN header OR "Back" NOT IN header THEN
    THROW ValidationException("Missing required columns: 'Front' and 'Back'")
  END IF

  frontIndex = indexOf(header, "Front")
  backIndex = indexOf(header, "Back")

  // Read rows
  rows = []
  WHILE (line = reader.readNext()) IS NOT NULL DO
    row = {
      front: line[frontIndex],
      back: line[backIndex]
    }
    rows.ADD(row)
  END WHILE

  reader.close()

  RETURN rows
END FUNCTION
```

**Helper Function: parseExcel(file)**
```
FUNCTION parseExcel(file):
  workbook = NEW XSSFWorkbook(file.inputStream) // Apache POI
  sheet = workbook.getSheetAt(0) // First sheet

  // Read header row
  headerRow = sheet.getRow(0)
  header = extractRowValues(headerRow)

  IF "Front" NOT IN header OR "Back" NOT IN header THEN
    THROW ValidationException("Missing required columns: 'Front' and 'Back'")
  END IF

  frontIndex = indexOf(header, "Front")
  backIndex = indexOf(header, "Back")

  // Read data rows
  rows = []
  FOR i = 1 TO sheet.lastRowNum DO
    dataRow = sheet.getRow(i)
    IF dataRow IS NULL THEN
      CONTINUE
    END IF

    row = {
      front: getCellValue(dataRow, frontIndex),
      back: getCellValue(dataRow, backIndex)
    }
    rows.ADD(row)
  END FOR

  workbook.close()

  RETURN rows
END FUNCTION
```

**Post-conditions**:
- Valid cards imported and saved
- CardBoxPositions initialized (Box 1, due tomorrow)
- Folder stats invalidated
- Import summary returned with errors
- CardsImportedEvent published

**Transaction Scope**:
- BEGIN: Before batch insert
- COMMIT: After all cards and positions inserted
- ROLLBACK: On database error (atomic import)

**Performance Considerations**:
- Batch insert 1000 cards at a time
- Flush and clear entity manager to avoid memory issues
- Stream processing for large files
- Timeout: 2 minutes max

**Domain Events Published**:
- CardsImportedEvent

**Error Scenarios**:
- Deck not found → 404 ResourceNotFoundException
- File too large → 400 ValidationException
- Unsupported format → 400 ValidationException
- Too many rows → 400 ValidationException
- Missing columns → 400 ValidationException
- Timeout → 500 InternalServerError

---

### Method: exportCards(deckId, format, filter, userId)

**Purpose**: Export cards from deck to CSV or Excel file

**Input**:
- deckId: UUID
- format: Enum (CSV / XLSX)
- filter: Enum (ALL / DUE_ONLY)
- userId: UUID

**Output**:
- File (byte array with content type)

**Pre-conditions**:
- User owns the deck
- Deck exists and not deleted
- Deck has cards to export

**Pseudo-code**:
```
FUNCTION exportCards(deckId, format, filter, userId):
  // NO transaction (read-only, file generation)

  // Step 1: Validate deck
  deck = deckRepository.findByIdAndUserIdAndDeletedAtIsNull(deckId, userId)
  IF deck IS NULL THEN
    THROW ResourceNotFoundException("Deck not found")
  END IF

  // Step 2: Load cards based on filter
  cards = []

  SWITCH filter:
    CASE ALL:
      cards = cardRepository.findByDeckIdAndDeletedAtIsNull(deckId)

    CASE DUE_ONLY:
      cards = cardBoxPositionRepository.findDueCardsInDeck(
        userId, deckId, CURRENT_DATE()
      ).map(position => position.card)
  END SWITCH

  IF SIZE(cards) == 0 THEN
    THROW ValidationException("No cards to export")
  END IF

  IF SIZE(cards) > 50000 THEN
    THROW ValidationException("Too many cards to export (max 50,000). Total: " + SIZE(cards))
  END IF

  // Step 3: Generate file
  fileName = deck.name + "_export_" + CURRENT_DATE() + "." + LOWERCASE(format)

  fileBytes = NULL
  contentType = NULL

  SWITCH format:
    CASE CSV:
      fileBytes = generateCsv(cards, userId)
      contentType = "text/csv"

    CASE XLSX:
      fileBytes = generateExcel(cards, userId)
      contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
  END SWITCH

  // Step 4: Log event
  LOG_INFO("Cards exported: deckId=" + deckId + ", format=" + format +
           ", count=" + SIZE(cards))

  // Step 5: Return file
  response = NEW FileResponse()
  response.fileName = fileName
  response.contentType = contentType
  response.fileBytes = fileBytes

  RETURN response
END FUNCTION
```

**Helper Function: generateCsv(cards, userId)**
```
FUNCTION generateCsv(cards, userId):
  output = NEW ByteArrayOutputStream()
  writer = NEW CSVWriter(output, UTF8_ENCODING)

  // Write header
  writer.writeNext(["Front", "Back", "Created Date", "Review Count", "Current Box"])

  // Write data rows
  FOR EACH card IN cards DO
    position = cardBoxPositionRepository.findByCardIdAndUserId(card.id, userId)

    row = [
      card.front,
      card.back,
      FORMAT_DATE(card.createdAt),
      position?.reviewCount OR 0,
      position?.currentBox OR 1
    ]
    writer.writeNext(row)
  END FOR

  writer.close()

  RETURN output.toByteArray()
END FUNCTION
```

**Helper Function: generateExcel(cards, userId)**
```
FUNCTION generateExcel(cards, userId):
  workbook = NEW XSSFWorkbook() // Apache POI
  sheet = workbook.createSheet("Cards")

  // Create header row with styling
  headerRow = sheet.createRow(0)
  headerStyle = createHeaderStyle(workbook) // Bold, background color

  createCell(headerRow, 0, "Front", headerStyle)
  createCell(headerRow, 1, "Back", headerStyle)
  createCell(headerRow, 2, "Created Date", headerStyle)
  createCell(headerRow, 3, "Review Count", headerStyle)
  createCell(headerRow, 4, "Current Box", headerStyle)

  // Write data rows
  rowIndex = 1
  FOR EACH card IN cards DO
    position = cardBoxPositionRepository.findByCardIdAndUserId(card.id, userId)

    dataRow = sheet.createRow(rowIndex)
    createCell(dataRow, 0, card.front)
    createCell(dataRow, 1, card.back)
    createCell(dataRow, 2, FORMAT_DATE(card.createdAt))
    createCell(dataRow, 3, position?.reviewCount OR 0)
    createCell(dataRow, 4, position?.currentBox OR 1)

    rowIndex = rowIndex + 1
  END FOR

  // Auto-size columns
  FOR col = 0 TO 4 DO
    sheet.autoSizeColumn(col)
  END FOR

  // Write to byte array
  output = NEW ByteArrayOutputStream()
  workbook.write(output)
  workbook.close()

  RETURN output.toByteArray()
END FUNCTION
```

**Post-conditions**:
- File generated with all cards
- File downloaded by user

**Transaction Scope**:
- Implicit read-only transaction

**Performance Considerations**:
- Max 50,000 cards per export
- Timeout: 30 seconds max
- Stream writing for large files

**Error Scenarios**:
- Deck not found → 404 ResourceNotFoundException
- No cards to export → 400 ValidationException
- Too many cards → 400 ValidationException
- File generation error → 500 InternalServerError

---

## Summary

This document provides detailed pseudo-code for all major service layer methods in RepeatWise MVP. Key highlights:

### Design Patterns Used
- **Strategy Pattern**: Review order strategies, forgotten card actions
- **Visitor Pattern**: Folder statistics calculation
- **Composite Pattern**: Folder tree structure
- **Repository Pattern**: Database access abstraction
- **DTO Pattern**: Data transfer between layers

### Transaction Management
- **Read-only** operations: Implicit transactions
- **Write operations**: Explicit `@Transactional` with clear boundaries
- **Batch operations**: Flush and clear entity manager to avoid memory issues
- **Async operations**: Separate transactions in background threads

### Performance Optimizations
- Composite database indexes for critical queries
- Batch insert/update operations (1000 items per batch)
- Denormalized caching (folder_stats table)
- Lazy loading with JOIN FETCH to avoid N+1 problems
- Query result limits (max 200 cards per review session)

### Error Handling
- Custom exceptions with appropriate HTTP status codes
- Validation at multiple layers (DTO, service, database)
- Rollback on errors for transactional operations
- Detailed error messages for user feedback

### Domain Events
- Published after transaction commits (async)
- Handled by separate listeners
- Used for stats updates, notifications, logging

This pseudo-code is ready for conversion into Java (Spring Boot) or TypeScript implementations.

---

**End of Document**
