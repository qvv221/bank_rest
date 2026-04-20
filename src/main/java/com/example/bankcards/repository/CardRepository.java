package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий банковских карт {@link Card}.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Все карты владельца.
     */
    List<Card> findByOwnerId(Long ownerId);

    /**
     * Выбирает карту по id.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.id = :id")
    Optional<Card> findByIdForUpdate(@Param("id") Long id);

    /**
     * Удалить карту.
     */
    void deleteByOwnerId(Long ownerId);

    boolean existsByNumber(String number);
}
