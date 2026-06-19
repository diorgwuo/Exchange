package com.yuldashov.spring.dto;

import java.math.BigDecimal;

public class ExchangeDTOResponse {
    private CurrencyResponse baseCurrency;
    private CurrencyResponse targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}
