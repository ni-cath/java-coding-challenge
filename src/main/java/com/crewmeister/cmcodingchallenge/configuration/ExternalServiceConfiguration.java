package com.crewmeister.cmcodingchallenge.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ExternalServiceConfiguration {

    @Value("${client.quotes.service.url}")
    private String serviceUrl;

    @Value("${client.quotes.service.data.endpoint}")
    private String endpoint;

    @Value("${client.retry.attempts:  #{3}}")
    private int retryNumber;

    @Value("${client.retry.timeout.milliseconds:  #{10000}}")
    private int timeoutMs;
}
