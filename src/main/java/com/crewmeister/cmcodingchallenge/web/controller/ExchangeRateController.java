package com.crewmeister.cmcodingchallenge.web.controller;

import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.ConvertService;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.util.ApiPath;
import com.crewmeister.cmcodingchallenge.web.dto.CurrencyConversionRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = ApiPath.V1, produces = "application/json")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    private final CurrencyService currencyService;

    private final ConvertService convertService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService,
                                  CurrencyService currencyService,
                                  ConvertService convertService) {
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
        this.convertService = convertService;
    }

    @GetMapping("/rates")
    @ResponseBody
    public Collection<CurrencyConversionRate> getAllRates(@RequestParam(required = false) String date) {
        Collection<ExchangeRate> exchangeRates = exchangeRateService.getExchangeRates(date);

        return exchangeRates.stream()
                .map(rate ->
                        CurrencyConversionRate.builder()
                                .conversionRate(rate.getRate())
                                .currencyCode(rate.getCurrency().getCurrencyCode())
                                .actualDate(rate.getDate())
                                .build()
                ).collect(Collectors.toList());
    }

    @GetMapping("/currencies")
    @ResponseBody
    public Collection<String> getCurrencies() {
        return currencyService.getAllAvailableCurrencyCodes();
    }

    @GetMapping("/convert")
    @ResponseBody
    public Double getConvertedAmount(@RequestParam String cur,
                                     @RequestParam Double amount,
                                     @RequestParam(required = false) String date) {
        //todo: fix Bad request: Unable to get exchange rates for invalid currency code: USD
        return convertService.convert(cur, amount, date);
    }
}
