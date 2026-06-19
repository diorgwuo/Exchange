package com.yuldashov.spring.service;

import com.yuldashov.spring.dto.CurrencyRequest;
import com.yuldashov.spring.dto.CurrencyResponse;
import com.yuldashov.spring.expection.DublicateException;
import com.yuldashov.spring.expection.NotFoundException;
import com.yuldashov.spring.model.Currency;
import com.yuldashov.spring.repository.CurrencyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {
    private CurrencyDAO currencyDAO;

    @Autowired
    public CurrencyService(CurrencyDAO currencyDAO) {
        this.currencyDAO = currencyDAO;
    }

    public CurrencyResponse create(CurrencyRequest request) {
        if (currencyDAO.existByCode(request.getCode())) {
            throw new DublicateException ("Валюта с кодом " + request.getCode() + " уже существует");
        }

        Currency currency = new Currency();
        currency.setFullName(request.getFullName());
        currency.setCode(request.getCode());
        currency.setSign(request.getSign());

        currencyDAO.create(currency);

        Currency saved = currencyDAO.lastCreate();
        CurrencyResponse response = new CurrencyResponse();
        response.setId(saved.getId());
        response.setName(saved.getFullName());
        response.setCode(saved.getCode());
        response.setSign(saved.getSign());
        return response;
    }

    public List<Currency> index() {
        List<Currency> allCurrency = currencyDAO.index();
        return allCurrency;
    }

    public CurrencyResponse getByCode(String code) {
        if (currencyDAO.existByCode(code)) {
            Currency currency = currencyDAO.getByCode(code);
            CurrencyResponse response = new CurrencyResponse();
            response.setId(currency.getId());
            response.setName(currency.getFullName());
            response.setCode(currency.getCode());
            response.setSign(currency.getSign());
            return response;
        }
        else {
            throw new NotFoundException("Не найдено такой валюты");
        }
    }
}
