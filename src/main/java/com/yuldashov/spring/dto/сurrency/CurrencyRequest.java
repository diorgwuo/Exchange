package com.yuldashov.spring.dto.сurrency;

import jakarta.validation.constraints.NotBlank;

public record CurrencyRequest(
        @NotBlank(message = "Укажите имя")
        String fullName,
        @NotBlank(message = "Укажите код валюты")
        String code,
        @NotBlank(message = "Укажите знак валюты")
        String sign) {
}
