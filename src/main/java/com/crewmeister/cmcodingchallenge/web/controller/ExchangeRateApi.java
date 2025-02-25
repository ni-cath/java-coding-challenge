package com.crewmeister.cmcodingchallenge.web.controller;

import com.crewmeister.cmcodingchallenge.common.constants.AppConstants;
import com.crewmeister.cmcodingchallenge.web.dto.ExchangeRateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@RequestMapping(value = AppConstants.EXCHANGE_RATE, produces = "application/json")
public interface ExchangeRateApi {

    @Operation(summary = "Get all exchange rates", description = "Fetches all exchange rates from the database")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "400", description = "Invalid date string")
    @ApiResponse(responseCode = "404", description = "Rate not found")
    @ApiResponse(responseCode = "404", description = "Currency not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/rates")
    @ResponseBody
    Iterable<ExchangeRateDto> getAllRates(@RequestParam(required = false) String date, @RequestParam(required = false) String cur,
                                          @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "30") int size);

    @Operation(summary = "Get all currencies", description = "Fetches all currency codes from the database")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/currencies")
    @ResponseBody
    Collection<String> getCurrencies();

    @Operation(summary = "Get converted amount",
            description = "Converts a given amount from a specified currency to EUR based on the exchange rate. " +
                    "If a date is provided, the conversion uses the exchange rate for that day; " +
                    "otherwise, the latest available exchange rate is used")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "400", description = "Invalid date string")
    @ApiResponse(responseCode = "404", description = "Rate not found")
    @ApiResponse(responseCode = "404", description = "Currency not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/convert")
    @ResponseBody
    Double getConvertedAmount(@RequestParam String cur, @RequestParam Double amount, @RequestParam(required = false) String date);
}
