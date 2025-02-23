package com.crewmeister.cmcodingchallenge.web.controller.impl;

import com.crewmeister.cmcodingchallenge.web.controller.ExchangeRateControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExchangeRateControllerAdviceImpl implements ExchangeRateControllerAdvice {

    @Override
    public String handleInvalidDateParamValue(Exception e) {
        return "Bad request: " + e.getMessage();
    }

    @Override
    public String handleNotFoundCurrencyValue(Exception e) {
        return "Currency was not found: " + e.getMessage();
    }

    @Override
    public String handleNotFoundRateValue(Exception e) {
        return "Rate was not found (no rates for weekends or public holidays): " + e.getMessage();
    }

    @Override
    public String handleIllegalArgumentException(Exception e) {
        return "Bad request: " + e.getMessage();
    }

    @Override
    public String handleRuntimeException(Exception e) {
        return "Internal server error: " + e.getMessage();
    }
}
