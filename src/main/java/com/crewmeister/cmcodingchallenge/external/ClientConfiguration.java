package com.crewmeister.cmcodingchallenge.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Value("${client.retry.attempts}")
    private int retryNumber;

    @Value("${client.retry.timeout.milliseconds}")
    private int timeoutMs;
}
