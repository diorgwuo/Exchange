package com.yuldashov.spring.dto;

import com.yuldashov.spring.model.Currency;

import java.math.BigDecimal;

public record ExchangeDTOResponse(Currency baseCurrency,
                                  Currency targetCurrency,
                                  BigDecimal rate,
                                  BigDecimal amount,
                                  BigDecimal convertedAmount
                                  ) {
}
