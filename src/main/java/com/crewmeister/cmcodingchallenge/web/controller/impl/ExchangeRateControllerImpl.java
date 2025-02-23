package com.crewmeister.cmcodingchallenge.web.controller.impl;

import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.ConvertService;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.web.controller.ExchangeRateController;
import com.crewmeister.cmcodingchallenge.web.dto.CurrencyConversionRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

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
    public Collection<CurrencyConversionRate> getAllRates(@RequestParam(required = false) String date) {
        Collection<ExchangeRate> exchangeRates = exchangeRateService.getExchangeRates(date);

        return exchangeRates.stream()
                .map(rate ->
                        CurrencyConversionRate.builder()
                                .conversionRate(rate.getRate())
                                .currencyCode(rate.getCurrency().getCurrencyCode())
                                .date(rate.getDate())
                                .build()
                ).collect(Collectors.toList());
    }

    @Override
    public Collection<String> getCurrencies() {
        return currencyService.getAllAvailableCurrencyCodes();
    }

    @Override
    public Double getConvertedAmount(@RequestParam String cur,
                                     @RequestParam Double amount,
                                     @RequestParam(required = false) String date) {
        return convertService.convert(cur, amount, date);
    }
}
