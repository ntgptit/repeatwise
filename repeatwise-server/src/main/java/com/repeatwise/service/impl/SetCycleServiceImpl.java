package com.repeatwise.service.impl;

import com.repeatwise.dto.SetCycleDto;
import com.repeatwise.enums.CycleStatus;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.exception.CycleNotCompleteException;
import com.repeatwise.mapper.SetCycleMapper;
import com.repeatwise.model.Set;
import com.repeatwise.model.SetCycle;
import com.repeatwise.repository.SetCycleRepository;
import com.repeatwise.repository.SetRepository;
import com.repeatwise.service.SetCycleService;
import com.repeatwise.service.RemindScheduleService;
import com.repeatwise.service.NotificationService;
import com.repeatwise.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SetCycleServiceImpl implements SetCycleService {

    private final SetCycleRepository setCycleRepository;
    private final SetRepository setRepository;
    private final SetCycleMapper setCycleMapper;
    private final RemindScheduleService remindScheduleService;
    private final NotificationService notificationService;

    @Override
    public SetCycleDto startCycle(UUID setId, UUID userId) {
        ServiceUtils.logOperationStart("cycle start", setId, userId);
        
        Set set = ServiceUtils.findEntityOrThrow(
                () -> setRepository.findByIdAndUserId(setId, userId), 
                "Set", 
                setId, 
                userId
        );
        
        // Check if there's already an active cycle
        Optional<SetCycle> activeCycle = setCycleRepository.findActiveCycleBySetId(setId);
        if (activeCycle.isPresent()) {
            throw new IllegalStateException("Set already has an active cycle");
        }
        
        // Get next cycle number
        Integer nextCycleNo = setCycleRepository.findNextCycleNumber(setId);
        
        SetCycle cycle = SetCycle.builder()
                .set(set)
                .cycleNo(nextCycleNo)
                .startedAt(LocalDate.now())
                .status(CycleStatus.ACTIVE)
                .build();
        
        SetCycle savedCycle = setCycleRepository.save(cycle);
        ServiceUtils.logOperationSuccess("cycle start", savedCycle.getId());
        
        // Automatically create reminders for this cycle
        try {
            remindScheduleService.scheduleRemindersForNextCycle(setId, userId, LocalDate.now());
            log.info("Reminders created successfully for cycle ID: {}", savedCycle.getId());
        } catch (Exception e) {
            log.warn("Failed to create reminders for cycle ID: {}, error: {}", savedCycle.getId(), e.getMessage());
        }
        
        return setCycleMapper.toDto(savedCycle);
    }

    @Override
    public SetCycleDto finishCycle(UUID cycleId, UUID userId) {
        ServiceUtils.logOperationStart("cycle finish", cycleId, userId);
        
        SetCycle cycle = ServiceUtils.findEntityOrThrow(
                () -> setCycleRepository.findById(cycleId), 
                "Cycle", 
                cycleId
        );
        
        // Verify user owns this cycle
        if (!cycle.getSet().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Cycle not found with ID: " + cycleId + " for user ID: " + userId);
        }
        
        // Check if cycle has 5 reviews
        long reviewCount = cycle.getReviews().size();
        if (reviewCount < 5) {
            throw new CycleNotCompleteException("Cycle must have 5 reviews before finishing. Current: " + reviewCount);
        }
        
        // Calculate average score
        BigDecimal avgScore = calculateAverageScore(cycleId);
        
        // Calculate next cycle delay using SRS algorithm
        int nextCycleDelay = calculateNextCycleDelay(cycleId);
        
        // Update cycle
        cycle.setStatus(CycleStatus.FINISHED);
        cycle.setFinishedAt(LocalDate.now());
        cycle.setAvgScore(avgScore);
        cycle.setNextCycleDelayDays(nextCycleDelay);
        
        SetCycle updatedCycle = setCycleRepository.save(cycle);
        ServiceUtils.logOperationSuccess("cycle finish", updatedCycle.getId());
        
        // Create notification for cycle completion
        try {
            notificationService.createCycleCompletedNotification(
                userId, 
                cycle.getSet().getId(), 
                cycle.getSet().getName(), 
                cycle.getCycleNo()
            );
            log.info("Cycle completed notification created for set: {}", cycle.getSet().getName());
        } catch (Exception e) {
            log.warn("Failed to create cycle completed notification for cycle ID: {}, error: {}", cycleId, e.getMessage());
        }
        
        // Automatically schedule next cycle
        try {
            UUID setId = cycle.getSet().getId();
            LocalDate nextCycleDate = LocalDate.now().plusDays(nextCycleDelay);
            
            // Update set with next cycle information
            Set set = cycle.getSet();
            set.setLastCycleEndDate(LocalDate.now());
            set.setNextCycleStartDate(nextCycleDate);
            set.setCurrentCycle(set.getCurrentCycle() + 1);
            setRepository.save(set);
            
            // Create reminders for next cycle
            remindScheduleService.scheduleRemindersForNextCycle(setId, userId, nextCycleDate);
            
            log.info("Next cycle automatically scheduled for set {} on date {} (delay: {} days)", 
                    setId, nextCycleDate, nextCycleDelay);
        } catch (Exception e) {
            log.warn("Failed to automatically schedule next cycle for cycle ID: {}, error: {}", 
                    cycleId, e.getMessage());
        }
        
        return setCycleMapper.toDto(updatedCycle);
    }

    @Override
    public Optional<SetCycleDto> findById(UUID id) {
        ServiceUtils.logEntityLookup("cycle", id);
        return setCycleRepository.findById(id)
                .map(setCycleMapper::toDto);
    }

    @Override
    public Optional<SetCycleDto> findByIdAndSetId(UUID id, UUID setId) {
        ServiceUtils.logEntityLookup("cycle", id, setId);
        return setCycleRepository.findByIdAndSetId(id, setId)
                .map(setCycleMapper::toDto);
    }

    @Override
    public Optional<SetCycleDto> findActiveCycleBySetId(UUID setId) {
        ServiceUtils.logEntityLookup("active cycle", setId);
        return setCycleRepository.findActiveCycleBySetId(setId)
                .map(setCycleMapper::toDto);
    }

    @Override
    public List<SetCycleDto> findBySetId(UUID setId) {
        ServiceUtils.logEntityLookup("cycles for set", setId);
        return setCycleRepository.findBySetId(setId)
                .stream()
                .map(setCycleMapper::toDto)
                .toList();
    }

    @Override
    public List<SetCycleDto> findBySetIdAndStatus(UUID setId, CycleStatus status) {
        ServiceUtils.logEntityLookup("cycles for set with status", setId, status);
        return setCycleRepository.findBySetIdAndStatus(setId, status)
                .stream()
                .map(setCycleMapper::toDto)
                .toList();
    }

    @Override
    public List<SetCycleDto> findCyclesReadyToFinish() {
        ServiceUtils.logEntityLookup("cycles ready to finish");
        return setCycleRepository.findCyclesReadyToFinish()
                .stream()
                .map(setCycleMapper::toDto)
                .toList();
    }

    @Override
    public BigDecimal calculateAverageScore(UUID cycleId) {
        ServiceUtils.logEntityLookup("average score calculation", cycleId);
        return setCycleRepository.findAverageScoreBySetCycleId(cycleId);
    }

    @Override
    public int calculateNextCycleDelay(UUID cycleId) {
        ServiceUtils.logEntityLookup("next cycle delay calculation", cycleId);
        
        SetCycle cycle = setCycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found with ID: " + cycleId));
        
        // Get average score
        BigDecimal avgScore = calculateAverageScore(cycleId);
        if (avgScore == null) {
            throw new IllegalStateException("Cannot calculate delay without average score");
        }
        
        // Get word count
        int wordCount = cycle.getSet().getWordCount();
        
        // Apply SRS algorithm
        SRSConfig config = SRSConfig.DEFAULT;
        double avgScoreValue = avgScore.doubleValue();
        
        int delay = config.baseDelay() - 
                   (int)(config.penalty() * (100 - avgScoreValue)) + 
                   (int)(config.scaling() * wordCount);
        
        // Apply min/max constraints
        delay = Math.max(config.minDelay(), Math.min(config.maxDelay(), delay));
        
        log.debug("Calculated delay: {} days (avgScore: {}, wordCount: {})", delay, avgScoreValue, wordCount);
        return delay;
    }

    @Override
    public CycleStatistics getCycleStatistics(UUID cycleId) {
        log.debug("Getting statistics for cycle ID: {}", cycleId);
        
        SetCycle cycle = setCycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found with ID: " + cycleId));
        
        int reviewCount = cycle.getReviews().size();
        
        return new CycleStatistics(
                cycle.getCycleNo(),
                cycle.getStatus(),
                cycle.getStartedAt(),
                cycle.getFinishedAt(),
                cycle.getAvgScore(),
                reviewCount,
                cycle.getNextCycleDelayDays()
        );
    }

    @Override
    public List<SetCycleDto> findCyclesByUserId(UUID userId) {
        ServiceUtils.logEntityLookup("cycles for user", userId);
        return setCycleRepository.findCyclesByUserId(userId)
                .stream()
                .map(setCycleMapper::toDto)
                .toList();
    }

    @Override
    public List<SetCycleDto> findCyclesByUserIdAndStatus(UUID userId, CycleStatus status) {
        ServiceUtils.logEntityLookup("cycles for user with status", userId, status);
        return setCycleRepository.findCyclesByUserIdAndStatus(userId, status)
                .stream()
                .map(setCycleMapper::toDto)
                .toList();
    }

    @Override
    public Integer getNextCycleNumber(UUID setId) {
        ServiceUtils.logEntityLookup("next cycle number", setId);
        return setCycleRepository.findNextCycleNumber(setId);
    }

    @Override
    public SetCycleDto updateCycleAverageScore(UUID cycleId, BigDecimal avgScore) {
        ServiceUtils.logOperationStart("cycle average score update", cycleId, avgScore);
        
        SetCycle cycle = ServiceUtils.findEntityOrThrow(
                () -> setCycleRepository.findById(cycleId), 
                "Cycle", 
                cycleId
        );
        
        cycle.setAvgScore(avgScore);
        SetCycle updatedCycle = setCycleRepository.save(cycle);
        
        ServiceUtils.logOperationSuccess("cycle average score update", updatedCycle.getId());
        return setCycleMapper.toDto(updatedCycle);
    }
} 
