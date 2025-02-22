package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;

import java.util.Optional;
import java.util.Set;

public interface CurrencyService {
    /**
     * Get the set of all available currencies from DB
     * @return a set of currencies or empty set
     */
    Set<String> getAllAvailableCurrencyCodes();

    /**
     * Get currency by code
     * @param currencyCode currency code
     * @return optional value
     */
    Optional<Currency> getCurrency(String currencyCode);

    /**
     * Try to get currency by code, crate it if necessary
     * @param currencyCode currency code
     * @return created or found value
     */
    Currency getOrCreateCurrency(String currencyCode);

    /**
     * Save all currencies
     * @param currencies currencies to save
     */
    void saveAll(Iterable<Currency> currencies);
}
