package com.crewmeister.cmcodingchallenge.external.bundesbank.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.LinkedHashSet;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BundesbankCurrencyDimension {

    @JsonProperty("values")
    private LinkedHashSet<BundesbankCurrencyValue> values;
}
