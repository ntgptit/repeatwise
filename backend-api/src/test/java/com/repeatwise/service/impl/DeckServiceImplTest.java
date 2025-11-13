package com.repeatwise.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

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
import com.repeatwise.service.DeckService.DeckCopyResult;
import com.repeatwise.service.DeckService.DeckDeletionResult;
import com.repeatwise.service.FolderService;

@ExtendWith(MockitoExtension.class)
class DeckServiceImplTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID DECK_ID = UUID.randomUUID();
    private static final UUID FOLDER_ID = UUID.randomUUID();
    private static final UUID TARGET_FOLDER_ID = UUID.randomUUID();

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FolderService folderService;

    @Mock
    private DeckMapper deckMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private DeckServiceImpl deckService;

    @Test
    @DisplayName("Create deck successfully when request is valid and folder is absent")
    void should_CreateDeck_When_RequestValid() {
        final var request = CreateDeckRequest.builder()
                .name("  My Deck  ")
                .description("  Description  ")
                .build();

        final var user = createUser();
        when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(this.deckRepository.existsByUserIdAndFolderIsNullAndNameIgnoreCaseAndDeletedAtIsNull(USER_ID, "My Deck"))
                .thenReturn(false);

        final Deck mappedDeck = Deck.builder().build();
        when(this.deckMapper.toEntity(request)).thenReturn(mappedDeck);
        when(this.deckRepository.save(any(Deck.class))).thenAnswer(invocation -> {
            final Deck toSave = invocation.getArgument(0);
            toSave.setId(DECK_ID);
            return toSave;
        });

        final var response = DeckResponse.builder()
                .id(DECK_ID)
                .name("My Deck")
                .build();
        when(this.deckMapper.toResponse(any(Deck.class))).thenReturn(response);

        final DeckResponse result = this.deckService.createDeck(request, USER_ID);

        assertThat(result).isEqualTo(response);

        final ArgumentCaptor<Deck> deckCaptor = ArgumentCaptor.forClass(Deck.class);
        verify(this.deckRepository).save(deckCaptor.capture());
        final Deck savedDeck = deckCaptor.getValue();

        assertThat(savedDeck.getName()).isEqualTo("My Deck");
        assertThat(savedDeck.getDescription()).isEqualTo("Description");
        assertThat(savedDeck.getUser()).isEqualTo(user);
        assertThat(savedDeck.getFolder()).isNull();
        assertThat(savedDeck.getCards()).isEmpty();

        verifyNoInteractions(this.folderService);
    }

    @Test
    @DisplayName("Throw RepeatWiseException when deck name already exists during createDeck")
    void should_ThrowException_When_CreateDeckNameDuplicate() {
        final var request = CreateDeckRequest.builder()
                .name("Existing")
                .folderId(FOLDER_ID)
                .build();

        when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(createUser()));
        when(this.folderService.getFolderEntityById(FOLDER_ID, USER_ID)).thenReturn(createFolder(FOLDER_ID));
        when(this.deckRepository.existsByUserIdAndFolderIdAndNameIgnoreCaseAndDeletedAtIsNull(USER_ID, FOLDER_ID, "Existing"))
                .thenReturn(true);

        final var thrown = catchThrowable(() -> this.deckService.createDeck(request, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.DECK_NAME_ALREADY_EXISTS);

        verify(this.deckRepository, never()).save(any(Deck.class));
        verify(this.deckMapper, never()).toEntity(any(CreateDeckRequest.class));
    }

    @Test
    @DisplayName("Update deck successfully when name and description change")
    void should_UpdateDeck_When_NameChanged() {
        final var request = UpdateDeckRequest.builder()
                .name("  New Name ")
                .description(" New Desc ")
                .build();

        final var existingDeck = createDeck();
        existingDeck.setFolder(createFolder(FOLDER_ID));
        final var previousUpdatedAt = LocalDateTime.now().minusDays(1);
        existingDeck.setUpdatedAt(previousUpdatedAt);

        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(existingDeck));
        when(this.deckRepository.existsByUserIdAndFolderIdAndIdNotAndNameIgnoreCaseAndDeletedAtIsNull(
                USER_ID, FOLDER_ID, DECK_ID, "New Name"))
                .thenReturn(false);
        when(this.deckRepository.save(existingDeck)).thenAnswer(invocation -> invocation.getArgument(0));

        final var response = DeckResponse.builder()
                .id(DECK_ID)
                .name("New Name")
                .description("New Desc")
                .build();
        when(this.deckMapper.toResponse(existingDeck)).thenReturn(response);

        final DeckResponse result = this.deckService.updateDeck(DECK_ID, request, USER_ID);

        assertThat(result).isEqualTo(response);
        assertThat(existingDeck.getName()).isEqualTo("New Name");
        assertThat(existingDeck.getDescription()).isEqualTo("New Desc");
        assertThat(existingDeck.getUpdatedAt()).isAfter(previousUpdatedAt);

        verify(this.deckRepository).save(existingDeck);
        verify(this.deckRepository).existsByUserIdAndFolderIdAndIdNotAndNameIgnoreCaseAndDeletedAtIsNull(
                USER_ID, FOLDER_ID, DECK_ID, "New Name");
    }

    @Test
    @DisplayName("Return current deck when updateDeck has no changes")
    void should_ReturnExistingDeck_When_UpdateNoChanges() {
        final var request = UpdateDeckRequest.builder().build();

        final var existingDeck = createDeck();
        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(existingDeck));

        final var response = DeckResponse.builder().id(DECK_ID).build();
        when(this.deckMapper.toResponse(existingDeck)).thenReturn(response);

        final DeckResponse result = this.deckService.updateDeck(DECK_ID, request, USER_ID);

        assertThat(result).isEqualTo(response);
        verify(this.deckRepository, never()).save(any(Deck.class));
    }

    @Test
    @DisplayName("Move deck to another folder successfully")
    void should_MoveDeck_When_TargetFolderDifferent() {
        final var request = MoveDeckRequest.builder()
                .targetFolderId(TARGET_FOLDER_ID)
                .build();

        final var existingDeck = createDeck();
        existingDeck.setFolder(createFolder(FOLDER_ID));
        final var previousUpdatedAt = LocalDateTime.now().minusDays(1);
        existingDeck.setUpdatedAt(previousUpdatedAt);

        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(existingDeck));
        final var targetFolder = createFolder(TARGET_FOLDER_ID);
        when(this.folderService.getFolderEntityById(TARGET_FOLDER_ID, USER_ID)).thenReturn(targetFolder);
        when(this.deckRepository.existsByUserIdAndFolderIdAndIdNotAndNameIgnoreCaseAndDeletedAtIsNull(
                USER_ID, TARGET_FOLDER_ID, DECK_ID, existingDeck.getName()))
                .thenReturn(false);
        when(this.deckRepository.save(existingDeck)).thenAnswer(invocation -> invocation.getArgument(0));

        final var response = DeckResponse.builder().id(DECK_ID).folderId(TARGET_FOLDER_ID).build();
        when(this.deckMapper.toResponse(existingDeck)).thenReturn(response);

        final DeckResponse result = this.deckService.moveDeck(DECK_ID, request, USER_ID);

        assertThat(result).isEqualTo(response);
        assertThat(existingDeck.getFolder()).isEqualTo(targetFolder);
        assertThat(existingDeck.getUpdatedAt()).isAfter(previousUpdatedAt);

        verify(this.folderService).getFolderEntityById(TARGET_FOLDER_ID, USER_ID);
        verify(this.deckRepository).save(existingDeck);
    }

    @Test
    @DisplayName("Throw RepeatWiseException when moveDeck targets the same folder")
    void should_ThrowException_When_MoveDeckSameLocation() {
        final var request = MoveDeckRequest.builder()
                .targetFolderId(FOLDER_ID)
                .build();

        final var existingDeck = createDeck();
        existingDeck.setFolder(createFolder(FOLDER_ID));

        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(existingDeck));

        final var thrown = catchThrowable(() -> this.deckService.moveDeck(DECK_ID, request, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.DECK_ALREADY_IN_LOCATION);

        verify(this.folderService, never()).getFolderEntityById(any(UUID.class), any(UUID.class));
        verify(this.deckRepository, never()).save(any(Deck.class));
    }

    @Test
    @DisplayName("Copy deck successfully with suffix when name conflicts")
    void should_CopyDeck_When_RequestValid() {
        final var request = CopyDeckRequest.builder()
                .destinationFolderId(TARGET_FOLDER_ID)
                .appendCopySuffix(true)
                .build();

        final var sourceDeck = createDeck();
        sourceDeck.setName("Original");
        final var keptCard = Card.builder()
                .deck(sourceDeck)
                .front("Front")
                .back("Back")
                .build();
        final var deletedCard = Card.builder()
                .deck(sourceDeck)
                .front("Deleted")
                .back("Card")
                .build();
        deletedCard.setDeletedAt(LocalDateTime.now());
        sourceDeck.setCards(new ArrayList<>(List.of(keptCard, deletedCard)));

        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(sourceDeck));
        final var targetFolder = createFolder(TARGET_FOLDER_ID);
        when(this.folderService.getFolderEntityById(TARGET_FOLDER_ID, USER_ID)).thenReturn(targetFolder);
        when(this.deckRepository.existsByUserIdAndFolderIdAndNameIgnoreCaseAndDeletedAtIsNull(USER_ID, TARGET_FOLDER_ID, "Original"))
                .thenReturn(true);
        when(this.deckRepository.existsByUserIdAndFolderIdAndNameIgnoreCaseAndDeletedAtIsNull(USER_ID, TARGET_FOLDER_ID, "Original (copy)"))
                .thenReturn(false);
        when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(createUser()));

        when(this.deckRepository.save(any(Deck.class))).thenAnswer(invocation -> {
            final Deck toSave = invocation.getArgument(0);
            toSave.setId(UUID.randomUUID());
            return toSave;
        });

        final var copiedResponse = DeckResponse.builder()
                .id(UUID.randomUUID())
                .name("Original (copy)")
                .build();
        when(this.deckMapper.toResponse(any(Deck.class))).thenReturn(copiedResponse);
        when(this.messageSource.getMessage(eq("success.deck.copied"), any(Object[].class), any(Locale.class)))
                .thenReturn("Deck copied");

        final DeckCopyResult result = this.deckService.copyDeck(DECK_ID, request, USER_ID);

        assertThat(result.deck()).isEqualTo(copiedResponse);
        assertThat(result.message()).isEqualTo("Deck copied");
        assertThat(result.copiedCards()).isEqualTo(1);

        final ArgumentCaptor<Deck> deckCaptor = ArgumentCaptor.forClass(Deck.class);
        verify(this.deckRepository).save(deckCaptor.capture());
        final Deck savedDeck = deckCaptor.getValue();

        assertThat(savedDeck.getName()).isEqualTo("Original (copy)");
        assertThat(savedDeck.getFolder()).isEqualTo(targetFolder);
        assertThat(savedDeck.getCards()).hasSize(1);
        final Card copiedCard = savedDeck.getCards().get(0);
        assertThat(copiedCard.getFront()).isEqualTo("Front");
        assertThat(copiedCard.getBack()).isEqualTo("Back");
        assertThat(copiedCard.getDeck()).isEqualTo(savedDeck);
    }

    @Test
    @DisplayName("Throw RepeatWiseException when copyDeck exceeds card limit")
    void should_ThrowException_When_CopyDeckTooLarge() {
        final var request = CopyDeckRequest.builder().build();

        final var sourceDeck = createDeck();
        final List<Card> cards = new ArrayList<>();
        IntStream.rangeClosed(0, 10_000).forEach(index -> cards.add(Card.builder()
                .deck(sourceDeck)
                .front("Front " + index)
                .back("Back " + index)
                .build()));
        sourceDeck.setCards(cards);

        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(sourceDeck));

        final var thrown = catchThrowable(() -> this.deckService.copyDeck(DECK_ID, request, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.DECK_TOO_LARGE);

        verify(this.deckRepository, never()).save(any(Deck.class));
    }

    @Test
    @DisplayName("Throw RepeatWiseException when copyDeck new name exists and suffix not allowed")
    void should_ThrowException_When_CopyDeckNameConflictAndNoSuffix() {
        final var request = CopyDeckRequest.builder()
                .newName("Conflicting")
                .appendCopySuffix(false)
                .build();

        final var sourceDeck = createDeck();
        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(sourceDeck));
        when(this.deckRepository.existsByUserIdAndFolderIsNullAndNameIgnoreCaseAndDeletedAtIsNull(USER_ID, "Conflicting"))
                .thenReturn(true);

        final var thrown = catchThrowable(() -> this.deckService.copyDeck(DECK_ID, request, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.DECK_NAME_ALREADY_EXISTS);

        verify(this.deckRepository, never()).save(any(Deck.class));
    }

    @Test
    @DisplayName("Soft delete deck successfully and return result")
    void should_DeleteDeck_When_DeckExists() {
        final var existingDeck = createDeck();
        existingDeck.setName("Sample");
        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(existingDeck));
        when(this.messageSource.getMessage(eq("success.deck.deleted"), any(Object[].class), any(Locale.class)))
                .thenReturn("Deck deleted");

        final DeckDeletionResult result = this.deckService.deleteDeck(DECK_ID, USER_ID);

        assertThat(result.deckId()).isEqualTo(existingDeck.getId());
        assertThat(result.message()).isEqualTo("Deck deleted");
        assertThat(result.deletedAt()).isNotNull();
        assertThat(existingDeck.getDeletedAt()).isEqualTo(result.deletedAt());

        verify(this.deckRepository).save(existingDeck);
    }

    @Test
    @DisplayName("Return deck response when getDeckById succeeds")
    void should_GetDeckById_When_DeckExists() {
        final var existingDeck = createDeck();
        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(existingDeck));
        final var response = DeckResponse.builder().id(DECK_ID).build();
        when(this.deckMapper.toResponse(existingDeck)).thenReturn(response);

        final DeckResponse result = this.deckService.getDeckById(DECK_ID, USER_ID);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Get decks in folder when folderId is provided")
    void should_GetDecksByFolder_When_FolderIdProvided() {
        final var folderDeck = createDeck();
        when(this.folderService.getFolderEntityById(FOLDER_ID, USER_ID)).thenReturn(createFolder(FOLDER_ID));
        when(this.deckRepository.findByUserIdAndFolderId(USER_ID, FOLDER_ID)).thenReturn(List.of(folderDeck));
        when(this.deckMapper.toResponse(folderDeck)).thenReturn(DeckResponse.builder().id(DECK_ID).build());

        final List<DeckResponse> result = this.deckService.getDecks(USER_ID, FOLDER_ID);

        assertThat(result).hasSize(1);
        verify(this.folderService).getFolderEntityById(FOLDER_ID, USER_ID);
        verify(this.deckRepository).findByUserIdAndFolderId(USER_ID, FOLDER_ID);
    }

    @Test
    @DisplayName("Get root decks when folderId is null")
    void should_GetRootDecks_When_FolderIdNull() {
        final var rootDeck = createDeck();
        when(this.deckRepository.findRootDecksByUserId(USER_ID)).thenReturn(List.of(rootDeck));
        when(this.deckMapper.toResponse(rootDeck)).thenReturn(DeckResponse.builder().id(DECK_ID).build());

        final List<DeckResponse> result = this.deckService.getDecks(USER_ID, null);

        assertThat(result).hasSize(1);
        verify(this.deckRepository).findRootDecksByUserId(USER_ID);
        verifyNoInteractions(this.folderService);
    }

    @Test
    @DisplayName("Get all decks for user successfully")
    void should_GetAllDecks_When_UserHasDecks() {
        final var deck = createDeck();
        when(this.deckRepository.findAllByUserId(USER_ID)).thenReturn(List.of(deck));
        when(this.deckMapper.toResponse(deck)).thenReturn(DeckResponse.builder().id(DECK_ID).build());

        final List<DeckResponse> result = this.deckService.getAllDecks(USER_ID);

        assertThat(result).hasSize(1);
        verify(this.deckRepository).findAllByUserId(USER_ID);
    }

    private static User createUser() {
        final var user = User.builder()
                .email("user@example.com")
                .username("user")
                .passwordHash("hashed")
                .build();
        user.setId(USER_ID);
        return user;
    }

    private static Deck createDeck() {
        final var deck = Deck.builder()
                .name("Deck")
                .description("Desc")
                .cards(new ArrayList<>())
                .user(createUser())
                .build();
        deck.setId(DECK_ID);
        return deck;
    }

    private static Folder createFolder(UUID id) {
        final var folder = Folder.builder()
                .name("Folder")
                .path("/" + id)
                .user(createUser())
                .depth(0)
                .sortOrder(0)
                .build();
        folder.setId(id);
        return folder;
    }
}

