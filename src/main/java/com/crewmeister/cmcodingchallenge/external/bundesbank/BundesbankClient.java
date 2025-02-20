package com.crewmeister.cmcodingchallenge.external.bundesbank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BundesbankClient {

    @Value("${bundesbank.url}")
    private String bundesbankUrl;

    @Value("${client.retry.attempts:  #{3}}")
    private int retries;

    @Value("${client.retry.timeout.milliseconds:  #{10000}}")
    private int timeoutMs;

    public void getCurrencyQuotesForToday() {

        //todo: get from bundesbank url with retries
    }
}
