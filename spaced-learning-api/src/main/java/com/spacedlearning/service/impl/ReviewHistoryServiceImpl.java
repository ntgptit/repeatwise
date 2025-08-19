package com.spacedlearning.service.impl;

import com.spacedlearning.dto.review.ReviewHistoryCreateRequest;
import com.spacedlearning.dto.review.ReviewHistoryResponse;
import com.spacedlearning.dto.review.ReviewHistoryUpdateRequest;
import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.ReviewHistory;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.ReviewStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.ReviewHistoryMapper;
import com.spacedlearning.repository.LearningSetRepository;
import com.spacedlearning.repository.ReviewHistoryRepository;
import com.spacedlearning.service.ReviewHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewHistoryServiceImpl implements ReviewHistoryService {

    private final ReviewHistoryRepository reviewHistoryRepository;
    private final LearningSetRepository learningSetRepository;
    private final ReviewHistoryMapper reviewHistoryMapper;

    @Override
    public ReviewHistoryResponse createReview(ReviewHistoryCreateRequest request, User user) {
        log.info("Creating review for set: {} by user: {}", request.getSetId(), user.getId());
        
        // Validate that the set belongs to the user
        LearningSet learningSet = getLearningSetByIdAndUser(request.getSetId(), user);
        
        // Check if review already exists for this set, cycle, and review number
        Optional<ReviewHistory> existingReview = reviewHistoryRepository.findBySetAndCycleNoAndReviewNo(
            learningSet, request.getCycleNo(), request.getReviewNo());
        
        if (existingReview.isPresent()) {
            throw SpacedLearningException.resourceAlreadyExists("Review", "combination", 
                String.format("set=%s,cycle=%d,review=%d", request.getSetId(), request.getCycleNo(), request.getReviewNo()));
        }
        
        // Create new review
        ReviewHistory reviewHistory = reviewHistoryMapper.toEntity(request);
        reviewHistory.setSet(learningSet);
        
        ReviewHistory savedReview = reviewHistoryRepository.save(reviewHistory);
        log.info("Created review with ID: {}", savedReview.getId());
        
        return reviewHistoryMapper.toResponse(savedReview);
    }

    @Override
    public ReviewHistoryResponse updateReview(UUID reviewId, ReviewHistoryUpdateRequest request, User user) {
        log.info("Updating review: {} by user: {}", reviewId, user.getId());
        
        ReviewHistory reviewHistory = getReviewHistoryByIdAndUser(reviewId, user);
        
        // Check if review can be edited (within 24 hours)
        if (!canEditReview(reviewId, user)) {
            throw SpacedLearningException.validationError("Review cannot be edited after 24 hours");
        }
        
        reviewHistoryMapper.updateEntityFromRequest(request, reviewHistory);
        
        ReviewHistory updatedReview = reviewHistoryRepository.save(reviewHistory);
        log.info("Updated review: {}", reviewId);
        
        return reviewHistoryMapper.toResponse(updatedReview);
    }

    @Override
    public ReviewHistoryResponse getReview(UUID reviewId, User user) {
        log.info("Getting review: {} for user: {}", reviewId, user.getId());
        
        ReviewHistory reviewHistory = getReviewHistoryByIdAndUser(reviewId, user);
        return reviewHistoryMapper.toResponse(reviewHistory);
    }

    @Override
    public Page<ReviewHistoryResponse> getReviewsBySet(UUID setId, User user, Pageable pageable) {
        log.info("Getting reviews for set: {} by user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        
        // Note: This would need a custom repository method for pagination
        // For now, we'll get all reviews and manually paginate
        List<ReviewHistory> reviews = reviewHistoryRepository.findBySetOrderByCycleNoAscReviewNoAsc(learningSet);
        
        // Simple pagination implementation
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), reviews.size());
        
        if (start > reviews.size()) {
            return Page.empty(pageable);
        }
        
        List<ReviewHistoryResponse> reviewResponses = reviewHistoryMapper.toResponseList(reviews.subList(start, end));
        
        // Create a new page with the content
        return new org.springframework.data.domain.PageImpl<>(reviewResponses, pageable, reviews.size());
    }

    @Override
    public List<ReviewHistoryResponse> getReviewsBySetAndCycle(UUID setId, Integer cycleNo, User user) {
        log.info("Getting reviews for set: {} cycle: {} by user: {}", setId, cycleNo, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        List<ReviewHistory> reviews = reviewHistoryRepository.findBySetAndCycleNoOrderByReviewNoAsc(learningSet, cycleNo);
        
        return reviewHistoryMapper.toResponseList(reviews);
    }

    @Override
    public boolean canEditReview(UUID reviewId, User user) {
        log.info("Checking if review: {} can be edited by user: {}", reviewId, user.getId());
        
        ReviewHistory reviewHistory = getReviewHistoryByIdAndUser(reviewId, user);
        
        // Check if review was created within the last 24 hours
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        return reviewHistory.getCreatedAt().isAfter(cutoffTime);
    }

    @Override
    public List<ReviewHistoryResponse> getRecentReviews(UUID setId, User user) {
        log.info("Getting recent reviews for set: {} by user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        
        List<ReviewHistory> recentReviews = reviewHistoryRepository.findRecentReviews(learningSet, cutoffTime);
        return reviewHistoryMapper.toResponseList(recentReviews);
    }

    @Override
    public Double calculateAverageScoreForCycle(UUID setId, Integer cycleNo, User user) {
        log.info("Calculating average score for set: {} cycle: {} by user: {}", setId, cycleNo, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        
        Optional<Double> avgScore = reviewHistoryRepository.calculateAverageScoreForCycle(learningSet, cycleNo);
        return avgScore.orElse(0.0);
    }

    @Override
    public List<ReviewHistoryResponse> getReviewsByDateRange(UUID setId, LocalDateTime startDate, LocalDateTime endDate, User user) {
        log.info("Getting reviews by date range for set: {} by user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        List<ReviewHistory> reviews = reviewHistoryRepository.findBySetAndDateRange(learningSet, startDate, endDate);
        
        return reviewHistoryMapper.toResponseList(reviews);
    }

    @Override
    public List<ReviewHistoryResponse> getAllUserReviews(User user) {
        log.info("Getting all reviews for user: {}", user.getId());
        
        List<ReviewHistory> reviews = reviewHistoryRepository.findByUserId(user.getId());
        return reviewHistoryMapper.toResponseList(reviews);
    }

    // Private helper methods
    private LearningSet getLearningSetByIdAndUser(UUID setId, User user) {
        return learningSetRepository.findByUserAndIdAndDeletedAtIsNull(user, setId)
            .orElseThrow(() -> SpacedLearningException.resourceNotFound("Learning Set", setId));
    }

    private ReviewHistory getReviewHistoryByIdAndUser(UUID reviewId, User user) {
        ReviewHistory reviewHistory = reviewHistoryRepository.findById(reviewId)
            .orElseThrow(() -> SpacedLearningException.resourceNotFound("Review", reviewId));
        
        // Verify that the review belongs to a set owned by the user
        if (!reviewHistory.getSet().getUser().getId().equals(user.getId())) {
            throw SpacedLearningException.forbidden("Access denied to review");
        }
        
        return reviewHistory;
    }
}
