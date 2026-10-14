package com.yuldashov.spring.controllers;

import com.yuldashov.spring.dto.сurrency.CurrencyRequest;
import com.yuldashov.spring.dto.сurrency.CurrencyResponse;
import com.yuldashov.spring.model.Currency;
import com.yuldashov.spring.service.CurrencyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CurrencyController {
    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/currencies")
    public List<Currency> index() {
        return currencyService.index();
    }

    @PostMapping("/currencies")
    @ResponseStatus(HttpStatus.CREATED)
    public CurrencyResponse create(
            @Valid
            @RequestBody
            CurrencyRequest request) {
        return currencyService.create(request);
    }

    @GetMapping("/currency/{code}")
    public CurrencyResponse getByCode(@PathVariable("code") String code) {
        return currencyService.getByCode(code);
    }

}
