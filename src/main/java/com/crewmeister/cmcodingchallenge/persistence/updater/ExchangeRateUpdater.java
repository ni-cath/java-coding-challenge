package com.crewmeister.cmcodingchallenge.persistence.updater;

import com.crewmeister.cmcodingchallenge.config.ExchangeRateUpdaterConfig;
import com.crewmeister.cmcodingchallenge.external.dto.CurrencyQuote;
import com.crewmeister.cmcodingchallenge.external.dto.ExchangeRateResponse;
import com.crewmeister.cmcodingchallenge.external.client.ExternalQuoteClient;
import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import com.crewmeister.cmcodingchallenge.helper.DateParsingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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

    private final ExchangeRateUpdaterConfig configuration;

    private final Map<String, Currency> currencyMap = new HashMap<>();

    @Autowired
    public ExchangeRateUpdater(ExternalQuoteClient externalQuoteClient,
                               ExchangeRateService exchangeRateService,
                               CurrencyService currencyService, ExchangeRateUpdaterConfig configuration) {
        this.externalQuoteClient = externalQuoteClient;
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
        this.configuration = configuration;
    }

    @Async
    @Scheduled(cron = "${client.data.update.cron.expression}")
    public void updateDailyExchangeRates() {
        logger.info("Start to update daily currency quotes");
        ExchangeRateResponse receivedList = externalQuoteClient.getCurrencyQuotesForToday();
        logger.info("Receive data from external service");
        updateExchangeRates(receivedList);
        logger.info("Finish updating daily currency quotes");
    }

    public void updateAllExchangeRates() {
        logger.info("Start to update all currency quotes");

        LocalDate startDate = DateParsingHelper.parseDate(configuration.getEarliestDate());
        LocalDate endDate = LocalDate.now();
        List<ExchangeRateResponse> exchangeRateResponses = externalQuoteClient.getCurrencyQuotesForInterval(startDate, endDate);

        logger.info("Receive {} batches from external service for {} to {}", exchangeRateResponses.size(), startDate, endDate);
        for(ExchangeRateResponse exchangeRateResponse : exchangeRateResponses) {
            updateExchangeRates(exchangeRateResponse);
        }
        logger.info("Finish updating all currency quotes");
    }

    private void updateExchangeRates(ExchangeRateResponse exchangeRateResponse) {
        List<ExchangeRate> exchangeRatesForUpdate = new ArrayList<>(exchangeRateResponse.getCurrencyQuotes().size());

        for(CurrencyQuote quote: exchangeRateResponse.getCurrencyQuotes()) {
            exchangeRatesForUpdate.add(
                    ExchangeRate.builder()
                            .rate(quote.getExchangeRate())
                            .date(exchangeRateResponse.getDate())
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
