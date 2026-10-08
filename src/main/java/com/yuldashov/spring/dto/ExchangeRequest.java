package com.yuldashov.spring.dto;

import java.math.BigDecimal;

public class ExchangeRequest {
    private final String baseCurrencyCode;
    private final String targetCurrencyCode;
    private final BigDecimal rate;

    public ExchangeRequest(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

}
