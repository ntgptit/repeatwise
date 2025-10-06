package com.spacedlearning.service;

import com.spacedlearning.entity.LearningSet;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service interface for Spaced Repetition System (SRS) calculations
 * Implements the SRS algorithm for calculating review intervals
 */
public interface SRSService {

    /**
     * Calculate the next review time for a specific review in a cycle
     * 
     * @param learningSet The learning set
     * @param cycleNumber Current cycle number
     * @param reviewNumber Review number (1-5)
     * @return Next review time
     */
    LocalDateTime calculateNextReviewTime(LearningSet learningSet, Integer cycleNumber, Integer reviewNumber);

    /**
     * Calculate the delay for the next cycle based on average score
     * 
     * @param learningSet The learning set
     * @param averageScore Average score of the completed cycle
     * @return Delay in days for the next cycle
     */
    int calculateCycleDelay(LearningSet learningSet, double averageScore);

    /**
     * Get SRS configuration for a user
     * 
     * @param userId User ID
     * @return SRS configuration
     */
    SRSConfiguration getSRSConfiguration(UUID userId);

    /**
     * Update SRS configuration for a user
     * 
     * @param userId User ID
     * @param configuration SRS configuration
     */
    void updateSRSConfiguration(UUID userId, SRSConfiguration configuration);

    /**
     * SRS Configuration class
     */
    class SRSConfiguration {
        private int[] reviewIntervals = {1, 3, 7, 14, 30}; // Days for each review in first cycle
        private double[] scoreThresholds = {40.0, 70.0, 85.0}; // Score thresholds
        private int[] cycleDelays = {1, 3, 7, 14, 30}; // Days delay for next cycle based on score
        private int maxCycleDelay = 90; // Maximum delay between cycles

        // Getters and setters
        public int[] getReviewIntervals() { return reviewIntervals; }
        public void setReviewIntervals(int[] reviewIntervals) { this.reviewIntervals = reviewIntervals; }
        
        public double[] getScoreThresholds() { return scoreThresholds; }
        public void setScoreThresholds(double[] scoreThresholds) { this.scoreThresholds = scoreThresholds; }
        
        public int[] getCycleDelays() { return cycleDelays; }
        public void setCycleDelays(int[] cycleDelays) { this.cycleDelays = cycleDelays; }
        
        public int getMaxCycleDelay() { return maxCycleDelay; }
        public void setMaxCycleDelay(int maxCycleDelay) { this.maxCycleDelay = maxCycleDelay; }
    }
}
