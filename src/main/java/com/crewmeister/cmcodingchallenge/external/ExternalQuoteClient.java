package com.crewmeister.cmcodingchallenge.external;

import java.util.List;

public interface ExternalQuoteClient {

    /**
     * retrieve today currency quotes for all supported currencies by external service
     * @return currency quotes for today
     */
    ExchangingRateList getCurrencyQuotesForToday();

    /**
     * retrieve all currency quotes for all supported currencies by external service
     * @return currency quotes from the earliest date
     */
    List<ExchangingRateList> getCurrencyQuotesFromEarliestDate();
}
