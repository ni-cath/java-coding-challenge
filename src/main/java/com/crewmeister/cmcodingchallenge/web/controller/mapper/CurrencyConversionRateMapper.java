package com.crewmeister.cmcodingchallenge.web.controller.mapper;

import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.web.dto.CurrencyConversionRate;

public class CurrencyConversionRateMapper {
    public static CurrencyConversionRate toDto(ExchangeRate exchangeRate) {
        return CurrencyConversionRate.builder()
                .conversionRate(exchangeRate.getRate())
                .date(exchangeRate.getDate())
                .currencyCode(exchangeRate.getCurrency().getCurrencyCode())
                .build();
    }
}
