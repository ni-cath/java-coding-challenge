package com.crewmeister.cmcodingchallenge.service.impl;

import com.crewmeister.cmcodingchallenge.persistence.entity.Currency;
import com.crewmeister.cmcodingchallenge.persistence.repository.CurrencyRepository;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyServiceImpl(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public Set<String> getAllAvailableCurrencyCodes() {
        return currencyRepository.findAll().stream()
                .map(Currency::getCurrencyCode)
                .collect(Collectors.toSet());
    }


    @Override
    public Optional<Currency> getCurrency(String currencyCode) {
        return currencyRepository.findByCurrencyCode(currencyCode);
    }

    @Override
    public void save(Currency currency) {
        currencyRepository.save(currency);
    }
}
