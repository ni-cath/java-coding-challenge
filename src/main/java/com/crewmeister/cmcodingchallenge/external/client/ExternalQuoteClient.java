package com.crewmeister.cmcodingchallenge.external.client;

import com.crewmeister.cmcodingchallenge.external.dto.ExchangingRateList;

import java.time.LocalDate;
import java.util.List;

public interface ExternalQuoteClient {

    /**
     * retrieve today currency quotes for all supported currencies by external service
     * @return currency quotes for today
     */
    ExchangingRateList getCurrencyQuotesForToday();

    /**
     * retrieve all currency quotes for all supported currencies by external service for the time interval
     * @param startDate start date
     * @param endDate   end date
     * @return currency quotes from the earliest date
     */
    List<ExchangingRateList> getCurrencyQuotesForInterval(LocalDate startDate, LocalDate endDate);
}
