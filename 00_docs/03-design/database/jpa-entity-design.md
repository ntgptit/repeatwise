# JPA Entity Design - RepeatWise MVP

## 1. Overview

This document provides complete JPA entity design for RepeatWise MVP, covering:
- JPA best practices and patterns
- Entity relationships and mappings
- Cascade strategies and orphan removal
- Fetch strategies and N+1 prevention
- Auditing and soft delete implementation
- All entities with code examples

**Technology Stack**:
- Spring Boot 3.x
- Spring Data JPA (Hibernate 6.x)
- PostgreSQL 15+
- Java 17

---

## 2. JPA Best Practices

### 2.1 Entity Design Principles

**1. Always Use @Entity and @Table**
```java
@Entity
@Table(name = "users") // Explicit table name
public class User {
    // ...
}
```

**2. Prefer UUID for Primary Keys**
```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```

**Why UUID over BIGSERIAL**:
- No database dependency for ID generation
- Distributed systems friendly
- Better security (no sequential IDs)
- Easier for client-side validation

**3. Use Column Annotations**
```java
@Column(name = "email", unique = true, nullable = false, length = 255)
private String email;
```

**4. Implement equals() and hashCode()**
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    return id != null && id.equals(user.id);
}

@Override
public int hashCode() {
    return getClass().hashCode();
}
```

**5. Use Enums for Fixed Values**
```java
@Enumerated(EnumType.STRING) // Use STRING, not ORDINAL
@Column(nullable = false)
private Language language;
```

### 2.2 Relationship Best Practices

**1. LAZY Loading by Default**
```java
@ManyToOne(fetch = FetchType.LAZY) // Always LAZY
@JoinColumn(name = "user_id")
private User user;
```

**2. Bidirectional Relationships**
```java
// Parent
@OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Deck> decks = new ArrayList<>();

// Child
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "folder_id")
private Folder folder;
```

**3. Avoid @OneToMany with @JoinColumn**
```java
// BAD: Creates unnecessary join table
@OneToMany
@JoinColumn(name = "user_id")
private List<Folder> folders;

// GOOD: Use mappedBy
@OneToMany(mappedBy = "user")
private List<Folder> folders;
```

### 2.3 Cascade Types

**CascadeType Options**:
- `PERSIST`: Cascade persist operations
- `MERGE`: Cascade merge operations
- `REMOVE`: Cascade remove operations
- `REFRESH`: Cascade refresh operations
- `DETACH`: Cascade detach operations
- `ALL`: All of the above

**Usage Guidelines**:
```java
// Aggregate root -> owned entities: Use ALL
@OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Deck> decks;

// Reference to independent entity: No cascade
@ManyToOne(fetch = FetchType.LAZY)
private User user; // Don't cascade
```

### 2.4 Fetch Strategies

**Preventing N+1 Queries**:

**Option 1: JOIN FETCH in JPQL**
```java
@Query("SELECT f FROM Folder f JOIN FETCH f.decks WHERE f.user.id = :userId")
List<Folder> findByUserWithDecks(@Param("userId") UUID userId);
```

**Option 2: @EntityGraph**
```java
@EntityGraph(attributePaths = {"decks", "subFolders"})
@Query("SELECT f FROM Folder f WHERE f.user.id = :userId")
List<Folder> findByUserWithChildren(@Param("userId") UUID userId);
```

**Option 3: Batch Fetching**
```java
@OneToMany(mappedBy = "folder")
@BatchSize(size = 10) // Fetch 10 at once
private List<Deck> decks;
```

---

## 3. Base Entity Class

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```

**Enable Auditing in Configuration**:
```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
```

---

## 4. Core Entities

### 4.1 User Entity

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_deleted_at", columnList = "deleted_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "timezone", nullable = false, length = 50)
    @Builder.Default
    private String timezone = "Asia/Ho_Chi_Minh";

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 20)
    @Builder.Default
    private Language language = Language.VIETNAMESE;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false, length = 20)
    @Builder.Default
    private Theme theme = Theme.SYSTEM;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Folder> folders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private SRSSettings srsSettings;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStats userStats;

    // Business methods
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void restore() {
        this.deletedAt = null;
    }

    // Lifecycle callbacks
    @PrePersist
    public void prePersist() {
        // Initialize default relationships
        if (srsSettings == null) {
            srsSettings = SRSSettings.createDefault(this);
        }
        if (userStats == null) {
            userStats = UserStats.createDefault(this);
        }
    }
}

enum Language {
    VIETNAMESE, ENGLISH
}

enum Theme {
    LIGHT, DARK, SYSTEM
}
```

**Key Points**:
- Soft delete with `deletedAt` field
- Bidirectional relationships with cascade ALL
- Default values for timezone, language, theme
- Lifecycle callback to initialize default settings
- Indexes on email and deletedAt

---

### 4.2 RefreshToken Entity

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_tokens_user", columnList = "user_id"),
    @Index(name = "idx_refresh_tokens_hash", columnList = "token_hash"),
    @Index(name = "idx_refresh_tokens_expires", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash; // bcrypt hashed

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    // Business methods
    public static RefreshToken create(User user, String rawToken, String hashedToken) {
        return RefreshToken.builder()
            .user(user)
            .tokenHash(hashedToken)
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build();
    }

    public boolean isValid() {
        if (revokedAt != null) return false;
        return LocalDateTime.now().isBefore(expiresAt);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void revoke() {
        this.revokedAt = LocalDateTime.now();
    }
}
```

**Key Points**:
- ManyToOne relationship to User (unidirectional from child side)
- Token stored as bcrypt hash (never plain text)
- Business methods for validation and revocation
- Indexes for efficient lookup

---

### 4.3 Folder Entity (Composite Pattern)

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "folders", indexes = {
    @Index(name = "idx_folders_user_parent", columnList = "user_id, parent_folder_id"),
    @Index(name = "idx_folders_path", columnList = "path"),
    @Index(name = "idx_folders_depth", columnList = "depth"),
    @Index(name = "idx_folders_deleted_at", columnList = "deleted_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder; // Null = root level

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "path", nullable = false, length = 500)
    private String path; // Materialized path: /uuid1/uuid2/uuid3

    @Column(name = "depth", nullable = false)
    private Integer depth; // 0 = root, max 10

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Bidirectional relationships (Composite Pattern)
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Folder> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Deck> decks = new ArrayList<>();

    // Business methods
    public void addSubFolder(Folder subFolder) {
        if (this.depth >= 9) {
            throw new IllegalArgumentException("Cannot create folder beyond depth 10");
        }
        subFolder.setParentFolder(this);
        subFolder.setUser(this.user);
        subFolder.setDepth(this.depth + 1);
        subFolder.setPath(this.path + "/" + subFolder.getId());
        this.subFolders.add(subFolder);
    }

    public void addDeck(Deck deck) {
        deck.setFolder(this);
        deck.setUser(this.user);
        this.decks.add(deck);
    }

    public void moveTo(Folder newParent) {
        // Validate circular reference
        if (newParent != null && newParent.getPath().startsWith(this.path)) {
            throw new IllegalArgumentException("Cannot move folder into itself or descendants");
        }

        // Validate depth after move
        int newDepth = (newParent == null) ? 0 : newParent.getDepth() + 1;
        int depthDelta = newDepth - this.depth;
        int maxDescendantDepth = calculateMaxDescendantDepth();

        if (maxDescendantDepth + depthDelta > 10) {
            throw new IllegalArgumentException("Move would exceed max depth 10");
        }

        // Update parent
        this.parentFolder = newParent;
        recalculatePathAndDepth();
    }

    private int calculateMaxDescendantDepth() {
        int maxDepth = this.depth;
        for (Folder subFolder : subFolders) {
            maxDepth = Math.max(maxDepth, subFolder.calculateMaxDescendantDepth());
        }
        return maxDepth;
    }

    private void recalculatePathAndDepth() {
        if (parentFolder == null) {
            this.path = "/" + this.getId();
            this.depth = 0;
        } else {
            this.path = parentFolder.getPath() + "/" + this.getId();
            this.depth = parentFolder.getDepth() + 1;
        }

        // Recursively update children
        for (Folder subFolder : subFolders) {
            subFolder.recalculatePathAndDepth();
        }
    }

    public int countTotalItems() {
        int count = 1; // Self
        for (Folder subFolder : subFolders) {
            count += subFolder.countTotalItems();
        }
        count += decks.size();
        return count;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        // Cascade soft delete
        for (Folder subFolder : subFolders) {
            subFolder.softDelete();
        }
        for (Deck deck : decks) {
            deck.softDelete();
        }
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void restore() {
        this.deletedAt = null;
        // Cascade restore
        for (Folder subFolder : subFolders) {
            subFolder.restore();
        }
        for (Deck deck : decks) {
            deck.restore();
        }
    }

    @PrePersist
    public void prePersist() {
        if (parentFolder == null) {
            this.depth = 0;
            this.path = "/" + this.getId();
        } else {
            this.depth = parentFolder.getDepth() + 1;
            this.path = parentFolder.getPath() + "/" + this.getId();
        }
    }
}
```

**Key Points**:
- Self-referencing relationship (parentFolder)
- Materialized path for efficient tree queries
- Depth constraint (max 10 levels)
- Cascade soft delete to children
- Business logic for move/copy validation
- Composite pattern implementation

---

### 4.4 Deck Entity

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "decks", indexes = {
    @Index(name = "idx_decks_folder_user", columnList = "folder_id, user_id"),
    @Index(name = "idx_decks_user_deleted", columnList = "user_id, deleted_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deck extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder; // Nullable for root-level decks

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relationships
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Card> cards = new ArrayList<>();

    // Business methods
    public void addCard(Card card) {
        card.setDeck(this);
        this.cards.add(card);
    }

    public void moveTo(Folder newFolder) {
        this.folder = newFolder;
    }

    public int getCardCount() {
        return (int) cards.stream()
            .filter(card -> !card.isDeleted())
            .count();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        for (Card card : cards) {
            card.softDelete();
        }
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void restore() {
        this.deletedAt = null;
        for (Card card : cards) {
            card.restore();
        }
    }
}
```

**Key Points**:
- Optional folder relationship (can be at root level)
- Cascade delete to cards
- Soft delete implementation
- Helper methods for card management

---

### 4.5 Card Entity

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cards", indexes = {
    @Index(name = "idx_cards_deck", columnList = "deck_id"),
    @Index(name = "idx_cards_deleted", columnList = "deck_id, deleted_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(name = "front", nullable = false, length = 5000)
    private String front; // Question

    @Column(name = "back", nullable = false, length = 5000)
    private String back; // Answer

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relationships
    @OneToOne(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private CardBoxPosition boxPosition;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewLog> reviewLogs = new ArrayList<>();

    // Business methods
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void restore() {
        this.deletedAt = null;
    }

    public void initializeBoxPosition(User user) {
        if (boxPosition == null) {
            this.boxPosition = CardBoxPosition.createNew(this, user);
        }
    }
}
```

**Key Points**:
- Simple front/back text fields
- OneToOne with CardBoxPosition
- OneToMany with ReviewLog
- Soft delete support

---

### 4.6 CardBoxPosition Entity (SRS Core)

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "card_box_position", indexes = {
    @Index(name = "idx_card_box_user_due", columnList = "user_id, due_date, current_box"),
    @Index(name = "idx_card_box_card_user", columnList = "card_id, user_id"),
    @Index(name = "idx_card_box_user_box", columnList = "user_id, current_box")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBoxPosition extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "current_box", nullable = false)
    @Builder.Default
    private Integer currentBox = 1; // 1-7

    @Column(name = "ease_factor", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal easeFactor = new BigDecimal("2.5");

    @Column(name = "interval_days", nullable = false)
    private Integer intervalDays;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    @Column(name = "review_count", nullable = false)
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "lapse_count", nullable = false)
    @Builder.Default
    private Integer lapseCount = 0;

    // Business methods
    public static CardBoxPosition createNew(Card card, User user) {
        return CardBoxPosition.builder()
            .card(card)
            .user(user)
            .currentBox(1)
            .intervalDays(1)
            .dueDate(LocalDate.now())
            .build();
    }

    public void updateAfterReview(Rating rating, SRSSettings settings) {
        this.lastReviewedAt = LocalDateTime.now();
        this.reviewCount++;

        switch (rating) {
            case AGAIN: // Forgot
                handleForgottenCard(settings);
                this.lapseCount++;
                break;
            case HARD: // Difficult
                // Stay in same box, reduce interval
                this.intervalDays = getBoxInterval(this.currentBox) / 2;
                this.dueDate = LocalDate.now().plusDays(this.intervalDays);
                break;
            case GOOD: // Normal
                moveToNextBox();
                break;
            case EASY: // Very easy
                moveToNextBox();
                this.intervalDays *= 2; // Double interval
                this.dueDate = LocalDate.now().plusDays(this.intervalDays);
                break;
        }
    }

    private void handleForgottenCard(SRSSettings settings) {
        switch (settings.getForgottenCardAction()) {
            case MOVE_TO_BOX_1:
                this.currentBox = 1;
                this.intervalDays = getBoxInterval(1);
                this.dueDate = LocalDate.now().plusDays(this.intervalDays);
                break;
            case MOVE_DOWN_N_BOXES:
                int targetBox = Math.max(1, this.currentBox - settings.getMoveDownBoxes());
                this.currentBox = targetBox;
                this.intervalDays = getBoxInterval(targetBox);
                this.dueDate = LocalDate.now().plusDays(this.intervalDays);
                break;
            case STAY_IN_BOX:
                this.intervalDays = getBoxInterval(this.currentBox) / 2;
                this.dueDate = LocalDate.now().plusDays(this.intervalDays);
                break;
        }
    }

    private void moveToNextBox() {
        if (this.currentBox < 7) {
            this.currentBox++;
        }
        this.intervalDays = getBoxInterval(this.currentBox);
        this.dueDate = LocalDate.now().plusDays(this.intervalDays);
    }

    private Integer getBoxInterval(Integer box) {
        return switch (box) {
            case 1 -> 1;
            case 2 -> 3;
            case 3 -> 7;
            case 4 -> 14;
            case 5 -> 30;
            case 6 -> 60;
            case 7 -> 120;
            default -> 1;
        };
    }

    public boolean isDue() {
        return LocalDate.now().isAfter(dueDate) || LocalDate.now().isEqual(dueDate);
    }
}

enum Rating {
    AGAIN, HARD, GOOD, EASY
}
```

**Key Points**:
- Composite index on (user_id, due_date, current_box) for review queries
- Business logic for SRS algorithm
- Box intervals: 1, 3, 7, 14, 30, 60, 120 days
- Rating-based box transitions

---

### 4.7 ReviewLog Entity

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "review_logs", indexes = {
    @Index(name = "idx_review_logs_user_date", columnList = "user_id, reviewed_at"),
    @Index(name = "idx_review_logs_card", columnList = "card_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating", nullable = false, length = 20)
    private Rating rating;

    @Column(name = "previous_box", nullable = false)
    private Integer previousBox;

    @Column(name = "new_box", nullable = false)
    private Integer newBox;

    @Column(name = "interval_days", nullable = false)
    private Integer intervalDays;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;

    // Factory method
    public static ReviewLog create(Card card, User user, Rating rating,
                                    Integer prevBox, Integer newBox, Integer interval) {
        return ReviewLog.builder()
            .card(card)
            .user(user)
            .rating(rating)
            .previousBox(prevBox)
            .newBox(newBox)
            .intervalDays(interval)
            .reviewedAt(LocalDateTime.now())
            .build();
    }
}
```

**Key Points**:
- Immutable log records (no setters after creation)
- Indexed on user_id and reviewed_at for history queries
- Factory method for consistent creation

---

### 4.8 SRSSettings Entity

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "srs_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SRSSettings extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "total_boxes", nullable = false)
    @Builder.Default
    private Integer totalBoxes = 7; // Fixed for MVP

    @Enumerated(EnumType.STRING)
    @Column(name = "review_order", nullable = false, length = 20)
    @Builder.Default
    private ReviewOrder reviewOrder = ReviewOrder.RANDOM;

    @Column(name = "notification_enabled", nullable = false)
    @Builder.Default
    private Boolean notificationEnabled = true;

    @Column(name = "notification_time", nullable = false)
    @Builder.Default
    private LocalTime notificationTime = LocalTime.of(9, 0);

    @Enumerated(EnumType.STRING)
    @Column(name = "forgotten_card_action", nullable = false, length = 30)
    @Builder.Default
    private ForgottenCardAction forgottenCardAction = ForgottenCardAction.MOVE_TO_BOX_1;

    @Column(name = "move_down_boxes", nullable = false)
    @Builder.Default
    private Integer moveDownBoxes = 1;

    @Column(name = "new_cards_per_day", nullable = false)
    @Builder.Default
    private Integer newCardsPerDay = 20;

    @Column(name = "max_reviews_per_day", nullable = false)
    @Builder.Default
    private Integer maxReviewsPerDay = 200;

    // Factory method
    public static SRSSettings createDefault(User user) {
        return SRSSettings.builder()
            .user(user)
            .build(); // Uses @Builder.Default values
    }
}

enum ReviewOrder {
    ASCENDING, DESCENDING, RANDOM
}

enum ForgottenCardAction {
    MOVE_TO_BOX_1, MOVE_DOWN_N_BOXES, STAY_IN_BOX
}
```

**Key Points**:
- OneToOne with User
- Default values for all settings
- Enums for configurable behavior

---

### 4.9 NotificationSettings Entity

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "notification_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettings extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "daily_reminder_enabled", nullable = false)
    @Builder.Default
    private Boolean dailyReminderEnabled = true;

    @Column(name = "daily_reminder_time", nullable = false)
    @Builder.Default
    private LocalTime dailyReminderTime = LocalTime.of(9, 0); // 09:00

    @Column(name = "daily_reminder_days", nullable = false, length = 50)
    @Builder.Default
    private String dailyReminderDays = "MON,TUE,WED,THU,FRI,SAT,SUN";

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_method", nullable = false, length = 20)
    @Builder.Default
    private NotificationMethod notificationMethod = NotificationMethod.EMAIL;

    @Column(name = "notification_email", length = 255)
    private String notificationEmail;

    @Column(name = "push_token", length = 500)
    private String pushToken;

    public static NotificationSettings createDefault(User user) {
        return NotificationSettings.builder()
            .user(user)
            .build();
    }

    public String getEffectiveEmail() {
        return notificationEmail != null ? notificationEmail : user.getEmail();
    }

    public boolean shouldNotifyOnDay(String dayOfWeek) {
        return dailyReminderEnabled &&
               dailyReminderDays.contains(dayOfWeek.toUpperCase());
    }

    public enum NotificationMethod {
        EMAIL, PUSH, SMS
    }
}
```

---

### 4.10 NotificationLog Entity

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notification_logs", indexes = {
    @Index(name = "idx_notification_logs_user_date", columnList = "user_id, sent_at"),
    @Index(name = "idx_notification_logs_status", columnList = "status, sent_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_method", nullable = false, length = 20)
    private NotificationMethod notificationMethod;

    @Column(name = "recipient", nullable = false, length = 255)
    private String recipient;

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "sent_at", nullable = false)
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    public static NotificationLog createPending(
        User user, NotificationType type, NotificationMethod method,
        String recipient, String subject, String body, Map<String, Object> metadata
    ) {
        return NotificationLog.builder()
            .user(user).notificationType(type).notificationMethod(method)
            .recipient(recipient).subject(subject).body(body).metadata(metadata)
            .status(NotificationStatus.PENDING).build();
    }

    public void markAsSent() { this.status = NotificationStatus.SENT; }
    public void markAsDelivered() { this.status = NotificationStatus.DELIVERED; this.deliveredAt = LocalDateTime.now(); }
    public void markAsFailed(String error) { this.status = NotificationStatus.FAILED; this.errorMessage = error; }

    public enum NotificationType { DAILY_REMINDER, STREAK_REMINDER, ACHIEVEMENT, SYSTEM }
    public enum NotificationMethod { EMAIL, PUSH, SMS }
    public enum NotificationStatus { PENDING, SENT, DELIVERED, FAILED, BOUNCED }
}
```

---

### 4.11 UserStats Entity

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "total_cards_learned", nullable = false)
    @Builder.Default
    private Integer totalCardsLearned = 0;

    @Column(name = "streak_days", nullable = false)
    @Builder.Default
    private Integer streakDays = 0;

    @Column(name = "last_study_date")
    private LocalDate lastStudyDate;

    @Column(name = "total_study_time_minutes", nullable = false)
    @Builder.Default
    private Integer totalStudyTimeMinutes = 0;

    // Factory method
    public static UserStats createDefault(User user) {
        return UserStats.builder()
            .user(user)
            .build();
    }

    // Business methods
    public void recordReview(LocalDate today) {
        this.totalCardsLearned++;
        updateStreak(today);
    }

    private void updateStreak(LocalDate today) {
        if (lastStudyDate == null) {
            streakDays = 1;
        } else if (lastStudyDate.equals(today)) {
            // Same day, no change
        } else if (lastStudyDate.equals(today.minusDays(1))) {
            // Consecutive day
            streakDays++;
        } else {
            // Streak broken
            streakDays = 1;
        }
        lastStudyDate = today;
    }
}
```

**Key Points**:
- OneToOne with User
- Business logic for streak calculation
- Updated synchronously after each review

---

### 4.12 FolderStats Entity (Composite Key)

```java
package com.repeatwise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "folder_stats")
@IdClass(FolderStatsId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderStats {

    @Id
    @Column(name = "folder_id")
    private UUID folderId;

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "total_cards_count", nullable = false)
    @Builder.Default
    private Integer totalCardsCount = 0;

    @Column(name = "due_cards_count", nullable = false)
    @Builder.Default
    private Integer dueCardsCount = 0;

    @Column(name = "new_cards_count", nullable = false)
    @Builder.Default
    private Integer newCardsCount = 0;

    @Column(name = "mature_cards_count", nullable = false)
    @Builder.Default
    private Integer matureCardsCount = 0; // Box >= 5

    @Column(name = "last_computed_at")
    private LocalDateTime lastComputedAt;

    // Business methods
    public boolean isStale(int maxAgeMinutes) {
        if (lastComputedAt == null) return true;
        return lastComputedAt.isBefore(LocalDateTime.now().minusMinutes(maxAgeMinutes));
    }

    public void markComputed() {
        this.lastComputedAt = LocalDateTime.now();
    }

    public void invalidate() {
        this.lastComputedAt = null;
    }
}

// Composite Key Class
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class FolderStatsId implements Serializable {
    private UUID folderId;
    private UUID userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FolderStatsId)) return false;
        FolderStatsId that = (FolderStatsId) o;
        return Objects.equals(folderId, that.folderId) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(folderId, userId);
    }
}
```

**Key Points**:
- Composite primary key using @IdClass
- Denormalized cache table
- TTL-based staleness check
- Manual invalidation support

---

## 5. Auditing Configuration

### 5.1 Enable JPA Auditing

```java
package com.repeatwise.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
```

### 5.2 Auditable Entity

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
}
```

### 5.3 Audit Aware Implementation

```java
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.of(authentication.getName());
    }
}
```

---

## 6. Soft Delete Implementation

### 6.1 Soft Deletable Interface

```java
public interface SoftDeletable {
    LocalDateTime getDeletedAt();
    void setDeletedAt(LocalDateTime deletedAt);

    default void softDelete() {
        setDeletedAt(LocalDateTime.now());
    }

    default boolean isDeleted() {
        return getDeletedAt() != null;
    }

    default void restore() {
        setDeletedAt(null);
    }
}
```

### 6.2 Query Filtering

**Using @Where annotation (Hibernate)**:
```java
@Entity
@Table(name = "folders")
@Where(clause = "deleted_at IS NULL")
public class Folder extends BaseEntity implements SoftDeletable {
    // ...
}
```

**Using Query Methods**:
```java
public interface FolderRepository extends JpaRepository<Folder, UUID> {
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.deletedAt IS NULL")
    List<Folder> findActiveByUserId(@Param("userId") UUID userId);

    // Include deleted
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId")
    List<Folder> findAllByUserIdIncludingDeleted(@Param("userId") UUID userId);
}
```

---

## 7. Entity Lifecycle Callbacks

```java
@Entity
public class Card extends BaseEntity {

    @PrePersist
    public void prePersist() {
        // Validation before insert
        if (front == null || front.trim().isEmpty()) {
            throw new IllegalStateException("Card front cannot be empty");
        }
    }

    @PostPersist
    public void postPersist() {
        // After insert (e.g., log event)
        log.info("Card created: {}", this.id);
    }

    @PreUpdate
    public void preUpdate() {
        // Before update
        if (front == null || front.trim().isEmpty()) {
            throw new IllegalStateException("Card front cannot be empty");
        }
    }

    @PostUpdate
    public void postUpdate() {
        // After update
        log.info("Card updated: {}", this.id);
    }

    @PreRemove
    public void preRemove() {
        // Before delete
        log.warn("Card being deleted: {}", this.id);
    }

    @PostRemove
    public void postRemove() {
        // After delete
        log.info("Card deleted: {}", this.id);
    }

    @PostLoad
    public void postLoad() {
        // After entity loaded from database
    }
}
```

---

## 8. Best Practices Summary

### 8.1 DO's

✅ Use LAZY loading by default
✅ Use @EntityGraph or JOIN FETCH for N+1 prevention
✅ Implement equals() and hashCode() based on ID
✅ Use enums with STRING type
✅ Use composite indexes for multi-column queries
✅ Implement soft delete for important entities
✅ Use @CreatedDate and @LastModifiedDate for auditing
✅ Validate data in business logic, not just database
✅ Use builder pattern for entity creation
✅ Use factory methods for complex initialization

### 8.2 DON'Ts

❌ Don't use EAGER loading
❌ Don't use bidirectional @ManyToMany (use intermediate entity)
❌ Don't use @OneToMany with @JoinColumn
❌ Don't load entire collections in loops (N+1 problem)
❌ Don't use ORDINAL for enums
❌ Don't cascade ALL on @ManyToOne relationships
❌ Don't expose entities directly in API (use DTOs)
❌ Don't store plain text passwords
❌ Don't hardcode business logic in entities
❌ Don't use primitive types for nullable fields

---

## 9. Conclusion

This JPA entity design provides:
- Comprehensive entity relationships
- Performance-optimized fetch strategies
- Soft delete implementation
- Auditing support
- Business logic encapsulation
- Type safety with enums
- Proper indexing strategies

**Next Steps**:
1. Implement repositories (see query-optimization.md)
2. Create migration scripts (see migration-scripts.md)
3. Write integration tests
4. Monitor query performance with EXPLAIN ANALYZE

**References**:
- Spring Data JPA Documentation: https://spring.io/projects/spring-data-jpa
- Hibernate Best Practices: https://vladmihalcea.com/
- JPA 2.2 Specification: https://jcp.org/en/jsr/detail?id=338
