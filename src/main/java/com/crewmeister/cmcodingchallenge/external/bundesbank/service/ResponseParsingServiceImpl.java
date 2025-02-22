package com.crewmeister.cmcodingchallenge.external.bundesbank.service;

import com.crewmeister.cmcodingchallenge.exception.BundesbankClientException;
import com.crewmeister.cmcodingchallenge.external.CurrencyQuote;
import com.crewmeister.cmcodingchallenge.external.ExchangingRateList;
import com.crewmeister.cmcodingchallenge.external.ResponseParsingService;
import com.crewmeister.cmcodingchallenge.external.bundesbank.response.*;
import com.crewmeister.cmcodingchallenge.util.DateParsingHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ResponseParsingServiceImpl implements ResponseParsingService {

    private final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public List<ExchangingRateList> parse(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        LinkedHashSet<BundesbankCurrencyValue> currencies = getCurrencyData(rootNode, objectMapper);
        List<BundesbankDataSet> dataSets = getExchangingRates(rootNode, objectMapper);

        List<ExchangingRateList> exchangingRateList = new ArrayList<>();

        for(BundesbankDataSet dataSet: dataSets) {
            exchangingRateList.add(
                    new ExchangingRateList(
                            DateParsingHelper.parseDateTime(dataSet.getValidFrom(), DT),
                            getCurrencyQuotes(dataSet.getSeries(), currencies.iterator())
                    )
            );
        }

        return exchangingRateList;
    }

    private LinkedHashSet<BundesbankCurrencyValue> getCurrencyData(JsonNode rootNode, ObjectMapper objectMapper) {
        JsonNode currencyNode = navigateToCurrencies(rootNode);
        try {
            return objectMapper.treeToValue(currencyNode, BundesbankCurrencyDimension.class).getValues();

        } catch (JsonProcessingException e) {
            throw new BundesbankClientException("Can't parse Bundesbank response: invalid currency node");
        }
    }

    /**
     * Find a currency data in response
     * @param rootNode - root node of json response
     * @return BBK_STD_CURRENCY link
     */
    private JsonNode navigateToCurrencies(JsonNode rootNode) {
        JsonNode seriesNode = rootNode.path("data")
                .path("structure")
                .path("dimensions")
                .path("series");

        for (JsonNode node : seriesNode) {
            String JSON_CURRENCY_ID = "BBK_STD_CURRENCY";
            if (JSON_CURRENCY_ID.equals(node.get("id").asText())) {
                return node;
            }
        }

        throw new BundesbankClientException("Can't parse Bundesbank response: currency node don't exist");
    }


    private List<BundesbankDataSet> getExchangingRates(JsonNode rootNode, ObjectMapper objectMapper) {
        JsonNode dataSetsNode = navigateToExchangingRates(rootNode);

        try {
            return objectMapper.treeToValue(dataSetsNode, BundesbankExchangeRateWrapper.class).getDataSets();
        } catch (JsonProcessingException e) {
            throw new BundesbankClientException("Can't parse Bundesbank response: invalid exchanging rate node");
        }
    }

    /**
     * Find an exchanging rate data in response
     * @param rootNode - root node of json response
     * @return data link
     */
    private JsonNode navigateToExchangingRates(JsonNode rootNode) {
        return rootNode.path("data");
    }

    private List<CurrencyQuote> getCurrencyQuotes(LinkedHashMap<String, BundesbankSeries> series,
                                                  Iterator<BundesbankCurrencyValue> currencyValueIterator) {
        List<CurrencyQuote> currencyQuotes = new ArrayList<>();

        for(Map.Entry<String, BundesbankSeries> dataSetEntry: series.entrySet()) {

            if (dataSetEntry.getValue().getObservations() == null) {
                continue;
            }

            for(Map.Entry<String, Object[]> rate: dataSetEntry.getValue().getObservations().entrySet()) {
                String rateValue = (String) rate.getValue()[0];

                CurrencyQuote quote = new CurrencyQuote(currencyValueIterator.next().getId(), new BigDecimal(rateValue));
                currencyQuotes.add(quote);
            }
        }

        return currencyQuotes;
    }
}
