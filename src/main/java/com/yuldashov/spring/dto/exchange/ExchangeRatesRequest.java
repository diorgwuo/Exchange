package com.yuldashov.spring.dto.exchange;

import java.math.BigDecimal;

public record ExchangeRatesRequest(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
}
