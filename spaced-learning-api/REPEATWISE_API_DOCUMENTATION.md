# RepeatWise API Documentation

## Overview

This document describes the REST API implementation for the RepeatWise spaced repetition system, which is built according to the business specification in `repeatwise-business-spec.md`.

## Architecture

The API follows a layered architecture:
- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic and orchestrate operations
- **Repositories**: Handle data access operations
- **Entities**: JPA entities representing the domain model
- **DTOs**: Data Transfer Objects for API requests/responses
- **Mappers**: Convert between entities and DTOs using ModelMapper

## Core Entities

### 1. LearningSet
Represents a learning set (the main unit of management in RepeatWise).

**Key Features:**
- UUID-based identification
- Name, description, category, word count
- Status tracking (NOT_STARTED, LEARNING, REVIEWING, MASTERED)
- Current cycle tracking
- Soft delete support

### 2. ReviewHistory
Tracks individual review sessions within learning cycles.

**Key Features:**
- Links to LearningSet and cycle/review numbers
- Score tracking (0-100%)
- Status tracking (COMPLETED, SKIPPED)
- 24-hour edit window
- Note support for skipped reviews

### 3. RemindSchedule
Manages reminder scheduling for learning sets.

**Key Features:**
- Date-based scheduling
- Status tracking (PENDING, SENT, DONE, SKIPPED, RESCHEDULED, CANCELLED)
- Reschedule limit (max 2 times)
- Overload handling

### 4. SRSConfiguration
Configurable parameters for the spaced repetition algorithm.

**Key Features:**
- Base delay, penalty factor, scaling factor
- Min/max delay constraints
- Low score threshold
- Max sets per day limit

## API Endpoints

### Learning Sets (`/api/v1/learning-sets`)

#### CRUD Operations
- `POST /` - Create a new learning set
- `GET /{setId}` - Get a specific learning set
- `GET /{setId}/detail` - Get detailed view with review history and reminders
- `PUT /{setId}` - Update a learning set
- `DELETE /{setId}` - Soft delete a learning set

#### Listing and Search
- `GET /` - Get all user's learning sets (paginated)
- `GET /category/{category}` - Get sets by category
- `GET /search?q={term}` - Search sets by name

#### Status Management
- `POST /{setId}/start-learning` - Mark set as learning
- `POST /{setId}/start-reviewing` - Mark set as reviewing
- `POST /{setId}/mark-mastered` - Mark set as mastered

#### SRS Operations
- `GET /due-for-review?date={date}` - Get sets due for review
- `GET /overdue?date={date}` - Get overdue sets
- `POST /{setId}/schedule-next-cycle` - Schedule next cycle
- `POST /handle-overload?date={date}` - Handle daily overload

#### Statistics
- `GET /stats/count-by-status?status={status}` - Get count by status

### Review History (`/api/v1/reviews`)

#### CRUD Operations
- `POST /` - Create a new review
- `GET /{reviewId}` - Get a specific review
- `PUT /{reviewId}` - Update a review (within 24 hours)

#### Querying
- `GET /set/{setId}` - Get all reviews for a set
- `GET /set/{setId}/cycle/{cycleNo}` - Get reviews for specific cycle
- `GET /set/{setId}/recent` - Get recent reviews (24 hours)
- `GET /set/{setId}/cycle/{cycleNo}/average-score` - Calculate cycle average
- `GET /set/{setId}/date-range` - Get reviews by date range
- `GET /user/all` - Get all user reviews

#### Validation
- `GET /{reviewId}/can-edit` - Check if review can be edited

### Reminder Schedules (`/api/v1/reminders`)

#### CRUD Operations
- `POST /` - Create a new reminder
- `GET /{reminderId}` - Get a specific reminder
- `PUT /{reminderId}` - Update a reminder
- `DELETE /{reminderId}` - Delete a reminder

#### Querying
- `GET /set/{setId}` - Get reminders for a set
- `GET /date/{date}` - Get reminders for a date
- `GET /overdue` - Get overdue reminders
- `GET /today` - Get reminders to send today

#### Status Management
- `POST /{reminderId}/mark-sent` - Mark as sent
- `POST /{reminderId}/mark-done` - Mark as done
- `POST /{reminderId}/mark-skipped` - Mark as skipped

#### Rescheduling
- `GET /{reminderId}/can-reschedule` - Check reschedule availability
- `POST /{reminderId}/reschedule` - Reschedule reminder
- `POST /handle-overload?date={date}` - Handle daily overload

## Business Logic Implementation

### 1. Spaced Repetition Algorithm

The SRS algorithm is implemented in `SRSConfiguration.calculateNextCycleDelay()`:

```java
next_cycle_delay_days = base_delay - penalty * (100 - avg_score) + scaling * word_count
```

**Constraints:**
- Minimum delay: 7 days
- Maximum delay: 90 days
- If avg_score < 40%: delay = 7 days

### 2. Cycle Management

Each learning set has 5 reviews per cycle:
- Reviews are tracked by cycle number and review number (1-5)
- After completing 5 reviews, the cycle average is calculated
- Next cycle is scheduled based on SRS algorithm
- Cycle number is incremented

### 3. Overload Management

Maximum 3 sets per day per user:
- Priority: overdue sets > lower average scores > lower word counts
- Excess sets are rescheduled to next available date
- FIFO ordering is maintained

### 4. Review Editing Window

Reviews can only be edited within 24 hours of creation:
- `canEditReview()` checks creation time
- Updates are rejected after 24 hours
- All changes are logged in activity logs

### 5. Reminder Rescheduling

Reminders can be rescheduled up to 2 times:
- `canReschedule()` checks reschedule count
- Reschedule count is tracked and incremented
- After 2 reschedules, further rescheduling is blocked

## Data Validation

### Request Validation
- All DTOs use Jakarta Validation annotations
- Field length limits (name ≤ 100 chars, description ≤ 500 chars)
- Score range validation (0-100)
- Required field validation
- Date format validation (ISO format)

### Business Rule Validation
- User ownership verification
- Duplicate prevention (reviews, reminders)
- Status transition validation
- Reschedule limit enforcement

## Error Handling

### Exception Types
- `SpacedLearningException` - Custom business exceptions
- `ResourceNotFoundException` - Entity not found
- `ValidationException` - Invalid input data
- `ForbiddenException` - Access denied

### HTTP Status Codes
- `200 OK` - Successful operations
- `201 Created` - Resource created
- `204 No Content` - Successful deletion
- `400 Bad Request` - Validation errors
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Access denied
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists

## Security

### Authentication
- JWT-based authentication
- User context injection via `@AuthenticationPrincipal`
- All endpoints require authentication

### Authorization
- Users can only access their own resources
- Ownership verification on all operations
- Soft delete prevents data loss

## Database Schema

### Key Tables
- `sets` - Learning sets
- `review_histories` - Review records
- `remind_schedules` - Reminder scheduling
- `srs_configurations` - SRS parameters
- `activity_logs` - Audit trail
- `notification_logs` - Notification tracking

### Relationships
- Sets belong to users (many-to-one)
- Reviews belong to sets (many-to-one)
- Reminders belong to sets and users (many-to-one)
- All entities support soft delete

## Configuration

### SRS Parameters (Configurable)
- `base_delay_days`: 30 (default)
- `penalty_factor`: 0.2 (default)
- `scaling_factor`: 0.02 (default)
- `min_delay_days`: 7 (default)
- `max_delay_days`: 90 (default)
- `low_score_threshold`: 40 (default)
- `max_sets_per_day`: 3 (default)

## Usage Examples

### Creating a Learning Set
```bash
POST /api/v1/learning-sets
{
  "name": "Basic Vocabulary",
  "description": "Essential words for beginners",
  "category": "VOCABULARY",
  "wordCount": 50
}
```

### Recording a Review
```bash
POST /api/v1/reviews
{
  "setId": "uuid-here",
  "cycleNo": 1,
  "reviewNo": 3,
  "score": 85,
  "status": "COMPLETED"
}
```

### Scheduling a Reminder
```bash
POST /api/v1/reminders
{
  "setId": "uuid-here",
  "remindDate": "2024-01-15"
}
```

## Future Enhancements

1. **Notification Service**: Integration with push notifications and email
2. **Analytics**: Advanced learning analytics and insights
3. **Social Features**: Sharing and community features
4. **Mobile Sync**: Offline support and synchronization
5. **AI Integration**: Personalized learning recommendations

## Testing

The API includes comprehensive test coverage:
- Unit tests for services and business logic
- Integration tests for repositories
- API tests for controllers
- Security tests for authentication and authorization

## Deployment

The API is containerized with Docker and includes:
- PostgreSQL database
- Flyway migrations
- Health checks
- Monitoring and logging
- Environment-specific configurations
