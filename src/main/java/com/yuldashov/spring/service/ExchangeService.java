package com.yuldashov.spring.service;

import com.yuldashov.spring.dto.*;
import com.yuldashov.spring.expection.NotFoundException;
import com.yuldashov.spring.model.Currency;
import com.yuldashov.spring.model.ExchangeRate;
import com.yuldashov.spring.repository.CurrencyDAO;
import com.yuldashov.spring.repository.ExchangeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.spi.CurrencyNameProvider;

@Service
public class ExchangeService {
    private final ExchangeDAO exchangeDAO;
    private final CurrencyService currencyService;
    private CurrencyDAO currencyDAO;

    @Autowired
    public ExchangeService(CurrencyDAO currencyDAO, ExchangeDAO exchangeDAO, CurrencyService currencyService) {
        this.currencyDAO = currencyDAO;
        this.exchangeDAO = exchangeDAO;
        this.currencyService = currencyService;
    }



    public ExchangeResponse create(ExchangeRequest request) {
        String baseCurrencyCode = request.getBaseCurrencyCode();
        String targetCurrencyCode = request.getTargetCurrencyCode();

        if (currencyDAO.existByCode(baseCurrencyCode)
                && currencyDAO.existByCode(targetCurrencyCode)) {
            int baseCurrencyId = currencyDAO.getByCode(request.getBaseCurrencyCode()).getId();
            int targetCurrnencyId = currencyDAO.getByCode(request.getTargetCurrencyCode()).getId();
            exchangeDAO.create(baseCurrencyId,targetCurrnencyId,request.getRate());

            ExchangeRate exchangeRate = exchangeDAO.lastCreate();
            Currency baseCurrency = currencyDAO.getByCode(baseCurrencyCode);
            Currency targetCurrency = currencyDAO.getByCode(targetCurrencyCode);

            ExchangeResponse exchangeResponse = new ExchangeResponse();
            exchangeResponse.setId(exchangeRate.getId());
            exchangeResponse.setBaseCurrency(toCurrencyResponse(baseCurrency));
            exchangeResponse.setTargetCurrency(toCurrencyResponse(targetCurrency));
            exchangeResponse.setRate(exchangeRate.getRate());
            return exchangeResponse;
        }
        else {
            throw new NotFoundException("Одна (или обе) валюта из валютной пары не существует в БД");
        }
        }
    private CurrencyResponse toCurrencyResponse(Currency currency) {
        CurrencyResponse response = new CurrencyResponse();
        response.setId(currency.getId());
        response.setName(currency.getFullName());
        response.setCode(currency.getCode());
        response.setSign(currency.getSign());
        return response;
    }

    public List<ExchangeResponse> index() {
        List<ExchangeRate> saved = exchangeDAO.index();
        List<ExchangeResponse> responseList = new ArrayList<>();

        for (ExchangeRate exchange : saved) {
            ExchangeResponse response = toResponse(exchange);
            responseList.add(response);
        }
        return responseList;
    }

    private ExchangeResponse toResponse(ExchangeRate exchange) {
        Currency baseCurrency = currencyDAO.getById(exchange.getBaseCurrencyId());
        Currency targetCurrency = currencyDAO.getById(exchange.getTargetCurrencyId());

        CurrencyResponse baseResponse = new CurrencyResponse();
        baseResponse.setId(baseCurrency.getId());
        baseResponse.setName(baseCurrency.getFullName());
        baseResponse.setCode(baseCurrency.getCode());
        baseResponse.setSign(baseCurrency.getSign());

        CurrencyResponse targetResponse = new CurrencyResponse();
        targetResponse.setId(targetCurrency.getId());
        targetResponse.setName(targetCurrency.getFullName());
        targetResponse.setCode(targetCurrency.getCode());
        targetResponse.setSign(targetCurrency.getSign());

        ExchangeResponse exchangeResponse = new ExchangeResponse();
        exchangeResponse.setId(exchange.getId());
        exchangeResponse.setBaseCurrency(baseResponse);
        exchangeResponse.setTargetCurrency(targetResponse);
        exchangeResponse.setRate(exchange.getRate());

        return exchangeResponse;

    }

    public ExchangeResponse getCoupleCurrency(String baseCurrencyCode, String targetCurrencyCode) {
        int baseCurrencyId = currencyDAO.getCodeById(baseCurrencyCode);
        int targetCurrencyId = currencyDAO.getCodeById(targetCurrencyCode);
        if (baseCurrencyId>0 && targetCurrencyId>0) {
            ExchangeRate exchange = exchangeDAO.findCoupleCurrency(baseCurrencyId,targetCurrencyId);
            if (exchange!=null) {
                ExchangeResponse response = toResponse(exchange);
                return response;
            }
            else {
                throw new NotFoundException("Обменный курс для пары не найден");
            }
        }
        throw new NotFoundException("Не найдены такие валюты");
    }

    public ExchangeResponse update(String code, Map<String,String> params) {
        String baseCode = code.substring(0,3);
        String targetCode = code.substring(3,6);
        String body = params.get("rate");
        if (body == null) {
            throw new NotFoundException("Поле в форме не заполнено");
        }
        BigDecimal rate = new BigDecimal(body);
        int baseCurrencyId = currencyDAO.getCodeById(baseCode);
        int targetCurrencyId = currencyDAO.getCodeById(targetCode);
        if (baseCurrencyId>0 && targetCurrencyId>0) {
            ExchangeRate exchange = exchangeDAO.findCoupleCurrency(baseCurrencyId,targetCurrencyId);
            if (exchange!=null) {
                exchangeDAO.update(exchange.getId(), rate);
                ExchangeRate exchangeNew = exchangeDAO.findCoupleCurrency(baseCurrencyId,targetCurrencyId);
                ExchangeResponse response = toResponse(exchangeNew);
                return response;
            }
            else {
                throw new NotFoundException("Обменный курс для пары не найден");
            }
        }
        throw new NotFoundException("Не найдены такие валюты");
    }

    public ExchangeDTOResponse calculateExchange(String baseCode,
                                                 String targetCode,
                                                 BigDecimal amount) {
        ExchangeDTOResponse response = new ExchangeDTOResponse();
        CurrencyResponse baseCurrency = new CurrencyResponse();
        CurrencyResponse targetCurrency = new CurrencyResponse();
        int baseId = currencyDAO.getCodeById(baseCode);
        int targetId = currencyDAO.getCodeById(targetCode);
        baseCurrency = currencyService.getByCode(baseCode);
        targetCurrency = currencyService.getByCode(targetCode);

        BigDecimal convertedAmount;
        BigDecimal rate = null;

        ExchangeRate exchangeCouple = exchangeDAO.findCoupleCurrency(baseId,targetId);

        if (exchangeCouple != null) {
            rate = exchangeCouple.getRate();
        }

        if (rate == null) {
            ExchangeRate reverseExchange = exchangeDAO.findCoupleCurrency(targetId,baseId);
            BigDecimal reverseRate = reverseExchange.getRate();
            rate = BigDecimal.ONE.divide(reverseRate,4);
        }

        if (rate == null) {
            int usdId = currencyDAO.getCodeById("USD");
            ExchangeRate firstExchange = exchangeDAO.findCoupleCurrency(usdId,baseId);
            ExchangeRate secondExchange = exchangeDAO.findCoupleCurrency(usdId, targetId);

            BigDecimal firstRate = firstExchange.getRate();
            BigDecimal secondRate = secondExchange.getRate();

            rate = secondRate.divide(firstRate,4);
        }

        convertedAmount = amount.multiply(rate);

        response.setRate(rate);
        response.setAmount(amount);
        response.setConvertedAmount(convertedAmount);
        response.setBaseCurrency(baseCurrency);
        response.setTargetCurrency(targetCurrency);

        return response;
    }
}
