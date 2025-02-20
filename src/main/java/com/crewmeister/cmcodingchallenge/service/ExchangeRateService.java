package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.model.ExchangeRate;

import java.util.Collection;
import java.util.Set;

public interface ExchangeRateService {

    /**
     * Get the set of all available currencies from DB
     * @return a set of currencies or empty set
     */
    Set<String> getAllAvailableCurrencies();

    /**
     * Get exchange rates for a particular date or all exchange rates
     * @param date particular date for exchanging rates
     * @return a collection of exchange rates
     */
    Collection<ExchangeRate> getExchangeRates(String date);

    /**
     * get a foreign exchange amount for a given currency converted to EUR on a particular day
     * @param targetCurrency - given currency
     * @param amount    amount to convert
     * @param date      date to find the exchanging rate, by default use current date
     * @return exchanged amount from EUR to target currency
     */
    Double convert(String targetCurrency, Double amount, String date);
}
