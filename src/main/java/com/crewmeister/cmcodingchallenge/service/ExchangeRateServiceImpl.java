package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.exception.InvalidDateParamValue;
import com.crewmeister.cmcodingchallenge.model.ExchangeRate;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import com.crewmeister.cmcodingchallenge.util.BigDecimalRoundingHelper;
import com.crewmeister.cmcodingchallenge.util.DateParsingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);

    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public Set<String> getAllAvailableCurrencies() {
        return new HashSet<>(exchangeRateRepository.findDistinctCurrencyByDate(LocalDate.now()));
    }

    @Override
    public Collection<ExchangeRate> getExchangeRates(String date) {
        if (date == null || date.isBlank()) {
            return getAllExchangeRates();
        }

        LocalDate parsedDate = DateParsingHelper.parseDate(date);

        if (DateParsingHelper.isNullOrFutureDate(parsedDate)) {
            logInvalidDateWarning(date);
            throw new InvalidDateParamValue("Unable to get exchange rates for invalid date: " + date);
        }

        return getExchangeRatesByDate(parsedDate);
    }

    @Override
    public Double convert(String targetCurrency, Double amount, String date) {
        LocalDate parsedDate = LocalDate.now();

        if (date != null && !date.isBlank()) {
            parsedDate = DateParsingHelper.parseDate(date);
        }

        if (DateParsingHelper.isNullOrFutureDate(parsedDate)) {
            logInvalidDateWarning(date);
            throw new InvalidDateParamValue("Unable to convert money at exchange rate for invalid date: " + date);
        }

        BigDecimal convertedAmount = convert(targetCurrency, new BigDecimal(amount), parsedDate);
        return BigDecimalRoundingHelper.round(convertedAmount).doubleValue();
    }

    private BigDecimal convert(String targetCurrency, BigDecimal amount, LocalDate date) {
        Optional<ExchangeRate> rateOpt = exchangeRateRepository.findByCurrencyAndDate(targetCurrency, date);

        if (rateOpt.isEmpty()) {
            throw new RuntimeException("Exchange rate not found for " + targetCurrency + " on " + date);
        }

        return amount.divide(rateOpt.get().getRate(), BigDecimalRoundingHelper.getMathContext());
    }

    private Collection<ExchangeRate> getExchangeRatesByDate(LocalDate date) {
        return exchangeRateRepository.findByDate(date);
    }

    private Collection<ExchangeRate> getAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }

    private void logInvalidDateWarning(String date) {
        logger.warn("Invalid request date param {}", date);
    }
}