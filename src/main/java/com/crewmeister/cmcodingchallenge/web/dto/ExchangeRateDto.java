package com.crewmeister.cmcodingchallenge.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ExchangeRateDto {

    private String currencyCode;

    private Double conversionRate;

    private LocalDate date;
}
