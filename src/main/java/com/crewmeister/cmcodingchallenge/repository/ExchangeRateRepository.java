package com.crewmeister.cmcodingchallenge.repository;

import com.crewmeister.cmcodingchallenge.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    List<String> findDistinctCurrencyByDate(LocalDate date);

    List<ExchangeRate> findByDate(LocalDate date);

    Optional<ExchangeRate> findByCurrencyAndDate(String currency, LocalDate date);
}
