package com.crewmeister.cmcodingchallenge.service.impl;

import com.crewmeister.cmcodingchallenge.common.exception.InvalidDateException;
import com.crewmeister.cmcodingchallenge.common.exception.ExchangeRateNotFoundException;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.CurrencyConversionService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.helper.BigDecimalRoundingHelper;
import com.crewmeister.cmcodingchallenge.helper.DateParsingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final Logger logger = LoggerFactory.getLogger(CurrencyConversionServiceImpl.class);

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public CurrencyConversionServiceImpl(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    public Double convert(String targetCurrency, Double amount, LocalDate date) {
        if (DateParsingHelper.isNullOrFutureDate(date)) {
            logger.warn("Invalid request date param {}", date);
            throw new InvalidDateException("Unable to convert money at exchange rate for invalid date: " + date);
        }

        Optional<ExchangeRate> rateOpt = exchangeRateService.getExchangeRate(date, targetCurrency);

        if (rateOpt.isEmpty()) {
            throw new ExchangeRateNotFoundException("Exchange rate not found for " + targetCurrency + " on " + date);
        }

        BigDecimal convertedAmount = convert(BigDecimal.valueOf(amount), BigDecimal.valueOf(rateOpt.get().getRate()));
        return BigDecimalRoundingHelper.round(convertedAmount).doubleValue();
    }

    private BigDecimal convert(BigDecimal amount, BigDecimal rate) {
        return amount.divide(rate, BigDecimalRoundingHelper.getMathContext());
    }
}
