package com.crewmeister.cmcodingchallenge.persistence.updater;

import com.crewmeister.cmcodingchallenge.external.CurrencyQuote;
import com.crewmeister.cmcodingchallenge.external.ExchangingRateList;
import com.crewmeister.cmcodingchallenge.external.ExternalQuoteClient;
import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.util.DateParsingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableAsync
@Component
public class ExchangeRateUpdater {

    private final Logger logger = LoggerFactory.getLogger(ExchangeRateUpdater.class);

    private final ExternalQuoteClient externalQuoteClient;

    private final ExchangeRateService exchangeRateService;

    private final CurrencyService currencyService;

    private final ExchangeRateUpdaterConfiguration configuration;

    private final Map<String, Currency> currencyMap = new HashMap<>();

    @Autowired
    public ExchangeRateUpdater(ExternalQuoteClient externalQuoteClient,
                               ExchangeRateService exchangeRateService,
                               CurrencyService currencyService, ExchangeRateUpdaterConfiguration configuration) {
        this.externalQuoteClient = externalQuoteClient;
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
        this.configuration = configuration;
    }

    @Async
    @Scheduled(cron = "0 * * * * *") //"2025-02-20T14:58:37.626Z",
    public void updateDailyExchangeRates() {
        logger.info("Start to update daily currency quotes");
        ExchangingRateList receivedList = externalQuoteClient.getCurrencyQuotesForToday();
        logger.info("Receive data from external service");
        updateExchangeRates(receivedList);
        logger.info("Finish updating daily currency quotes");
    }

    public void updateAllExchangeRates() {
        logger.info("Start to update all currency quotes");

        LocalDate startDate = DateParsingHelper.parseDate(configuration.getEarliestDate());
        LocalDate endDate = LocalDate.now();
        List<ExchangingRateList> exchangingRateLists = externalQuoteClient.getCurrencyQuotesForInterval(startDate, endDate);

        logger.info("Receive data from external service for {} to {}", startDate, endDate);
        for(ExchangingRateList exchangingRateList: exchangingRateLists) {
            updateExchangeRates(exchangingRateList);
        }
        logger.info("Finish updating all currency quotes");
    }

    private void updateExchangeRates(ExchangingRateList exchangingRateList) {
        LocalDateTime date = exchangingRateList.getDate();

        List<ExchangeRate> exchangeRatesForUpdate = new ArrayList<>(exchangingRateList.getCurrencyQuotes().size());

        for(CurrencyQuote quote: exchangingRateList.getCurrencyQuotes()) {
            exchangeRatesForUpdate.add(
                    ExchangeRate.builder()
                            .rate(quote.getExchangeRate())
                            .date(date)
                            .currency(getOrCreateCurrency(quote.getCurrencyCode()))
                            .build()
            );
        }

        exchangeRateService.saveAll(exchangeRatesForUpdate);
    }

    private Currency getOrCreateCurrency(String currencyCode) {
        if (currencyMap.containsKey(currencyCode)) {
            return currencyMap.get(currencyCode);
        }

        Currency currency = Currency.builder().currencyCode(currencyCode).build();

        currencyMap.put(currencyCode, currency);
        currencyService.save(currency);

        return currency;
    }
}
