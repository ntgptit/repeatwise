package com.spacedlearning.repository;

import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.SetCategory;
import com.spacedlearning.entity.enums.SetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningSetRepository extends JpaRepository<LearningSet, UUID> {

    // Find sets by user
    Page<LearningSet> findByUserAndDeletedAtIsNull(User user, Pageable pageable);
    
    List<LearningSet> findByUserAndDeletedAtIsNull(User user);
    
    // Find sets by user and status
    List<LearningSet> findByUserAndStatusAndDeletedAtIsNull(User user, SetStatus status);
    
    // Find sets by user and category
    Page<LearningSet> findByUserAndCategoryAndDeletedAtIsNull(User user, SetCategory category, Pageable pageable);
    
    // Find sets that need review on a specific date
    @Query("SELECT ls FROM LearningSet ls " +
           "JOIN RemindSchedule rs ON ls.id = rs.set.id " +
           "WHERE rs.user = :user AND rs.remindDate = :date " +
           "AND rs.status = 'PENDING' AND ls.deletedAt IS NULL " +
           "ORDER BY rs.remindDate ASC, ls.currentCycle ASC, ls.wordCount ASC")
    List<LearningSet> findSetsDueForReview(@Param("user") User user, @Param("date") LocalDate date);
    
    // Find sets that are overdue for review
    @Query("SELECT ls FROM LearningSet ls " +
           "JOIN RemindSchedule rs ON ls.id = rs.set.id " +
           "WHERE rs.user = :user AND rs.remindDate < :date " +
           "AND rs.status = 'PENDING' AND ls.deletedAt IS NULL " +
           "ORDER BY rs.remindDate ASC, ls.currentCycle ASC, ls.wordCount ASC")
    List<LearningSet> findOverdueSets(@Param("user") User user, @Param("date") LocalDate date);
    
    // Count sets by user and status
    long countByUserAndStatusAndDeletedAtIsNull(User user, SetStatus status);
    
    // Find set by user and ID
    Optional<LearningSet> findByUserAndIdAndDeletedAtIsNull(User user, UUID id);
    
    // Search sets by name (case insensitive)
    @Query("SELECT ls FROM LearningSet ls " +
           "WHERE ls.user = :user AND LOWER(ls.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND ls.deletedAt IS NULL")
    Page<LearningSet> searchByUserAndName(@Param("user") User user, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find sets that have completed a cycle and need next cycle scheduling
    @Query("SELECT ls FROM LearningSet ls " +
           "WHERE ls.user = :user AND ls.status IN ('LEARNING', 'REVIEWING') " +
           "AND ls.deletedAt IS NULL " +
           "AND (SELECT COUNT(rh) FROM ReviewHistory rh WHERE rh.set = ls AND rh.cycleNo = ls.currentCycle) = 5")
    List<LearningSet> findSetsWithCompletedCycles(@Param("user") User user);
}
