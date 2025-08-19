package com.spacedlearning.service.impl;

import com.spacedlearning.dto.set.LearningSetCreateRequest;
import com.spacedlearning.dto.set.LearningSetDetailResponse;
import com.spacedlearning.dto.set.LearningSetResponse;
import com.spacedlearning.dto.set.LearningSetUpdateRequest;
import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.RemindSchedule;
import com.spacedlearning.entity.SRSConfiguration;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.RemindStatus;
import com.spacedlearning.entity.enums.SetCategory;
import com.spacedlearning.entity.enums.SetStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.LearningSetMapper;
import com.spacedlearning.repository.LearningSetRepository;
import com.spacedlearning.repository.RemindScheduleRepository;
import com.spacedlearning.repository.ReviewHistoryRepository;
import com.spacedlearning.repository.SRSConfigurationRepository;
import com.spacedlearning.service.LearningSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LearningSetServiceImpl implements LearningSetService {

    private final LearningSetRepository learningSetRepository;
    private final RemindScheduleRepository remindScheduleRepository;
    private final ReviewHistoryRepository reviewHistoryRepository;
    private final SRSConfigurationRepository srsConfigurationRepository;
    private final LearningSetMapper learningSetMapper;

    @Override
    public LearningSetResponse createSet(LearningSetCreateRequest request, User user) {
        log.info("Creating new learning set for user: {}", user.getId());
        
        LearningSet learningSet = learningSetMapper.toEntity(request);
        learningSet.setUser(user);
        
        LearningSet savedSet = learningSetRepository.save(learningSet);
        log.info("Created learning set with ID: {}", savedSet.getId());
        
        return learningSetMapper.toResponse(savedSet);
    }

    @Override
    public LearningSetResponse updateSet(UUID setId, LearningSetUpdateRequest request, User user) {
        log.info("Updating learning set: {} for user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        learningSetMapper.updateEntityFromRequest(request, learningSet);
        
        LearningSet updatedSet = learningSetRepository.save(learningSet);
        log.info("Updated learning set: {}", setId);
        
        return learningSetMapper.toResponse(updatedSet);
    }

    @Override
    public void deleteSet(UUID setId, User user) {
        log.info("Deleting learning set: {} for user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        learningSet.setDeletedAt(LocalDateTime.now());
        
        learningSetRepository.save(learningSet);
        log.info("Deleted learning set: {}", setId);
    }

    @Override
    public LearningSetResponse getSet(UUID setId, User user) {
        log.info("Getting learning set: {} for user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        return learningSetMapper.toResponse(learningSet);
    }

    @Override
    public LearningSetDetailResponse getSetDetail(UUID setId, User user) {
        log.info("Getting detailed learning set: {} for user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        
        // Calculate additional details
        LearningSetDetailResponse response = learningSetMapper.toDetailResponse(learningSet);
        
        // Calculate current cycle average score
        Optional<Double> avgScore = reviewHistoryRepository.calculateAverageScoreForCycle(
            learningSet, learningSet.getCurrentCycle());
        response.setCurrentCycleAverageScore(avgScore.orElse(0.0));
        
        // Count completed reviews in current cycle
        long completedReviews = reviewHistoryRepository.countBySetAndCycleNoAndStatus(
            learningSet, learningSet.getCurrentCycle(), com.spacedlearning.entity.enums.ReviewStatus.COMPLETED);
        response.setCompletedReviewsInCurrentCycle((int) completedReviews);
        
        // Get next review date
        List<RemindSchedule> activeReminders = remindScheduleRepository.findActiveRemindersForSet(learningSet);
        if (!activeReminders.isEmpty()) {
            response.setNextReviewDate(activeReminders.get(0).getRemindDate().atStartOfDay());
        }
        
        return response;
    }

    @Override
    public Page<LearningSetResponse> getUserSets(User user, Pageable pageable) {
        log.info("Getting learning sets for user: {}", user.getId());
        
        Page<LearningSet> sets = learningSetRepository.findByUserAndDeletedAtIsNull(user, pageable);
        return sets.map(learningSetMapper::toResponse);
    }

    @Override
    public Page<LearningSetResponse> getUserSetsByCategory(User user, SetCategory category, Pageable pageable) {
        log.info("Getting learning sets by category: {} for user: {}", category, user.getId());
        
        Page<LearningSet> sets = learningSetRepository.findByUserAndCategoryAndDeletedAtIsNull(user, category, pageable);
        return sets.map(learningSetMapper::toResponse);
    }

    @Override
    public Page<LearningSetResponse> searchUserSets(User user, String searchTerm, Pageable pageable) {
        log.info("Searching learning sets with term: '{}' for user: {}", searchTerm, user.getId());
        
        if (StringUtils.isBlank(searchTerm)) {
            return getUserSets(user, pageable);
        }
        
        Page<LearningSet> sets = learningSetRepository.searchByUserAndName(user, searchTerm.trim(), pageable);
        return sets.map(learningSetMapper::toResponse);
    }

    @Override
    public void startLearning(UUID setId, User user) {
        log.info("Starting learning for set: {} by user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        learningSet.startLearning();
        
        learningSetRepository.save(learningSet);
        log.info("Started learning for set: {}", setId);
    }

    @Override
    public void startReviewing(UUID setId, User user) {
        log.info("Starting reviewing for set: {} by user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        learningSet.startReviewing();
        
        learningSetRepository.save(learningSet);
        log.info("Started reviewing for set: {}", setId);
    }

    @Override
    public void markAsMastered(UUID setId, User user) {
        log.info("Marking set as mastered: {} by user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        learningSet.markAsMastered();
        
        learningSetRepository.save(learningSet);
        log.info("Marked set as mastered: {}", setId);
    }

    @Override
    public List<LearningSet> getSetsDueForReview(User user, LocalDate date) {
        log.info("Getting sets due for review on: {} for user: {}", date, user.getId());
        
        return learningSetRepository.findSetsDueForReview(user, date);
    }

    @Override
    public List<LearningSet> getOverdueSets(User user, LocalDate date) {
        log.info("Getting overdue sets for user: {} as of: {}", user.getId(), date);
        
        return learningSetRepository.findOverdueSets(user, date);
    }

    @Override
    public void scheduleNextCycle(UUID setId, User user) {
        log.info("Scheduling next cycle for set: {} by user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        
        // Calculate average score for current cycle
        Optional<Double> avgScoreOpt = reviewHistoryRepository.calculateAverageScoreForCycle(
            learningSet, learningSet.getCurrentCycle());
        
        if (avgScoreOpt.isEmpty()) {
            log.warn("No review history found for set: {} cycle: {}", setId, learningSet.getCurrentCycle());
            return;
        }
        
        double avgScore = avgScoreOpt.get();
        
        // Get SRS configuration
        SRSConfiguration srsConfig = srsConfigurationRepository.findByIsActiveTrue()
            .orElseThrow(() -> SpacedLearningException.resourceNotFound("SRS Configuration", "active"));
        
        // Calculate next cycle delay
        int nextCycleDelay = srsConfig.calculateNextCycleDelay(avgScore, learningSet.getWordCount());
        
        // Increment cycle
        learningSet.incrementCycle();
        learningSetRepository.save(learningSet);
        
        // Schedule next reminder
        LocalDate nextReviewDate = LocalDate.now().plusDays(nextCycleDelay);
        scheduleReminder(learningSet, user, nextReviewDate);
        
        log.info("Scheduled next cycle for set: {} with delay: {} days", setId, nextCycleDelay);
    }

    @Override
    public void handleOverload(User user, LocalDate date) {
        log.info("Handling overload for user: {} on date: {}", user.getId(), date);
        
        SRSConfiguration srsConfig = srsConfigurationRepository.findByIsActiveTrue()
            .orElseThrow(() -> SpacedLearningException.resourceNotFound("SRS Configuration", "active"));
        
        int maxSetsPerDay = srsConfig.getMaxSetsPerDay();
        
        // Get all sets due for review on the given date
        List<LearningSet> setsDueForReview = getSetsDueForReview(user, date);
        
        if (setsDueForReview.size() <= maxSetsPerDay) {
            log.info("No overload detected for user: {} on date: {}", user.getId(), date);
            return;
        }
        
        // Keep only the first maxSetsPerDay sets (already ordered by priority)
        List<LearningSet> setsToKeep = setsDueForReview.subList(0, maxSetsPerDay);
        List<LearningSet> setsToReschedule = setsDueForReview.subList(maxSetsPerDay, setsDueForReview.size());
        
        // Reschedule the remaining sets
        for (LearningSet set : setsToReschedule) {
            rescheduleSet(set, user, date.plusDays(1));
        }
        
        log.info("Handled overload for user: {} - kept: {} sets, rescheduled: {} sets", 
            user.getId(), setsToKeep.size(), setsToReschedule.size());
    }

    @Override
    public long countSetsByStatus(User user, SetStatus status) {
        return learningSetRepository.countByUserAndStatusAndDeletedAtIsNull(user, status);
    }

    @Override
    public List<LearningSet> getSetsWithCompletedCycles(User user) {
        log.info("Getting sets with completed cycles for user: {}", user.getId());
        
        return learningSetRepository.findSetsWithCompletedCycles(user);
    }

    // Private helper methods
    private LearningSet getLearningSetByIdAndUser(UUID setId, User user) {
        return learningSetRepository.findByUserAndIdAndDeletedAtIsNull(user, setId)
            .orElseThrow(() -> SpacedLearningException.resourceNotFound("Learning Set", setId));
    }

    private void scheduleReminder(LearningSet learningSet, User user, LocalDate remindDate) {
        // Cancel existing active reminders for this set
        List<RemindSchedule> activeReminders = remindScheduleRepository.findActiveRemindersForSet(learningSet);
        for (RemindSchedule reminder : activeReminders) {
            reminder.markAsCancelled();
            remindScheduleRepository.save(reminder);
        }
        
        // Create new reminder
        RemindSchedule newReminder = RemindSchedule.builder()
            .set(learningSet)
            .user(user)
            .remindDate(remindDate)
            .status(RemindStatus.PENDING)
            .rescheduleCount(0)
            .build();
        
        remindScheduleRepository.save(newReminder);
        log.info("Scheduled reminder for set: {} on date: {}", learningSet.getId(), remindDate);
    }

    private void rescheduleSet(LearningSet learningSet, User user, LocalDate newDate) {
        // Find the reminder for this set on the original date
        Optional<RemindSchedule> reminderOpt = remindScheduleRepository.findBySetAndRemindDate(learningSet, newDate.minusDays(1));
        
        if (reminderOpt.isPresent()) {
            RemindSchedule reminder = reminderOpt.get();
            if (reminder.canReschedule()) {
                reminder.reschedule(newDate);
                remindScheduleRepository.save(reminder);
                log.info("Rescheduled set: {} to date: {}", learningSet.getId(), newDate);
            } else {
                log.warn("Cannot reschedule set: {} - max reschedule count reached", learningSet.getId());
            }
        }
    }
}
