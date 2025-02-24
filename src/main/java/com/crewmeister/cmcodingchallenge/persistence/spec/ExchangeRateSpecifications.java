package com.crewmeister.cmcodingchallenge.persistence.spec;

import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.entity.ExchangeRate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ExchangeRateSpecifications {

    public static Specification<ExchangeRate> exchangeRateForCurrency(Currency currency) {
        return (root, query, cb) -> currency == null ? null : cb.equal(root.get("currency"), currency);
    }

    public static Specification<ExchangeRate> exchangeRateForDate(LocalDate date) {
        return (root, query, cb) -> date == null ? null : cb.equal(root.get("date"), date);
    }
}
