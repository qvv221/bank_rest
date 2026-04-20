package com.example.bankcards.service.impl;

import com.example.bankcards.constant.BankRestErrorCode;
import com.example.bankcards.constant.Status;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Client;
import com.example.bankcards.exception.BankRestRuntimeException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация переводов между картами текущего пользователя.
 */
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void transfer(TransferRequest request) {
        if (request.getFromCardId().equals(request.getToCardId())) {
            throw new BankRestRuntimeException(BankRestErrorCode.BAD_REQUEST, "Нельзя переводить на ту же карту");
        }
        Client current = userService.getCurrentUser();

        Card from = cardRepository.findByIdForUpdate(request.getFromCardId())
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Карта не найдена: " + request.getFromCardId()));

        Card to = cardRepository.findByIdForUpdate(request.getToCardId())
                .orElseThrow(() -> new BankRestRuntimeException(BankRestErrorCode.NOT_FOUND, "Карта не найдена: " + request.getToCardId()));

        if (!from.getOwner().getId().equals(current.getId()) || !to.getOwner().getId().equals(current.getId())) {
            throw new BankRestRuntimeException(BankRestErrorCode.FORBIDDEN, "Перевод возможен только между своими картами");
        }
        validateTransferCard(from);
        validateTransferCard(to);

        Long amount = request.getAmount();

        if (from.getBalance() < amount) {
            throw new BankRestRuntimeException(BankRestErrorCode.BAD_REQUEST, "Недостаточно средств");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        cardRepository.save(from);
        cardRepository.save(to);
    }

    private void validateTransferCard(Card card) {
        if (card.getStatus() != Status.ACTIVE) {
            throw new BankRestRuntimeException(BankRestErrorCode.BAD_REQUEST, "Карта должна быть активна");
        }
    }
}
