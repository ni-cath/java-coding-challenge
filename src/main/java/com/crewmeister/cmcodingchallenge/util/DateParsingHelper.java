package com.crewmeister.cmcodingchallenge.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParsingHelper {

    //todo: make configurable
    private static final DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Logger logger = LoggerFactory.getLogger(DateParsingHelper.class);

    public static LocalDate parseDate(String date) {
        LocalDate parsedDate = null;
        try {
            parsedDate = LocalDate.parse(date, dt);
        } catch (DateTimeParseException e) {
            logger.warn("Can't parse the date {} with the pattern {}", date, dt);
        }

        return parsedDate;
    }

    public static boolean isNullOrFutureDate(LocalDate date) {
        return date == null || date.isAfter(LocalDate.now());
    }
}
