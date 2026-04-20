package com.example.bankcards.service;

import com.example.bankcards.constant.Role;
import com.example.bankcards.constant.Status;
import com.example.bankcards.constant.BankRestErrorCode;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Client;
import com.example.bankcards.exception.BankRestRuntimeException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.CardServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CardServiceImplTest {

    private static final String CARD_KEY = "test_card_secret_key_string";

    @Test
    void create_card_saves_number_and_returns_dto() throws Exception {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        CardMapper cardMapper = Mockito.mock(CardMapper.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = Mockito.mock(UserService.class);

        Client client = new Client();
        client.setId(5L);
        client.setUsername("username");
        client.setEmail("email");
        client.setPassword("password");
        client.setRole(Role.USER);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(cardRepository.existsByNumber(anyString())).thenReturn(false);

        Card card = new Card();
        card.setId(99L);
        card.setOwner(client);
        card.setValidityPeriod("12/29");
        card.setStatus(Status.ACTIVE);
        card.setBalance(100L);

        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card c = invocation.getArgument(0);
            card.setNumber(c.getNumber());
            return card;
        });

        CardDto cardDto = new CardDto();
        cardDto.setId(99L);
        when(cardMapper.toDto(card)).thenReturn(cardDto);

        CardServiceImpl service = new CardServiceImpl(cardRepository, cardMapper, userRepository, userService);

        Field field = CardServiceImpl.class.getDeclaredField("cardNumberKey");
        field.setAccessible(true);
        field.set(service, CARD_KEY);

        CreateCardRequest createCardRequest = new CreateCardRequest();
        createCardRequest.setUsername("username");
        createCardRequest.setNumber("411111111111");
        createCardRequest.setValidityPeriod("12/29");
        createCardRequest.setBalance(100L);

        assertEquals(cardDto, service.createCard(createCardRequest));

        verify(cardRepository).save(Mockito.argThat(c ->
                c.getOwner() == client
                        && c.getStatus() == Status.ACTIVE
                        && c.getBalance() == 100L
                        && c.getNumber() != null
                        && !c.getNumber().equals("411111111111")));
    }

    @Test
    void create_card_fails_when_number_already_exists() throws Exception {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        CardMapper cardMapper = Mockito.mock(CardMapper.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = Mockito.mock(UserService.class);

        Client client = new Client();
        client.setId(5L);
        client.setUsername("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(client));
        when(cardRepository.existsByNumber(anyString())).thenReturn(true);

        CardServiceImpl service = new CardServiceImpl(cardRepository, cardMapper, userRepository, userService);
        Field field = CardServiceImpl.class.getDeclaredField("cardNumberKey");
        field.setAccessible(true);
        field.set(service, CARD_KEY);

        CreateCardRequest createCardRequest = new CreateCardRequest();
        createCardRequest.setUsername("username");
        createCardRequest.setNumber("411111111111");
        createCardRequest.setValidityPeriod("12/29");
        createCardRequest.setBalance(100L);

        BankRestRuntimeException ex = assertThrows(BankRestRuntimeException.class, () -> service.createCard(createCardRequest));
        assertEquals(BankRestErrorCode.CONFLICT, ex.getErrorCode());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void request_block_by_id_sets_blocked_when_card_active() throws Exception {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        CardMapper cardMapper = Mockito.mock(CardMapper.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = Mockito.mock(UserService.class);

        Client owner = new Client();
        owner.setId(10L);
        when(userService.getCurrentUser()).thenReturn(owner);

        Card card = new Card();
        card.setId(7L);
        card.setOwner(owner);
        card.setStatus(Status.ACTIVE);
        when(cardRepository.findById(7L)).thenReturn(Optional.of(card));

        CardServiceImpl service = new CardServiceImpl(cardRepository, cardMapper, userRepository, userService);
        Field field = CardServiceImpl.class.getDeclaredField("cardNumberKey");
        field.setAccessible(true);
        field.set(service, CARD_KEY);

        service.requestBlockById(7L);

        assertEquals(Status.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void request_block_by_id_skips_save_when_already_blocked() throws Exception {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        CardMapper cardMapper = Mockito.mock(CardMapper.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = Mockito.mock(UserService.class);

        Client client = new Client();
        client.setId(10L);
        when(userService.getCurrentUser()).thenReturn(client);

        Card card = new Card();
        card.setId(7L);
        card.setOwner(client);
        card.setStatus(Status.BLOCKED);
        when(cardRepository.findById(7L)).thenReturn(Optional.of(card));

        CardServiceImpl service = new CardServiceImpl(cardRepository, cardMapper, userRepository, userService);
        Field field = CardServiceImpl.class.getDeclaredField("cardNumberKey");
        field.setAccessible(true);
        field.set(service, CARD_KEY);

        service.requestBlockById(7L);

        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void request_block_by_id_fails_when_not_owner() throws Exception {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        CardMapper cardMapper = Mockito.mock(CardMapper.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = Mockito.mock(UserService.class);

        Client client = new Client();
        client.setId(10L);
        Client otherOwner = new Client();
        otherOwner.setId(99L);
        when(userService.getCurrentUser()).thenReturn(client);

        Card card = new Card();
        card.setId(7L);
        card.setOwner(otherOwner);
        card.setStatus(Status.ACTIVE);
        when(cardRepository.findById(7L)).thenReturn(Optional.of(card));

        CardServiceImpl service = new CardServiceImpl(cardRepository, cardMapper, userRepository, userService);
        Field field = CardServiceImpl.class.getDeclaredField("cardNumberKey");
        field.setAccessible(true);
        field.set(service, CARD_KEY);

        BankRestRuntimeException ex = assertThrows(BankRestRuntimeException.class, () -> service.requestBlockById(7L));
        assertEquals(BankRestErrorCode.FORBIDDEN, ex.getErrorCode());
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void request_block_by_id_fails_when_card_not_active() throws Exception {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        CardMapper cardMapper = Mockito.mock(CardMapper.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = Mockito.mock(UserService.class);

        Client owner = new Client();
        owner.setId(10L);
        when(userService.getCurrentUser()).thenReturn(owner);

        Card card = new Card();
        card.setId(7L);
        card.setOwner(owner);
        card.setStatus(Status.EXPIRED);
        when(cardRepository.findById(7L)).thenReturn(Optional.of(card));

        CardServiceImpl service = new CardServiceImpl(cardRepository, cardMapper, userRepository, userService);
        Field field = CardServiceImpl.class.getDeclaredField("cardNumberKey");
        field.setAccessible(true);
        field.set(service, CARD_KEY);

        BankRestRuntimeException ex = assertThrows(BankRestRuntimeException.class, () -> service.requestBlockById(7L));
        assertEquals(BankRestErrorCode.BAD_REQUEST, ex.getErrorCode());
    }
}
