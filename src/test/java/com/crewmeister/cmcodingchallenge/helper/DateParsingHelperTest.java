package com.crewmeister.cmcodingchallenge.helper;

import com.crewmeister.cmcodingchallenge.common.constants.AppConstants;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DateParsingHelperTest {

    @Test
    void parseDate_GivenValidDateString_ExpectParsedDate() {
        Optional<LocalDate> parsedDate = DateParsingHelper.parseDate("2025-11-30");

        assertTrue(parsedDate.isPresent());
        assertEquals(30, parsedDate.get().getDayOfMonth());
        assertEquals(11, parsedDate.get().getMonthValue());
        assertEquals(2025, parsedDate.get().getYear());
    }

    @Test
    void parseDate_GivenInvalidDateString_ExpectEmptyOptional() {
        Optional<LocalDate> parsedDate = DateParsingHelper.parseDate("2025/11/30");

        assertTrue(parsedDate.isEmpty());
    }

    @Test
    void isNullOrFutureDate_GivenTodayDate_ExpectFalse() {
        assertFalse(DateParsingHelper.isNullOrFutureDate(LocalDate.now()));
    }

    @Test
    void isNullOrFutureDate_GivenTomorrowDate_ExpectTrue() {
        assertTrue(DateParsingHelper.isNullOrFutureDate(LocalDate.now().plusDays(1)));
    }

    @Test
    void isNullOrFutureDate_GivenNullDate_ExpectTrue() {
        assertTrue(DateParsingHelper.isNullOrFutureDate(null));
    }

    @Test
    void getYesterdayDateAsString() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String resultDateString = DateParsingHelper.getYesterdayDateAsString();

        assertEquals(yesterday.format(AppConstants.DATE_FORMATTER), resultDateString);
    }

    @Test
    void getTodayDateAsString() {
        LocalDate yesterday = LocalDate.now();
        String resultDateString = DateParsingHelper.getTodayDateAsString();

        assertEquals(yesterday.format(AppConstants.DATE_FORMATTER), resultDateString);
    }

    @Test
    void getDateAsString() {
        LocalDate now = LocalDate.now();
        String date = DateParsingHelper.getDateAsString(now);

        assertEquals(now.format(AppConstants.DATE_FORMATTER), date);
    }
}