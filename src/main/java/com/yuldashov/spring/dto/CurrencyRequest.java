package com.yuldashov.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CurrencyRequest {

    @NotBlank(message = "Укажите имя")
    private String fullName;
    @NotBlank (message = "Укажите код валюты")
    private String code;
    @NotBlank (message = "Укажите знак валюты")
    private String sign;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
