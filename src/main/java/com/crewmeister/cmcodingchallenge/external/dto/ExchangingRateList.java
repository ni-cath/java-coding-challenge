package com.crewmeister.cmcodingchallenge.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

@Data
@AllArgsConstructor
public class ExchangingRateList {

    private LocalDate date;

    private Collection<CurrencyQuote> currencyQuotes;
}
