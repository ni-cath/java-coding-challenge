package com.crewmeister.cmcodingchallenge.external.service;

import com.crewmeister.cmcodingchallenge.external.dto.ExchangeRateResponse;

import java.io.InputStream;
import java.util.List;

public interface ExchangeRateParsingService {

    /**
     * Parse stream response from external service to ExchangingRateList
     * @param stream response body
     * @return exchanging rate list
     */
    List<ExchangeRateResponse> parse(InputStream stream);
}
