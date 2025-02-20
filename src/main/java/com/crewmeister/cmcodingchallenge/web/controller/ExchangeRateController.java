package com.crewmeister.cmcodingchallenge.web.controller;

import com.crewmeister.cmcodingchallenge.model.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateServiceImpl;
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

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rates")
    @ResponseBody
    public Collection<CurrencyConversionRate> getAllRates(@RequestParam(required = false) String date) {
        Collection<ExchangeRate> exchangeRates = exchangeRateService.getExchangeRates(date);

        return exchangeRates.stream()
                .map(rate ->
                        new CurrencyConversionRate(rate.getCurrency(), rate.getRate(), rate.getDate()))
                .collect(Collectors.toList());
    }

    @GetMapping("/currencies")
    @ResponseBody
    public Collection<String> getCurrencies() {
        return exchangeRateService.getAllAvailableCurrencies();
    }

    @GetMapping("/convert")
    @ResponseBody
    public Double getConvertedAmount(@RequestParam String cur,
                                     @RequestParam Double amount,
                                     @RequestParam(required = false) String date) {
        return exchangeRateService.convert(cur, amount, date);
    }
}
