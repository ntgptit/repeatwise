package com.repeatwise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.repeatwise.enums.CycleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "set_cycles")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetCycle extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    private Set set;

    @Column(name = "cycle_no", nullable = false)
    private Integer cycleNo;

    @Column(name = "started_at", nullable = false)
    private LocalDate startedAt;

    @Column(name = "finished_at")
    private LocalDate finishedAt;

    @Column(name = "avg_score", precision = 5, scale = 2)
    private BigDecimal avgScore;

    @Column(name = "next_cycle_delay_days")
    private Integer nextCycleDelayDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CycleStatus status;

    // Relationships
    @OneToMany(mappedBy = "setCycle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SetReview> reviews = new ArrayList<>();

    @Override
    protected void onPrePersist() {
        if (status == null) {
            status = CycleStatus.ACTIVE;
        }
    }
} 
