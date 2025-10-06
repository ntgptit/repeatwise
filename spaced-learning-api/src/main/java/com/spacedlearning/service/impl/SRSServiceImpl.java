package com.spacedlearning.service.impl;

import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.SRSConfiguration;
import com.spacedlearning.entity.User;
import com.spacedlearning.repository.SRSConfigurationRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.service.SRSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of SRSService
 * Implements the Spaced Repetition System algorithm
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SRSServiceImpl implements SRSService {

    private final SRSConfigurationRepository srsConfigurationRepository;
    private final UserRepository userRepository;

    // Default SRS configuration
    private static final int[] DEFAULT_REVIEW_INTERVALS = {1, 3, 7, 14, 30}; // Days
    private static final double[] DEFAULT_SCORE_THRESHOLDS = {40.0, 70.0, 85.0};
    private static final int[] DEFAULT_CYCLE_DELAYS = {1, 3, 7, 14, 30}; // Days
    private static final int DEFAULT_MAX_CYCLE_DELAY = 90;

    @Override
    public LocalDateTime calculateNextReviewTime(LearningSet learningSet, Integer cycleNumber, Integer reviewNumber) {
        log.debug("Calculating next review time for set {} cycle {} review {}", 
                learningSet.getId(), cycleNumber, reviewNumber);

        SRSConfiguration config = getSRSConfiguration(learningSet.getUser().getId());
        
        // Get base time (current time or last review time)
        LocalDateTime baseTime = learningSet.getLastReviewedAt() != null ? 
                learningSet.getLastReviewedAt() : LocalDateTime.now();

        // Calculate interval based on cycle and review number
        int intervalDays = calculateReviewInterval(config, cycleNumber, reviewNumber);

        // Add interval to base time
        LocalDateTime nextReviewTime = baseTime.plusDays(intervalDays);

        // Adjust for user's preferred reminder time
        nextReviewTime = adjustForUserPreferredTime(nextReviewTime, learningSet.getUser());

        log.debug("Calculated next review time: {} (interval: {} days)", nextReviewTime, intervalDays);
        return nextReviewTime;
    }

    @Override
    public int calculateCycleDelay(LearningSet learningSet, double averageScore) {
        log.debug("Calculating cycle delay for set {} with average score {}", 
                learningSet.getId(), averageScore);

        SRSConfiguration config = getSRSConfiguration(learningSet.getUser().getId());
        
        // Determine delay based on average score
        int delayDays = determineDelayByScore(config, averageScore);
        
        // Apply cycle progression factor
        delayDays = applyCycleProgressionFactor(delayDays, learningSet.getCurrentCycle());
        
        // Ensure delay doesn't exceed maximum
        delayDays = Math.min(delayDays, config.getMaxCycleDelay());

        log.debug("Calculated cycle delay: {} days", delayDays);
        return delayDays;
    }

    @Override
    public SRSConfiguration getSRSConfiguration(UUID userId) {
        return srsConfigurationRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultConfiguration(userId));
    }

    @Override
    public void updateSRSConfiguration(UUID userId, SRSConfiguration configuration) {
        SRSConfiguration existingConfig = srsConfigurationRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultConfiguration(userId));

        existingConfig.setReviewIntervals(configuration.getReviewIntervals());
        existingConfig.setScoreThresholds(configuration.getScoreThresholds());
        existingConfig.setCycleDelays(configuration.getCycleDelays());
        existingConfig.setMaxCycleDelay(configuration.getMaxCycleDelay());

        srsConfigurationRepository.save(existingConfig);
        log.info("Updated SRS configuration for user {}", userId);
    }

    // Private helper methods

    private int calculateReviewInterval(SRSConfiguration config, Integer cycleNumber, Integer reviewNumber) {
        int[] intervals = config.getReviewIntervals();
        
        // For first cycle, use standard intervals
        if (cycleNumber == 1) {
            return intervals[Math.min(reviewNumber - 1, intervals.length - 1)];
        }
        
        // For subsequent cycles, apply progression factor
        int baseInterval = intervals[Math.min(reviewNumber - 1, intervals.length - 1)];
        double progressionFactor = Math.pow(1.5, cycleNumber - 1); // 1.5x multiplier per cycle
        
        return (int) Math.round(baseInterval * progressionFactor);
    }

    private int determineDelayByScore(SRSConfiguration config, double averageScore) {
        double[] thresholds = config.getScoreThresholds();
        int[] delays = config.getCycleDelays();
        
        if (averageScore < thresholds[0]) {
            return delays[0]; // Poor performance - short delay
        } else if (averageScore < thresholds[1]) {
            return delays[1]; // Below average - medium delay
        } else if (averageScore < thresholds[2]) {
            return delays[2]; // Good performance - longer delay
        } else {
            return delays[Math.min(3, delays.length - 1)]; // Excellent performance - longest delay
        }
    }

    private int applyCycleProgressionFactor(int baseDelay, Integer currentCycle) {
        // Increase delay slightly with each cycle to prevent overload
        double factor = 1.0 + (currentCycle - 1.0) * 0.1; // 10% increase per cycle
        return (int) Math.round(baseDelay * factor);
    }

    private LocalDateTime adjustForUserPreferredTime(LocalDateTime reviewTime, User user) {
        // Adjust to user's preferred reminder time
        return reviewTime.toLocalDate().atTime(user.getDefaultReminderTime());
    }

    private SRSConfiguration createDefaultConfiguration(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        SRSConfiguration config = SRSConfiguration.builder()
                .user(user)
                .reviewIntervals(DEFAULT_REVIEW_INTERVALS)
                .scoreThresholds(DEFAULT_SCORE_THRESHOLDS)
                .cycleDelays(DEFAULT_CYCLE_DELAYS)
                .maxCycleDelay(DEFAULT_MAX_CYCLE_DELAY)
                .build();

        return srsConfigurationRepository.save(config);
    }
}
