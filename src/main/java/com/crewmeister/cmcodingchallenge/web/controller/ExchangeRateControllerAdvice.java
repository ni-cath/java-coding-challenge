package com.crewmeister.cmcodingchallenge.web.controller;

import com.crewmeister.cmcodingchallenge.exception.InvalidDateParamValueException;
import com.crewmeister.cmcodingchallenge.exception.NotFoundCurrencyException;
import com.crewmeister.cmcodingchallenge.exception.NotFoundRateException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface ExchangeRateControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDateParamValueException.class)
    String handleInvalidDateParamValue(Exception e);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundCurrencyException.class)
    String handleNotFoundCurrencyValue(Exception e);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundRateException.class)
    String handleNotFoundRateValue(Exception e);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    String handleIllegalArgumentException(Exception e);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    String handleRuntimeException(Exception e);
}
