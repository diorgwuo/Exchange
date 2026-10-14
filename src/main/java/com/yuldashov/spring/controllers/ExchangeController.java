package com.yuldashov.spring.controllers;

import com.yuldashov.spring.dto.exchange.ExchangeResponse;
import com.yuldashov.spring.dto.exchange.ExchangeRateDTO;
import com.yuldashov.spring.dto.exchange.ExchangeRatesRequest;
import com.yuldashov.spring.dto.exchange.ExchangeRatesResponse;
import com.yuldashov.spring.service.ExchangeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ExchangeController {
    private final ExchangeService exchangeService;

    @Autowired
    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/exchangeRates")
    @ResponseStatus(HttpStatus.CREATED)
    public ExchangeRatesResponse create(ExchangeRatesRequest request) {
        return exchangeService.create(request);
    }

    @GetMapping("/exchangeRates")
    public List<ExchangeRatesResponse> index() {
        return exchangeService.index();
    }

    @GetMapping("/exchangeRate/{code}")
    public ExchangeRatesResponse getByCode(
            @PathVariable("code") String code) {
        return exchangeService.getCoupleCurrency(code);
    }

    @PatchMapping(value = "/exchangeRate/{code}")
    public ExchangeRatesResponse update(@PathVariable("code") String code, @RequestBody @Valid ExchangeRateDTO rateDto) {
        return exchangeService.update(code, rateDto);
    }

    @GetMapping(value = "/exchange")
    public ExchangeResponse Exchange(@RequestParam("from") String baseCurrency,
                                     @RequestParam("to") String targetCurrency,
                                     @RequestParam("amount") BigDecimal amount) {
        return exchangeService.calculateExchange(baseCurrency, targetCurrency, amount);
    }

}
