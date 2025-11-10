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
 * Deck entity for flashcard collections
 */
@Entity
@Table(name = "decks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deck extends SoftDeletableEntity {

    @NotNull(message = "{error.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @NotBlank(message = "{error.deck.name.required}")
    @Size(max = 100, message = "{error.deck.name.too.long}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "{error.deck.description.size}")
    @Column(name = "description", length = 500)
    private String description;

    // Relationships
    @Builder.Default
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards = new ArrayList<>();

    /**
     * Check if this is a root-level deck (not in any folder)
     */
    public boolean isRootLevel() {
        return this.folder == null;
    }

    /**
     * Get the total number of cards in this deck
     */
    public int getCardCount() {
        return this.cards != null ? this.cards.size() : 0;
    }
}
