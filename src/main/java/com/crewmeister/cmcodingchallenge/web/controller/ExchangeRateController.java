package com.crewmeister.cmcodingchallenge.web.controller;

import com.crewmeister.cmcodingchallenge.constant.AppConstants;
import com.crewmeister.cmcodingchallenge.web.dto.CurrencyConversionRate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@RequestMapping(value = AppConstants.V1, produces = "application/json")
public interface ExchangeRateController {

    @GetMapping("/rates")
    @ResponseBody
    Iterable<CurrencyConversionRate> getAllRates(@RequestParam(required = false) String date, @RequestParam(required = false) String cur,
                                                 @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "30") int size);

    @GetMapping("/currencies")
    @ResponseBody
    Collection<String> getCurrencies();

    @GetMapping("/convert")
    @ResponseBody
    Double getConvertedAmount(@RequestParam String cur, @RequestParam Double amount, @RequestParam(required = false) String date);
}
