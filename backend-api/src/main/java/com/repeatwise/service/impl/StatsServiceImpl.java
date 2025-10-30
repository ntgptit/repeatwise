package com.repeatwise.service.impl;

import com.repeatwise.dto.response.stats.BoxDistributionResponse;
import com.repeatwise.dto.response.stats.UserStatsResponse;
import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.entity.Deck;
import com.repeatwise.entity.Folder;
import com.repeatwise.entity.SrsSettings;
import com.repeatwise.entity.UserStats;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.repository.CardBoxPositionRepository;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.repository.FolderRepository;
import com.repeatwise.repository.ReviewLogRepository;
import com.repeatwise.repository.SrsSettingsRepository;
import com.repeatwise.repository.UserStatsRepository;
import com.repeatwise.service.IStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Statistics Service
 *
 * Requirements:
 * - UC-031: View User Statistics
 * - UC-032: View Box Distribution
 *
 * @author RepeatWise Team
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements IStatsService {

    private final UserStatsRepository userStatsRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final CardBoxPositionRepository cardBoxPositionRepository;
    private final SrsSettingsRepository srsSettingsRepository;
    private final DeckRepository deckRepository;
    private final FolderRepository folderRepository;
    private final MessageSource messageSource;

    // ==================== UC-031: Get User Statistics ====================

    @Override
    public UserStatsResponse getUserStats(final UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Getting user statistics: userId={}", LogEvent.START, userId);

        // Step 1: Get user stats
        final UserStats stats = getUserStatsEntity(userId);

        // Step 2: Get reviews today
        final Instant startOfDay = Instant.now().atZone(java.time.ZoneId.systemDefault())
            .toLocalDate().atStartOfDay()
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant();
        final Instant endOfDay = startOfDay.plusSeconds(86400);

        final long reviewsToday = reviewLogRepository.countReviewsToday(userId, startOfDay, endOfDay);

        // Step 3: Get reviews for past 7 days
        final Instant startDate = Instant.now().minusSeconds(7 * 86400);
        final List<Object[]> reviewCounts = reviewLogRepository.findReviewCountsPast7Days(userId, startDate);

        // Step 4: Build reviewsPast7Days array
        final List<Integer> reviewsPast7Days = buildReviewsPast7Days(reviewCounts);

        log.info("event={} User statistics retrieved: userId={}", LogEvent.SUCCESS, userId);

        return UserStatsResponse.builder()
            .userId(userId)
            .totalCards(stats.getTotalCards())
            .totalDecks(stats.getTotalDecks())
            .totalFolders(stats.getTotalFolders())
            .cardsReviewedToday((int) reviewsToday)
            .streakDays(stats.getStreakDays())
            .lastStudyDate(stats.getLastStudyDate())
            .totalStudyTimeMinutes(stats.getTotalStudyTimeMinutes())
            .reviewsPast7Days(reviewsPast7Days)
            .build();
    }

    // ==================== UC-032: Get Box Distribution ====================

    @Override
    public BoxDistributionResponse getBoxDistribution(
            final String scopeType,
            final UUID scopeId,
            final UUID userId) {

        Objects.requireNonNull(scopeType, "Scope type cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Getting box distribution: scopeType={}, scopeId={}, userId={}",
            LogEvent.START, scopeType, scopeId, userId);

        // Step 1: Get SRS settings to know total boxes
        final SrsSettings settings = getSrsSettings(userId);
        final int totalBoxes = settings.getTotalBoxes();

        // Step 2: Query box distribution data based on scope
        final List<Object[]> distributionData = queryBoxDistributionData(scopeType, scopeId, userId);

        // Step 3: Build distribution map
        final Map<Integer, Long> boxDistribution = buildBoxDistributionMap(distributionData, totalBoxes);

        // Step 4: Calculate total
        final int totalCards = calculateTotalCards(boxDistribution);

        log.info("event={} Box distribution retrieved: scopeType={}, totalCards={}, userId={}",
            LogEvent.SUCCESS, scopeType, totalCards, userId);

        return BoxDistributionResponse.builder()
            .boxDistribution(boxDistribution)
            .totalCards(totalCards)
            .build();
    }

    // ==================== Helper Methods ====================

    private List<Integer> buildReviewsPast7Days(final List<Object[]> reviewCounts) {
        final Map<LocalDate, Long> countsByDate = new HashMap<>();
        for (final Object[] row : reviewCounts) {
            final LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            final Long count = ((Number) row[1]).longValue();
            countsByDate.put(date, count);
        }

        final List<Integer> result = new ArrayList<>();
        final LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            final LocalDate date = today.minusDays(i);
            final Long count = countsByDate.getOrDefault(date, 0L);
            result.add(count.intValue());
        }

        return result;
    }

    private List<Object[]> queryBoxDistributionData(
            final String scopeType,
            final UUID scopeId,
            final UUID userId) {

        if ("ALL".equalsIgnoreCase(scopeType)) {
            return cardBoxPositionRepository.findBoxDistributionByUserId(userId);
        }

        if ("DECK".equalsIgnoreCase(scopeType)) {
            if (scopeId == null) {
                throw new IllegalArgumentException("Scope ID is required for DECK scope");
            }
            getDeckWithOwnershipCheck(scopeId, userId);
            return cardBoxPositionRepository.findBoxDistributionByUserIdAndDeckId(userId, scopeId);
        }

        if ("FOLDER".equalsIgnoreCase(scopeType)) {
            if (scopeId == null) {
                throw new IllegalArgumentException("Scope ID is required for FOLDER scope");
            }
            final Folder folder = getFolderWithOwnershipCheck(scopeId, userId);
            final List<Deck> decks = getDecksInFolderRecursive(folder, userId);
            final List<UUID> deckIds = decks.stream()
                .map(Deck::getId)
                .collect(Collectors.toList());

            if (deckIds.isEmpty()) {
                return Collections.emptyList();
            }

            return cardBoxPositionRepository.findBoxDistributionByUserIdAndDeckIds(userId, deckIds);
        }

        throw new IllegalArgumentException("Invalid scope type: " + scopeType);
    }

    private Map<Integer, Long> buildBoxDistributionMap(
            final List<Object[]> distributionData,
            final int totalBoxes) {

        final Map<Integer, Long> distribution = new HashMap<>();
        for (final Object[] row : distributionData) {
            final Integer box = ((Number) row[0]).intValue();
            final Long count = ((Number) row[1]).longValue();
            distribution.put(box, count);
        }

        // Fill missing boxes with 0
        final Map<Integer, Long> result = new HashMap<>();
        for (int box = 1; box <= totalBoxes; box++) {
            result.put(box, distribution.getOrDefault(box, 0L));
        }

        return result;
    }

    private int calculateTotalCards(final Map<Integer, Long> distribution) {
        return distribution.values().stream()
            .mapToInt(Long::intValue)
            .sum();
    }

    private UserStats getUserStatsEntity(final UUID userId) {
        return userStatsRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "STATS_001",
                getMessage("error.userstats.not.found")
            ));
    }

    private SrsSettings getSrsSettings(final UUID userId) {
        return srsSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "SRS_001",
                getMessage("error.srs.settings.not.found")
            ));
    }

    private Deck getDeckWithOwnershipCheck(final UUID deckId, final UUID userId) {
        return deckRepository.findByIdAndUserId(deckId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "DECK_002",
                getMessage("error.deck.not.found", deckId)
            ));
    }

    private Folder getFolderWithOwnershipCheck(final UUID folderId, final UUID userId) {
        return folderRepository.findByIdAndUserId(folderId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "FOLDER_002",
                getMessage("error.folder.not.found", folderId)
            ));
    }

    private List<Deck> getDecksInFolderRecursive(final Folder folder, final UUID userId) {
        final List<Deck> decks = new ArrayList<>();
        collectDecksRecursive(folder, userId, decks);
        return decks;
    }

    private void collectDecksRecursive(final Folder folder, final UUID userId, final List<Deck> decks) {
        final List<Deck> folderDecks = deckRepository.findByFolderId(folder.getId());
        decks.addAll(folderDecks);

        final List<Folder> children = folderRepository.findChildrenByParentId(userId, folder.getId());
        for (final Folder child : children) {
            collectDecksRecursive(child, userId, decks);
        }
    }

    private String getMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}

