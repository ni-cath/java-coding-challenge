package com.crewmeister.cmcodingchallenge.service.impl;

import com.crewmeister.cmcodingchallenge.exception.InvalidDateParamValueException;
import com.crewmeister.cmcodingchallenge.exception.NotFoundCurrencyException;
import com.crewmeister.cmcodingchallenge.exception.NotFoundRateException;
import com.crewmeister.cmcodingchallenge.persistence.ExchangeRateSpecifications;
import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.util.DateParsingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
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
    public Optional<ExchangeRate> getExchangeRate(LocalDate date, String currencyCode) {
        validateDate(date);

        return exchangeRateRepository.findByCurrencyAndDate(getCurrency(currencyCode), date);
    }


    @Override
    public Page<ExchangeRate> getExchangeRates(@NonNull int page, @NonNull int size, @Nullable LocalDate date, @Nullable String currencyCode) {
        // if the date is provided, it should be valid
        if (date != null) {
            validateDate(date);
        }

        // if the currency code is provided, it should be valid
        Currency currency = currencyCode == null ? null : getCurrency(currencyCode);
        Specification<ExchangeRate> spec = Specification
                .where(ExchangeRateSpecifications.exchangeRateForCurrency(currency))
                .and(ExchangeRateSpecifications.exchangeRateForDate(date));

        if (exchangeRateRepository.count(spec) == 0) {
            throw new NotFoundRateException("Unable to get exchange rates for " + currencyCode + " currency for " + date);
        }

        return exchangeRateRepository.findAll(spec, PageRequest.of(page, size));
    }

    @Override
    public Collection<ExchangeRate> getExchangeRates(LocalDate date, String currencyCode) {
        return List.of();
    }

    @Override
    public void saveAll(Iterable<ExchangeRate> exchangeRates) {
        exchangeRateRepository.saveAll(exchangeRates);
    }

    private void validateDate(LocalDate date) {
        if (DateParsingHelper.isNullOrFutureDate(date)) {
            logger.warn("Invalid date param {}", date);
            throw new InvalidDateParamValueException("Unable to get exchange rates for invalid date: " + date);
        }
    }

    private Currency getCurrency(String currencyCode) {
        Optional<Currency> currency = currencyService.getCurrency(currencyCode);

        if (currency.isEmpty()) {
            logger.warn("Invalid currency code param {}", currencyCode);
            throw new NotFoundCurrencyException("Unable to get exchange rates for invalid currency code: " + currencyCode);
        }

        return currency.get();
    }
}
