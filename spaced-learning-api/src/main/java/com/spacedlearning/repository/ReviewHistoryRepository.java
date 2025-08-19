package com.spacedlearning.repository;

import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.ReviewHistory;
import com.spacedlearning.entity.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewHistoryRepository extends JpaRepository<ReviewHistory, UUID> {

    // Find review histories by set
    List<ReviewHistory> findBySetOrderByCycleNoAscReviewNoAsc(LearningSet set);
    
    // Find review histories by set and cycle
    List<ReviewHistory> findBySetAndCycleNoOrderByReviewNoAsc(LearningSet set, Integer cycleNo);
    
    // Find review histories by set and status
    List<ReviewHistory> findBySetAndStatus(LearningSet set, ReviewStatus status);
    
    // Find review history by set, cycle, and review number
    Optional<ReviewHistory> findBySetAndCycleNoAndReviewNo(LearningSet set, Integer cycleNo, Integer reviewNo);
    
    // Count completed reviews for a set in a specific cycle
    long countBySetAndCycleNoAndStatus(LearningSet set, Integer cycleNo, ReviewStatus status);
    
    // Find review histories created within last 24 hours (for editing)
    @Query("SELECT rh FROM ReviewHistory rh " +
           "WHERE rh.set = :set AND rh.createdAt >= :cutoffTime " +
           "ORDER BY rh.createdAt DESC")
    List<ReviewHistory> findRecentReviews(@Param("set") LearningSet set, @Param("cutoffTime") LocalDateTime cutoffTime);
    
    // Calculate average score for a set in a specific cycle
    @Query("SELECT AVG(rh.score) FROM ReviewHistory rh " +
           "WHERE rh.set = :set AND rh.cycleNo = :cycleNo AND rh.status = 'COMPLETED'")
    Optional<Double> calculateAverageScoreForCycle(@Param("set") LearningSet set, @Param("cycleNo") Integer cycleNo);
    
    // Find all review histories for a user (across all sets)
    @Query("SELECT rh FROM ReviewHistory rh " +
           "JOIN rh.set ls " +
           "WHERE ls.user.id = :userId " +
           "ORDER BY rh.createdAt DESC")
    List<ReviewHistory> findByUserId(@Param("userId") UUID userId);
    
    // Find review histories by date range
    @Query("SELECT rh FROM ReviewHistory rh " +
           "WHERE rh.set = :set AND rh.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY rh.createdAt DESC")
    List<ReviewHistory> findBySetAndDateRange(@Param("set") LearningSet set, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
}
