package com.yuldashov.spring.dto.exchange;

import com.yuldashov.spring.dto.сurrency.CurrencyResponse;

import java.math.BigDecimal;

public record ExchangeRatesResponse(int id, CurrencyResponse baseCurrency, CurrencyResponse targetCurrency, BigDecimal rate) {}
