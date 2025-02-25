package com.crewmeister.cmcodingchallenge.helper;

import com.crewmeister.cmcodingchallenge.config.ExternalServiceConfig;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CloseableHttpClientHelper {

    private final ExternalServiceConfig configuration;

    @Autowired
    public CloseableHttpClientHelper(ExternalServiceConfig configuration) {
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
