package com.repeatwise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.repeatwise.enums.SetStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sets")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Set extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "word_count", nullable = false)
    private Integer wordCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SetStatus status;

    @Column(name = "current_cycle", nullable = false)
    private Integer currentCycle;

    @Column(name = "last_cycle_end_date")
    private LocalDate lastCycleEndDate;

    @Column(name = "next_cycle_start_date")
    private LocalDate nextCycleStartDate;

    // Relationships
    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SetCycle> cycles = new ArrayList<>();

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RemindSchedule> remindSchedules = new ArrayList<>();

    @Override
    protected void onPrePersist() {
        if (status == null) {
            status = SetStatus.NOT_STARTED;
        }
        if (currentCycle == null) {
            currentCycle = 1;
        }
    }
} 
