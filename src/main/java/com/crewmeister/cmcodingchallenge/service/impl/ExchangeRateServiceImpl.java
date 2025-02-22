package com.crewmeister.cmcodingchallenge.service.impl;

import com.crewmeister.cmcodingchallenge.exception.InvalidDateParamValueException;
import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.util.DateParsingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);

    private final ExchangeRateRepository exchangeRateRepository;

    private final CurrencyService currencyService;

    @Autowired
    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository, CurrencyService currencyService) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyService = currencyService;
    }

    @Override
    public Collection<ExchangeRate> getExchangeRates(String date) {
        if (date == null || date.isBlank()) {
            return getAllExchangeRates();
        }

        LocalDateTime parsedDate = DateParsingHelper.parseDateTime(date);

        if (DateParsingHelper.isNullOrFutureDate(parsedDate)) {
            logger.warn("Invalid date param {}", date);
            throw new InvalidDateParamValueException("Unable to get exchange rates for invalid date: " + date);
        }

        return getExchangeRatesByDate(parsedDate);
    }

    @Override
    public Optional<ExchangeRate> getExchangeRates(LocalDateTime date, String currencyCode) {
        Optional<Currency> currency = currencyService.getCurrency(currencyCode);

        if (currency.isEmpty()) {
            logger.warn("Invalid currency code param {}", currencyCode);
            throw new InvalidDateParamValueException("Unable to get exchange rates for invalid currency code: " + currencyCode);
        }

        return exchangeRateRepository.findByCurrencyAndDate(currency.get(), date);
    }

    @Override
    public void saveAll(Iterable<ExchangeRate> exchangeRates) {
        exchangeRateRepository.saveAll(exchangeRates);
    }

    private Collection<ExchangeRate> getExchangeRatesByDate(LocalDateTime date) {
        return exchangeRateRepository.findByDate(date);
    }

    private Collection<ExchangeRate> getAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }
}