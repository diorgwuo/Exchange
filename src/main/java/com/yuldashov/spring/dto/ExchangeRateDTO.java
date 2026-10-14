package com.yuldashov.spring.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ExchangeRateDTO(
        @NotNull(message = "Поле в форме не заполнено")
        BigDecimal rate) { }
