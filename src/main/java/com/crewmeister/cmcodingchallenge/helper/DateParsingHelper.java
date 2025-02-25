package com.crewmeister.cmcodingchallenge.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static com.crewmeister.cmcodingchallenge.common.constants.AppConstants.DATE_FORMATTER;

public class DateParsingHelper {
    private static final Logger logger = LoggerFactory.getLogger(DateParsingHelper.class);

    /**
     * Parses date using {@link com.crewmeister.cmcodingchallenge.common.constants.AppConstants#DATE_FORMATTER} format
     * @param date string with date and time to parse
     * @return parsed date time object or empty optional value if the string with provided format cannot be parsed
     */
    public static Optional<LocalDate> parseDate(String date) {
        try {
            return Optional.of(LocalDate.parse(date, DATE_FORMATTER));
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse date {} with pattern {}", date, DATE_FORMATTER);
            return Optional.empty();
        }
    }

    /**
     * Checks if the date is null or in the future
     * @param date date to check
     * @return true if the date is null or in the future, otherwise return false
     */
    public static boolean isNullOrFutureDate(LocalDate date) {
        return date == null || date.isAfter(LocalDate.now());
    }

    /**
     * Gets the string representation of yesterday's date
     * @return date as string using in format {@link com.crewmeister.cmcodingchallenge.common.constants.AppConstants#DATE_FORMATTER}
     */
    public static String getYesterdayDateAsString() {
        return LocalDate.now().minusDays(1).format(DATE_FORMATTER);
    }

    /**
     * Gets the string representation of today's date
     * @return date as string using in format {@link com.crewmeister.cmcodingchallenge.common.constants.AppConstants#DATE_FORMATTER}
     */
    public static String getTodayDateAsString() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * Gets the string representation of a specific date
     * @param date date
     * @return date as string using in format {@link com.crewmeister.cmcodingchallenge.common.constants.AppConstants#DATE_FORMATTER}
     */
    public static String getDateAsString(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}
