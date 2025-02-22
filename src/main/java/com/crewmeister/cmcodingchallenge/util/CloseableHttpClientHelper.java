package com.crewmeister.cmcodingchallenge.util;

import com.crewmeister.cmcodingchallenge.configuration.ExternalServiceConfiguration;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CloseableHttpClientHelper {

    private final ExternalServiceConfiguration configuration;

    @Autowired
    public CloseableHttpClientHelper(ExternalServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    public CloseableHttpClient getHttpClientWithTimeoutAndRetries() {
        return HttpClientBuilder
                .create()
                .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(configuration.getTimeoutMs()).build())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(configuration.getRetryNumber(), true))
                .build();
    }
}
