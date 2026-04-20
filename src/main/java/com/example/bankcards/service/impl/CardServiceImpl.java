package com.example.bankcards.service.impl;

import com.example.bankcards.constant.BankRestErrorCode;
import com.example.bankcards.constant.Role;
import com.example.bankcards.constant.Status;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Client;
import com.example.bankcards.exception.BankRestRuntimeException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.CardNumberCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Операции с картами.
 */
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${crypto.cardNumberKey}")
    private String cardNumberKey;

    @Override
    @Transactional(readOnly = true)
    public Page<CardDto> getAll(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(cardMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CardDto getById(Long cardId) {
        Client current = userService.getCurrentUser();
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Карта не найдена"));

        if (current.getRole() != Role.ADMIN
                && !Objects.equals(card.getOwner().getId(), current.getId())) {
            throw new BankRestRuntimeException(BankRestErrorCode.FORBIDDEN, "Нет доступа к карте");
        }
        return cardMapper.toDto(card);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardDto> getAll() {
        Client current = userService.getCurrentUser();
        return cardRepository.findByOwnerId(current.getId()).stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @Override
    public CardDto createCard(CreateCardRequest request) {
        Client owner = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Владелец не найден"));

        String number = CardNumberCrypto.encrypt(request.getNumber(), cardNumberKey);
        if (cardRepository.existsByNumber(number)) {
            throw new BankRestRuntimeException(BankRestErrorCode.CONFLICT, "Карта с таким номером уже существует");
        }

        Card card = new Card();
        card.setOwner(owner);
        card.setNumber(number);
        card.setValidityPeriod(request.getValidityPeriod());
        card.setStatus(Status.ACTIVE);
        card.setBalance(request.getBalance());
        return cardMapper.toDto(cardRepository.save(card));
    }

    @Override
    public void requestBlockById(Long cardId) {
        Client current = userService.getCurrentUser();
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Карта не найдена"));

        if (!card.getOwner().getId().equals(current.getId())) {
            throw new BankRestRuntimeException(BankRestErrorCode.FORBIDDEN, "Нет доступа к карте");
        }
        if (card.getStatus() == Status.BLOCKED) {
            return;
        }
        if (card.getStatus() != Status.ACTIVE) {
            throw new BankRestRuntimeException(BankRestErrorCode.BAD_REQUEST,
                    "Блокировка по запросу доступна только для активной карты");
        }
        card.setStatus(Status.BLOCKED);
        cardRepository.save(card);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getBalanceById(Long cardId) {
        Client current = userService.getCurrentUser();
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Карта не найдена"));

        if (current.getRole() != Role.ADMIN && !Objects.equals(card.getOwner().getId(), current.getId())) {
            throw new BankRestRuntimeException(BankRestErrorCode.FORBIDDEN, "Нет доступа к карте");
        }
        return card.getBalance();
    }

    @Override
    public void blockById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Карта не найдена"));

        card.setStatus(Status.BLOCKED);
        cardRepository.save(card);
    }

    @Override
    public void activateById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Карта не найдена"));

        card.setStatus(Status.ACTIVE);
        cardRepository.save(card);
    }

    @Override
    public void deleteById(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Карта не найдена");
        }
        cardRepository.deleteById(cardId);
    }
}
