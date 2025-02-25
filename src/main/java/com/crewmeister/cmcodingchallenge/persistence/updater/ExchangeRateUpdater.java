package com.crewmeister.cmcodingchallenge.persistence.updater;

import com.crewmeister.cmcodingchallenge.common.exception.InvalidDateException;
import com.crewmeister.cmcodingchallenge.config.ExchangeRateUpdaterConfig;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@EnableAsync
@Component
public class ExchangeRateUpdater {

    private final Logger logger = LoggerFactory.getLogger(ExchangeRateUpdater.class);

    private final ExternalQuoteClient externalQuoteClient;

    private final ExchangeRateService exchangeRateService;

    private final CurrencyService currencyService;

    private final ExchangeRateUpdaterConfig configuration;

    private final Map<String, Currency> currencyMap = new ConcurrentHashMap<>();

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
        logger.info("Starting to update daily currency quotes");
        ExchangeRateResponse receivedList = externalQuoteClient.getCurrencyQuotesForToday();
        logger.info("Received data from external service");
        updateExchangeRates(receivedList);
        logger.info("Finished updating daily currency quotes");
    }

    public void updateAllExchangeRates() {
        logger.info("Starting to update all currency quotes");

        LocalDate startDate = DateParsingHelper.parseDate(configuration.getEarliestDate())
                .orElseThrow(() -> new InvalidDateException((String.format("Provided earliest date %s has invalid format", configuration.getEarliestDate()))));
        LocalDate endDate = LocalDate.now();
        List<ExchangeRateResponse> exchangeRateResponses = externalQuoteClient.getCurrencyQuotesForInterval(startDate, endDate);

        logger.info("Receive {} batches from external service for {} to {}", exchangeRateResponses.size(), startDate, endDate);
        exchangeRateResponses.forEach(this::updateExchangeRates);
        logger.info("Finish updating all currency quotes");
    }

    private void updateExchangeRates(ExchangeRateResponse exchangeRateResponse) {
        List<ExchangeRate> exchangeRatesForUpdate = exchangeRateResponse.getCurrencyQuotes().stream()
                .map(quote -> ExchangeRate.builder()
                        .rate(quote.getExchangeRate())
                        .date(exchangeRateResponse.getDate())
                        .currency(getOrCreateCurrency(quote.getCurrencyCode()))
                        .build())
                .collect(Collectors.toList());

        exchangeRateService.saveAll(exchangeRatesForUpdate);
    }

    private Currency getOrCreateCurrency(String currencyCode) {
        return currencyMap.computeIfAbsent(currencyCode, code -> {
            Currency currency = Currency.builder().currencyCode(code).build();
            currencyService.save(currency);
            return currency;
        });
    }
}
