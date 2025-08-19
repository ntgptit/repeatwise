package com.spacedlearning.service;

import com.spacedlearning.dto.review.ReviewHistoryCreateRequest;
import com.spacedlearning.dto.review.ReviewHistoryResponse;
import com.spacedlearning.dto.review.ReviewHistoryUpdateRequest;
import com.spacedlearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReviewHistoryService {

    // CRUD operations
    ReviewHistoryResponse createReview(ReviewHistoryCreateRequest request, User user);
    
    ReviewHistoryResponse updateReview(UUID reviewId, ReviewHistoryUpdateRequest request, User user);
    
    ReviewHistoryResponse getReview(UUID reviewId, User user);
    
    Page<ReviewHistoryResponse> getReviewsBySet(UUID setId, User user, Pageable pageable);
    
    List<ReviewHistoryResponse> getReviewsBySetAndCycle(UUID setId, Integer cycleNo, User user);
    
    // Business operations
    boolean canEditReview(UUID reviewId, User user);
    
    List<ReviewHistoryResponse> getRecentReviews(UUID setId, User user);
    
    Double calculateAverageScoreForCycle(UUID setId, Integer cycleNo, User user);
    
    // Statistics
    List<ReviewHistoryResponse> getReviewsByDateRange(UUID setId, LocalDateTime startDate, LocalDateTime endDate, User user);
    
    List<ReviewHistoryResponse> getAllUserReviews(User user);
}
