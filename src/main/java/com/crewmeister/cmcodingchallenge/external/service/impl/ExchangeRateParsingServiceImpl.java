package com.crewmeister.cmcodingchallenge.external.service.impl;

import com.crewmeister.cmcodingchallenge.external.dto.CurrencyQuote;
import com.crewmeister.cmcodingchallenge.external.dto.ExchangeRateResponse;
import com.crewmeister.cmcodingchallenge.external.service.ExchangeRateParsingService;
import com.crewmeister.cmcodingchallenge.helper.DateParsingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class ExchangeRateParsingServiceImpl implements ExchangeRateParsingService {

    private final Logger logger = LoggerFactory.getLogger(ExchangeRateParsingServiceImpl.class);

    @Override
    public List<ExchangeRateResponse> parse(InputStream stream) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return parse(reader);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ExchangeRateResponse> parse(BufferedReader reader) throws IOException {
        String headerLine = reader.readLine();
        if (headerLine == null) return List.of();

        String[] headers = headerLine.split(",");
        List<Integer> currencyIndexes = extractCurrencyIndexes(headers);
        List<String> currencyNames = extractCurrencyNames(headers);

        // skip the line with last update
        logger.debug("Skipping line with last updated dates: {}", reader.readLine());

        List<ExchangeRateResponse> exchangeRateResponses = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            processLine(line, currencyIndexes, currencyNames, exchangeRateResponses);
        }

        return exchangeRateResponses;
    }

    private List<Integer> extractCurrencyIndexes(String[] headers) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 1; i < headers.length; i++) {
            if (!headers[i].endsWith("_FLAGS")) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private List<String> extractCurrencyNames(String[] headers) {
        List<String> names = new ArrayList<>();
        for (int i = 1; i < headers.length; i++) {
            if (!headers[i].endsWith("_FLAGS")) {
                // ****.*.BGN.EUR.**.**.***
                names.add(headers[i].split("\\.")[2]);
            }
        }
        return names;
    }

    private void processLine(String line, List<Integer> currencyIndexes, List<String> currencyNames,
                             List<ExchangeRateResponse> exchangeRateResponses) {
        String[] values = line.split(",");

        if (isCommentLine(values[0])) {
            logger.debug("Skipping comment line: {}", line);
            return; // skip rows with comments like: "Comment on 2015-01-15: ..."
        }

        Optional<LocalDate> dateOpt = DateParsingHelper.parseDate(values[0]);

        dateOpt.ifPresentOrElse(date -> {
            if (isWeekend(date)) {
                return; // skip weekends
            }

            Collection<CurrencyQuote> currencyQuotes = extractCurrencyQuotes(currencyIndexes, currencyNames, values);
            if (!currencyQuotes.isEmpty()) {
                exchangeRateResponses.add(new ExchangeRateResponse(date, currencyQuotes));
            }
        }, () -> logger.warn("Unable to parse date {} from line: {}", values[0], line)
        );
    }

    private boolean isCommentLine(String value) {
        return value.contains("\"");
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    private Collection<CurrencyQuote> extractCurrencyQuotes(List<Integer> currencyIndexes, List<String> currencyNames, String[] values) {
        Collection<CurrencyQuote> currencyQuotes = new ArrayList<>();

        for (int i = 0; i < currencyIndexes.size(); i++) {
            int index = currencyIndexes.get(i);
            String value = values[index];

            if (isValidCurrencyValue(value)) {
                currencyQuotes.add(new CurrencyQuote(currencyNames.get(i), Double.parseDouble(value)));
            }
        }
        return currencyQuotes;
    }

    private boolean isValidCurrencyValue(String value) {
        return !value.isEmpty() && !value.equals(".") && !value.equals("No value available");
    }
}
