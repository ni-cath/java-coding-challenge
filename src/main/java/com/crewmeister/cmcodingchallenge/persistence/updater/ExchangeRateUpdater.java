package com.crewmeister.cmcodingchallenge.persistence.updater;

import com.crewmeister.cmcodingchallenge.external.CurrencyQuote;
import com.crewmeister.cmcodingchallenge.external.ExchangingRateList;
import com.crewmeister.cmcodingchallenge.external.bundesbank.BundesbankClient;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EnableAsync
@Component
public class ExchangeRateUpdater {

    private final Logger logger = LoggerFactory.getLogger(ExchangeRateUpdater.class);

    private final BundesbankClient bundesbankClient;

    private final ExchangeRateService exchangeRateService;

    private final CurrencyService currencyService;

    @Autowired
    public ExchangeRateUpdater(BundesbankClient bundesbankClient, ExchangeRateService exchangeRateService, CurrencyService currencyService) {
        this.bundesbankClient = bundesbankClient;
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
    }

    @Async
    @Scheduled(cron = "0 * * * * *") //"2025-02-20T14:58:37.626Z",
    public void updateDailyExchangeRates() {
        logger.info("Start to update daily currency quotes");
        ExchangingRateList receivedList = bundesbankClient.getCurrencyQuotesForToday();
        logger.info("Receive data from external service");


        List<ExchangeRate> listToSave = new ArrayList<>(receivedList.getCurrencyQuotes().size());
        LocalDateTime date = receivedList.getDate();

        for(CurrencyQuote quote: receivedList.getCurrencyQuotes()) {
            listToSave.add(
                    ExchangeRate.builder()
                            .rate(quote.getExchangeRate())
                            .date(date)
                            .currency(currencyService.getOrCreateCurrency(quote.getCurrencyCode()))
                            .build()
            );
        }

        exchangeRateService.saveAll(listToSave);

        logger.info("Finish updating daily currency quotes");
    }
}
