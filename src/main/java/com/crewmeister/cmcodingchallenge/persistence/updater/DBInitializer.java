package com.crewmeister.cmcodingchallenge.persistence.updater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DBInitializer {

    private final Logger logger = LoggerFactory.getLogger(DBInitializer.class);

    private final ExchangeRateUpdater exchangeRateUpdater;

    @Autowired
    public DBInitializer(ExchangeRateUpdater exchangeRateUpdater) {
        this.exchangeRateUpdater = exchangeRateUpdater;
    }

    @PostConstruct
    public void initializeDB() {
        logger.info("Start DB initialization");
        exchangeRateUpdater.updateAllExchangeRates();
        logger.info("Saved exchanging rates to DB");
    }
}
