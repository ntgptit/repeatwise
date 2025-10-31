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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Folder entity - Hierarchical organization of decks (Composite Pattern)
 *
 * Requirements:
 * - UC-005: Create Folder Hierarchy
 * - UC-006: Rename Folder
 * - UC-007: Move Folder
 * - UC-008: Copy Folder
 * - UC-009: Delete Folder (Soft Delete)
 * - UC-010: View Folder Statistics
 * - Schema: folders table (section 2.3)
 *
 * Business Rules:
 * - BR-010: Folder naming (1-100 chars, trim whitespace, no special chars)
 * - BR-011: Max depth = 10 levels
 * - BR-012: Materialized path format: /uuid1/uuid2/uuid3
 * - BR-013: Unique name within same parent folder
 *
 * Design Pattern: Composite Pattern
 * - Component: FolderComponent interface
 * - Composite: Folder (has children)
 * - Leaf: Deck (no children)
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "folders", indexes = {
        @Index(name = "idx_folders_user", columnList = "user_id"),
        @Index(name = "idx_folders_parent", columnList = "user_id, parent_folder_id"),
        @Index(name = "idx_folders_path", columnList = "user_id, path")
}, uniqueConstraints = {
        @UniqueConstraint(name = "idx_folders_name_parent", columnNames = { "user_id", "parent_folder_id", "name" })
})
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Folder extends SoftDeletableEntity {

    @NotBlank(message = "{folder.name.required}")
    @Size(min = 1, max = 100, message = "{folder.name.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "{folder.description.size}")
    @Column(name = "description", length = 500)
    private String description;

    // ==================== Relationships ====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Folder> childFolders = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Deck> decks = new ArrayList<>();

    // ==================== Hierarchy Properties ====================

    /**
     * Depth in folder tree (0-based)
     * Root folder: depth = 0
     * Max depth: 10 (constraint: BR-011)
     */
    @Min(value = 0, message = "{folder.depth.min}")
    @Max(value = 10, message = "{folder.depth.max}")
    @Column(name = "depth", nullable = false)
    @Builder.Default
    private Integer depth = 0;

    /**
     * Materialized path for fast descendant queries (BR-012)
     * Format: /uuid1/uuid2/uuid3
     * Root: /550e8400-e29b-41d4-a716-446655440000
     * Child: /550e8400-e29b-41d4-a716-446655440000/661e8400-e29b-41d4-a716-446655440001
     *
     * Used for queries like: WHERE path LIKE '/parent_id/%'
     */
    @NotBlank(message = "{folder.path.required}")
    @Size(max = 1000, message = "{folder.path.size}")
    @Column(name = "path", nullable = false, length = 1000)
    private String path;

    // ==================== Business Methods ====================

    /**
     * Add child folder (Composite Pattern)
     */
    public void addChild(final Folder child) {
        this.childFolders.add(child);
        child.setParentFolder(this);
        child.setDepth(this.depth + 1);
    }

    /**
     * Add deck to folder
     */
    public void addDeck(final Deck deck) {
        this.decks.add(deck);
        deck.setFolder(this);
    }

    /**
     * Calculate path for this folder
     * Called when creating or moving folder
     * 
     * UC-007: Root folder depth = 1 (not 0)
     */
    public void calculatePath() {
        if (this.parentFolder == null) {
            // Root folder: /folder_id
            this.path = "/" + getId();
            // UC-007: Root folder depth = 1 (not 0)
            // Only set depth if not already set correctly
            if (this.depth == null || this.depth == 0) {
                this.depth = 1;
            }
        } else {
            // Child folder: parent_path/folder_id
            this.path = this.parentFolder.getPath() + "/" + getId();
            // UC-007: Nested folder depth = parent.depth + 1
            this.depth = this.parentFolder.getDepth() + 1;
        }
    }

    /**
     * Check if this folder can have children (max depth not reached)
     */
    public boolean canHaveChildren() {
        return this.depth < 10;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Folder folder)) {
            return false;
        }
        return (getId() != null) && getId().equals(folder.getId());
    }

    /**
     * Get total number of descendants (folders + decks)
     * Used for copy/move validation
     */
    public int getTotalDescendants() {
        var count = this.childFolders.size() + this.decks.size();
        for (final Folder child : this.childFolders) {
            count += child.getTotalDescendants();
        }
        return count;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Check if this folder is an ancestor of given folder
     * Used for circular reference prevention (UC-007)
     */
    public boolean isAncestorOf(final Folder other) {
        if (other == null) {
            return false;
        }
        return other.getPath().startsWith(this.path + "/") || other.getPath().equals(this.path);
    }

    /**
     * Check if this folder is a root folder
     */
    public boolean isRoot() {
        return (this.parentFolder == null) && (this.depth == 0);
    }

    /**
     * Remove child folder (Composite Pattern)
     */
    public void removeChild(final Folder child) {
        this.childFolders.remove(child);
        child.setParentFolder(null);
    }

    // ==================== Lifecycle Methods ====================

    /**
     * Remove deck from folder
     */
    public void removeDeck(final Deck deck) {
        this.decks.remove(deck);
        deck.setFolder(null);
    }

    // ==================== Equals & HashCode ====================

    @Override
    public String toString() {
        return "Folder{" +
                "id=" + getId() +
                ", name='" + this.name + '\'' +
                ", depth=" + this.depth +
                ", path='" + this.path + '\'' +
                ", parentFolderId=" + (this.parentFolder != null ? this.parentFolder.getId() : null) +
                '}';
    }

    @PrePersist
    @PreUpdate
    public void trimName() {
        if (this.name != null) {
            this.name = this.name.trim();
        }
        if (this.description != null) {
            this.description = this.description.trim();
        }
    }

    /**
     * Check if moving to target parent would exceed max depth
     * Used for validation in move and copy operations
     */
    public boolean wouldExceedMaxDepth(final Folder targetParent, final Integer maxDescendantDepth) {
        final var newDepth = targetParent == null ? 0 : targetParent.getDepth() + 1;
        final var depthDelta = newDepth - this.depth;
        final var resultingMaxDepth = maxDescendantDepth + depthDelta;
        return resultingMaxDepth > 10;
    }
}
