package com.repeatwise.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import com.repeatwise.dto.request.card.CreateCardRequest;
import com.repeatwise.dto.request.card.UpdateCardRequest;
import com.repeatwise.dto.response.card.CardResponse;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.entity.Deck;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.mapper.CardMapper;
import com.repeatwise.repository.CardBoxPositionRepository;
import com.repeatwise.repository.CardRepository;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID DECK_ID = UUID.randomUUID();
    private static final UUID CARD_ID = UUID.randomUUID();

    @Mock
    private CardRepository cardRepository;

    @Mock
    private com.repeatwise.repository.DeckRepository deckRepository;

    @Mock
    private CardBoxPositionRepository cardBoxPositionRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    @DisplayName("Creates card successfully when request is valid")
    void should_CreateCard_When_RequestValid() {
        final var request = CreateCardRequest.builder()
                .deckId(DECK_ID)
                .front("  Front  ")
                .back("  Back  ")
                .build();

        final var deck = createDeck();
        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(deck));

        final var mappedCard = createCard(deck);
        when(this.cardMapper.toEntity(request)).thenReturn(mappedCard);
        when(this.cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            final Card toSave = invocation.getArgument(0, Card.class);
            toSave.setId(CARD_ID);
            return toSave;
        });
        when(this.cardBoxPositionRepository.save(any(CardBoxPosition.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, CardBoxPosition.class));

        final var response = CardResponse.builder()
                .id(CARD_ID)
                .deckId(DECK_ID)
                .front("Front")
                .back("Back")
                .build();
        when(this.cardMapper.toResponse(any(Card.class))).thenReturn(response);

        final var result = this.cardService.createCard(request, USER_ID);

        assertThat(result).isEqualTo(response);

        final ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        verify(this.cardRepository).save(cardCaptor.capture());
        final var savedCard = cardCaptor.getValue();

        assertThat(savedCard.getDeck()).isEqualTo(deck);
        assertThat(savedCard.getFront()).isEqualTo("Front");
        assertThat(savedCard.getBack()).isEqualTo("Back");
        verify(this.cardBoxPositionRepository).save(any(CardBoxPosition.class));
    }

    @Test
    @DisplayName("Throws RepeatWiseException when deck is missing during createCard")
    void should_ThrowException_When_DeckNotFoundOnCreate() {
        final var request = CreateCardRequest.builder()
                .deckId(DECK_ID)
                .front("Front")
                .back("Back")
                .build();

        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.empty());

        final var thrown = catchThrowable(() -> this.cardService.createCard(request, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.DECK_NOT_FOUND);

        verify(this.cardRepository, never()).save(any(Card.class));
        verifyNoInteractions(this.cardMapper, this.cardBoxPositionRepository);
    }

    @Test
    @DisplayName("Updates card successfully when content changes")
    void should_UpdateCard_When_ContentChanged() {
        final var request = UpdateCardRequest.builder()
                .front("  New Front ")
                .back(" New Back  ")
                .build();

        final var card = createCard(createDeck());
        card.setId(CARD_ID);
        final var previousUpdatedAt = LocalDateTime.now().minusMinutes(5);
        card.setUpdatedAt(previousUpdatedAt);

        when(this.cardRepository.findActiveByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.of(card));
        when(this.cardRepository.save(card)).thenAnswer(invocation -> invocation.getArgument(0, Card.class));

        final var response = CardResponse.builder()
                .id(CARD_ID)
                .deckId(DECK_ID)
                .front("New Front")
                .back("New Back")
                .build();
        when(this.cardMapper.toResponse(card)).thenReturn(response);

        final var result = this.cardService.updateCard(CARD_ID, request, USER_ID);

        assertThat(result).isEqualTo(response);
        assertThat(card.getFront()).isEqualTo("New Front");
        assertThat(card.getBack()).isEqualTo("New Back");
        assertThat(card.getUpdatedAt()).isAfter(previousUpdatedAt);

        verify(this.cardRepository).save(card);
    }

    @Test
    @DisplayName("Returns existing card when no changes in updateCard")
    void should_ReturnExistingCard_When_NoChangesOnUpdate() {
        final var request = UpdateCardRequest.builder().build();

        final var card = createCard(createDeck());
        card.setId(CARD_ID);

        when(this.cardRepository.findActiveByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.of(card));
        final var response = CardResponse.builder().id(CARD_ID).build();
        when(this.cardMapper.toResponse(card)).thenReturn(response);

        final var result = this.cardService.updateCard(CARD_ID, request, USER_ID);

        assertThat(result).isEqualTo(response);
        verify(this.cardRepository, never()).save(any(Card.class));
    }

    @Test
    @DisplayName("Throws RepeatWiseException when front is blank after trim in updateCard")
    void should_ThrowException_When_UpdateFrontBlank() {
        final var request = UpdateCardRequest.builder()
                .front("   ")
                .build();

        final var card = createCard(createDeck());
        card.setId(CARD_ID);

        when(this.cardRepository.findActiveByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.of(card));

        final var thrown = catchThrowable(() -> this.cardService.updateCard(CARD_ID, request, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.CARD_FRONT_REQUIRED);

        verify(this.cardRepository, never()).save(any(Card.class));
    }

    @Test
    @DisplayName("Soft deletes card successfully")
    void should_DeleteCard_When_Active() {
        final var deck = createDeck();
        final var card = createCard(deck);
        card.setId(CARD_ID);

        final var position = CardBoxPosition.createNew(card, deck.getUser());
        card.getCardBoxPositions().add(position);

        when(this.cardRepository.findActiveWithPositionsByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.of(card));
        when(this.cardRepository.save(card)).thenAnswer(invocation -> invocation.getArgument(0, Card.class));
        when(this.messageSource.getMessage(eq("success.card.deleted"), isNull(), any(Locale.class)))
                .thenReturn("Card deleted");

        final var result = this.cardService.deleteCard(CARD_ID, USER_ID);

        assertThat(result.cardId()).isEqualTo(CARD_ID);
        assertThat(result.message()).isEqualTo("Card deleted");
        assertThat(card.getDeletedAt()).isNotNull();
        assertThat(card.getCardBoxPositions())
                .allSatisfy(pos -> assertThat(pos.getDeletedAt()).isNotNull());

        verify(this.cardRepository).save(card);
        verify(this.messageSource).getMessage(eq("success.card.deleted"), isNull(), any(Locale.class));
    }

    @Test
    @DisplayName("Throws RepeatWiseException when card already deleted")
    void should_ThrowException_When_CardAlreadyDeleted() {
        when(this.cardRepository.findActiveWithPositionsByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.empty());
        final var deletedCard = createCard(createDeck());
        deletedCard.setId(CARD_ID);
        deletedCard.setDeletedAt(LocalDateTime.now().minusDays(1));
        when(this.cardRepository.findDeletedByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.of(deletedCard));

        final var thrown = catchThrowable(() -> this.cardService.deleteCard(CARD_ID, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.CARD_ALREADY_DELETED);

        verify(this.cardRepository, never()).save(any(Card.class));
        verifyNoInteractions(this.messageSource);
    }

    @Test
    @DisplayName("Throws RepeatWiseException when card not found during deleteCard")
    void should_ThrowException_When_CardNotFoundOnDelete() {
        when(this.cardRepository.findActiveWithPositionsByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.empty());
        when(this.cardRepository.findDeletedByIdAndUserId(CARD_ID, USER_ID)).thenReturn(Optional.empty());

        final var thrown = catchThrowable(() -> this.cardService.deleteCard(CARD_ID, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.CARD_NOT_FOUND);

        verify(this.cardRepository, never()).save(any(Card.class));
    }

    @Test
    @DisplayName("Returns cards by deck when deck exists and belongs to user")
    void should_GetCardsByDeck_When_DeckAccessible() {
        final var deck = createDeck();
        when(this.deckRepository.findByIdAndUserId(DECK_ID, USER_ID)).thenReturn(Optional.of(deck));

        final var card = createCard(deck);
        card.setId(CARD_ID);
        when(this.cardRepository.findActiveByDeckIdAndUserId(DECK_ID, USER_ID)).thenReturn(List.of(card));

        final var response = CardResponse.builder()
                .id(CARD_ID)
                .deckId(DECK_ID)
                .front("Front")
                .back("Back")
                .build();
        when(this.cardMapper.toResponse(card)).thenReturn(response);

        final var result = this.cardService.getCardsByDeck(DECK_ID, USER_ID);

        assertThat(result).containsExactly(response);
        verify(this.deckRepository).findByIdAndUserId(DECK_ID, USER_ID);
        verify(this.cardRepository).findActiveByDeckIdAndUserId(DECK_ID, USER_ID);
    }

    private static Deck createDeck() {
        final var user = createUser();
        final var deck = Deck.builder()
                .name("Deck")
                .description("Desc")
                .user(user)
                .cards(new ArrayList<>())
                .build();
        deck.setId(DECK_ID);
        return deck;
    }

    private static Card createCard(Deck deck) {
        return Card.builder()
                .deck(deck)
                .front("Front")
                .back("Back")
                .cardBoxPositions(new ArrayList<>())
                .reviewLogs(new ArrayList<>())
                .build();
    }

    private static User createUser() {
        final var user = User.builder()
                .email("user@example.com")
                .username("user")
                .passwordHash("hashed-password")
                .build();
        user.setId(USER_ID);
        user.setCardBoxPositions(new ArrayList<>());
        user.setDecks(new ArrayList<>());
        return user;
    }
}
