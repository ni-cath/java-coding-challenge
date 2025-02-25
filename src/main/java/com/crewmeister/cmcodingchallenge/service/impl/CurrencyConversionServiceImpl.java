package com.crewmeister.cmcodingchallenge.service.impl;

import com.crewmeister.cmcodingchallenge.common.exception.ExchangeRateNotFoundException;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.CurrencyConversionService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.helper.BigDecimalRoundingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.crewmeister.cmcodingchallenge.common.constants.AppConstants.MATH_CONTEXT;

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
        ExchangeRate rateOpt = exchangeRateService.getExchangeRate(date, targetCurrency)
                .orElseThrow(() -> new ExchangeRateNotFoundException(String.format("Exchange rate not found for %s on %s", targetCurrency, date)));

        BigDecimal convertedAmount = convert(BigDecimal.valueOf(amount), BigDecimal.valueOf(rateOpt.getRate()));
        return BigDecimalRoundingHelper.round(convertedAmount).doubleValue();
    }

    private BigDecimal convert(BigDecimal amount, BigDecimal rate) {
        if (rate.compareTo(BigDecimal.ZERO) == 0) {
            logger.error("Division by zero: Exchange rate is zero");
            throw new ExchangeRateNotFoundException("Exchange rate cannot be zero.");
        }

        return amount.divide(rate, MATH_CONTEXT);
    }
}
