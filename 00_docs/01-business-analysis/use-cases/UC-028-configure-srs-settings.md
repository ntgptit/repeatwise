# UC-028: Configure SRS Settings

## 1. Brief Description

User updates personal SRS settings such as total boxes, review order, daily limits, notification preferences, and forgotten card action.

## 2. Actors

- Primary Actor: Authenticated User
- Secondary Actor: Settings Service

## 3. Preconditions

- User is authenticated
- SRS settings exist for the user (initialized at registration)

## 4. Postconditions

### Success Postconditions

- SRS settings updated and persisted
- Subsequent review sessions follow the updated rules

### Failure Postconditions

- No changes saved
- Error displayed

## 5. Main Success Scenario (Basic Flow)

1. User opens Settings > SRS
2. System shows current values:
   - total_boxes (default 7)
   - review_order (e.g., DUE_DATE_ASC|RANDOM)
   - new_cards_per_day
   - max_reviews_per_day
   - forgotten_card_action (MOVE_TO_BOX_1)
   - notification_enabled, notification_time
3. User updates desired fields and clicks “Save”
4. System validates ranges and formats
5. System updates `srs_settings`
6. UI shows success message

## 6. Alternative Flows

### 6a. Invalid Values

1. total_boxes outside allowed range (e.g., 3..10)
2. System returns 400 with field-level errors

### 6b. Time Format Error

1. notification_time invalid (not HH:mm)
2. Return 400 with message

## 7. Special Requirements

- Server-side validation mirrors client checks
- Future: preset profiles (Conservative, Balanced, Aggressive)

## 8. Business Rules / Constraints

- BR-SRS-01: total_boxes default 7, allowed range 3..10
- BR-SRS-02: forgotten_card_action default MOVE_TO_BOX_1
- BR-SRS-04: new_cards_per_day and max_reviews_per_day must be positive integers with sensible caps

## 9. Frequency of Occurrence

- Infrequent; mostly after onboarding or as preferences change

## 10. Open Issues

- Additional fine-grained interval controls (future)

## 11. Related Use Cases

- UC-023: Review Cards (SRS)
- UC-024: Rate Card

## 12. Business Rules References

- BR-SRS-01..02, BR-SRS-04

## 13. UI Mockup Notes

- Form with grouped controls; save button disabled until dirty and valid

## 14. API Endpoint

```
PATCH /api/srs-settings
```

Request Body (example):

```json
{
  "totalBoxes": 7,
  "reviewOrder": "DUE_DATE_ASC",
  "newCardsPerDay": 20,
  "maxReviewsPerDay": 200,
  "forgottenCardAction": "MOVE_TO_BOX_1",
  "notificationEnabled": true,
  "notificationTime": "09:00"
}
```

Success (200): returns updated settings

Errors:

- 400 invalid values
- 401 unauthorized (not logged in)

## 15. Test Cases

- TC-028-001: Update within valid ranges -> 200
- TC-028-002: total_boxes = 2 -> 400
- TC-028-003: notification_time = "25:00" -> 400
