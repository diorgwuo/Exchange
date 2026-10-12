package com.yuldashov.spring.controllers;

import com.yuldashov.spring.dto.*;
import com.yuldashov.spring.expection.NotFoundException;
import com.yuldashov.spring.service.ExchangeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.stream.Collectors;

@RestController
public class ExchangeController {
    private final ExchangeService exchangeService;

    @Autowired
    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/exchangeRates")
    @ResponseStatus(HttpStatus.CREATED)
    public ExchangeResponse create(ExchangeRequest request) {
        return exchangeService.create(request);
    }

    @GetMapping("/exchangeRates")
    public List<ExchangeResponse> index() {
        return exchangeService.index();
    }

    @GetMapping("/exchangeRate/{code}")
    public ExchangeResponse getByCode(
            @PathVariable("code") String code) {
        return exchangeService.getCoupleCurrency(code);
    }

    @PatchMapping(value = "/exchangeRate/{code}")
    public ExchangeResponse update(@PathVariable("code") String code, @RequestBody ExchangeRateDTO rateDto) {
        return exchangeService.update(code, rateDto);
    }

    @GetMapping(value = "/exchange")
    public ExchangeDTOResponse Exchange(@RequestParam("from") String baseCurrency,
                                        @RequestParam("to") String targetCurrency,
                                        @RequestParam("amount") BigDecimal amount) {
        return exchangeService.calculateExchange(baseCurrency, targetCurrency, amount);
    }

}
