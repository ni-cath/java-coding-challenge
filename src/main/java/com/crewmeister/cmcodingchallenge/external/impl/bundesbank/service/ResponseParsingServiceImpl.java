package com.crewmeister.cmcodingchallenge.external.impl.bundesbank.service;

import com.crewmeister.cmcodingchallenge.external.ExchangingRateList;
import com.crewmeister.cmcodingchallenge.external.ResponseParsingService;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ResponseParsingServiceImpl implements ResponseParsingService {

    private final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    @Override
    public List<ExchangingRateList> parse(InputStream stream) {
        return List.of();
    }
}
