package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {

    /**
     * Получение карты по идентификатору.
     */
    CardDto getById(Long cardId);

    /**
     * Все карты в системе с пагинацией.
     */
    Page<CardDto> getAll(Pageable pageable);

    /**
     * Все карты текущего пользовател.
     */
    List<CardDto> getAll();

    /**
     * Создание новой карты для пользователя.
     */
    CardDto createCard(CreateCardRequest request);

    /**
     * Запрос на блокировку карты.
     */
    void requestBlockById(Long cardId);

    /**
     * Получение баланса карты по id.
     */
    Long getBalanceById(Long cardId);

    /**
     * Блокировка карты по id.
     */
    void blockById(Long cardId);

    /**
     * Активация карты по id.
     */
    void activateById(Long cardId);

    /**
     * Удаление карты по id.
     */
    void deleteById(Long cardId);
}
