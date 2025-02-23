package com.crewmeister.cmcodingchallenge.external.impl.bundesbank.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.LinkedHashMap;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BundesbankDataSet {

    @JsonProperty("validFrom")
    private String validFrom;

    @JsonProperty("series")
    private LinkedHashMap<String, BundesbankSeries> series;
}
