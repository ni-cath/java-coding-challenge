package com.crewmeister.cmcodingchallenge.service;

import java.time.LocalDate;

public interface CurrencyConversionService {
    /**
     * get a foreign exchange amount for a given currency converted to EUR on a particular day
     * @param targetCurrency    given currency
     * @param amount            amount to convert
     * @param date              date to find the exchanging rate, by default use current date
     * @return exchanged amount from EUR to target currency
     */
    Double convert(String targetCurrency, Double amount, LocalDate date);
}
