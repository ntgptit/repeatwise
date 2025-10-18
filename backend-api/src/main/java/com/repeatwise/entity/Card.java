package com.repeatwise.entity;

import org.hibernate.annotations.SQLRestriction;

import com.repeatwise.entity.base.SoftDeletableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Card entity - Flashcard with front/back text
 *
 * Requirements:
 * - UC-014: Create Card (future)
 * - UC-019: Review Cards with SRS (future)
 *
 * Note: This is a STUB for now to allow Deck entity to compile.
 * Full implementation will be added when card features are developed.
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "cards", indexes = {
        @Index(name = "idx_cards_deck", columnList = "deck_id"),
        @Index(name = "idx_cards_user", columnList = "user_id")
})
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Card extends SoftDeletableEntity {

    @NotBlank(message = "{card.front.required}")
    @Size(max = 1000, message = "{card.front.size}")
    @Column(name = "front", nullable = false, length = 1000)
    private String front;

    @NotBlank(message = "{card.back.required}")
    @Size(max = 1000, message = "{card.back.size}")
    @Column(name = "back", nullable = false, length = 1000)
    private String back;

    // ==================== Relationships ====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    // ==================== SRS Fields (Future) ====================
    // These will be added when SRS algorithm is implemented

    // @Column(name = "box_number")
    // private Integer boxNumber = 1;

    // @Column(name = "review_count")
    // private Integer reviewCount = 0;

    // @Column(name = "due_date")
    // private LocalDate dueDate;

    // @Column(name = "last_reviewed_at")
    // private Instant lastReviewedAt;

    // ==================== Equals & HashCode ====================

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Card card)) {
            return false;
        }
        return (getId() != null) && getId().equals(card.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + getId() +
                ", deckId=" + (this.deck != null ? this.deck.getId() : null) +
                ", front='" + (this.front != null ? this.front.substring(0, Math.min(20, this.front.length())) + "..."
                        : null) + '\'' +
                '}';
    }
}
