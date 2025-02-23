package com.crewmeister.cmcodingchallenge.repository;

import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Collection<ExchangeRate> findByDate(LocalDate date);

    Optional<ExchangeRate> findByCurrencyAndDate(Currency currency, LocalDate date);
}
