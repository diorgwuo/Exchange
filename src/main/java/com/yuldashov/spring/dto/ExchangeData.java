package com.yuldashov.spring.dto;

import com.yuldashov.spring.model.Currency;
import com.yuldashov.spring.model.ExchangeRate;

public record ExchangeData(
        Currency baseCurrency,
        Currency targetCurrency,
        ExchangeRate exchange
) {
}
