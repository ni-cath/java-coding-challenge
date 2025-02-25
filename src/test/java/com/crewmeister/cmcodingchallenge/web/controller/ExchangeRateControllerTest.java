package com.crewmeister.cmcodingchallenge.web.controller;

import com.crewmeister.cmcodingchallenge.service.CurrencyConversionService;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static com.crewmeister.cmcodingchallenge.ExchangeRateTestHelper.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private CurrencyConversionService currencyConversionService;

    @Test
    void getCurrencies_GivenValidUrl_ExpectSuccessCurrenciesData() throws Exception {
        when(currencyService.getAllAvailableCurrencyCodes())
                .thenReturn(Set.of("AUD", "BGN", "BRL"));

        mockMvc.perform(get("/api/v1/exchange-rates/currencies"))
                .andExpect(status().isOk());
    }

    @Test
    void getConvertedAmount_givenValidInputData_ExpectSuccessConvertedAmount() throws Exception {
        when(currencyConversionService.convert("USD", 1000.01, LocalDate.of(2025, 1, 16)))
                .thenReturn(950.0095);

        mockMvc.perform(get("/api/v1/exchange-rates/convert?cur=USD&amount=1000.01&date=2025-01-16"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("950.0095")));
    }

    @Test
    void getConvertedAmount_givenInvalidDateFormat_ExpectBadRequestResponse() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/convert?cur=USD&amount=1000.01&date=2025/01/16"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getConvertedAmount_givenCurrencyAndAmount_ExpectSuccessConvertedAmount() throws Exception {
        when(currencyConversionService.convert("USD", 1000.01, null))
                .thenReturn(950.0095);

        mockMvc.perform(get("/api/v1/exchange-rates/convert?cur=USD&amount=1000.01"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("950.0095")));
    }

    @Test
    void getExchangeRate_givenValidAllInputParam_ExpectSuccessRates() throws Exception {
        when(exchangeRateService.getExchangeRates(0, 5, LocalDate.of(2025, 1, 15), "USD"))
                .thenReturn(getPageOfUSDExchangeRatesAroundDate(LocalDate.of(2025, 1, 15)));

        mockMvc.perform(get("/api/v1/exchange-rates/rates?date=2025-01-15&cur=USD&page=0&size=5"))
                .andExpect(status().isOk());
    }

    @Test
    void getExchangeRate_givenValidDataAndCurrencyParam_ExpectSuccessRates() throws Exception {
        when(exchangeRateService.getExchangeRates(0, 30, LocalDate.of(2025, 1, 15), "USD"))
                .thenReturn(getPageOfUSDExchangeRatesAroundDate(LocalDate.of(2025, 1, 15)));

        mockMvc.perform(get("/api/v1/exchange-rates/rates?date=2025-01-15&cur=USD"))
                .andExpect(status().isOk());
    }

    @Test
    void getExchangeRate_givenInvalidDateFormatParam_ExpectBadRequestResponse() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/rates?date=2025/01/15&cur=USD"))
                .andExpect(status().isBadRequest());
    }
}
