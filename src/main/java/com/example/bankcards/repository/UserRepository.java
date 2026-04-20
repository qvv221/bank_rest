package com.example.bankcards.repository;

import com.example.bankcards.constant.Role;
import com.example.bankcards.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий пользователей {@link Client}.
 */
@Repository
public interface UserRepository extends JpaRepository<Client, Long> {

    /**
     * Получить пользователя по username.
     */
    Optional<Client> findByUsername(String username);

    /**
     * Проверить существует ли пользователь.
     */
    boolean existsByUsername(String username);

    /**
     * Проверить занят ли email.
     */
    boolean existsByEmail(String email);

    /**
     * Найти пользователей по username или email.
     */
    List<Client> findByUsernameOrEmail(String username, String email);

    /**
     * Число пользователей с указанной ролью.
     */
    long countByRole(Role role);
}
