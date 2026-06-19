package com.yuldashov.spring.controllers;

import com.yuldashov.spring.dto.CurrencyRequest;
import com.yuldashov.spring.dto.CurrencyResponse;
import com.yuldashov.spring.dto.ErrorResponse;
import com.yuldashov.spring.model.Currency;
import com.yuldashov.spring.service.CurrencyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CurrencyController {
    private final CurrencyService currencyService;


    @Autowired
    public CurrencyController(CurrencyService currencyService){
        this.currencyService = currencyService;
    }

    @GetMapping("/currencies")
    public List<Currency> index() {
        return currencyService.index();
    }

    @PostMapping("/currencies")
    public ResponseEntity<CurrencyResponse> create(@Valid CurrencyRequest request) {

            return new ResponseEntity<>(currencyService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/currency/{code}")
    public CurrencyResponse getByCode(@PathVariable("code") String code) {
        return currencyService.getByCode(code);
    }

    @GetMapping(value = {"/currency","/currency/"})
    public ResponseEntity<ErrorResponse> getWithoutCode() {
        ErrorResponse error = new ErrorResponse(400, "Bad request", "Не введен code в URL");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
