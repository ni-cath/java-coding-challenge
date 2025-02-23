package com.crewmeister.cmcodingchallenge.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParsingHelper {

    //todo: change local date time to ISO
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Logger logger = LoggerFactory.getLogger(DateParsingHelper.class);

    public static LocalDate parseDate(String date) {
        LocalDate parsedDate = null;
        try {
            parsedDate = LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.warn("Can't parse the date {} with the pattern {}", date, DATE_FORMATTER);
        }

        return parsedDate;
    }

    public static LocalDateTime parseDateTime(String dateTime) {
        return parseDateTime(dateTime, DATE_TIME_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String dateTime, DateTimeFormatter dt) {
        LocalDateTime parsedDateTime = null;
        try {
            parsedDateTime = LocalDateTime.parse(dateTime, dt);
        } catch (DateTimeParseException e) {
            logger.warn("Can't parse the date time {} with the pattern {}", dateTime, dt);
        }

        return parsedDateTime;
    }

    public static boolean isNullOrFutureDate(LocalDateTime date) {
        return date == null || date.isAfter(LocalDateTime.now());
    }

    public static boolean isNullOrFutureDate(String date) {
        if (date == null || date.isBlank()) {
            return true;
        }

        try {
            LocalDate parsedDate = LocalDate.parse(date, DATE_FORMATTER);
            return parsedDate.isAfter(LocalDate.now());

        } catch (DateTimeParseException e) {
            return true;
        }
    }

    public static String getYesterdayDateAsString() {
        return LocalDateTime.now().minusDays(1).format(DATE_FORMATTER);
    }

    public static String getTodayDateAsString() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    public static String getDateAsString(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}
