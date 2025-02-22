package com.crewmeister.cmcodingchallenge.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParsingHelper {

    //todo: change local date time to ISO
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Logger logger = LoggerFactory.getLogger(DateParsingHelper.class);

    public static LocalDateTime parseDateTime(String date) {
        return parseDateTime(date, DATE_TIME_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String date, DateTimeFormatter dt) {
        LocalDateTime parsedDate = null;
        try {
            parsedDate = LocalDateTime.parse(date, dt);
        } catch (DateTimeParseException e) {
            logger.warn("Can't parse the date {} with the pattern {}", date, dt);
        }

        return parsedDate;
    }

    public static boolean isNullOrFutureDate(LocalDateTime date) {
        return date == null || date.isAfter(LocalDateTime.now());
    }

    public static boolean isNullOrFutureDate(String date) {
        if (date == null || date.isBlank()) {
            return true;
        }

        try {
            LocalDateTime parsedDate = LocalDateTime.parse(date, DATE_FORMATTER);
            return parsedDate.isAfter(LocalDateTime.now());

        } catch (DateTimeParseException e) {
            return true;
        }
    }

    public static String getYesterdayDateAsString() {
        return LocalDateTime.now().minusDays(1).format(DATE_FORMATTER);
    }
}
