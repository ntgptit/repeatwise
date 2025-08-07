package com.repeatwise.service.impl;

import com.repeatwise.dto.SetDto;
import com.repeatwise.enums.SetStatus;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.mapper.SetMapper;
import com.repeatwise.model.Set;
import com.repeatwise.model.User;
import com.repeatwise.repository.SetRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.SetService;
import com.repeatwise.service.NotificationService;
import com.repeatwise.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SetServiceImpl implements SetService {

    private final SetRepository setRepository;
    private final UserRepository userRepository;
    private final SetMapper setMapper;
    private final NotificationService notificationService;

    @Override
    public SetDto createSet(UUID userId, SetDto setDto) {
        ServiceUtils.logOperationStart("set creation", userId);
        
        // Validate word count
        if (setDto.getWordCount() == null || setDto.getWordCount() <= 0) {
            throw new IllegalArgumentException("Word count must be greater than 0");
        }
        
        User user = ServiceUtils.findEntityOrThrow(
                () -> userRepository.findById(userId), 
                "User", 
                userId
        );
        
        Set set = setMapper.toEntity(setDto);
        set.setUser(user);
        set.setStatus(SetStatus.NOT_STARTED);
        set.setCurrentCycle(1);
        
        Set savedSet = setRepository.save(set);
        ServiceUtils.logOperationSuccess("set creation", savedSet.getId());
        
        return setMapper.toDto(savedSet);
    }

    @Override
    public Optional<SetDto> findById(UUID id) {
        ServiceUtils.logEntityLookup("set", id);
        return setRepository.findById(id)
                .map(setMapper::toDto);
    }

    @Override
    public Optional<SetDto> findByIdAndUserId(UUID id, UUID userId) {
        ServiceUtils.logEntityLookup("set", id, userId);
        return setRepository.findByIdAndUserId(id, userId)
                .map(setMapper::toDto);
    }

    @Override
    public SetDto updateSet(UUID id, UUID userId, SetDto setDto) {
        ServiceUtils.logOperationStart("set update", id, userId);
        
        // Validate word count
        if (setDto.getWordCount() == null || setDto.getWordCount() <= 0) {
            throw new IllegalArgumentException("Word count must be greater than 0");
        }
        
        Set existingSet = ServiceUtils.findEntityOrThrow(
                () -> setRepository.findByIdAndUserId(id, userId), 
                "Set", 
                id, 
                userId
        );
        
        // Update fields
        existingSet.setName(setDto.getName());
        existingSet.setDescription(setDto.getDescription());
        existingSet.setWordCount(setDto.getWordCount());
        
        Set updatedSet = setRepository.save(existingSet);
        ServiceUtils.logOperationSuccess("set update", updatedSet.getId());
        
        return setMapper.toDto(updatedSet);
    }

    @Override
    public void deleteSet(UUID id, UUID userId) {
        ServiceUtils.logOperationStart("set deletion", id, userId);
        
        Set set = ServiceUtils.findEntityOrThrow(
                () -> setRepository.findByIdAndUserId(id, userId), 
                "Set", 
                id, 
                userId
        );
        
        setRepository.delete(set);
        ServiceUtils.logOperationSuccess("set deletion", id);
    }

    @Override
    public List<SetDto> findByUserId(UUID userId) {
        ServiceUtils.logEntityLookup("sets for user", userId);
        return setRepository.findByUserId(userId)
                .stream()
                .map(setMapper::toDto)
                .toList();
    }

    @Override
    public List<SetDto> findByUserIdAndStatus(UUID userId, SetStatus status) {
        ServiceUtils.logEntityLookup("sets for user with status", userId, status);
        return setRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(setMapper::toDto)
                .toList();
    }

    @Override
    public List<SetDto> findSetsToReviewToday(UUID userId) {
        ServiceUtils.logEntityLookup("sets to review today", userId);
        LocalDate today = LocalDate.now();
        return setRepository.findSetsToReviewToday(today)
                .stream()
                .filter(set -> set.getUser().getId().equals(userId))
                .map(setMapper::toDto)
                .toList();
    }

    @Override
    public List<SetDto> findOverdueSets(UUID userId) {
        ServiceUtils.logEntityLookup("overdue sets", userId);
        LocalDate today = LocalDate.now();
        return setRepository.findOverdueSets(today)
                .stream()
                .filter(set -> set.getUser().getId().equals(userId))
                .map(setMapper::toDto)
                .toList();
    }

    @Override
    public List<SetDto> findActiveSets(UUID userId) {
        ServiceUtils.logEntityLookup("active sets", userId);
        return setRepository.findActiveSetsByUserId(userId)
                .stream()
                .map(setMapper::toDto)
                .toList();
    }

    @Override
    public List<SetDto> findMasteredSets(UUID userId) {
        ServiceUtils.logEntityLookup("mastered sets", userId);
        return setRepository.findMasteredSetsByUserId(userId)
                .stream()
                .map(setMapper::toDto)
                .toList();
    }

    @Override
    public List<SetDto> findNotStartedSets(UUID userId) {
        ServiceUtils.logEntityLookup("not started sets", userId);
        return setRepository.findNotStartedSetsByUserId(userId)
                .stream()
                .map(setMapper::toDto)
                .toList();
    }

    @Override
    public SetDto startLearning(UUID setId, UUID userId) {
        ServiceUtils.logOperationStart("set learning start", setId, userId);
        
        Set set = ServiceUtils.findEntityOrThrow(
                () -> setRepository.findByIdAndUserId(setId, userId), 
                "Set", 
                setId, 
                userId
        );
        
        set.setStatus(SetStatus.LEARNING);
        Set updatedSet = setRepository.save(set);
        
        ServiceUtils.logOperationSuccess("set learning start", updatedSet.getId());
        return setMapper.toDto(updatedSet);
    }

    @Override
    public SetDto markAsMastered(UUID setId, UUID userId) {
        ServiceUtils.logOperationStart("set mastered marking", setId, userId);
        
        Set set = ServiceUtils.findEntityOrThrow(
                () -> setRepository.findByIdAndUserId(setId, userId), 
                "Set", 
                setId, 
                userId
        );
        
        set.setStatus(SetStatus.MASTERED);
        Set updatedSet = setRepository.save(set);
        
        // Create notification for set mastered
        try {
            notificationService.createSetMasteredNotification(userId, setId, set.getName());
            log.info("Set mastered notification created for set: {}", set.getName());
        } catch (Exception e) {
            log.warn("Failed to create set mastered notification for set ID: {}, error: {}", setId, e.getMessage());
        }
        
        ServiceUtils.logOperationSuccess("set mastered marking", updatedSet.getId());
        return setMapper.toDto(updatedSet);
    }

    @Override
    public List<SetDto> getDailyReviewSets(UUID userId, LocalDate date) {
        ServiceUtils.logEntityLookup("daily review sets", userId, date);
        
        // Get sets due for review today
        List<Set> setsToReview = setRepository.findSetsToReviewOnDate(date)
                .stream()
                .filter(set -> set.getUser().getId().equals(userId))
                .toList();
        
        // Get overdue sets
        List<Set> overdueSets = setRepository.findOverdueSets(date)
                .stream()
                .filter(set -> set.getUser().getId().equals(userId))
                .toList();
        
        // Combine and prioritize (overdue first, then by word count)
        List<Set> allSets = new java.util.ArrayList<>();
        allSets.addAll(overdueSets);
        allSets.addAll(setsToReview);
        
        // Sort by priority: overdue first, then by word count (descending)
        allSets.sort((s1, s2) -> {
            boolean s1Overdue = s1.getNextCycleStartDate().isBefore(date);
            boolean s2Overdue = s2.getNextCycleStartDate().isBefore(date);
            
            if (s1Overdue && !s2Overdue) return -1;
            if (!s1Overdue && s2Overdue) return 1;
            
            // Both overdue or both not overdue, sort by word count
            return Integer.compare(s2.getWordCount(), s1.getWordCount());
        });
        
        // Limit to 3 sets per day
        return allSets.stream()
                .limit(3)
                .map(setMapper::toDto)
                .toList();
    }

    @Override
    public SetDto scheduleNextCycle(UUID setId, UUID userId, LocalDate nextCycleDate) {
        ServiceUtils.logOperationStart("next cycle scheduling", setId, userId, nextCycleDate);
        
        Set set = ServiceUtils.findEntityOrThrow(
                () -> setRepository.findByIdAndUserId(setId, userId), 
                "Set", 
                setId, 
                userId
        );
        
        set.setNextCycleStartDate(nextCycleDate);
        set.setCurrentCycle(set.getCurrentCycle() + 1);
        
        Set updatedSet = setRepository.save(set);
        ServiceUtils.logOperationSuccess("next cycle scheduling", updatedSet.getId());
        
        return setMapper.toDto(updatedSet);
    }

    @Override
    public SetStatistics getSetStatistics(UUID setId, UUID userId) {
        ServiceUtils.logEntityLookup("set statistics", setId, userId);
        
        Set set = setRepository.findByIdAndUserId(setId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Set not found with ID: " + setId + " for user ID: " + userId));
        
        // Calculate statistics
        long totalCycles = set.getCycles().size();
        long completedCycles = set.getCycles().stream()
                .filter(cycle -> cycle.getStatus().name().equals("FINISHED"))
                .count();
        
        double averageScore = set.getCycles().stream()
                .filter(cycle -> cycle.getAvgScore() != null)
                .mapToDouble(cycle -> cycle.getAvgScore().doubleValue())
                .average()
                .orElse(0.0);
        
        LocalDate lastReviewDate = set.getCycles().stream()
                .flatMap(cycle -> cycle.getReviews().stream())
                .map(review -> review.getReviewedAt())
                .max(LocalDate::compareTo)
                .orElse(null);
        
        return new SetStatistics(
                totalCycles,
                completedCycles,
                averageScore,
                lastReviewDate,
                set.getNextCycleStartDate(),
                set.getCurrentCycle(),
                set.getStatus()
        );
    }
} 
