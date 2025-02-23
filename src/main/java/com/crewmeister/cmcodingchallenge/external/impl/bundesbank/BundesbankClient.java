package com.crewmeister.cmcodingchallenge.external.impl.bundesbank;

import com.crewmeister.cmcodingchallenge.configuration.ExternalServiceConfiguration;
import com.crewmeister.cmcodingchallenge.exception.BundesbankClientException;
import com.crewmeister.cmcodingchallenge.external.ExchangingRateList;
import com.crewmeister.cmcodingchallenge.external.ExternalQuoteClient;
import com.crewmeister.cmcodingchallenge.external.ResponseParsingService;
import com.crewmeister.cmcodingchallenge.util.CloseableHttpClientHelper;
import com.crewmeister.cmcodingchallenge.util.DateParsingHelper;
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

import static com.crewmeister.cmcodingchallenge.constant.AppConstants.DETAIL_PARAM;
import static com.crewmeister.cmcodingchallenge.constant.AppConstants.END_PERIOD_PARAM;
import static com.crewmeister.cmcodingchallenge.constant.AppConstants.FORMAT_PARAM;
import static com.crewmeister.cmcodingchallenge.constant.AppConstants.START_PERIOD_PARAM;


@Component
public class BundesbankClient implements ExternalQuoteClient {

    private final ExternalServiceConfiguration configuration;

    private final CloseableHttpClientHelper httpClientHelper;

    private final ResponseParsingService responseParsingService;


    @Autowired
    public BundesbankClient(ExternalServiceConfiguration configuration,
                            CloseableHttpClientHelper httpClientHelper,
                            ResponseParsingService responseParsingService) {
        this.configuration = configuration;
        this.httpClientHelper = httpClientHelper;
        this.responseParsingService = responseParsingService;
    }

    @Override
    public ExchangingRateList getCurrencyQuotesForToday() {
        List<ExchangingRateList> exchangingRateLists = sendRequest(buildDailyRequest());

        for(ExchangingRateList exchangingRateList: exchangingRateLists) {
            // use today quotes if they already published
            if (exchangingRateList.getDate().isEqual(LocalDate.now())) {
                return exchangingRateList;
            }

            // otherwise use yesterday quotes
            if (exchangingRateList.getDate().isEqual(LocalDate.now().minusDays(1))) {
                return exchangingRateList;
            }
        }

        throw new BundesbankClientException("No actual quotes received");
    }

    @Override
    public List<ExchangingRateList> getCurrencyQuotesForInterval(LocalDate startDate, LocalDate endDate) {
        return sendRequest(
                buildIntervalRequest(
                        DateParsingHelper.getDateAsString(startDate),
                        DateParsingHelper.getDateAsString(endDate)
                )
        );
    }

    private HttpGet buildDailyRequest() {
        return buildIntervalRequest(DateParsingHelper.getYesterdayDateAsString(), DateParsingHelper.getTodayDateAsString());
    }

    private HttpGet buildIntervalRequest(String startDate, String endDate) {
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
            throw new BundesbankClientException("Can not build request for the Bundesbank service: " + e.getMessage());
        }

        return request;
    }

    private List<ExchangingRateList> sendRequest(HttpGet request) {
        try(CloseableHttpClient httpClient = httpClientHelper.getHttpClientWithTimeoutAndRetries()) {
            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new BundesbankClientException("Receive unsuccessful response from Bundesbank service: " + response.getEntity().toString());
            }

            return parse(response.getEntity().getContent());

        } catch (IOException e) {
            throw new BundesbankClientException("Error occurred when trying to retrieve currency quotes from Bundesbank service: " + e.getMessage());
        }
    }

    private List<ExchangingRateList> parse(InputStream stream) throws JsonProcessingException {
        return responseParsingService.parse(stream);
    }
}
