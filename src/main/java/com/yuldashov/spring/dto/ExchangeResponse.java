package com.yuldashov.spring.dto;

import java.math.BigDecimal;

public record ExchangeResponse(int id, CurrencyResponse baseCurrency, CurrencyResponse targetCurrency, BigDecimal rate) {}
