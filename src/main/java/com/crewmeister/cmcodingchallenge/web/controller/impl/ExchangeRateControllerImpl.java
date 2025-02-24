package com.crewmeister.cmcodingchallenge.web.controller.impl;

import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.ConvertService;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.util.DateParsingHelper;
import com.crewmeister.cmcodingchallenge.web.controller.ExchangeRateController;
import com.crewmeister.cmcodingchallenge.web.controller.mapper.CurrencyConversionRateMapper;
import com.crewmeister.cmcodingchallenge.web.dto.CurrencyConversionRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collection;

@RestController
public class ExchangeRateControllerImpl implements ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    private final CurrencyService currencyService;

    private final ConvertService convertService;

    @Autowired
    public ExchangeRateControllerImpl(ExchangeRateService exchangeRateService,
                                      CurrencyService currencyService,
                                      ConvertService convertService) {
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
        this.convertService = convertService;
    }

    @Override
    public Page<CurrencyConversionRate> getAllRates(@RequestParam(required = false) String date,
                                                    @RequestParam(required = false) String cur,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "30") int size) {
        LocalDate parsedDate = DateParsingHelper.parseDate(date);
        Page<ExchangeRate> exchangeRatePage = exchangeRateService.getExchangeRates(page, size, parsedDate, cur);
        return exchangeRatePage.map(CurrencyConversionRateMapper::toDto);
    }

    @Override
    public Collection<String> getCurrencies() {
        return currencyService.getAllAvailableCurrencyCodes();
    }

    @Override
    public Double getConvertedAmount(@RequestParam String cur,
                                     @RequestParam Double amount,
                                     @RequestParam(required = false) String date) {
        LocalDate parsedDate = DateParsingHelper.parseDate(date);
        return convertService.convert(cur, amount, parsedDate);
    }
}
