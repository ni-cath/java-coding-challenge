package com.crewmeister.cmcodingchallenge.repository;

import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCurrencyCode(String currencyCode);
}
