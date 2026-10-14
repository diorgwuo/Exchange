package com.yuldashov.spring.model;

import com.yuldashov.spring.dto.сurrency.CurrencyResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Currency {
    private int id;
    private String fullName;
    private String code;
    private String sign;

    public CurrencyResponse mapToResponse() {
        return new CurrencyResponse(this.getId(),
                this.getFullName(),
                this.getCode(),
                this.getSign()
        );
    }
}
