package com.spacedlearning.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * SetItem entity representing individual items within a learning set
 * Maps to the 'set_items' table in the database
 */
@Entity
@Table(name = "set_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class SetItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    private LearningSet learningSet;

    @NotBlank
    @Column(name = "front_content", columnDefinition = "TEXT", nullable = false)
    private String frontContent;

    @NotBlank
    @Column(name = "back_content", columnDefinition = "TEXT", nullable = false)
    private String backContent;

    @Min(value = 1, message = "Item order must be positive")
    @Column(name = "item_order", nullable = false)
    private Integer itemOrder;

    // Helper methods
    public void setLearningSet(LearningSet learningSet) {
        this.learningSet = learningSet;
        if (learningSet != null && !learningSet.getSetItems().contains(this)) {
            learningSet.addSetItem(this);
        }
    }

    /**
     * Get the display order for this item
     */
    public int getDisplayOrder() {
        return itemOrder != null ? itemOrder : 0;
    }
}
