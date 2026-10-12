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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeService {
    private final ExchangeDAO exchangeDAO;
    private final CurrencyService currencyService;
    private final CurrencyDAO currencyDAO;

    @Autowired
    public ExchangeService(CurrencyDAO currencyDAO, ExchangeDAO exchangeDAO, CurrencyService currencyService) {
        this.currencyDAO = currencyDAO;
        this.exchangeDAO = exchangeDAO;
        this.currencyService = currencyService;
    }


    public ExchangeResponse create(ExchangeRequest request) {
        String baseCurrencyCode = request.getBaseCurrencyCode();
        String targetCurrencyCode = request.getTargetCurrencyCode();
        Currency baseCurrency = currencyDAO.getByCode(baseCurrencyCode);
        Currency targetCurrency = currencyDAO.getByCode(targetCurrencyCode);

        if (currencyDAO.existByCode(baseCurrencyCode)
                && currencyDAO.existByCode(targetCurrencyCode)) {

            int baseCurrencyId = baseCurrency.getId();
            int targetCurrencyId = targetCurrency.getId();
            int id = exchangeDAO.create(baseCurrencyId, targetCurrencyId, request.getRate());

            return new ExchangeResponse(id,
                    toCurrencyResponse(baseCurrency),
                    toCurrencyResponse(targetCurrency),
                    request.getRate()
            );
        } else {
            throw new NotFoundException("Одна (или обе) валюта из валютной пары не существует в БД");
        }
    }

    private CurrencyResponse toCurrencyResponse(Currency currency) {
        return currency.mapToResponse();
    }

    public List<ExchangeResponse> index() {
        return exchangeDAO.index().stream()
                .map(rate -> rate.mapToResponse(
                        currencyDAO.getById(rate.getBaseCurrencyId()),
                        currencyDAO.getById(rate.getTargetCurrencyId())
                ))
                .collect(Collectors.toList());
    }

    public ExchangeResponse getCoupleCurrency(String code) {
        String baseCode = code.substring(0, 3);
        String targetCode = code.substring(3, 6);
        Currency baseCurrency = currencyDAO.getByCode(baseCode);
        Currency targetCurrency = currencyDAO.getByCode(targetCode);
        int baseCurrencyId = baseCurrency.getId();
        int targetCurrencyId = targetCurrency.getId();
        if (baseCurrencyId > 0 && targetCurrencyId > 0) {
            ExchangeRate exchange = exchangeDAO.findCoupleCurrency(baseCurrencyId, targetCurrencyId);

            if (exchange != null) {
                return exchange.mapToResponse(baseCurrency, targetCurrency);
            } else {
                throw new NotFoundException("Обменный курс для пары не найден");
            }
        }
        throw new NotFoundException("Не найдены такие валюты");
    }

    public ExchangeResponse update(String code, ExchangeRateDTO rateDto) {
        String baseCode = code.substring(0, 3);
        String targetCode = code.substring(3, 6);
        BigDecimal rate = rateDto.rate();
        if (rate == null) {
            throw new NotFoundException("Поле в форме не заполнено");
        }
        Currency baseCurrency = currencyDAO.getByCode(baseCode);
        Currency targetCurrency = currencyDAO.getByCode(targetCode);
        int baseCurrencyId = baseCurrency.getId();
        int targetCurrencyId = targetCurrency.getId();
        if (baseCurrencyId > 0 && targetCurrencyId > 0) {
            ExchangeRate exchange = exchangeDAO.findCoupleCurrency(baseCurrencyId, targetCurrencyId);
            if (exchange != null) {
                exchangeDAO.update(exchange.getId(), rate);
                ExchangeRate exchangeNew = exchangeDAO.findCoupleCurrency(baseCurrencyId, targetCurrencyId);
                return exchangeNew.mapToResponse(baseCurrency, targetCurrency);
            } else {
                throw new NotFoundException("Обменный курс для пары не найден");
            }
        }
        throw new NotFoundException("Не найдены такие валюты");
    }

    public ExchangeDTOResponse calculateExchange(String baseCode,
                                                 String targetCode,
                                                 BigDecimal amount) {
        CurrencyResponse baseCurrency = currencyService.getByCode(baseCode);
        CurrencyResponse targetCurrency = currencyService.getByCode(targetCode);
        int baseId = baseCurrency.id();
        int targetId = targetCurrency.id();

        BigDecimal rate = calculatorRate(baseId, targetId);
        BigDecimal convertedAmount = amount.multiply(rate);

        return new ExchangeDTOResponse(baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }

    BigDecimal calculatorRate(int baseId, int targetId) {

        ExchangeRate exchangeCouple = exchangeDAO.findCoupleCurrency(baseId, targetId);

        if (exchangeCouple != null) {
            return exchangeCouple.getRate();
        }

        ExchangeRate reverseExchange = exchangeDAO.findCoupleCurrency(targetId, baseId);

        if (reverseExchange != null) {
            BigDecimal reverseRate = reverseExchange.getRate();
            return BigDecimal.ONE.divide(reverseRate, 4);
        }

        return calculatorRateUseUSD(baseId, targetId);
    }

    BigDecimal calculatorRateUseUSD(int baseId, int targetId) {
        int usdId = currencyDAO.getCodeById("USD");
        ExchangeRate firstExchange = exchangeDAO.findCoupleCurrency(usdId, baseId);
        ExchangeRate secondExchange = exchangeDAO.findCoupleCurrency(usdId, targetId);

        BigDecimal firstRate = firstExchange.getRate();
        BigDecimal secondRate = secondExchange.getRate();

        return secondRate.divide(firstRate, 4);
    }
}
