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
     * Save currency
     * @param currency to save
     */
    void save(Currency currency);
}
