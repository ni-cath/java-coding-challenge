package com.crewmeister.cmcodingchallenge.persistence.updater;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ExchangeRateUpdaterConfiguration {

    @Value("${client.quotes.service.earliest.date}")
    private String earliestDate;
}
