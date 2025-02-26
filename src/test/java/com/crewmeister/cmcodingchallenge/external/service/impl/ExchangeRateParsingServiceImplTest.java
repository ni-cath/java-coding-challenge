package com.crewmeister.cmcodingchallenge.external.service.impl;

import com.crewmeister.cmcodingchallenge.external.dto.ExchangeRateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ExchangeRateParsingServiceImplTest {
    @InjectMocks
    ExchangeRateParsingServiceImpl exchangeRateParsingService;

    @Test
    void testParse_EmptyCSV() {
        String csv = "";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        List<ExchangeRateResponse> result = exchangeRateParsingService.parse(inputStream);

        assertTrue(result.isEmpty());
    }

    @Test
    void testParse_RealCSVFile() throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("BBEX3.D..EUR.BB.AC.000.csv");
        assertNotNull(inputStream, "BBEX3.D..EUR.BB.AC.000.csv not found in resources");

        List<ExchangeRateResponse> result = exchangeRateParsingService.parse(inputStream);
        assertFalse(result.isEmpty());
        assertEquals(6695, result.size());

        // 2002-05-24
        Optional<ExchangeRateResponse> rateResponse2002 = result.stream()
                .filter(it -> it.getDate().isEqual(LocalDate.of(2002, 5, 24)))
                .findFirst();

        assertTrue(rateResponse2002.isPresent());
        assertEquals(39, rateResponse2002.get().getCurrencyQuotes().size());

        //2025-02-13
        Optional<ExchangeRateResponse> rateResponse2025 = result.stream()
                .filter(it -> it.getDate().isEqual(LocalDate.of(2025, 2, 13)))
                .findFirst();

        assertTrue(rateResponse2025.isPresent());
        assertEquals(30, rateResponse2025.get().getCurrencyQuotes().size());
    }
}
