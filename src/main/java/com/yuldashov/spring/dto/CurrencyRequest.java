package com.yuldashov.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record CurrencyRequest(
        @NotBlank(message = "Укажите имя")
        String fullName,
        @NotBlank(message = "Укажите код валюты")
        String code,
        @NotBlank(message = "Укажите знак валюты")
        String sign) {
}
