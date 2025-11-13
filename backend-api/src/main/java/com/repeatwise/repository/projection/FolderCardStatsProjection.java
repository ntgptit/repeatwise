package com.repeatwise.repository.projection;

/**
 * Projection for aggregated card statistics within a folder subtree.
 */
public interface FolderCardStatsProjection {

    long getTotalCards();

    long getDueCards();

    long getNewCards();

    long getLearningCards();

    long getReviewCards();

    long getMasteredCards();
}

