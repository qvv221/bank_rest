package com.example.bankcards.service;

import com.example.bankcards.constant.Role;
import com.example.bankcards.constant.Status;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Client;
import com.example.bankcards.constant.BankRestErrorCode;
import com.example.bankcards.exception.BankRestRuntimeException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class TransferServiceTest {

    @Test
    void transfer_moves_money_between_own_cards() {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        UserService userService = Mockito.mock(UserService.class);

        TransferService transferService = new TransferServiceImpl(cardRepository, userService);

        Client client = new Client();
        client.setId(10L);
        client.setUsername("username");
        client.setEmail("email");
        client.setPassword("password");
        client.setRole(Role.USER);
        Mockito.when(userService.getCurrentUser()).thenReturn(client);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setOwner(client);
        fromCard.setStatus(Status.ACTIVE);
        fromCard.setBalance(10000L);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setOwner(client);
        toCard.setStatus(Status.ACTIVE);
        toCard.setBalance(500L);

        Mockito.when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromCard));
        Mockito.when(cardRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toCard));

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(1000L);

        transferService.transfer(transferRequest);

        assertEquals(9000L, fromCard.getBalance());
        assertEquals(1500L, toCard.getBalance());
    }

    @Test
    void transfer_fails_when_not_enough_money() {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        UserService userService = Mockito.mock(UserService.class);

        TransferService transferService = new TransferServiceImpl(cardRepository, userService);

        Client client = new Client();
        client.setId(10L);
        client.setUsername("username");
        client.setEmail("email");
        client.setPassword("password");
        client.setRole(Role.USER);
        Mockito.when(userService.getCurrentUser()).thenReturn(client);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setOwner(client);
        fromCard.setStatus(Status.ACTIVE);
        fromCard.setBalance(100L);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setOwner(client);
        toCard.setStatus(Status.ACTIVE);
        toCard.setBalance(500L);

        Mockito.when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromCard));
        Mockito.when(cardRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toCard));

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(1000L);

        assertThrows(BankRestRuntimeException.class, () -> transferService.transfer(transferRequest));
    }

    @Test
    void transfer_fails_when_from_and_to_same_card() {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        UserService userService = Mockito.mock(UserService.class);
        TransferServiceImpl transferService = new TransferServiceImpl(cardRepository, userService);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromCardId(5L);
        transferRequest.setToCardId(5L);
        transferRequest.setAmount(100L);

        BankRestRuntimeException ex = assertThrows(BankRestRuntimeException.class, () -> transferService.transfer(transferRequest));
        assertEquals(BankRestErrorCode.BAD_REQUEST, ex.getErrorCode());
        verify(userService, never()).getCurrentUser();
        verify(cardRepository, never()).findByIdForUpdate(any());
    }

    @Test
    void transfer_fails_when_from_card_owned_by_another_user() {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        UserService userService = Mockito.mock(UserService.class);
        TransferServiceImpl transferService = new TransferServiceImpl(cardRepository, userService);

        Client client = new Client();
        client.setId(10L);
        Mockito.when(userService.getCurrentUser()).thenReturn(client);

        Client stranger = new Client();
        stranger.setId(99L);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setOwner(stranger);
        fromCard.setStatus(Status.ACTIVE);
        fromCard.setBalance(5000L);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setOwner(client);
        toCard.setStatus(Status.ACTIVE);
        toCard.setBalance(100L);

        Mockito.when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromCard));
        Mockito.when(cardRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toCard));

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(50L);

        BankRestRuntimeException ex = assertThrows(BankRestRuntimeException.class, () -> transferService.transfer(transferRequest));
        assertEquals(BankRestErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void transfer_fails_when_from_card_not_active() {
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        UserService userService = Mockito.mock(UserService.class);
        TransferServiceImpl transferService = new TransferServiceImpl(cardRepository, userService);

        Client client = new Client();
        client.setId(10L);
        Mockito.when(userService.getCurrentUser()).thenReturn(client);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setOwner(client);
        fromCard.setStatus(Status.BLOCKED);
        fromCard.setBalance(5000L);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setOwner(client);
        toCard.setStatus(Status.ACTIVE);
        toCard.setBalance(100L);

        Mockito.when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromCard));
        Mockito.when(cardRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toCard));

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(50L);

        BankRestRuntimeException ex = assertThrows(BankRestRuntimeException.class, () -> transferService.transfer(transferRequest));
        assertEquals(BankRestErrorCode.BAD_REQUEST, ex.getErrorCode());
    }
}

