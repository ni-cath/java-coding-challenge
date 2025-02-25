package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.common.exception.CurrencyNotFoundException;
import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.persistence.repository.ExchangeRateRepository;
import com.crewmeister.cmcodingchallenge.service.impl.ExchangeRateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.crewmeister.cmcodingchallenge.ExchangeRateTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceImplTest {

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private CurrencyService currencyService;

    private ExchangeRate usdRate;
    private Currency usd;


    @BeforeEach
    void setUp() {
        usd = getUSD();
        usdRate = getUSDRate(0.95, LocalDate.of(2025, 1, 16));
    }

    @Test
    void getExchangeRate_GivenValidDateAndCurrency_ExpectSuccessRate() {
        when(currencyService.getCurrency("USD"))
                .thenReturn(Optional.of(usd));
        when(exchangeRateRepository.findByCurrencyAndDate(usd, LocalDate.of(2025, 1, 16)))
                .thenReturn(Optional.of(usdRate));

        Optional<ExchangeRate> foundRate = exchangeRateService.getExchangeRate(LocalDate.of(2025, 1, 16), "USD");

        assertTrue(foundRate.isPresent());
        assertEquals("USD", foundRate.get().getCurrency().getCurrencyCode());
        assertEquals(0.95, foundRate.get().getRate());
    }

    @Test
    void getExchangeRate_TryToGetNonExistingRate_ExpectEmptyOptionalValue() {
        when(currencyService.getCurrency("USD"))
                .thenReturn(Optional.of(usd));
        when(exchangeRateRepository.findByCurrencyAndDate(usd, LocalDate.of(2025, 1, 16)))
                .thenReturn(Optional.empty());

        Optional<ExchangeRate> exchangeRate = exchangeRateService.getExchangeRate(LocalDate.of(2025, 1, 16), "USD");
        assertTrue(exchangeRate.isEmpty());
    }


    @Test
    void getExchangeRate_GivenInvalidCurrencyCode_ExpectNotFoundException() {
        when(currencyService.getCurrency("CZK"))
                .thenReturn(Optional.empty());

        assertThrows(CurrencyNotFoundException.class, () ->
                exchangeRateService.getExchangeRate(LocalDate.of(2024, 2, 20), "CZK")
        );
    }
}
