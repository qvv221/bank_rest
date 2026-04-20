package com.example.bankcards.service;

import com.example.bankcards.constant.BankRestErrorCode;
import com.example.bankcards.constant.Role;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.entity.Client;
import com.example.bankcards.exception.BankRestRuntimeException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Test
    void create_user_password_and_returns_dto() {
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);

        when(passwordEncoder.encode("password")).thenReturn("HASH");
        when(userRepository.findByUsernameOrEmail("username", "email")).thenReturn(List.of());
        when(userRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("username");
        when(userMapper.toDto(any(Client.class))).thenReturn(userDto);

        UserService service = new UserServiceImpl(passwordEncoder, userRepository, cardRepository, userMapper);

        UserRequest request = new UserRequest();
        request.setUsername("username");
        request.setEmail("email");
        request.setPassword("password");
        request.setRole(Role.USER);

        assertEquals(userDto, service.createUser(request));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void create_user_throws_when_username_taken() {
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);

        Client client = new Client();
        client.setUsername("username");
        client.setEmail("truth_email");

        when(userRepository.findByUsernameOrEmail("username", "fake_email")).thenReturn(List.of(client));

        UserService service = new UserServiceImpl(passwordEncoder, userRepository, cardRepository, userMapper);

        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("username");
        userRequest.setEmail("fake_email");
        userRequest.setPassword("password");
        userRequest.setRole(Role.USER);

        BankRestRuntimeException ex = assertThrows(BankRestRuntimeException.class, () -> service.createUser(userRequest));
        assertEquals(BankRestErrorCode.CONFLICT, ex.getErrorCode());
    }

    @Test
    void create_user_throws_when_email_taken() {
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);

        Client client = new Client();
        client.setUsername("username");
        client.setEmail("taken_email");

        when(userRepository.findByUsernameOrEmail("new_username", "taken_email")).thenReturn(List.of(client));

        UserServiceImpl service = new UserServiceImpl(passwordEncoder, userRepository, cardRepository, userMapper);

        UserRequest request = new UserRequest();
        request.setUsername("new_username");
        request.setEmail("taken_email");
        request.setPassword("password");
        request.setRole(Role.USER);

        BankRestRuntimeException ex = assertThrows(BankRestRuntimeException.class, () -> service.createUser(request));
        assertEquals(BankRestErrorCode.CONFLICT, ex.getErrorCode());
    }

    @Test
    void update_user_fails_when_no_fields_to_update() {
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);

        UserServiceImpl service = new UserServiceImpl(passwordEncoder, userRepository, cardRepository, userMapper);

        UserRequest userRequest = new UserRequest();

        BankRestRuntimeException ex = assertThrows(BankRestRuntimeException.class, () -> service.updateUser(1L, userRequest));
        assertEquals(BankRestErrorCode.BAD_REQUEST, ex.getErrorCode());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void get_user_by_id_fails_when_user_not_found() {
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        CardRepository cardRepository = Mockito.mock(CardRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);

        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        UserServiceImpl service = new UserServiceImpl(passwordEncoder, userRepository, cardRepository, userMapper);

        BankRestRuntimeException ex = assertThrows(BankRestRuntimeException.class, () -> service.getUserById(42L));
        assertEquals(BankRestErrorCode.NOT_FOUND, ex.getErrorCode());
    }
}
