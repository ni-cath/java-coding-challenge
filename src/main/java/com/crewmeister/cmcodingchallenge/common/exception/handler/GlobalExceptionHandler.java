package com.crewmeister.cmcodingchallenge.common.exception.handler;

import com.crewmeister.cmcodingchallenge.common.exception.CurrencyNotFoundException;
import com.crewmeister.cmcodingchallenge.common.exception.ExchangeRateNotFoundException;
import com.crewmeister.cmcodingchallenge.common.exception.InvalidDateException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDateException.class)
    public String handleInvalidDateParamValue(Exception e) {
        return "Bad request: " + e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CurrencyNotFoundException.class)
    public String handleNotFoundCurrencyValue(Exception e) {
        return "Currency was not found: " + e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExchangeRateNotFoundException.class)
    public String handleNotFoundRateValue(Exception e) {
        return "Rate was not found: " + e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    public String handleIllegalArgumentException(Exception e){
        return "Bad request: " + e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(Exception e){
        return "Internal server error: " + e.getMessage();
    }
}
