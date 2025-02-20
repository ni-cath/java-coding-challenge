package com.crewmeister.cmcodingchallenge.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CurrencyConversionRate {

    private String currencyCode;

    private BigDecimal conversionRate;

    private LocalDate actualDate;
}
