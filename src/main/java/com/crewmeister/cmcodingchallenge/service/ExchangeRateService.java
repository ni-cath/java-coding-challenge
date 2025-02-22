package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface ExchangeRateService {

    /**
     * Get exchange rates for a particular date of all exchange rates
     * @param date particular date for exchanging rates
     * @return a collection of exchange rates
     */
    Collection<ExchangeRate> getExchangeRates(String date);

    /**
     * Get exchange rates for a particular date for the currency
     * @param date          particular date for exchanging rates
     * @param currencyCode  currency code
     * @return optional value
     */
    Optional<ExchangeRate> getExchangeRates(LocalDateTime date, String currencyCode);

    /**
     * Saving all exchanging rates to DB
     * @param exchangeRates exchange rate collection to save
     */
    void saveAll(Iterable<ExchangeRate> exchangeRates);
}
