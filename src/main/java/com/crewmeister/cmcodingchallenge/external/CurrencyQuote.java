package com.crewmeister.cmcodingchallenge.external;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CurrencyQuote {

    String currencyCode;

    Double exchangeRate;
}
