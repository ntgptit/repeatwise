package com.repeatwise.repository;

import com.repeatwise.entity.CardBoxPosition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for CardBoxPosition entity
 *
 * Requirements:
 * - UC-023: Review Cards (SRS)
 * - UC-024: Rate Card
 * - Provides CRUD operations and custom queries for card box positions
 *
 * Query Methods:
 * - Find card box position by user and card
 * - Find due cards for review
 * - Count cards by box
 *
 * @author RepeatWise Team
 */
@Repository
public interface CardBoxPositionRepository extends JpaRepository<CardBoxPosition, UUID> {

    /**
     * Find card box position by user and card
     * Used for retrieving SRS state
     *
     * @param userId User UUID
     * @param cardId Card UUID
     * @return Optional CardBoxPosition
     */
    @Query("SELECT cbp FROM CardBoxPosition cbp " +
           "WHERE cbp.user.id = :userId " +
           "AND cbp.card.id = :cardId " +
           "AND cbp.deletedAt IS NULL")
    Optional<CardBoxPosition> findByUserIdAndCardId(
        @Param("userId") UUID userId,
        @Param("cardId") UUID cardId
    );

    /**
     * Check if card box position exists for user and card
     *
     * @param userId User UUID
     * @param cardId Card UUID
     * @return true if exists
     */
    @Query("SELECT COUNT(cbp) > 0 FROM CardBoxPosition cbp " +
           "WHERE cbp.user.id = :userId " +
           "AND cbp.card.id = :cardId " +
           "AND cbp.deletedAt IS NULL")
    boolean existsByUserIdAndCardId(
        @Param("userId") UUID userId,
        @Param("cardId") UUID cardId
    );

    /**
     * Find all due cards for a user
     * UC-023: Review Cards (SRS)
     *
     * @param userId User UUID
     * @param today Today's date
     * @param pageable Pagination
     * @return List of due card box positions
     */
    @Query("SELECT cbp FROM CardBoxPosition cbp " +
           "JOIN FETCH cbp.card c " +
           "WHERE cbp.user.id = :userId " +
           "AND cbp.dueDate <= :today " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL " +
           "ORDER BY cbp.dueDate ASC, cbp.currentBox ASC")
    List<CardBoxPosition> findDueCardsByUserId(
        @Param("userId") UUID userId,
        @Param("today") LocalDate today,
        Pageable pageable
    );

    /**
     * Find due cards in a specific deck
     * UC-023: Review Cards (SRS) - Deck scope
     *
     * @param userId User UUID
     * @param deckId Deck UUID
     * @param today Today's date
     * @param pageable Pagination
     * @return List of due card box positions
     */
    @Query("SELECT cbp FROM CardBoxPosition cbp " +
           "JOIN FETCH cbp.card c " +
           "WHERE cbp.user.id = :userId " +
           "AND c.deck.id = :deckId " +
           "AND cbp.dueDate <= :today " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL " +
           "ORDER BY cbp.dueDate ASC, cbp.currentBox ASC")
    List<CardBoxPosition> findDueCardsByUserIdAndDeckId(
        @Param("userId") UUID userId,
        @Param("deckId") UUID deckId,
        @Param("today") LocalDate today,
        Pageable pageable
    );

    /**
     * Find all cards in a specific deck (ignoring due_date - for cram mode)
     * UC-029: Cram Mode - Deck scope
     *
     * @param userId User UUID
     * @param deckId Deck UUID
     * @param pageable Pagination
     * @return List of card box positions
     */
    @Query("SELECT cbp FROM CardBoxPosition cbp " +
           "JOIN FETCH cbp.card c " +
           "WHERE cbp.user.id = :userId " +
           "AND c.deck.id = :deckId " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL " +
           "ORDER BY RANDOM()")
    List<CardBoxPosition> findAllCardsByUserIdAndDeckId(
        @Param("userId") UUID userId,
        @Param("deckId") UUID deckId,
        Pageable pageable
    );

    /**
     * Find all cards in multiple decks (ignoring due_date - for cram mode)
     * UC-029: Cram Mode - Folder scope
     *
     * @param userId User UUID
     * @param deckIds List of deck UUIDs
     * @param pageable Pagination
     * @return List of card box positions
     */
    @Query("SELECT cbp FROM CardBoxPosition cbp " +
           "JOIN FETCH cbp.card c " +
           "WHERE cbp.user.id = :userId " +
           "AND c.deck.id IN :deckIds " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL " +
           "ORDER BY RANDOM()")
    List<CardBoxPosition> findAllCardsByUserIdAndDeckIds(
        @Param("userId") UUID userId,
        @Param("deckIds") List<UUID> deckIds,
        Pageable pageable
    );

    /**
     * Find due cards randomized (for random mode)
     * UC-030: Random Mode
     *
     * @param userId User UUID
     * @param deckId Deck UUID
     * @param today Today's date
     * @param pageable Pagination
     * @return List of due card box positions (randomized)
     */
    @Query("SELECT cbp FROM CardBoxPosition cbp " +
           "JOIN FETCH cbp.card c " +
           "WHERE cbp.user.id = :userId " +
           "AND c.deck.id = :deckId " +
           "AND cbp.dueDate <= :today " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL " +
           "ORDER BY RANDOM()")
    List<CardBoxPosition> findDueCardsByUserIdAndDeckIdRandom(
        @Param("userId") UUID userId,
        @Param("deckId") UUID deckId,
        @Param("today") LocalDate today,
        Pageable pageable
    );

    /**
     * Find due cards in multiple decks (folder scope)
     * UC-023: Review Cards (SRS) - Folder scope
     *
     * @param userId User UUID
     * @param deckIds List of deck UUIDs
     * @param today Today's date
     * @param pageable Pagination
     * @return List of due card box positions
     */
    @Query("SELECT cbp FROM CardBoxPosition cbp " +
           "JOIN FETCH cbp.card c " +
           "WHERE cbp.user.id = :userId " +
           "AND c.deck.id IN :deckIds " +
           "AND cbp.dueDate <= :today " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL " +
           "ORDER BY cbp.dueDate ASC, cbp.currentBox ASC")
    List<CardBoxPosition> findDueCardsByUserIdAndDeckIds(
        @Param("userId") UUID userId,
        @Param("deckIds") List<UUID> deckIds,
        @Param("today") LocalDate today,
        Pageable pageable
    );

    /**
     * Get box distribution for all cards (UC-032)
     *
     * @param userId User UUID
     * @return List of [box, count] pairs
     */
    @Query(value = "SELECT cbp.current_box, COUNT(*) as count " +
           "FROM card_box_position cbp " +
           "JOIN cards c ON cbp.card_id = c.id " +
           "WHERE cbp.user_id = :userId " +
           "AND cbp.deleted_at IS NULL " +
           "AND c.deleted_at IS NULL " +
           "GROUP BY cbp.current_box " +
           "ORDER BY cbp.current_box ASC",
           nativeQuery = true)
    List<Object[]> findBoxDistributionByUserId(@Param("userId") UUID userId);

    /**
     * Get box distribution for a deck (UC-032)
     *
     * @param userId User UUID
     * @param deckId Deck UUID
     * @return List of [box, count] pairs
     */
    @Query(value = "SELECT cbp.current_box, COUNT(*) as count " +
           "FROM card_box_position cbp " +
           "JOIN cards c ON cbp.card_id = c.id " +
           "WHERE cbp.user_id = :userId " +
           "AND c.deck_id = :deckId " +
           "AND cbp.deleted_at IS NULL " +
           "AND c.deleted_at IS NULL " +
           "GROUP BY cbp.current_box " +
           "ORDER BY cbp.current_box ASC",
           nativeQuery = true)
    List<Object[]> findBoxDistributionByUserIdAndDeckId(
        @Param("userId") UUID userId,
        @Param("deckId") UUID deckId
    );

    /**
     * Find due cards in multiple decks randomized (for random mode)
     * UC-030: Random Mode - Folder scope
     *
     * @param userId User UUID
     * @param deckIds List of deck UUIDs
     * @param today Today's date
     * @param pageable Pagination
     * @return List of due card box positions (randomized)
     */
    @Query("SELECT cbp FROM CardBoxPosition cbp " +
           "JOIN FETCH cbp.card c " +
           "WHERE cbp.user.id = :userId " +
           "AND c.deck.id IN :deckIds " +
           "AND cbp.dueDate <= :today " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL " +
           "ORDER BY RANDOM()")
    List<CardBoxPosition> findDueCardsByUserIdAndDeckIdsRandom(
        @Param("userId") UUID userId,
        @Param("deckIds") List<UUID> deckIds,
        @Param("today") LocalDate today,
        Pageable pageable
    );

    /**
     * Get box distribution for multiple decks (UC-032)
     *
     * @param userId User UUID
     * @param deckIds List of deck UUIDs
     * @return List of [box, count] pairs
     */
    @Query(value = "SELECT cbp.current_box, COUNT(*) as count " +
           "FROM card_box_position cbp " +
           "JOIN cards c ON cbp.card_id = c.id " +
           "WHERE cbp.user_id = :userId " +
           "AND c.deck_id IN :deckIds " +
           "AND cbp.deleted_at IS NULL " +
           "AND c.deleted_at IS NULL " +
           "GROUP BY cbp.current_box " +
           "ORDER BY cbp.current_box ASC",
           nativeQuery = true)
    List<Object[]> findBoxDistributionByUserIdAndDeckIds(
        @Param("userId") UUID userId,
        @Param("deckIds") List<UUID> deckIds
    );

    /**
     * Count due cards in decks within folder and descendants (recursive)
     * UC-010: View Folder Statistics - Count due cards in folder tree
     *
     * @param userId User UUID
     * @param folderId Folder UUID
     * @param folderPath Folder path prefix
     * @param today Today's date
     * @return Number of due cards
     */
    @Query("SELECT COUNT(cbp) FROM CardBoxPosition cbp " +
           "JOIN cbp.card c " +
           "JOIN c.deck d " +
           "WHERE cbp.user.id = :userId " +
           "AND (d.folder.id = :folderId OR d.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND cbp.dueDate <= :today " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL")
    Long countDueCardsByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath,
        @Param("today") LocalDate today
    );

    /**
     * Count new cards (reviewCount = 0) in decks within folder and descendants (recursive)
     * UC-010: View Folder Statistics - Count new cards in folder tree
     *
     * @param userId User UUID
     * @param folderId Folder UUID
     * @param folderPath Folder path prefix
     * @return Number of new cards
     */
    @Query("SELECT COUNT(cbp) FROM CardBoxPosition cbp " +
           "JOIN cbp.card c " +
           "JOIN c.deck d " +
           "WHERE cbp.user.id = :userId " +
           "AND (d.folder.id = :folderId OR d.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND cbp.reviewCount = 0 " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL")
    Long countNewCardsByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath
    );

    /**
     * Count mature cards (currentBox >= 5) in decks within folder and descendants (recursive)
     * UC-010: View Folder Statistics - Count mature cards in folder tree
     *
     * @param userId User UUID
     * @param folderId Folder UUID
     * @param folderPath Folder path prefix
     * @return Number of mature cards
     */
    @Query("SELECT COUNT(cbp) FROM CardBoxPosition cbp " +
           "JOIN cbp.card c " +
           "JOIN c.deck d " +
           "WHERE cbp.user.id = :userId " +
           "AND (d.folder.id = :folderId OR d.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND cbp.currentBox >= 5 " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL")
    Long countMatureCardsByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath
    );

    /**
     * Count total cards with CardBoxPosition in decks within folder and descendants (recursive)
     * UC-010: View Folder Statistics - Count total cards in folder tree
     *
     * @param userId User UUID
     * @param folderId Folder UUID
     * @param folderPath Folder path prefix
     * @return Number of cards
     */
    @Query("SELECT COUNT(cbp) FROM CardBoxPosition cbp " +
           "JOIN cbp.card c " +
           "JOIN c.deck d " +
           "WHERE cbp.user.id = :userId " +
           "AND (d.folder.id = :folderId OR d.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND cbp.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL")
    Long countTotalCardsByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath
    );
}

