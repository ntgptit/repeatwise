package com.repeatwise.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.repeatwise.entity.base.SoftDeletableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Folder entity for hierarchical organization of decks
 */
@Entity
@Table(name = "folders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folder extends SoftDeletableEntity {

    @NotNull(message = "{error.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @NotBlank(message = "{error.folder.name.required}")
    @Size(max = 100, message = "{error.folder.name.too.long}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "{error.folder.description.too.long}")
    @Column(name = "description", length = 500)
    private String description;

    @Builder.Default
    @Min(value = 0, message = "{error.folder.depth.min}")
    @Max(value = 10, message = "{error.folder.depth.max}")
    @Column(name = "depth", nullable = false)
    private Integer depth = 0;

    @NotBlank(message = "{error.folder.path.required}")
    @Size(max = 1000, message = "{error.folder.path.length}")
    @Pattern(regexp = "^(/[0-9a-f-]{36})+$", message = "{error.folder.path.invalid}")
    @Column(name = "path", nullable = false, length = 1000)
    private String path;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    // Relationships
    @Builder.Default
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> childFolders = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deck> decks = new ArrayList<>();

    @OneToOne(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private FolderStats folderStats;

    /**
     * Check if this is a root folder
     */
    public boolean isRoot() {
        return this.parentFolder == null;
    }

    /**
     * Get the full path as a list of folder IDs
     */
    public List<String> getPathSegments() {
        if ((this.path == null) || this.path.isEmpty()) {
            return new ArrayList<>();
        }
        final var segments = this.path.split("/");
        final List<String> result = new ArrayList<>();
        for (final String segment : segments) {
            if (!segment.isEmpty()) {
                result.add(segment);
            }
        }
        return result;
    }

    /**
     * Build path from parent folder
     */
    public void buildPath() {
        if (getId() == null) {
            setId(UUID.randomUUID());
        }

        if (this.parentFolder == null) {
            this.path = "/" + getId().toString();
            this.depth = 0;
        } else {
            this.path = this.parentFolder.getPath() + "/" + getId().toString();
            this.depth = this.parentFolder.getDepth() + 1;
        }
    }
}
