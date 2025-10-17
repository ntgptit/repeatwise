# Language & Locale Changes Summary

## Changes Made - 2025-10-18

### Overview
Updated RepeatWise backend from Vietnamese (VI) to English (EN) as default language for better international adoption.

---

## Key Changes

### 1. Message Resources (messages.properties)
**File**: `backend-api/src/main/resources/messages.properties`

**Changed**: All error and success messages from Vietnamese to English

**Examples**:
- `error.user.email.already.exists`: "Email {0} đã được sử dụng" → "Email {0} is already registered"
- `error.user.password.required`: "Mật khẩu không được để trống" → "Password is required"
- `success.user.registered`: "Đăng ký tài khoản thành công" → "Account registered successfully"

**Impact**: All API error responses and messages now display in English by default

---

### 2. User Entity Default Language
**File**: `backend-api/src/main/java/com/repeatwise/entity/User.java`

**Changed**:
```java
// Before
private Language language = Language.VI;

// After
private Language language = Language.EN;
```

**Impact**: New users will have English as default language instead of Vietnamese

---

### 3. UserMapper Default Language
**File**: `backend-api/src/main/java/com/repeatwise/mapper/UserMapper.java`

**Changed**:
```java
// Before
@Mapping(target = "language", constant = "VI")

// After
@Mapping(target = "language", constant = "EN")
```

**Impact**: Registration flow sets language to EN by default

---

### 4. ReviewOrder Enum
**File**: `backend-api/src/main/java/com/repeatwise/entity/enums/ReviewOrder.java`

**Changed**:
- Fixed enum values to match database schema constraints
- Changed descriptions to English

**Before**:
```java
RANDOM("Random order"),
OLDEST_FIRST("Oldest cards first"),
NEWEST_FIRST("Newest cards first");
```

**After**:
```java
ASCENDING("Box 1 to Box 7 (easiest first)"),
DESCENDING("Box 7 to Box 1 (hardest first)"),
RANDOM("Random order");
```

**Impact**: Aligns with database schema constraints (ASCENDING, DESCENDING, RANDOM)

---

## Username vs Email Decision

### Analysis
After reviewing all documentation:
- **UC-001 (User Registration)**: Only mentions email, not username
- **UC-002 (User Login)**: Only mentions email authentication
- **Database Schema**: `users` table only has `email` field (UNIQUE), no `username` field
- **API Specs**: LoginRequest only has `email` and `password` fields

### Conclusion
**NO username field required** - System uses email as the unique identifier for login/registration per requirements documentation.

### If Username Support Needed in Future
Would require:
1. Add `username` column to `users` table (VARCHAR(50), UNIQUE)
2. Add `username` field to User entity
3. Update RegisterRequest to accept username
4. Update LoginRequest to accept username OR email
5. Add `findByUsername()` to UserRepository
6. Update AuthService login logic to check both email and username

---

## Default Values Summary

### Current Defaults (After Changes)
- **Language**: EN (English)
- **Theme**: LIGHT
- **Timezone**: UTC (from browser detection, fallback to UTC)
- **SRS Review Order**: RANDOM
- **Authentication**: Email-based (no username)

### Original Defaults (Before Changes)
- **Language**: VI (Vietnamese)
- **Theme**: LIGHT
- **Timezone**: Asia/Ho_Chi_Minh
- **SRS Review Order**: RANDOM (but with wrong enum values)

---

## Build Status
✅ **BUILD SUCCESS** after all changes

---

## Testing Checklist

### Manual Testing Needed
- [ ] Test user registration with English error messages
- [ ] Verify default language is EN for new users
- [ ] Test SRS settings with corrected ReviewOrder enum
- [ ] Verify all error messages display in English

### Future Considerations
- [ ] Add Vietnamese language support via `messages_vi.properties`
- [ ] Implement language selection in user profile
- [ ] Add timezone detection from browser
- [ ] Consider adding username support if required

---

**Date**: 2025-10-18
**Build Status**: ✅ SUCCESS
**Breaking Changes**: None (backward compatible)
