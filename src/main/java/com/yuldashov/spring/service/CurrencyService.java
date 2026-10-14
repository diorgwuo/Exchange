package com.yuldashov.spring.service;

import com.yuldashov.spring.dto.сurrency.CurrencyRequest;
import com.yuldashov.spring.dto.сurrency.CurrencyResponse;
import com.yuldashov.spring.expection.DublicateException;
import com.yuldashov.spring.expection.NotFoundException;
import com.yuldashov.spring.model.Currency;
import com.yuldashov.spring.repository.CurrencyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CurrencyService {
    private final CurrencyDAO currencyDAO;

    @Autowired
    public CurrencyService(CurrencyDAO currencyDAO) {
        this.currencyDAO = currencyDAO;
    }

    @Transactional
    public CurrencyResponse create(CurrencyRequest request) {
        if (currencyDAO.existByCode(request.code())) {
            throw new DublicateException("Валюта с кодом " + request.code() + " уже существует");
        }

        Currency currency = new Currency();
        currency.setFullName(request.fullName());
        currency.setCode(request.code());
        currency.setSign(request.sign());

        int id = currencyDAO.create(currency);

        return new CurrencyResponse(id,
                currency.getFullName(),
                currency.getCode(),
                currency.getSign()
        );
    }

    public List<Currency> index() {
        return currencyDAO.index();
    }

    public CurrencyResponse getByCode(String code) {
        if (currencyDAO.existByCode(code)) {
            Currency currency = currencyDAO.getByCode(code);
            return new CurrencyResponse(currency.getId(),
                    currency.getFullName(),
                    currency.getCode(),
                    currency.getSign()
            );
        } else {
            throw new NotFoundException("Не найдено валюта" + code);
        }
    }
}
