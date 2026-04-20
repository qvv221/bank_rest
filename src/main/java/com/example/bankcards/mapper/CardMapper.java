package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardNumberCrypto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

/**
 * Маппер для {@link Card}.
 */
@Mapper(componentModel = "spring")
public abstract class CardMapper {

    protected String cardNumberKey;

    @Value("${crypto.cardNumberKey}")
    void setCardNumberKey(String cardNumberKey) {
        this.cardNumberKey = cardNumberKey;
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "number", source = "number", qualifiedByName = "numberToMasked")
    @Mapping(target = "owner", source = "owner.username")
    public abstract CardDto toDto(Card entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "owner", ignore = true)
    public abstract Card toEntity(CardDto dto);

    @Named("numberToMasked")
    public String numberToMasked(String value) {
        return CardNumberCrypto.numberToMasked(value, cardNumberKey);
    }
}
