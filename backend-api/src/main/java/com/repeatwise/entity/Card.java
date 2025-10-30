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
 * - UC-017: Create/Edit Card
 * - UC-018: Delete Card
 * - UC-019: Review Cards with SRS
 * - Schema: cards table (section 2.5)
 *
 * Business Rules:
 * - BR-CARD-001: Front and back text cannot be empty
 * - BR-CARD-002: Max length 5000 characters per side
 * - BR-CARD-003: Cards created without CardBoxPosition are auto-initialized on first access
 * - BR-CARD-004: Soft delete removes card from review schedule
 * - BR-CARD-005: Editing card during review session updates content but preserves SRS state
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "cards", indexes = {
        @Index(name = "idx_cards_deck", columnList = "deck_id")
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
    @Size(min = 1, max = 5000, message = "{card.front.size}")
    @Column(name = "front", nullable = false, columnDefinition = "TEXT")
    private String front;

    @NotBlank(message = "{card.back.required}")
    @Size(min = 1, max = 5000, message = "{card.back.size}")
    @Column(name = "back", nullable = false, columnDefinition = "TEXT")
    private String back;

    // ==================== Relationships ====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    // ==================== Business Methods ====================

    /**
     * Trim whitespace from front and back fields
     * Called before persist and update
     */
    @jakarta.persistence.PrePersist
    @jakarta.persistence.PreUpdate
    public void trimFields() {
        if (this.front != null) {
            this.front = this.front.trim();
        }
        if (this.back != null) {
            this.back = this.back.trim();
        }
    }

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
                ", front='" + (this.front != null ? this.front.substring(0, Math.min(30, this.front.length())) + "..."
                        : null) + '\'' +
                '}';
    }
}
