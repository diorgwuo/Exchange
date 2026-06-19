package com.yuldashov.spring.dto;

import java.math.BigDecimal;

public class ExchangeResponse {
    private int id;
    private CurrencyResponse baseCurrency;
    private CurrencyResponse targetCurrency;
    private BigDecimal rate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CurrencyResponse getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(CurrencyResponse baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public CurrencyResponse getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(CurrencyResponse targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
