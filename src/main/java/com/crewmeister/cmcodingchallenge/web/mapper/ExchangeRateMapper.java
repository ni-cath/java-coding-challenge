package com.crewmeister.cmcodingchallenge.web.mapper;

import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.web.dto.ExchangeRateDto;

public class ExchangeRateMapper {
    public static ExchangeRateDto toDto(ExchangeRate exchangeRate) {
        return ExchangeRateDto.builder()
                .conversionRate(exchangeRate.getRate())
                .date(exchangeRate.getDate())
                .currencyCode(exchangeRate.getCurrency().getCurrencyCode())
                .build();
    }
}
