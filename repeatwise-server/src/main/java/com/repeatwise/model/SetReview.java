package com.repeatwise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "set_reviews")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_cycle_id", nullable = false)
    private SetCycle setCycle;

    @Column(name = "review_no", nullable = false)
    private Integer reviewNo;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDate reviewedAt;

    @Column(name = "score", nullable = false)
    private Integer score;
} 
