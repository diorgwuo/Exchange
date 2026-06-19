package com.yuldashov.spring.repository;

import com.yuldashov.spring.dto.CurrencyRequest;
import com.yuldashov.spring.dto.CurrencyResponse;
import com.yuldashov.spring.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CurrencyDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CurrencyDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Currency> index() {
        return jdbcTemplate.query("SELECT * FROM Currencies", new BeanPropertyRowMapper<>(Currency.class));
    }

    public void create(Currency currency) {
        jdbcTemplate.update("INSERT INTO Currencies (FullName,Code,Sign) VALUES (?,?,?)",
                 currency.getFullName(), currency.getCode(), currency.getSign());
    }

    public Currency lastCreate () {
        return jdbcTemplate.queryForObject("SELECT * FROM Currencies ORDER BY id DESC LIMIT 1",
                new BeanPropertyRowMapper<>(Currency.class));
    }

    public boolean existByCode (String code) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Currencies WHERE code = ?", Integer.class, code);
        return count!=null && count>0;
    }

    public Currency getByCode(String code) {
        return jdbcTemplate.queryForObject("SELECT * FROM Currencies WHERE code = ?",new BeanPropertyRowMapper<>(Currency.class), code);
    }

    public Currency getById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM Currencies WHERE id = ?"
        ,new BeanPropertyRowMapper<>(Currency.class), id);
    }

    public int getCodeById(String Code) {
        Currency currency = jdbcTemplate.queryForObject("SELECT * FROM Currencies WHERE code = ?"
                ,new BeanPropertyRowMapper<>(Currency.class), Code);
        return currency.getId();
    }
}

