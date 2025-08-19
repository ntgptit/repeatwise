package com.spacedlearning.service;

import com.spacedlearning.dto.set.LearningSetCreateRequest;
import com.spacedlearning.dto.set.LearningSetDetailResponse;
import com.spacedlearning.dto.set.LearningSetResponse;
import com.spacedlearning.dto.set.LearningSetUpdateRequest;
import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.SetCategory;
import com.spacedlearning.entity.enums.SetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LearningSetService {

    // CRUD operations
    LearningSetResponse createSet(LearningSetCreateRequest request, User user);
    
    LearningSetResponse updateSet(UUID setId, LearningSetUpdateRequest request, User user);
    
    void deleteSet(UUID setId, User user);
    
    LearningSetResponse getSet(UUID setId, User user);
    
    LearningSetDetailResponse getSetDetail(UUID setId, User user);
    
    Page<LearningSetResponse> getUserSets(User user, Pageable pageable);
    
    Page<LearningSetResponse> getUserSetsByCategory(User user, SetCategory category, Pageable pageable);
    
    Page<LearningSetResponse> searchUserSets(User user, String searchTerm, Pageable pageable);
    
    // Business operations
    void startLearning(UUID setId, User user);
    
    void startReviewing(UUID setId, User user);
    
    void markAsMastered(UUID setId, User user);
    
    // SRS operations
    List<LearningSet> getSetsDueForReview(User user, LocalDate date);
    
    List<LearningSet> getOverdueSets(User user, LocalDate date);
    
    void scheduleNextCycle(UUID setId, User user);
    
    void handleOverload(User user, LocalDate date);
    
    // Statistics
    long countSetsByStatus(User user, SetStatus status);
    
    List<LearningSet> getSetsWithCompletedCycles(User user);
}
