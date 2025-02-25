package com.crewmeister.cmcodingchallenge.service.impl;

import com.crewmeister.cmcodingchallenge.common.exception.InvalidDateException;
import com.crewmeister.cmcodingchallenge.common.exception.CurrencyNotFoundException;
import com.crewmeister.cmcodingchallenge.common.exception.ExchangeRateNotFoundException;
import com.crewmeister.cmcodingchallenge.persistence.spec.ExchangeRateSpec;
import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.persistence.repository.ExchangeRateRepository;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.helper.DateParsingHelper;
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
        return exchangeRateRepository.findByCurrencyAndDate(getCurrency(currencyCode), getValidDate(date));
    }


    @Override
    public Page<ExchangeRate> getExchangeRates(@NonNull int page, @NonNull int size, @Nullable LocalDate date, @Nullable String currencyCode) {
        LocalDate specDate = date != null ? getValidDate(date) : null;
        Currency specCurrency = currencyCode != null ? getCurrency(currencyCode) : null;

        Specification<ExchangeRate> spec = buildSpecification(specCurrency, specDate);

        if (exchangeRateRepository.count(spec) == 0) {
            throw new ExchangeRateNotFoundException(
                    String.format("Unable to get exchange rates for %s currency for %s", currencyCode, date)
            );
        }

        return exchangeRateRepository.findAll(spec, PageRequest.of(page, size));
    }

    @Override
    public void saveAll(Iterable<ExchangeRate> exchangeRates) {
        exchangeRateRepository.saveAll(exchangeRates);
    }

    private LocalDate getValidDate(LocalDate date) {
        if (DateParsingHelper.isNullOrFutureDate(date)) {
            logger.warn("Invalid date param {}", date);
            throw new InvalidDateException("Unable to get exchange rates for invalid date: " + date);
        }

        return date;
    }

    private Currency getCurrency(String currencyCode) {
        return currencyService.getCurrency(currencyCode)
                .orElseThrow(() -> {
                    logger.warn("Invalid currency code {}", currencyCode);
                    return new CurrencyNotFoundException("Unable to get exchange rates for invalid currency code: " + currencyCode);
                });
    }

    private Specification<ExchangeRate> buildSpecification(Currency currency, LocalDate date) {
        return Specification
                .where(ExchangeRateSpec.exchangeRateForCurrency(currency))
                .and(ExchangeRateSpec.exchangeRateForDate(date));
    }
}
