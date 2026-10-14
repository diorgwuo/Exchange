package com.yuldashov.spring.dto.exchange;

import com.yuldashov.spring.model.Currency;

import java.math.BigDecimal;

public record ExchangeResponse(Currency baseCurrency,
                               Currency targetCurrency,
                               BigDecimal rate,
                               BigDecimal amount,
                               BigDecimal convertedAmount) {
}
