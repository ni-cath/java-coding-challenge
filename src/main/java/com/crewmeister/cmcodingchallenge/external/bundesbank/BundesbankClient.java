package com.crewmeister.cmcodingchallenge.external.bundesbank;

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
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static com.crewmeister.cmcodingchallenge.util.DateParsingHelper.isNullOrFutureDate;

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
            if (exchangingRateList.getDate().toLocalDate().isEqual(LocalDate.now())) {
                return exchangingRateList;
            }

            // otherwise use yesterday quotes
            if (exchangingRateList.getDate().toLocalDate().isEqual(LocalDate.now().minusDays(1))) {
                return exchangingRateList;
            }
        }

        throw new BundesbankClientException("No actual quotes received");
    }

    @Override
    public List<ExchangingRateList> getCurrencyQuotesFromEarliestDate() {
        return sendRequest(buildAllDataRequest());
    }

    private HttpGet buildDailyRequest() {
        return buildAllDataRequest(true);
    }

    private HttpGet buildAllDataRequest() {
        return buildAllDataRequest(false);
    }

    private HttpGet buildAllDataRequest(boolean isDaily) {
        HttpGet request = new HttpGet(configuration.getServiceUrl() + configuration.getEndpoint());
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US");

        String startDate = isDaily || isNullOrFutureDate(configuration.getStartDate()) ? DateParsingHelper.getYesterdayDateAsString() : configuration.getStartDate();

        URIBuilder uriBuilder = new URIBuilder(request.getURI())
                .addParameter("format", "application_json")
                .addParameter("detail", "dataonly")
                .addParameter("startPeriod", startDate);

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

            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode != HttpStatus.SC_OK) {
                throw new BundesbankClientException("Receive unsuccessful response from Bundesbank service: " + responseBody);
            }

            return parse(responseBody);

        } catch (IOException e) {
            throw new BundesbankClientException("Error occurred when trying to retrieve currency quotes from Bundesbank service: " + e.getMessage());
        }
    }

    private List<ExchangingRateList> parse(String json) throws JsonProcessingException {
        return responseParsingService.parse(json);
    }
}
