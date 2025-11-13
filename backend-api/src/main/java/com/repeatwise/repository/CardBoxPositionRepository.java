package com.repeatwise.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.repository.projection.FolderCardStatsProjection;

/**
 * Repository for card box positions (per-user SRS state).
 */
@Repository
public interface CardBoxPositionRepository extends JpaRepository<CardBoxPosition, UUID> {

    /**
     * Aggregate card statistics for the given deck IDs.
     */
    @Query("""
            SELECT
                COALESCE(COUNT(cbp), 0) AS totalCards,
                COALESCE(SUM(CASE WHEN cbp.dueDate <= :today THEN 1 ELSE 0 END), 0) AS dueCards,
                COALESCE(SUM(CASE WHEN cbp.reviewCount = 0 THEN 1 ELSE 0 END), 0) AS newCards,
                COALESCE(SUM(CASE WHEN cbp.reviewCount > 0 AND cbp.currentBox < 3 THEN 1 ELSE 0 END), 0) AS learningCards,
                COALESCE(SUM(CASE WHEN cbp.reviewCount > 0 AND cbp.currentBox BETWEEN 3 AND 4 THEN 1 ELSE 0 END), 0) AS reviewCards,
                COALESCE(SUM(CASE WHEN cbp.currentBox >= 5 THEN 1 ELSE 0 END), 0) AS masteredCards
            FROM CardBoxPosition cbp
            WHERE cbp.user.id = :userId
              AND cbp.deletedAt IS NULL
              AND cbp.card.deletedAt IS NULL
              AND cbp.card.deck.deletedAt IS NULL
              AND cbp.card.deck.id IN :deckIds
            """)
    FolderCardStatsProjection aggregateStats(@Param("userId") UUID userId,
            @Param("deckIds") List<UUID> deckIds,
            @Param("today") LocalDate today);
}

