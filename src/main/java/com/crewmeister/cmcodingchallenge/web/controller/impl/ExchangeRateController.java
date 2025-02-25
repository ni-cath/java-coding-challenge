package com.crewmeister.cmcodingchallenge.web.controller.impl;

import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.CurrencyConversionService;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.helper.DateParsingHelper;
import com.crewmeister.cmcodingchallenge.web.controller.ExchangeRateApi;
import com.crewmeister.cmcodingchallenge.web.mapper.ExchangeRateMapper;
import com.crewmeister.cmcodingchallenge.web.dto.ExchangeRateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collection;

@RestController
public class ExchangeRateController implements ExchangeRateApi {

    private final ExchangeRateService exchangeRateService;

    private final CurrencyService currencyService;

    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService,
                                  CurrencyService currencyService,
                                  CurrencyConversionService currencyConversionService) {
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
        this.currencyConversionService = currencyConversionService;
    }

    @Override
    public Page<ExchangeRateDto> getAllRates(String date, String cur, int page, int size) {
        LocalDate parsedDate = DateParsingHelper.parseDate(date).orElse(null);
        Page<ExchangeRate> exchangeRatePage = exchangeRateService.getExchangeRates(page, size, parsedDate, cur);
        return exchangeRatePage.map(ExchangeRateMapper::toDto);
    }

    @Override
    public Collection<String> getCurrencies() {
        return currencyService.getAllAvailableCurrencyCodes();
    }

    @Override
    public Double getConvertedAmount(String cur, Double amount, String date) {
        LocalDate parsedDate = DateParsingHelper.parseDate(date).orElse(null);
        return currencyConversionService.convert(cur, amount, parsedDate);
    }
}
