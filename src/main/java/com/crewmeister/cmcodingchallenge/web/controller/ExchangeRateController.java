package com.crewmeister.cmcodingchallenge.web.controller;

import com.crewmeister.cmcodingchallenge.common.constants.AppConstants;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.CurrencyConversionService;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.web.dto.ExchangeRateDto;
import com.crewmeister.cmcodingchallenge.web.mapper.ExchangeRateMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping(value = AppConstants.EXCHANGE_RATE, produces = "application/json")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final CurrencyService currencyService;
    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService,
                                  CurrencyService currencyService,
                                  CurrencyConversionService currencyConversionService) {
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
        this.currencyConversionService = currencyConversionService;
    }

    @Operation(summary = "Get all exchange rates", description = "Fetches all exchange rates from the database")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "400", description = "Invalid date string")
    @ApiResponse(responseCode = "404", description = "Rate not found")
    @ApiResponse(responseCode = "404", description = "Currency not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/rates")
    @ResponseBody
    Iterable<ExchangeRateDto> getAllRates(
            @RequestParam(required = false) @Parameter(description = "Specific date in YYYY-MM-DD format", example = "2023-01-05")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @Parameter(description = "Currency code for exchange rates", example = "USD") String cur,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number for pagination result rates starts with 0, by default = 0", example = "2") int page,
            @RequestParam(defaultValue = "30") @Parameter(description = "Page size for pagination result rates, by default = 30", example = "10") int size) {
        Page<ExchangeRate> exchangeRatePage = exchangeRateService.getExchangeRates(page, size, date, cur);
        return exchangeRatePage.map(ExchangeRateMapper::toDto);
    }

    @Operation(summary = "Get all currencies", description = "Fetches all currency codes from the database")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/currencies")
    @ResponseBody
    Collection<String> getCurrencies() {
        return currencyService.getAllAvailableCurrencyCodes();
    }

    @Operation(summary = "Get converted amount",
            description = "Converts a given amount from a specified currency to EUR based on the exchange rate. " +
                    "If a date is provided, the conversion uses the exchange rate for that day; " +
                    "otherwise, the latest available exchange rate is used")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class, example = "0.9987")))
    @ApiResponse(responseCode = "400", description = "Invalid date string")
    @ApiResponse(responseCode = "404", description = "Rate not found")
    @ApiResponse(responseCode = "404", description = "Currency not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/convert")
    @ResponseBody
    Double getConvertedAmount(
            @RequestParam @Parameter(description = "Currency code for exchange rates", example = "USD") String cur,
            @RequestParam @Parameter(description = "Currency code for exchange rates", example = "14.78") Double amount,
            @RequestParam(required = false) @Parameter(description = "Specific date in YYYY-MM-DD format", example = "2023-01-05")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return currencyConversionService.convert(cur, amount, date);
    }
}
