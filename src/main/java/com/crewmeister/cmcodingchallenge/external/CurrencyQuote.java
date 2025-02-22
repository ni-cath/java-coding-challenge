package com.crewmeister.cmcodingchallenge.external;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CurrencyQuote {

    String currencyCode;

    BigDecimal exchangeRate;
}
