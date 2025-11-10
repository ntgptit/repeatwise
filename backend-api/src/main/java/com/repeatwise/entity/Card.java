package com.repeatwise.entity;

import java.util.ArrayList;
import java.util.List;

import com.repeatwise.entity.base.SoftDeletableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Card entity for flashcards
 */
@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card extends SoftDeletableEntity {

    @NotNull(message = "{error.card.deck.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @NotBlank(message = "{error.card.front.required}")
    @Size(max = 5000, message = "{error.card.front.size}")
    @Column(name = "front", nullable = false, columnDefinition = "TEXT")
    private String front;

    @NotBlank(message = "{error.card.back.required}")
    @Size(max = 5000, message = "{error.card.back.size}")
    @Column(name = "back", nullable = false, columnDefinition = "TEXT")
    private String back;

    // Relationships
    @Builder.Default
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardBoxPosition> cardBoxPositions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLog> reviewLogs = new ArrayList<>();

    /**
     * Get the card box position for a specific user
     *
     * @param userId The user ID
     * @return The card box position for the user, or null if not found
     */
    public CardBoxPosition getPositionForUser(String userId) {
        if (this.cardBoxPositions == null) {
            return null;
        }
        return this.cardBoxPositions.stream()
                .filter(pos -> pos.getUser().getId().toString().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
