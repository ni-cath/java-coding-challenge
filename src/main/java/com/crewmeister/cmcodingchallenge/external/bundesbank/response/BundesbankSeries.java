package com.crewmeister.cmcodingchallenge.external.bundesbank.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class BundesbankSeries {

    @JsonProperty("observations")
    private Map<String, Object[]> observations;
}
