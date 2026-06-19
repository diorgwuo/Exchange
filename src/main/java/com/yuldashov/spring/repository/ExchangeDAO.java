package com.yuldashov.spring.repository;

import com.yuldashov.spring.dto.CurrencyResponse;
import com.yuldashov.spring.dto.ExchangeResponse;
import com.yuldashov.spring.expection.NotFoundException;
import com.yuldashov.spring.model.ExchangeRate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExchangeDAO {
    private final JdbcTemplate jdbcTemplate ;

    public ExchangeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
        jdbcTemplate.update("INSERT INTO ExchangeRates (base_currency_id,target_currency_id, rate) VALUES (?,?,?)"
                , baseCurrencyId, targetCurrencyId, rate);
    }

    public ExchangeRate lastCreate() {
        return jdbcTemplate.queryForObject("SELECT * FROM ExchangeRates ORDER BY id DESC LIMIT 1"
        , new BeanPropertyRowMapper<>(ExchangeRate.class));
    }

    public List<ExchangeRate> index() {
        return jdbcTemplate.query("SELECT * FROM ExchangeRates", new BeanPropertyRowMapper<>(ExchangeRate.class));
    }

    public ExchangeRate findCoupleCurrency(int baseCurrencyId, int targetCurrencyId) {

        try {
            return jdbcTemplate.queryForObject("SELECT * FROM ExchangeRates WHERE base_currency_id = ? AND target_currency_id = ?"
                    , new BeanPropertyRowMapper<>(ExchangeRate.class), baseCurrencyId, targetCurrencyId);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void update(int exchangeId ,BigDecimal rate) {
        jdbcTemplate.update("UPDATE ExchangeRates SET rate = ? WHERE id = ?",rate,exchangeId);
    }
}
