package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    /**
     * Сохранение пользователя.
     */
    Client save(Client client);

    /**
     * Создание нового пользователя.
     */
    Client create(Client client);

    /**
     * Создание пользователя.
     */
    UserDto createUser(UserRequest request);

    /**
     * Обновление пользователя.
     */
    UserDto updateUser(Long id, UserRequest request);

    /**
     * Получение пользователя по id.
     */
    UserDto getUserById(Long id);

    /**
     * Постраничный список всех пользователей.
     */
    Page<UserDto> listUsers(Pageable pageable);

    /**
     * Удаляет пользователя и связанные с ним карты.
     */
    void deleteUser(Long id);

    /**
     * Получение пользователя по username.
     */
    Client getByUsername(String username);

    /**
     * {@link UserDetailsService} для Spring Security.
     */
    UserDetailsService userDetailsService();

    /**
     * Получение текущего аутентифицированного пользователя.
     */
    Client getCurrentUser();
}
