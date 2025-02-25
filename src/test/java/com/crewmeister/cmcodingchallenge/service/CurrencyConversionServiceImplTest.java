package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.ExchangeRateTestHelper;
import com.crewmeister.cmcodingchallenge.common.constants.AppConstants;
import com.crewmeister.cmcodingchallenge.common.exception.ExchangeRateNotFoundException;
import com.crewmeister.cmcodingchallenge.helper.BigDecimalRoundingHelper;
import com.crewmeister.cmcodingchallenge.service.impl.CurrencyConversionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceImplTest {
    @InjectMocks
    private CurrencyConversionServiceImpl currencyConversionService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Test
    void convert_GiveValidInputData_ExpectedRoundingResult() {
        when(exchangeRateService.getExchangeRate(LocalDate.of(2025, 1, 15), "USD"))
                .thenReturn(Optional.of(ExchangeRateTestHelper.getUSDRate(0.95, LocalDate.of(2025, 1, 15))));

        double rate = currencyConversionService.convert("USD", 1000., LocalDate.of(2025, 1, 15));

        BigDecimal amountBD = BigDecimal.valueOf(1000.);
        BigDecimal rateBD = BigDecimal.valueOf(0.95);
        BigDecimal resultBD = amountBD.divide(rateBD, AppConstants.MATH_CONTEXT);

        Double expectedResult = BigDecimalRoundingHelper.round(resultBD).doubleValue();

        assertEquals(expectedResult, rate);
    }

    @Test
    void convert_GiveInvalidDateOrCurrency_ExpectedRateNotFoundException() {
        assertThrows(ExchangeRateNotFoundException.class, () ->
                currencyConversionService.convert("USD", 1000., LocalDate.of(2025, 1, 15))
        );
    }
}