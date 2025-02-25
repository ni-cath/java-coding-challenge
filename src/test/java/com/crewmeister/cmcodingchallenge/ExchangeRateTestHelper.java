package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.util.List;

public class ExchangeRateTestHelper {

    public static Currency getUSD() {
        return Currency.builder().currencyCode("USD").build();
    }

    public static Currency getCZK() {
        return Currency.builder().currencyCode("CZK").build();
    }

    public static ExchangeRate getUSDRate(double rate, LocalDate date) {
        return ExchangeRate.builder()
                .currency(getUSD())
                .rate(rate)
                .date(date)
                .build();
    }

    public static ExchangeRate getCZKRate(double rate, LocalDate date) {
        return ExchangeRate.builder()
                .currency(getCZK())
                .rate(rate)
                .date(date)
                .build();
    }

    public static Page<ExchangeRate> getPageOfUSDExchangeRatesAroundDate(LocalDate date) {
        List<ExchangeRate> exchangeRatesList = List.of(
                getUSDRate(0.95, date.minusDays(4)),
                getUSDRate(0.949, date.minusDays(3)),
                getUSDRate(0.952, date.minusDays(2)),
                getUSDRate(0.951, date.minusDays(1)),
                getUSDRate(0.951, date)
        );
        return new PageImpl<>(exchangeRatesList);
    }
}
