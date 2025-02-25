package com.crewmeister.cmcodingchallenge.persistence.repository;

import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long>, JpaSpecificationExecutor<ExchangeRate> {
    Optional<ExchangeRate> findByCurrencyAndDate(Currency currency, LocalDate date);
}
