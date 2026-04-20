package com.example.bankcards.service.impl;

import com.example.bankcards.constant.BankRestErrorCode;
import com.example.bankcards.constant.Role;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.entity.Client;
import com.example.bankcards.exception.BankRestRuntimeException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Реализация операций над пользователями и интеграция со Spring Security.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final UserMapper userMapper;

    @Override
    public Client save(Client client) {
        return userRepository.save(client);
    }

    @Override
    public Client create(Client client) {
        List<Client> users = userRepository.findByUsernameOrEmail(client.getUsername(), client.getEmail());
        if (!users.isEmpty()) {
            boolean usernameTaken = users.stream()
                    .anyMatch(u -> u.getUsername().equals(client.getUsername()));
            boolean emailTaken = users.stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(client.getEmail()));
            if (usernameTaken) {
                throw new BankRestRuntimeException(BankRestErrorCode.CONFLICT, "Пользователь с таким именем уже существует");
            }
            if (emailTaken) {
                throw new BankRestRuntimeException(BankRestErrorCode.CONFLICT, "Пользователь с таким email уже существует");
            }
        }
        return save(client);
    }

    @Override
    public UserDto createUser(UserRequest request) {
        Client newClient = new Client();
        newClient.setUsername(request.getUsername().trim());
        newClient.setEmail(request.getEmail().trim());
        newClient.setPassword(passwordEncoder.encode(request.getPassword()));
        newClient.setRole(request.getRole());
        return userMapper.toDto(create(newClient));
    }

    @Override
    public UserDto updateUser(Long id, UserRequest request) {
        if (!hasAnyUpdate(request)) {
            throw new BankRestRuntimeException(BankRestErrorCode.BAD_REQUEST, "Укажите хотя бы одно поле для обновления");
        }
        Client client = userRepository.findById(id)
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Пользователь не найден"));

        if (StringUtils.hasText(request.getUsername())) {
            String username = request.getUsername().trim();

            if (!username.equals(client.getUsername()) && userRepository.existsByUsername(username)) {
                throw new BankRestRuntimeException(BankRestErrorCode.CONFLICT, "Пользователь с таким именем уже существует");
            }
            client.setUsername(username);
        }

        if (StringUtils.hasText(request.getEmail())) {
            String email = request.getEmail().trim();

            if (!email.equalsIgnoreCase(client.getEmail()) && userRepository.existsByEmail(email)) {
                throw new BankRestRuntimeException(BankRestErrorCode.CONFLICT, "Пользователь с таким email уже существует");
            }
            client.setEmail(email);
        }

        if (StringUtils.hasText(request.getPassword())) {
            client.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null) {
            checkRole(client, request.getRole());
            client.setRole(request.getRole());
        }
        return userMapper.toDto(save(client));
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Пользователь не найден"));
    }

    @Override
    public Page<UserDto> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        Client current = getCurrentUser();

        if (current.getId().equals(id)) {
            throw new BankRestRuntimeException(BankRestErrorCode.FORBIDDEN, "Нельзя удалить свою учётную запись");
        }
        Client client = userRepository.findById(id)
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Пользователь не найден"));

        if (client.getRole() == Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new BankRestRuntimeException(BankRestErrorCode.FORBIDDEN, "Нельзя удалить последнего администратора");
        }
        cardRepository.deleteByOwnerId(id);
        userRepository.deleteById(id);
    }

    @Override
    public Client getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    @Override
    public Client getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new UsernameNotFoundException("Пользователь не аутентифицирован");
        }
        return getByUsername(auth.getName());
    }

    private static boolean hasAnyUpdate(UserRequest request) {
        return StringUtils.hasText(request.getUsername())
                || StringUtils.hasText(request.getEmail())
                || StringUtils.hasText(request.getPassword())
                || request.getRole() != null;
    }

    private void checkRole(Client client, Role role) {
        if (client.getRole() != Role.ADMIN || role == Role.ADMIN) {
            return;
        }

        if (userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new BankRestRuntimeException(BankRestErrorCode.FORBIDDEN, "Нельзя снять роль администратора с последнего администратора");
        }
    }
}
