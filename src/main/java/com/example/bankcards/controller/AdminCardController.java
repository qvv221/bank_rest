package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Управление картами администратором.
 */
@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "AdminCardController", description = "Управление картами администратором")
public class AdminCardController {

    private final CardService cardService;

    @Operation(description = "Получить карты пользователей")
    @GetMapping
    @Parameters({
            @Parameter(name = "page",
                    in = ParameterIn.QUERY,
                    description = "Номер страницы",
                    schema = @Schema(type = "integer", minimum = "0")),
            @Parameter(name = "size",
                    in = ParameterIn.QUERY,
                    description = "Размер страницы",
                    schema = @Schema(type = "integer", minimum = "0")),
            @Parameter(name = "sort",
                    in = ParameterIn.QUERY,
                    description = "Сортировка в формате: поле, (asc|desc)",
                    array = @ArraySchema(schema = @Schema(type = "string", example = "id,desc")))
    })
    public Page<CardDto> getAll(@ParameterObject Pageable pageable) {
        return cardService.getAll(pageable);
    }

    @Operation(description = "Получить карту по идентификатору")
    @GetMapping("/{id}")
    public CardDto getById(@PathVariable Long id) {
        return cardService.getById(id);
    }

    @Operation(description = "Создать карту для пользователя")
    @PostMapping
    public CardDto create(@Valid @RequestBody CreateCardRequest request) {
        return cardService.createCard(request);
    }

    @Operation(description = "Заблокировать карту по идентификатору")
    @PutMapping("/{id}/block")
    public void block(@PathVariable Long id) {
        cardService.blockById(id);
    }

    @Operation(description = "Активировать карту по идентификатору")
    @PutMapping("/{id}/activate")
    public void activate(@PathVariable Long id) {
        cardService.activateById(id);
    }

    @Operation(description = "Удалить карту по идентификатору")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        cardService.deleteById(id);
    }
}
