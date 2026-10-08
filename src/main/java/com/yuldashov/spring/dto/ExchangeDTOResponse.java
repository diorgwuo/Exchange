package com.yuldashov.spring.dto;

import java.math.BigDecimal;

public record ExchangeDTOResponse(CurrencyResponse baseCurrency,
                                  CurrencyResponse targetCurrency,
                                  BigDecimal rate,
                                  BigDecimal amount,
                                  BigDecimal convertedAmount
                                  ) {
}
