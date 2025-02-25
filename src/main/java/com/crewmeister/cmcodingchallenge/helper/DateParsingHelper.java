package com.crewmeister.cmcodingchallenge.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.crewmeister.cmcodingchallenge.common.constants.AppConstants.DATE_FORMATTER;
import static com.crewmeister.cmcodingchallenge.common.constants.AppConstants.DATE_TIME_FORMATTER;

public class DateParsingHelper {
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

    public static boolean isNullOrFutureDate(LocalDate date) {
        return date == null || date.isAfter(LocalDate.now());
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
