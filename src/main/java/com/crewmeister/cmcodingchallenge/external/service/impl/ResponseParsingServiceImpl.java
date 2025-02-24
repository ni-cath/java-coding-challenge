package com.crewmeister.cmcodingchallenge.external.service.impl;

import com.crewmeister.cmcodingchallenge.external.dto.CurrencyQuote;
import com.crewmeister.cmcodingchallenge.external.dto.ExchangingRateList;
import com.crewmeister.cmcodingchallenge.external.service.ResponseParsingService;
import com.crewmeister.cmcodingchallenge.util.DateParsingHelper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ResponseParsingServiceImpl implements ResponseParsingService {

    private final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<ExchangingRateList> parse(InputStream stream) {
        List<ExchangingRateList> exchangingRateLists;

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            exchangingRateLists = parse(reader);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return exchangingRateLists;
    }

    private List<ExchangingRateList> parse(BufferedReader reader) throws IOException {
        String headerLine = reader.readLine();
        if (headerLine == null) return List.of();

        String[] headers = headerLine.split(",");

        List<Integer> currencyIndexes = new ArrayList<>();
        List<String> currencyNames = new ArrayList<>();

        for (int i = 1; i < headers.length; i++) {
            if (headers[i].endsWith("_FLAGS")) {
                continue;
            }

            // ****.*.BGN.EUR.**.**.***
            currencyIndexes.add(i);
            currencyNames.add(headers[i].split("\\.")[2]);
        }

        // skip the line with last update
        String lastUpdated = reader.readLine();

        List<ExchangingRateList> exchangingRateLists = new ArrayList<>();

        // Read each row and parse exchange rates
        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");

            // for rows with comments like: "Comment on 2015-01-15: With effect from 15 January 2015, the Swiss National Bank abolished the minimum exchange rate of EUR 1 = CHF 1.20 that was introduced on 6 September 2011."
            if (values[0].contains("\"")) {
                continue;
            }

            LocalDate date = DateParsingHelper.parseDate(values[0]);
            // no quotes for weekend
            if (isWeekend(date)) {
                continue;
            }

            Collection<CurrencyQuote> currencyQuotes = getCurrencyQuotes(currencyIndexes, values, currencyNames);

            // empty for public holidays
            if (currencyQuotes.isEmpty()) {
                continue;
            }

            // since the csv file with all data provides only date save only the date
            // change getting the all data to provide time param if necessary,
            exchangingRateLists.add(new ExchangingRateList(date, currencyQuotes));
        }

        return exchangingRateLists;
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    private Collection<CurrencyQuote> getCurrencyQuotes(List<Integer> currencyIndexes, String[] values, List<String> currencyNames) {
        Collection<CurrencyQuote> currencyQuotes = new ArrayList<>();

        for (int i = 0; i < currencyIndexes.size(); i++) {
            int index = currencyIndexes.get(i);

            if (values[index].isEmpty() || values[index].equals(".") || values[index].equals("No value available")) {
                continue;
            }

            currencyQuotes.add(new CurrencyQuote(currencyNames.get(i), Double.parseDouble(values[index])));
        }
        return currencyQuotes;
    }
}
