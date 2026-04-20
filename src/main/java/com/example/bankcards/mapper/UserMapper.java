package com.example.bankcards.mapper;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Client;
import org.mapstruct.Mapper;

/**
 * Маппер для {@link Client} → {@link UserDto}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(Client client);
}
