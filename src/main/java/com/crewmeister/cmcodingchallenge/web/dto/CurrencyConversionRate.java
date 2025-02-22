package com.crewmeister.cmcodingchallenge.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CurrencyConversionRate {

    private String currencyCode;

    private BigDecimal conversionRate;

    private LocalDateTime actualDate;
}
