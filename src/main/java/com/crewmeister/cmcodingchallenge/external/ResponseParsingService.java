package com.crewmeister.cmcodingchallenge.external;

import java.io.InputStream;
import java.util.List;

public interface ResponseParsingService {

    /**
     * Parse stream response from external service to ExchangingRateList
     * @param stream response body
     * @return exchanging rate list
     */
    List<ExchangingRateList> parse(InputStream stream);
}
