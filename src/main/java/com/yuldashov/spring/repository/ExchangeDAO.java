package com.yuldashov.spring.repository;

import com.yuldashov.spring.dto.CurrencyResponse;
import com.yuldashov.spring.dto.ExchangeResponse;
import com.yuldashov.spring.expection.NotFoundException;
import com.yuldashov.spring.model.ExchangeRate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import javax.swing.plaf.PanelUI;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
public class ExchangeDAO {
    private final JdbcTemplate jdbcTemplate;

    public ExchangeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int create(int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO exchangeRates (base_currency_id,target_currency_id, rate) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, baseCurrencyId);
            ps.setInt(2, targetCurrencyId);
            ps.setBigDecimal(3, rate);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public List<ExchangeRate> index() {
        return jdbcTemplate.query("SELECT * FROM exchangeRates", new BeanPropertyRowMapper<>(ExchangeRate.class));
    }

    public ExchangeRate findCoupleCurrency(int baseCurrencyId, int targetCurrencyId) {
        return jdbcTemplate.queryForObject("SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?"
                , new BeanPropertyRowMapper<>(ExchangeRate.class), baseCurrencyId, targetCurrencyId);
    }

    public void update(int exchangeId, BigDecimal rate) {
        jdbcTemplate.update("UPDATE exchange_rates SET rate = ? WHERE id = ?", rate, exchangeId);
    }

    public boolean existCoupleCurrency(int baseCurrencyId, int targetCurrencyId) {
        return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?)"
                ,Boolean.class, baseCurrencyId, targetCurrencyId);
    }
}
