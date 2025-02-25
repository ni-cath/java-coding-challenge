package com.crewmeister.cmcodingchallenge.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ExchangeRateUpdaterConfig {

    @Value("${client.quotes.service.earliest.date}")
    private String earliestDate;
}
