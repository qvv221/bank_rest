package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.service.UserService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Управление пользователями.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "AdminUserController", description = "Управление пользователями")
public class AdminUserController {

    private final UserService userService;

    @Operation(description = "Получить пользователей")
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
    public Page<UserDto> list(@ParameterObject Pageable pageable) {
        return userService.listUsers(pageable);
    }

    @Operation(description = "Получить пользователя по идентификатору")
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(description = "Создать нового пользователя")
    @PostMapping
    public UserDto create(@Validated(UserRequest.Create.class) @RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @Operation(description = "Обновить параметры пользователя")
    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @Operation(description = "Удалить пользователя")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
