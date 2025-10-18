package com.repeatwise.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.repeatwise.entity.base.SoftDeletableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Deck entity - Container for flashcards
 *
 * Requirements:
 * - UC-011: Create Deck
 * - UC-012: Move Deck
 * - UC-013: Copy Deck
 * - UC-014: Delete Deck (Soft Delete)
 * - Schema: decks table (section 2.4)
 *
 * Business Rules:
 * - BR-031: Deck naming (1-100 chars, no trim on special chars)
 * - BR-032: Deck can be at root level (folder_id nullable)
 * - BR-033: Name unique within same folder (per user)
 * - BR-034: New deck has 0 cards initially
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "decks", indexes = {
        @Index(name = "idx_decks_user", columnList = "user_id"),
        @Index(name = "idx_decks_folder", columnList = "folder_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "idx_decks_name_folder", columnNames = { "user_id", "folder_id", "name" })
})
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Deck extends SoftDeletableEntity {

    @NotBlank(message = "{deck.name.required}")
    @Size(min = 1, max = 100, message = "{deck.name.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "{deck.description.size}")
    @Column(name = "description", length = 500)
    private String description;

    // ==================== Relationships ====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Parent folder (nullable for root-level decks)
     * BR-032: folder_id can be NULL
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Card> cards = new ArrayList<>();

    // ==================== Business Methods ====================

    /**
     * Add card to deck
     */
    public void addCard(final Card card) {
        this.cards.add(card);
        card.setDeck(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Deck deck)) {
            return false;
        }
        return (getId() != null) && getId().equals(deck.getId());
    }

    /**
     * Get total card count
     */
    public int getCardCount() {
        return this.cards.size();
    }

    /**
     * Check if deck has any cards
     */
    public boolean hasCards() {
        return !this.cards.isEmpty();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Check if deck is at root level (no folder)
     */
    public boolean isAtRoot() {
        return this.folder == null;
    }

    // ==================== Lifecycle Methods ====================

    /**
     * Move deck to new folder
     * Used in UC-012: Move Deck
     */
    public void moveTo(final Folder newFolder) {
        if (this.folder != null) {
            this.folder.removeDeck(this);
        }
        this.folder = newFolder;
        if (newFolder != null) {
            newFolder.addDeck(this);
        }
    }

    // ==================== Equals & HashCode ====================

    /**
     * Remove card from deck
     */
    public void removeCard(final Card card) {
        this.cards.remove(card);
        card.setDeck(null);
    }

    @Override
    public String toString() {
        return "Deck{" +
                "id=" + getId() +
                ", name='" + this.name + '\'' +
                ", folderId=" + (this.folder != null ? this.folder.getId() : null) +
                ", cardCount=" + this.cards.size() +
                '}';
    }

    @PrePersist
    @PreUpdate
    public void trimFields() {
        if (this.name != null) {
            this.name = this.name.trim();
        }
        if (this.description != null) {
            this.description = this.description.trim();
        }
    }
}
