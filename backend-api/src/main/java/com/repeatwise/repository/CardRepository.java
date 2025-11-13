package com.repeatwise.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.Card;

/**
 * Repository cho thực thể {@link Card}.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    /**
     * Tìm thẻ đang hoạt động theo ID và người dùng sở hữu.
     */
    @Query("""
            SELECT c FROM Card c
            WHERE c.id = :cardId
              AND c.deck.user.id = :userId
              AND c.deletedAt IS NULL
              AND c.deck.deletedAt IS NULL
            """)
    Optional<Card> findActiveByIdAndUserId(@Param("cardId") UUID cardId, @Param("userId") UUID userId);

    /**
     * Tìm thẻ đã bị soft delete theo ID và người dùng sở hữu.
     */
    @Query("""
            SELECT c FROM Card c
            WHERE c.id = :cardId
              AND c.deck.user.id = :userId
              AND c.deletedAt IS NOT NULL
            """)
    Optional<Card> findDeletedByIdAndUserId(@Param("cardId") UUID cardId, @Param("userId") UUID userId);

    /**
     * Tìm thẻ đang hoạt động kèm CardBoxPosition (phục vụ xóa thẻ).
     */
    @EntityGraph(attributePaths = "cardBoxPositions")
    @Query("""
            SELECT c FROM Card c
            WHERE c.id = :cardId
              AND c.deck.user.id = :userId
              AND c.deletedAt IS NULL
              AND c.deck.deletedAt IS NULL
            """)
    Optional<Card> findActiveWithPositionsByIdAndUserId(@Param("cardId") UUID cardId, @Param("userId") UUID userId);

    /**
     * Lấy danh sách thẻ trong một deck thuộc sở hữu của người dùng.
     */
    @Query("""
            SELECT c FROM Card c
            WHERE c.deck.id = :deckId
              AND c.deck.user.id = :userId
              AND c.deletedAt IS NULL
              AND c.deck.deletedAt IS NULL
            ORDER BY c.createdAt DESC
            """)
    List<Card> findActiveByDeckIdAndUserId(@Param("deckId") UUID deckId, @Param("userId") UUID userId);
}

