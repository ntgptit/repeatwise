package com.repeatwise.entity;

import com.repeatwise.entity.base.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
        if (cardBoxPositions == null) {
            return null;
        }
        return cardBoxPositions.stream()
                .filter(pos -> pos.getUser().getId().toString().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
