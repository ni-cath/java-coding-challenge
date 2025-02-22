package com.crewmeister.cmcodingchallenge.external;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ResponseParsingService {

    /**
     * Parse json response from external service to ExchangingRateList
     * @param json response body
     * @return exchanging rate list
     */
    List<ExchangingRateList> parse(String json) throws JsonProcessingException;
}
