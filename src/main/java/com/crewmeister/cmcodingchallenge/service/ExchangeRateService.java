package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface ExchangeRateService {

    /**
     * Get the currency exchange rate for a specific day
     * @param date          particular date for exchanging rates
     * @param currencyCode  currency code
     * @return optional value
     */
    Optional<ExchangeRate> getExchangeRate(LocalDate date, String currencyCode);

    /**
     * Get the exchange rates filtered by date and/or currency code as a pageable object
     * @param date          specific date for exchange rates (if null get data for all dates)
     * @param currencyCode  currency code for exchange rates (if null get data for all currencies)
     * @param page          param for pageable object, page number
     * @param size          param for pageable object, size of page
     * @return optional value
     */
    Page<ExchangeRate> getExchangeRates(@NonNull int page,
                                        @NonNull int size,
                                        @Nullable LocalDate date,
                                        @Nullable String currencyCode);

    /**
     * Get the exchange rates filtered by date and/or currency code
     * @param date          specific date for exchange rates (if null get data for all dates)
     * @param currencyCode  currency code for exchange rates (if null get data for all currencies)
     * @return optional value
     */
    Collection<ExchangeRate> getExchangeRates(@Nullable LocalDate date, @Nullable String currencyCode);

    /**
     * Saving all exchanging rates to DB
     * @param exchangeRates exchange rate collection to save
     */
    void saveAll(Iterable<ExchangeRate> exchangeRates);
}
