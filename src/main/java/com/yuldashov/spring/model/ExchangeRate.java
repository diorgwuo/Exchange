package com.yuldashov.spring.model;

import com.yuldashov.spring.dto.exchange.ExchangeRatesResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class ExchangeRate {
    private int id;
    private int baseCurrencyId;
    private int targetCurrencyId;
    private BigDecimal rate;

    public ExchangeRatesResponse mapToResponse(Currency base, Currency target) {
        return new ExchangeRatesResponse(
                this.getId(),
                base.mapToResponse(),
                target.mapToResponse(),
                this.getRate()
        );
    }
}
