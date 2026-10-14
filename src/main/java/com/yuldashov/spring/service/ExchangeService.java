package com.yuldashov.spring.service;

import com.yuldashov.spring.dto.exchange.*;
import com.yuldashov.spring.expection.NotFoundException;
import com.yuldashov.spring.model.Currency;
import com.yuldashov.spring.model.ExchangeRate;
import com.yuldashov.spring.repository.CurrencyDAO;
import com.yuldashov.spring.repository.ExchangeDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeDAO exchangeDAO;
    private final CurrencyDAO currencyDAO;

    public ExchangeRatesResponse create(ExchangeRatesRequest request) {
        String baseCurrencyCode = request.baseCurrencyCode();
        String targetCurrencyCode = request.targetCurrencyCode();
        if (currencyDAO.existByCode(baseCurrencyCode)
                && currencyDAO.existByCode(targetCurrencyCode)) {
            Currency baseCurrency = currencyDAO.getByCode(baseCurrencyCode);
            Currency targetCurrency = currencyDAO.getByCode(targetCurrencyCode);
            int baseCurrencyId = baseCurrency.getId();
            int targetCurrencyId = targetCurrency.getId();
            int id = exchangeDAO.create(baseCurrencyId, targetCurrencyId, request.rate());
            return new ExchangeRatesResponse(id,
                    baseCurrency.mapToResponse(),
                    targetCurrency.mapToResponse(),
                    request.rate()
            );
        } else {
            throw new NotFoundException("Одна (или обе) валюта из валютной пары не существует в БД");
        }
    }

    public List<ExchangeRatesResponse> index() {
        return exchangeDAO.index().stream()
                .map(rate -> rate.mapToResponse(
                        currencyDAO.getById(rate.getBaseCurrencyId()),
                        currencyDAO.getById(rate.getTargetCurrencyId())
                ))
                .collect(Collectors.toList());
    }

    public ExchangeRatesResponse getCoupleCurrency(String code) {
        ExchangeData exchangeData = findExchangeData(code);
        ExchangeRate exchange = exchangeData.exchange();
        return exchange.mapToResponse(exchangeData.baseCurrency(), exchangeData.targetCurrency());
    }

    @Transactional
    public ExchangeRatesResponse update(String code, ExchangeRateDTO rateDto) {
        ExchangeData exchangeData = findExchangeData(code);
        BigDecimal rate = rateDto.rate();
        ExchangeRate exchange = exchangeData.exchange();
        exchangeDAO.update(exchange.getId(), rate);
        exchange.setRate(rate);
        return exchange.mapToResponse(exchangeData.baseCurrency(), exchangeData.targetCurrency());
    }

    public ExchangeResponse calculateExchange(String baseCode,
                                              String targetCode,
                                              BigDecimal amount) {
        Currency baseCurrency = currencyDAO.getByCode(baseCode);
        Currency targetCurrency = currencyDAO.getByCode(targetCode);
        int baseId = baseCurrency.getId();
        int targetId = targetCurrency.getId();
        BigDecimal rate = calculateRate(baseId, targetId);
        BigDecimal convertedAmount = amount.multiply(rate);
        return new ExchangeResponse(baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }

    BigDecimal calculateRate(int baseId, int targetId) {
        if (exchangeDAO.existCoupleCurrency(baseId, targetId)) {
            ExchangeRate exchangeCouple = exchangeDAO.findCoupleCurrency(baseId, targetId);
            return exchangeCouple.getRate();
        }
        if (exchangeDAO.existCoupleCurrency(targetId, baseId)) {
            ExchangeRate reverseExchange = exchangeDAO.findCoupleCurrency(targetId, baseId);
            BigDecimal reverseRate = reverseExchange.getRate();
            return BigDecimal.ONE.divide(reverseRate, 4);
        }
        return calculateRateUseUSD(baseId, targetId);
    }

    BigDecimal calculateRateUseUSD(int baseId, int targetId) {
        int usdId = currencyDAO.getIdByCode("USD");
        if (!exchangeDAO.existCoupleCurrency(usdId, baseId) || !exchangeDAO.existCoupleCurrency(usdId, targetId)) {
            throw new NotFoundException("Обменных курсов для этих валют нет");
        }
        ExchangeRate firstExchange = exchangeDAO.findCoupleCurrency(usdId, baseId);
        ExchangeRate secondExchange = exchangeDAO.findCoupleCurrency(usdId, targetId);
        BigDecimal firstRate = firstExchange.getRate();
        BigDecimal secondRate = secondExchange.getRate();
        return secondRate.divide(firstRate, 4);
    }

    ExchangeData findExchangeData(String code) {
        String baseCode = code.substring(0, 3);
        String targetCode = code.substring(3, 6);
        if (!currencyDAO.existByCode(baseCode)) {
            throw new NotFoundException("Не найдена такая валюта:" + baseCode);
        }
        if (!currencyDAO.existByCode(targetCode)) {
            throw new NotFoundException("Не найдена такая валюта:" + targetCode);
        }
        Currency baseCurrency = currencyDAO.getByCode(baseCode);
        Currency targetCurrency = currencyDAO.getByCode(targetCode);
        int baseCurrencyId = baseCurrency.getId();
        int targetCurrencyId = targetCurrency.getId();
        if (!exchangeDAO.existCoupleCurrency(baseCurrencyId, targetCurrencyId)) {
            throw new NotFoundException("Обменный курс для пары не найден");
        }
        ExchangeRate exchange = exchangeDAO.findCoupleCurrency(baseCurrencyId, targetCurrencyId);
        return new ExchangeData(baseCurrency, targetCurrency, exchange);
    }
}