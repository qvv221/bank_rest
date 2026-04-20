package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;

public interface TransferService {

    /**
     * Перевод средств между двумя картами текущего пользователя.
     */
    void transfer(TransferRequest request);
}
