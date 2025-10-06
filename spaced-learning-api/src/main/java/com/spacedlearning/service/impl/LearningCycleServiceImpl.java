package com.spacedlearning.service.impl;

import com.spacedlearning.dto.learning.PerformReviewRequest;
import com.spacedlearning.dto.learning.PerformReviewResponse;
import com.spacedlearning.dto.learning.StartLearningCycleRequest;
import com.spacedlearning.dto.learning.StartLearningCycleResponse;
import com.spacedlearning.entity.LearningCycle;
import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.ReminderSchedule;
import com.spacedlearning.entity.ReviewHistory;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.ReminderStatus;
import com.spacedlearning.entity.enums.ReviewStatus;
import com.spacedlearning.entity.enums.SetStatus;
import com.spacedlearning.exception.BusinessException;
import com.spacedlearning.exception.ForbiddenException;
import com.spacedlearning.exception.ResourceNotFoundException;
import com.spacedlearning.repository.LearningSetRepository;
import com.spacedlearning.repository.ReminderScheduleRepository;
import com.spacedlearning.repository.ReviewHistoryRepository;
import com.spacedlearning.service.LearningCycleService;
import com.spacedlearning.service.SRSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Implementation of LearningCycleService
 * Implements business logic for UC-010 and UC-011
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LearningCycleServiceImpl implements LearningCycleService {

    private final LearningSetRepository learningSetRepository;
    private final ReminderScheduleRepository reminderScheduleRepository;
    private final ReviewHistoryRepository reviewHistoryRepository;
    private final SRSService srsService;

    private static final int MAX_ACTIVE_SETS = 10;
    private static final int REVIEWS_PER_CYCLE = 5;

    @Override
    public StartLearningCycleResponse startLearningCycle(StartLearningCycleRequest request, UUID userId) {
        log.info("Starting learning cycle for set {} by user {}", request.getSetId(), userId);

        // Validate set exists and belongs to user
        LearningSet learningSet = validateSetOwnership(request.getSetId(), userId);

        // Check if set can start learning cycle
        validateCanStartLearningCycle(learningSet);

        // Check overload prevention
        validateOverloadPrevention(userId);

        // Create new learning cycle
        LearningCycle newCycle = createNewLearningCycle(learningSet);

        // Update set status
        learningSet.setStatus(SetStatus.LEARNING);
        learningSet.setCurrentCycle(newCycle.getCycleNumber());
        learningSet.setUpdatedAt(LocalDateTime.now());

        // Create reminder schedules
        List<ReminderSchedule> reminderSchedules = createReminderSchedules(learningSet, newCycle);

        // Save entities
        learningSetRepository.save(learningSet);
        reminderScheduleRepository.saveAll(reminderSchedules);

        log.info("Successfully started learning cycle {} for set {}", newCycle.getCycleNumber(), learningSet.getId());

        return buildStartLearningCycleResponse(learningSet, newCycle, reminderSchedules);
    }

    @Override
    public PerformReviewResponse performReview(PerformReviewRequest request, UUID userId) {
        log.info("Performing review for set {} cycle {} review {} by user {}", 
                request.getSetId(), request.getCycleNumber(), request.getReviewNumber(), userId);

        // Validate set exists and belongs to user
        LearningSet learningSet = validateSetOwnership(request.getSetId(), userId);

        // Validate review request
        validateReviewRequest(request, learningSet);

        // Create review history
        ReviewHistory reviewHistory = createReviewHistory(request, learningSet);

        // Update reminder schedule status
        updateReminderScheduleStatus(learningSet, request.getCycleNumber(), request.getReviewNumber());

        // Check if cycle is completed
        boolean cycleCompleted = checkCycleCompletion(learningSet, request.getCycleNumber());

        // Check if set is mastered
        boolean setMastered = false;
        if (cycleCompleted) {
            setMastered = checkSetMastered(learningSet);
            if (setMastered) {
                learningSet.setStatus(SetStatus.MASTERED);
            }
        }

        // Update set statistics
        updateSetStatistics(learningSet);

        // Save entities
        reviewHistoryRepository.save(reviewHistory);
        learningSetRepository.save(learningSet);

        log.info("Successfully performed review for set {} cycle {} review {}", 
                learningSet.getId(), request.getCycleNumber(), request.getReviewNumber());

        return buildPerformReviewResponse(reviewHistory, learningSet, cycleCompleted, setMastered);
    }

    @Override
    public boolean canStartLearningCycle(UUID setId, UUID userId) {
        try {
            LearningSet learningSet = validateSetOwnership(setId, userId);
            return learningSet.isNotStarted() || 
                   (learningSet.getStatus() == SetStatus.REVIEWING && isCycleCompleted(learningSet));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canPerformReview(UUID setId, UUID userId) {
        try {
            LearningSet learningSet = validateSetOwnership(setId, userId);
            return learningSet.isLearning() || learningSet.isReviewing();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public StartLearningCycleResponse getCurrentCycleInfo(UUID setId, UUID userId) {
        LearningSet learningSet = validateSetOwnership(setId, userId);
        
        List<ReminderSchedule> reminderSchedules = reminderScheduleRepository
                .findByLearningSetAndCycleNumber(learningSet, learningSet.getCurrentCycle());

        return buildStartLearningCycleResponse(learningSet, null, reminderSchedules);
    }

    // Private helper methods

    private LearningSet validateSetOwnership(UUID setId, UUID userId) {
        LearningSet learningSet = learningSetRepository.findById(setId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning set not found with id: " + setId));

        if (!learningSet.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to access this learning set");
        }

        return learningSet;
    }

    private void validateCanStartLearningCycle(LearningSet learningSet) {
        if (learningSet.isLearning()) {
            throw new BusinessException("Set is already in learning phase");
        }

        if (learningSet.isMastered()) {
            throw new BusinessException("Set is already mastered");
        }
    }

    private void validateOverloadPrevention(UUID userId) {
        long activeSetsCount = learningSetRepository.countByUserIdAndStatusIn(
                userId, List.of(SetStatus.LEARNING, SetStatus.REVIEWING));

        if (activeSetsCount >= MAX_ACTIVE_SETS) {
            throw new BusinessException("You have too many active sets. Please complete some sets before starting new ones.");
        }
    }

    private void validateReviewRequest(PerformReviewRequest request, LearningSet learningSet) {
        if (!learningSet.isLearning() && !learningSet.isReviewing()) {
            throw new BusinessException("Set is not in learning or reviewing phase");
        }

        if (!request.getCycleNumber().equals(learningSet.getCurrentCycle())) {
            throw new BusinessException("Invalid cycle number");
        }

        if (request.getReviewNumber() < 1 || request.getReviewNumber() > REVIEWS_PER_CYCLE) {
            throw new BusinessException("Invalid review number");
        }

        // Check if review already exists
        boolean reviewExists = reviewHistoryRepository.existsByLearningSetAndCycleNumberAndReviewNumber(
                learningSet, request.getCycleNumber(), request.getReviewNumber());

        if (reviewExists) {
            throw new BusinessException("Review already completed for this cycle and review number");
        }
    }

    private LearningCycle createNewLearningCycle(LearningSet learningSet) {
        return LearningCycle.builder()
                .learningSet(learningSet)
                .cycleNumber(learningSet.getCurrentCycle())
                .startDate(LocalDate.now())
                .build();
    }

    private List<ReminderSchedule> createReminderSchedules(LearningSet learningSet, LearningCycle cycle) {
        return IntStream.rangeClosed(1, REVIEWS_PER_CYCLE)
                .mapToObj(reviewNumber -> {
                    LocalDateTime scheduledTime = srsService.calculateNextReviewTime(
                            learningSet, cycle.getCycleNumber(), reviewNumber);
                    
                    return ReminderSchedule.builder()
                            .learningSet(learningSet)
                            .user(learningSet.getUser())
                            .cycleNumber(cycle.getCycleNumber())
                            .reviewNumber(reviewNumber)
                            .scheduledTime(scheduledTime)
                            .status(ReminderStatus.PENDING)
                            .build();
                })
                .toList();
    }

    private ReviewHistory createReviewHistory(PerformReviewRequest request, LearningSet learningSet) {
        ReviewHistory.ReviewHistoryBuilder builder = ReviewHistory.builder()
                .learningSet(learningSet)
                .cycleNumber(request.getCycleNumber())
                .reviewNumber(request.getReviewNumber())
                .reviewDate(LocalDate.now());

        if (request.isSkipRequest()) {
            builder.status(ReviewStatus.SKIPPED)
                   .skipReason(request.getSkipReason());
        } else {
            builder.status(ReviewStatus.COMPLETED)
                   .score(request.getScore())
                   .notes(request.getNotes());
        }

        return builder.build();
    }

    private void updateReminderScheduleStatus(LearningSet learningSet, Integer cycleNumber, Integer reviewNumber) {
        ReminderSchedule reminderSchedule = reminderScheduleRepository
                .findByLearningSetAndCycleNumberAndReviewNumber(learningSet, cycleNumber, reviewNumber)
                .orElse(null);

        if (reminderSchedule != null) {
            reminderSchedule.setStatus(ReminderStatus.COMPLETED);
            reminderSchedule.setUpdatedAt(LocalDateTime.now());
            reminderScheduleRepository.save(reminderSchedule);
        }
    }

    private boolean checkCycleCompletion(LearningSet learningSet, Integer cycleNumber) {
        long completedReviews = reviewHistoryRepository.countByLearningSetAndCycleNumberAndStatus(
                learningSet, cycleNumber, ReviewStatus.COMPLETED);
        
        long skippedReviews = reviewHistoryRepository.countByLearningSetAndCycleNumberAndStatus(
                learningSet, cycleNumber, ReviewStatus.SKIPPED);

        return (completedReviews + skippedReviews) >= REVIEWS_PER_CYCLE;
    }

    private boolean checkSetMastered(LearningSet learningSet) {
        // BR-033: Set mastered conditions
        // - avg_score ≥ 85% trong 3 chu kỳ liên tiếp
        // - Không có skip trong 3 chu kỳ cuối
        // - Tổng thời gian học ≥ 30 ngày
        
        // This is a simplified implementation
        // In real implementation, you would check these conditions more thoroughly
        return learningSet.getAverageScore() != null && 
               learningSet.getAverageScore().doubleValue() >= 85.0 &&
               learningSet.getTotalReviews() >= 15; // 3 cycles * 5 reviews
    }

    private void updateSetStatistics(LearningSet learningSet) {
        learningSet.setTotalReviews(learningSet.getTotalReviews() + 1);
        learningSet.setLastReviewedAt(LocalDateTime.now());
        learningSet.updateAverageScore();
    }

    private boolean isCycleCompleted(LearningSet learningSet) {
        return checkCycleCompletion(learningSet, learningSet.getCurrentCycle());
    }

    private StartLearningCycleResponse buildStartLearningCycleResponse(
            LearningSet learningSet, LearningCycle cycle, List<ReminderSchedule> reminderSchedules) {
        
        List<StartLearningCycleResponse.ReminderScheduleInfo> reminderInfos = reminderSchedules.stream()
                .map(rs -> StartLearningCycleResponse.ReminderScheduleInfo.builder()
                        .reviewNumber(rs.getReviewNumber())
                        .scheduledTime(rs.getScheduledTime())
                        .status(rs.getStatus().name())
                        .build())
                .toList();

        return StartLearningCycleResponse.builder()
                .setId(learningSet.getId())
                .setName(learningSet.getName())
                .currentCycle(learningSet.getCurrentCycle())
                .status(learningSet.getStatus())
                .cycleStartTime(cycle != null ? cycle.getStartDate().atStartOfDay() : null)
                .reminderSchedules(reminderInfos)
                .message("Learning cycle started successfully")
                .build();
    }

    private PerformReviewResponse buildPerformReviewResponse(
            ReviewHistory reviewHistory, LearningSet learningSet, boolean cycleCompleted, boolean setMastered) {
        
        return PerformReviewResponse.builder()
                .reviewId(reviewHistory.getId())
                .setId(learningSet.getId())
                .setName(learningSet.getName())
                .cycleNumber(reviewHistory.getCycleNumber())
                .reviewNumber(reviewHistory.getReviewNumber())
                .score(reviewHistory.getScore())
                .status(reviewHistory.getStatus().name())
                .skipReason(reviewHistory.getSkipReason() != null ? reviewHistory.getSkipReason().name() : null)
                .notes(reviewHistory.getNotes())
                .reviewDate(reviewHistory.getReviewDate().atStartOfDay())
                .setStatus(learningSet.getStatus())
                .totalReviews(learningSet.getTotalReviews())
                .averageScore(learningSet.getAverageScore() != null ? learningSet.getAverageScore().doubleValue() : null)
                .cycleCompleted(cycleCompleted)
                .setMastered(setMastered)
                .message(buildSuccessMessage(setMastered, cycleCompleted))
                .build();
    }

    private String buildSuccessMessage(boolean setMastered, boolean cycleCompleted) {
        if (setMastered) {
            return "Congratulations! Set mastered!";
        } else if (cycleCompleted) {
            return "Cycle completed!";
        } else {
            return "Review completed!";
        }
    }
}
