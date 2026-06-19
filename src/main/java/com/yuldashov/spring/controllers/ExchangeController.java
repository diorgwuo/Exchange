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
    public ResponseEntity<ExchangeResponse> create(ExchangeRequest request) {
        return new ResponseEntity<>(exchangeService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/exchangeRates")
    public List<ExchangeResponse> index() {
        return exchangeService.index();
    }

    @GetMapping("/exchangeRate/{code}")
    public ExchangeResponse getByCode(
            @PathVariable("code") String code) {
        String baseCode = code.substring(0, 3);
        String targetCode = code.substring(3, 6);
        return exchangeService.getCoupleCurrency(baseCode, targetCode);
    }

    @GetMapping("/exchangeRate/")
    public ResponseEntity<ErrorResponse> getWithoutId() {
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad request", "Не введен Code в URL");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @PatchMapping(value = "/exchangeRate/{code}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ExchangeResponse update(@PathVariable("code") String code, @RequestBody Map<String, String> params) {
        return exchangeService.update(code,params);
    }

    @GetMapping(value = "/exchange")
    public ExchangeDTOResponse Exchange(@RequestParam("from") String baseCurrency,
                                        @RequestParam("to") String targetCurrency,
                                        @RequestParam("amount") BigDecimal amount) {
        return exchangeService.calculateExchange(baseCurrency,targetCurrency,amount);
    }

}
