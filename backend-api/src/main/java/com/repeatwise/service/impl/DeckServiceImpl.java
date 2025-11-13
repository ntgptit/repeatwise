package com.repeatwise.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.dto.request.deck.CopyDeckRequest;
import com.repeatwise.dto.request.deck.CreateDeckRequest;
import com.repeatwise.dto.request.deck.MoveDeckRequest;
import com.repeatwise.dto.request.deck.UpdateDeckRequest;
import com.repeatwise.dto.response.deck.DeckResponse;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.Deck;
import com.repeatwise.entity.Folder;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.mapper.DeckMapper;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.DeckService;
import com.repeatwise.service.FolderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Triển khai DeckService, bao phủ UC-013 đến UC-017.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeckServiceImpl implements DeckService {

    private static final int MAX_COPY_CARDS = 10_000;
    private static final String COPY_SUFFIX = " (copy)";
    private static final String COPY_SUFFIX_WITH_COUNTER = " (copy %d)";

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final FolderService folderService;
    private final DeckMapper deckMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public DeckResponse createDeck(CreateDeckRequest request, UUID userId) {
        log.debug("User {} yêu cầu tạo deck mới: {}", userId, request);

        final var user = getUserOrThrow(userId);
        final var folder = getFolderIfPresent(request.getFolderId(), userId);

        final var trimmedName = trimAndValidateName(request.getName());
        final var trimmedDescription = trimToNull(request.getDescription());

        validateDeckNameUnique(trimmedName, userId, request.getFolderId());

        var deck = this.deckMapper.toEntity(request);
        deck.setName(trimmedName);
        deck.setDescription(trimmedDescription);
        deck.setUser(user);
        deck.setFolder(folder);
        deck.setCards(new ArrayList<>());

        deck = this.deckRepository.save(deck);

        log.info("Tạo deck {} ({}) cho user {}", deck.getName(), deck.getId(), userId);

        return this.deckMapper.toResponse(deck);
    }

    @Override
    @Transactional
    public DeckResponse updateDeck(UUID deckId, UpdateDeckRequest request, UUID userId) {
        log.debug("User {} cập nhật deck {} với payload {}", userId, deckId, request);

        final var deck = getDeckOrThrow(deckId, userId);
        var changed = false;

        if (request.getName() != null) {
            final var trimmedName = trimAndValidateName(request.getName());
            if (!trimmedName.equals(deck.getName())) {
                validateDeckNameUniqueExcluding(trimmedName, userId, deck.getFolder() != null ? deck.getFolder().getId() : null,
                        deckId);
                deck.setName(trimmedName);
                changed = true;
            }
        }

        if (request.getDescription() != null) {
            final var trimmedDescription = trimToNull(request.getDescription());
            if (!Objects.equals(trimmedDescription, deck.getDescription())) {
                deck.setDescription(trimmedDescription);
                changed = true;
            }
        }

        if (!changed) {
            log.debug("Deck {} không thay đổi dữ liệu, trả về hiện trạng", deckId);
            return this.deckMapper.toResponse(deck);
        }

        deck.setUpdatedAt(LocalDateTime.now());

        final var updatedDeck = this.deckRepository.save(deck);

        log.info("Deck {} đã được cập nhật bởi user {}", deckId, userId);

        return this.deckMapper.toResponse(updatedDeck);
    }

    @Override
    @Transactional
    public DeckResponse moveDeck(UUID deckId, MoveDeckRequest request, UUID userId) {
        log.debug("User {} di chuyển deck {} tới thư mục {}", userId, deckId, request.getTargetFolderId());

        final var deck = getDeckOrThrow(deckId, userId);
        final var currentFolderId = deck.getFolder() != null ? deck.getFolder().getId() : null;
        final var targetFolderId = request.getTargetFolderId();

        if (Objects.equals(currentFolderId, targetFolderId)) {
            throw new RepeatWiseException(RepeatWiseError.DECK_ALREADY_IN_LOCATION, deck.getName());
        }

        final var targetFolder = getFolderIfPresent(targetFolderId, userId);

        validateDeckNameUniqueExcluding(deck.getName(), userId, targetFolderId, deckId);

        deck.setFolder(targetFolder);
        deck.setUpdatedAt(LocalDateTime.now());

        final var savedDeck = this.deckRepository.save(deck);

        log.info("Deck {} đã được di chuyển tới thư mục {} bởi user {}", deckId, targetFolderId, userId);

        return this.deckMapper.toResponse(savedDeck);
    }

    @Override
    @Transactional
    public DeckCopyResult copyDeck(UUID deckId, CopyDeckRequest request, UUID userId) {
        log.debug("User {} sao chép deck {} với request {}", userId, deckId, request);

        final var sourceDeck = getDeckOrThrow(deckId, userId);
        final var destinationFolderId = request.getDestinationFolderId();
        final var destinationFolder = getFolderIfPresent(destinationFolderId, userId);

        final var cardsToCopy = sourceDeck.getCards()
                .stream()
                .filter(card -> card.getDeletedAt() == null)
                .toList();

        if (cardsToCopy.size() > MAX_COPY_CARDS) {
            throw new RepeatWiseException(RepeatWiseError.DECK_TOO_LARGE, cardsToCopy.size(), MAX_COPY_CARDS);
        }

        final var targetName = resolveCopyName(sourceDeck.getName(), request, userId, destinationFolderId);
        final var user = getUserOrThrow(userId);
        final var newDeck = Deck.builder()
                .user(user)
                .folder(destinationFolder)
                .name(targetName)
                .description(sourceDeck.getDescription())
                .cards(new ArrayList<>())
                .build();

        copyCards(cardsToCopy, newDeck);

        final var savedDeck = this.deckRepository.save(newDeck);

        final var locale = LocaleContextHolder.getLocale();
        final var message = this.messageSource.getMessage(
                "success.deck.copied",
                new Object[] { savedDeck.getName(), cardsToCopy.size() },
                locale);

        log.info("User {} sao chép deck {} -> deck mới {} ({})", userId, deckId, savedDeck.getId(), savedDeck.getName());

        return new DeckCopyResult(this.deckMapper.toResponse(savedDeck), message, cardsToCopy.size());
    }

    @Override
    @Transactional
    public DeckDeletionResult deleteDeck(UUID deckId, UUID userId) {
        log.debug("User {} xóa deck {}", userId, deckId);

        final var deck = getDeckOrThrow(deckId, userId);

        final var now = LocalDateTime.now();
        deck.setDeletedAt(now);
        this.deckRepository.save(deck);

        final var locale = LocaleContextHolder.getLocale();
        final var message = this.messageSource.getMessage(
                "success.deck.deleted",
                new Object[] { deck.getName() },
                locale);

        log.info("Deck {} đã được soft delete bởi user {}", deckId, userId);

        return new DeckDeletionResult(deck.getId(), message, now);
    }

    @Override
    @Transactional(readOnly = true)
    public DeckResponse getDeckById(UUID deckId, UUID userId) {
        final var deck = getDeckOrThrow(deckId, userId);
        return this.deckMapper.toResponse(deck);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeckResponse> getDecks(UUID userId, UUID folderId) {
        if (folderId != null) {
            // đảm bảo thư mục thuộc về user
            getFolderIfPresent(folderId, userId);
            return this.deckRepository.findByUserIdAndFolderId(userId, folderId)
                    .stream()
                    .map(this.deckMapper::toResponse)
                    .toList();
        }

        return this.deckRepository.findRootDecksByUserId(userId)
                .stream()
                .map(this.deckMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeckResponse> getAllDecks(UUID userId) {
        return this.deckRepository.findAllByUserId(userId)
                .stream()
                .map(this.deckMapper::toResponse)
                .toList();
    }

    private void copyCards(List<Card> cardsToCopy, Deck targetDeck) {
        for (final Card card : cardsToCopy) {
            final var newCard = Card.builder()
                    .deck(targetDeck)
                    .front(card.getFront())
                    .back(card.getBack())
                    .build();
            targetDeck.getCards().add(newCard);
        }
    }

    private String resolveCopyName(String sourceName, CopyDeckRequest request, UUID userId, UUID destinationFolderId) {
        String baseName = sourceName;

        if ((request.getNewName() != null) && !request.getNewName().isBlank()) {
            baseName = trimAndValidateName(request.getNewName());
        }

        if (!deckNameExists(baseName, userId, destinationFolderId)) {
            return baseName;
        }

        if (!request.isAppendCopySuffix()) {
            throw new RepeatWiseException(RepeatWiseError.DECK_NAME_ALREADY_EXISTS, baseName);
        }

        // Thử với suffix (copy), sau đó (copy 2), (copy 3)...
        var candidate = baseName + COPY_SUFFIX;
        var counter = 2;
        while (deckNameExists(candidate, userId, destinationFolderId)) {
            candidate = baseName + COPY_SUFFIX_WITH_COUNTER.formatted(counter);
            counter++;
        }
        return candidate;
    }

    private boolean deckNameExists(String name, UUID userId, UUID folderId) {
        return this.deckRepository.existsByNameAndFolder(userId, folderId, name);
    }

    private void validateDeckNameUnique(String name, UUID userId, UUID folderId) {
        if (deckNameExists(name, userId, folderId)) {
            throw new RepeatWiseException(RepeatWiseError.DECK_NAME_ALREADY_EXISTS, name);
        }
    }

    private void validateDeckNameUniqueExcluding(String name, UUID userId, UUID folderId, UUID deckId) {
        final var exists = this.deckRepository.existsByNameAndFolderExcludingId(userId, folderId, name, deckId);
        if (exists) {
            throw new RepeatWiseException(RepeatWiseError.DECK_NAME_ALREADY_EXISTS, name);
        }
    }

    private Deck getDeckOrThrow(UUID deckId, UUID userId) {
        return this.deckRepository.findByIdAndUserId(deckId, userId)
                .orElseThrow(() -> new RepeatWiseException(
                        RepeatWiseError.DECK_NOT_FOUND,
                        deckId));
    }

    private Folder getFolderIfPresent(UUID folderId, UUID userId) {
        if (folderId == null) {
            return null;
        }
        return this.folderService.getFolderEntityById(folderId, userId);
    }

    private User getUserOrThrow(UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new RepeatWiseException(
                        RepeatWiseError.USER_NOT_FOUND,
                        userId));
    }

    private String trimAndValidateName(String name) {
        final var trimmed = name != null ? name.trim() : "";
        if (trimmed.isEmpty()) {
            throw new RepeatWiseException(RepeatWiseError.DECK_NAME_ALREADY_EXISTS, trimmed);
        }
        return trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        final var trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

