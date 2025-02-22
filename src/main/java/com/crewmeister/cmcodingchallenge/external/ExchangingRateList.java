package com.crewmeister.cmcodingchallenge.external;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
public class ExchangingRateList {

    private LocalDateTime date;

    private Collection<CurrencyQuote> currencyQuotes;
}
