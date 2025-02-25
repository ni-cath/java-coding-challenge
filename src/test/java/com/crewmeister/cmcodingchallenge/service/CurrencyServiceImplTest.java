package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.repository.CurrencyRepository;
import com.crewmeister.cmcodingchallenge.service.impl.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.crewmeister.cmcodingchallenge.ExchangeRateTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Mock
    private CurrencyRepository currencyRepository;

    private Currency usd;
    private Currency czk;

    @BeforeEach
    void setUp() {
        usd = getUSD();
        czk = getCZK();
    }

    @Test
    void getAllAvailableCurrencyCodes_GivenEmptyDB_ExpectEmptyList() {
        when(currencyRepository.findAll())
                .thenReturn(List.of());

        assertTrue(currencyService.getAllAvailableCurrencyCodes().isEmpty());
    }

    @Test
    void getAllAvailableCurrencyCodes_GivenSomeRates_ExpectListWithRates() {
        when(currencyRepository.findAll())
                .thenReturn(List.of(usd, czk));

        Set<String> availableCurrencyCodes = currencyService.getAllAvailableCurrencyCodes();

        assertTrue(availableCurrencyCodes.contains("USD"));
        assertTrue(availableCurrencyCodes.contains("CZK"));
    }

    @Test
    void getCurrency_GivenInvalidCurrencyCode_ExpectEmptyOptional() {
        when(currencyRepository.findByCurrencyCode("USD"))
                .thenReturn(Optional.empty());

        assertTrue(currencyService.getCurrency("USD").isEmpty());
    }

    @Test
    void getCurrency_GivenValidCurrencyCode_ExpectCurrency() {
        when(currencyRepository.findByCurrencyCode("USD"))
                .thenReturn(Optional.of(usd));

        Optional<Currency> currency = currencyService.getCurrency("USD");

        assertTrue(currency.isPresent());
        assertEquals("USD", currency.get().getCurrencyCode());
    }
}