package com.repeatwise.service.impl;

import com.repeatwise.dto.SetReviewDto;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.mapper.SetReviewMapper;
import com.repeatwise.model.SetCycle;
import com.repeatwise.model.SetReview;
import com.repeatwise.repository.SetCycleRepository;
import com.repeatwise.repository.SetReviewRepository;
import com.repeatwise.service.SetReviewService;
import com.repeatwise.service.SetCycleService;
import com.repeatwise.service.SetService;
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
public class SetReviewServiceImpl implements SetReviewService {

    private final SetReviewRepository setReviewRepository;
    private final SetCycleRepository setCycleRepository;
    private final SetReviewMapper setReviewMapper;
    private final SetCycleService setCycleService;
    private final SetService setService;

    @Override
    public SetReviewDto createReview(UUID cycleId, UUID userId, SetReviewDto reviewDto) {
        ServiceUtils.logOperationStart("review creation", cycleId, userId);
        
        SetCycle cycle = ServiceUtils.findEntityOrThrow(
                () -> setCycleRepository.findById(cycleId), 
                "Cycle", 
                cycleId
        );
        
        // Verify user owns this cycle
        if (!cycle.getSet().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Cycle not found with ID: " + cycleId + " for user ID: " + userId);
        }
        
        // Validate score range (0-100)
        if (reviewDto.getScore() < 0 || reviewDto.getScore() > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        
        // Get next review number
        Integer nextReviewNo = setReviewRepository.findNextReviewNumber(cycleId);
        
        SetReview review = SetReview.builder()
                .setCycle(cycle)
                .reviewNo(nextReviewNo)
                .reviewedAt(LocalDate.now())
                .score(reviewDto.getScore())
                .build();
        
        SetReview savedReview = setReviewRepository.save(review);
        ServiceUtils.logOperationSuccess("review creation", savedReview.getId());
        
        // Check if cycle is complete (5 reviews)
        long reviewCount = setReviewRepository.countBySetCycleId(cycleId);
        if (reviewCount >= 5) {
            log.info("Cycle {} has 5 reviews, automatically finishing cycle", cycleId);
            try {
                setCycleService.finishCycle(cycleId, userId);
                
                // Automatically schedule next cycle
                UUID setId = cycle.getSet().getId();
                int nextCycleDelay = setCycleService.calculateNextCycleDelay(cycleId);
                LocalDate nextCycleDate = LocalDate.now().plusDays(nextCycleDelay);
                setService.scheduleNextCycle(setId, userId, nextCycleDate);
                
                log.info("Next cycle scheduled for set {} on date {}", setId, nextCycleDate);
            } catch (Exception e) {
                log.warn("Failed to automatically finish cycle {}: {}", cycleId, e.getMessage());
            }
        }
        
        return setReviewMapper.toDto(savedReview);
    }

    @Override
    public Optional<SetReviewDto> findById(UUID id) {
        ServiceUtils.logEntityLookup("review", id);
        return setReviewRepository.findById(id)
                .map(setReviewMapper::toDto);
    }

    @Override
    public Optional<SetReviewDto> findByIdAndCycleId(UUID id, UUID cycleId) {
        ServiceUtils.logEntityLookup("review", id, cycleId);
        return setReviewRepository.findByIdAndSetCycleId(id, cycleId)
                .map(setReviewMapper::toDto);
    }

    @Override
    public SetReviewDto updateReview(UUID id, UUID userId, SetReviewDto reviewDto) {
        ServiceUtils.logOperationStart("review update", id, userId);
        
        SetReview review = ServiceUtils.findEntityOrThrow(
                () -> setReviewRepository.findById(id), 
                "Review", 
                id
        );
        
        // Verify user owns this review
        if (!review.getSetCycle().getSet().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Review not found with ID: " + id + " for user ID: " + userId);
        }
        
        // Validate score range (0-100)
        if (reviewDto.getScore() < 0 || reviewDto.getScore() > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        
        // Update fields
        review.setScore(reviewDto.getScore());
        review.setReviewedAt(LocalDate.now());
        
        SetReview updatedReview = setReviewRepository.save(review);
        ServiceUtils.logOperationSuccess("review update", updatedReview.getId());
        
        return setReviewMapper.toDto(updatedReview);
    }

    @Override
    public void deleteReview(UUID id, UUID userId) {
        ServiceUtils.logOperationStart("review deletion", id, userId);
        
        SetReview review = ServiceUtils.findEntityOrThrow(
                () -> setReviewRepository.findById(id), 
                "Review", 
                id
        );
        
        // Verify user owns this review
        if (!review.getSetCycle().getSet().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Review not found with ID: " + id + " for user ID: " + userId);
        }
        
        setReviewRepository.delete(review);
        ServiceUtils.logOperationSuccess("review deletion", id);
    }

    @Override
    public List<SetReviewDto> findByCycleId(UUID cycleId) {
        ServiceUtils.logEntityLookup("reviews for cycle", cycleId);
        return setReviewRepository.findBySetCycleId(cycleId)
                .stream()
                .map(setReviewMapper::toDto)
                .toList();
    }

    @Override
    public List<SetReviewDto> findReviewsByCycleIdOrderByReviewNo(UUID cycleId) {
        ServiceUtils.logEntityLookup("reviews for cycle ordered by review number", cycleId);
        return setReviewRepository.findReviewsBySetCycleIdOrderByReviewNo(cycleId)
                .stream()
                .map(setReviewMapper::toDto)
                .toList();
    }

    @Override
    public Optional<SetReviewDto> findByReviewNoAndCycleId(Integer reviewNo, UUID cycleId) {
        ServiceUtils.logEntityLookup("review by review number", reviewNo, cycleId);
        return setReviewRepository.findByReviewNoAndSetCycleId(reviewNo, cycleId)
                .map(setReviewMapper::toDto);
    }

    @Override
    public Integer getNextReviewNumber(UUID cycleId) {
        ServiceUtils.logEntityLookup("next review number", cycleId);
        return setReviewRepository.findNextReviewNumber(cycleId);
    }

    @Override
    public long countByCycleId(UUID cycleId) {
        ServiceUtils.logEntityLookup("review count for cycle", cycleId);
        return setReviewRepository.countBySetCycleId(cycleId);
    }

    @Override
    public List<SetReviewDto> findByScoreRange(UUID cycleId, Integer minScore, Integer maxScore) {
        ServiceUtils.logEntityLookup("reviews with score range", cycleId, minScore, maxScore);
        return setReviewRepository.findReviewsByScoreRange(minScore, maxScore)
                .stream()
                .filter(review -> review.getSetCycle().getId().equals(cycleId))
                .map(setReviewMapper::toDto)
                .toList();
    }

    @Override
    public List<SetReviewDto> findWithMinScore(UUID cycleId, Integer minScore) {
        ServiceUtils.logEntityLookup("reviews with minimum score", cycleId, minScore);
        return setReviewRepository.findReviewsWithMinScore(minScore)
                .stream()
                .filter(review -> review.getSetCycle().getId().equals(cycleId))
                .map(setReviewMapper::toDto)
                .toList();
    }

    @Override
    public List<SetReviewDto> findWithMaxScore(UUID cycleId, Integer maxScore) {
        ServiceUtils.logEntityLookup("reviews with maximum score", cycleId, maxScore);
        return setReviewRepository.findReviewsWithMaxScore(maxScore)
                .stream()
                .filter(review -> review.getSetCycle().getId().equals(cycleId))
                .map(setReviewMapper::toDto)
                .toList();
    }

    @Override
    public List<SetReviewDto> findReviewsReviewedOnDate(LocalDate date) {
        ServiceUtils.logEntityLookup("reviews reviewed on date", date);
        return setReviewRepository.findReviewsReviewedOnDate(date)
                .stream()
                .map(setReviewMapper::toDto)
                .toList();
    }

    @Override
    public List<SetReviewDto> findReviewsReviewedBetweenDates(LocalDate startDate, LocalDate endDate) {
        ServiceUtils.logEntityLookup("reviews reviewed between dates", startDate, endDate);
        return setReviewRepository.findReviewsReviewedBetweenDates(startDate, endDate)
                .stream()
                .map(setReviewMapper::toDto)
                .toList();
    }

    @Override
    public BigDecimal calculateAverageScore(UUID cycleId) {
        ServiceUtils.logEntityLookup("average score calculation", cycleId);
        return setReviewRepository.findAverageScoreBySetCycleId(cycleId);
    }

    @Override
    public Integer findHighestScore(UUID cycleId) {
        ServiceUtils.logEntityLookup("highest score", cycleId);
        return setReviewRepository.findHighestScoreBySetCycleId(cycleId);
    }

    @Override
    public Integer findLowestScore(UUID cycleId) {
        ServiceUtils.logEntityLookup("lowest score", cycleId);
        return setReviewRepository.findLowestScoreBySetCycleId(cycleId);
    }

    @Override
    public List<SetReviewDto> findByUserId(UUID userId) {
        ServiceUtils.logEntityLookup("reviews for user", userId);
        return setReviewRepository.findReviewsByUserId(userId)
                .stream()
                .map(setReviewMapper::toDto)
                .toList();
    }

    @Override
    public List<SetReviewDto> findByUserIdAndDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        ServiceUtils.logEntityLookup("reviews for user between dates", userId, startDate, endDate);
        return setReviewRepository.findReviewsByUserIdAndDateRange(userId, startDate, endDate)
                .stream()
                .map(setReviewMapper::toDto)
                .toList();
    }

    @Override
    public List<SetReviewDto> findBySetId(UUID setId) {
        ServiceUtils.logEntityLookup("reviews for set", setId);
        return setReviewRepository.findReviewsBySetId(setId)
                .stream()
                .map(setReviewMapper::toDto)
                .toList();
    }

    @Override
    public BigDecimal calculateAverageScoreBySetId(UUID setId) {
        ServiceUtils.logEntityLookup("average score calculation for set", setId);
        return setReviewRepository.findAverageScoreBySetId(setId);
    }

    @Override
    public Optional<SetReviewDto> findLatestReviewByCycleId(UUID cycleId) {
        ServiceUtils.logEntityLookup("latest review for cycle", cycleId);
        return setReviewRepository.findLatestReviewBySetCycleId(cycleId)
                .map(setReviewMapper::toDto);
    }

    @Override
    public ReviewStatistics getReviewStatistics(UUID cycleId) {
        ServiceUtils.logEntityLookup("review statistics", cycleId);
        
        List<SetReview> reviews = setReviewRepository.findReviewsBySetCycleIdOrderByReviewNo(cycleId);
        
        if (reviews.isEmpty()) {
            return new ReviewStatistics(0, BigDecimal.ZERO, null, null, null, null);
        }
        
        int totalReviews = reviews.size();
        BigDecimal averageScore = calculateAverageScore(cycleId);
        Integer highestScore = findHighestScore(cycleId);
        Integer lowestScore = findLowestScore(cycleId);
        LocalDate firstReviewDate = reviews.get(0).getReviewedAt();
        LocalDate lastReviewDate = reviews.get(reviews.size() - 1).getReviewedAt();
        
        return new ReviewStatistics(
                totalReviews,
                averageScore,
                highestScore,
                lowestScore,
                firstReviewDate,
                lastReviewDate
        );
    }
} 
