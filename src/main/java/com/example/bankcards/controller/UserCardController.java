package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Операции пользователя со своими картами.
 */
@RestController
@RequestMapping("/api/user/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Tag(name = "UserCardController", description = "Операции пользователя со своими картами")
public class UserCardController {

    private final CardService cardService;

    @Operation(description = "Получить все карты пользователя")
    @GetMapping
    public List<CardDto> getAll() {
        return cardService.getAll();
    }

    @Operation(description = "Получить карту пользователя")
    @GetMapping("/{id}")
    public CardDto getById(@PathVariable Long id) {
        return cardService.getById(id);
    }

    @Operation(description = "Получить баланс пользователя")
    @GetMapping("/{id}/balance")
    public Long getBalanceById(@PathVariable Long id) {
        return cardService.getBalanceById(id);
    }

    @Operation(description = "Заблокировать карту пользователя")
    @PostMapping("/{id}/block")
    public void requestBlockById(@PathVariable Long id) {
        cardService.requestBlockById(id);
    }
}
