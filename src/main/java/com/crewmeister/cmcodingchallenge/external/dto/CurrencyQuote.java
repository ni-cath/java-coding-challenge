package com.crewmeister.cmcodingchallenge.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CurrencyQuote {

    String currencyCode;

    Double exchangeRate;
}
