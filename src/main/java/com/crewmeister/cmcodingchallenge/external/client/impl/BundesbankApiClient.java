package com.crewmeister.cmcodingchallenge.external.client.impl;

import com.crewmeister.cmcodingchallenge.config.ExternalServiceConfig;
import com.crewmeister.cmcodingchallenge.common.exception.ExternalApiException;
import com.crewmeister.cmcodingchallenge.external.dto.ExchangeRateResponse;
import com.crewmeister.cmcodingchallenge.external.client.ExternalQuoteClient;
import com.crewmeister.cmcodingchallenge.external.service.ExchangeRateParsingService;
import com.crewmeister.cmcodingchallenge.helper.CloseableHttpClientHelper;
import com.crewmeister.cmcodingchallenge.helper.DateParsingHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import static com.crewmeister.cmcodingchallenge.common.constants.AppConstants.DETAIL_PARAM;
import static com.crewmeister.cmcodingchallenge.common.constants.AppConstants.END_PERIOD_PARAM;
import static com.crewmeister.cmcodingchallenge.common.constants.AppConstants.FORMAT_PARAM;
import static com.crewmeister.cmcodingchallenge.common.constants.AppConstants.START_PERIOD_PARAM;


@Component
public class BundesbankApiClient implements ExternalQuoteClient {

    private final ExternalServiceConfig configuration;

    private final CloseableHttpClientHelper httpClientHelper;

    private final ExchangeRateParsingService exchangeRateParsingService;


    @Autowired
    public BundesbankApiClient(ExternalServiceConfig configuration,
                               CloseableHttpClientHelper httpClientHelper,
                               ExchangeRateParsingService exchangeRateParsingService) {
        this.configuration = configuration;
        this.httpClientHelper = httpClientHelper;
        this.exchangeRateParsingService = exchangeRateParsingService;
    }

    @Override
    public ExchangeRateResponse getCurrencyQuotesForToday() {
        List<ExchangeRateResponse> exchangeRateResponses = sendRequest(buildDailyRequest());

        return exchangeRateResponses.stream()
                .filter(this::isValidQuoteForToday)
                .findFirst()
                .orElseThrow(() -> new ExternalApiException("No actual quotes received"));
    }

    @Override
    public List<ExchangeRateResponse> getCurrencyQuotesForInterval(LocalDate startDate, LocalDate endDate) {
        return sendRequest(buildRequestForDateRange(
                DateParsingHelper.getDateAsString(startDate), DateParsingHelper.getDateAsString(endDate))
        );
    }

    private boolean isValidQuoteForToday(ExchangeRateResponse response) {
        LocalDate quoteDate = response.getDate();
        return quoteDate.isEqual(LocalDate.now()) || quoteDate.isEqual(LocalDate.now().minusDays(1));
    }

    private HttpGet buildDailyRequest() {
        return buildRequestForDateRange(DateParsingHelper.getYesterdayDateAsString(), DateParsingHelper.getTodayDateAsString());
    }

    private HttpGet buildRequestForDateRange(String startDate, String endDate) {
        HttpGet request = new HttpGet(configuration.getServiceUrl() + configuration.getEndpoint());
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US");

        URIBuilder uriBuilder = new URIBuilder(request.getURI())
                .addParameter(FORMAT_PARAM, "csv")
                .addParameter(DETAIL_PARAM, "dataonly")
                .addParameter(START_PERIOD_PARAM, startDate)
                .addParameter(END_PERIOD_PARAM, endDate);

        try {
            request.setURI(uriBuilder.build());
        } catch (URISyntaxException e) {
            throw new ExternalApiException("Cannot build request for the Bundesbank service: " + e.getMessage());
        }

        return request;
    }

    private List<ExchangeRateResponse> sendRequest(HttpGet request) {
        try(CloseableHttpClient httpClient = httpClientHelper.getHttpClientWithTimeoutAndRetries()) {
            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new ExternalApiException("Unsuccessful response from Bundesbank service: " + response.getEntity().toString());
            }

            return parse(response.getEntity().getContent());

        } catch (IOException e) {
            throw new ExternalApiException("Error occurred when trying to retrieve currency quotes from Bundesbank service: " + e.getMessage());
        }
    }

    private List<ExchangeRateResponse> parse(InputStream stream) {
        return exchangeRateParsingService.parse(stream);
    }
}
