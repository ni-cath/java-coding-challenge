package com.crewmeister.cmcodingchallenge.service.impl;

import com.crewmeister.cmcodingchallenge.exception.InvalidDateParamValueException;
import com.crewmeister.cmcodingchallenge.exception.NotFoundRateException;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.ConvertService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.util.BigDecimalRoundingHelper;
import com.crewmeister.cmcodingchallenge.util.DateParsingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ConvertServiceImpl implements ConvertService {

    private final Logger logger = LoggerFactory.getLogger(ConvertServiceImpl.class);

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ConvertServiceImpl(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    public Double convert(String targetCurrency, Double amount, String date) {
        LocalDate parsedDate = LocalDate.now();

        if (date != null && !date.isBlank()) {
            parsedDate = DateParsingHelper.parseDate(date);
        }

        if (DateParsingHelper.isNullOrFutureDate(parsedDate)) {
            logger.warn("Invalid request date param {}", date);
            throw new InvalidDateParamValueException("Unable to convert money at exchange rate for invalid date: " + date);
        }

        BigDecimal convertedAmount = convert(targetCurrency, new BigDecimal(amount), parsedDate);
        return BigDecimalRoundingHelper.round(convertedAmount).doubleValue();
    }

    private BigDecimal convert(String targetCurrency, BigDecimal amount, LocalDate date) {
        Optional<ExchangeRate> rateOpt = exchangeRateService.getExchangeRates(date, targetCurrency);

        if (rateOpt.isEmpty()) {
            throw new NotFoundRateException("Exchange rate not found for " + targetCurrency + " on " + date);
        }

        return amount.divide(BigDecimal.valueOf(rateOpt.get().getRate()), BigDecimalRoundingHelper.getMathContext());
    }
}
