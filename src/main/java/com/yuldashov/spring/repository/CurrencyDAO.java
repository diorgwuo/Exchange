package com.yuldashov.spring.repository;

import com.yuldashov.spring.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CurrencyDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CurrencyDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Currency> index() {
        return jdbcTemplate.query("SELECT * FROM currencies", new BeanPropertyRowMapper<>(Currency.class));
    }

    public int create(Currency currency) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO currencies (fullname,code,sign) VALUES (?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, currency.getFullName());
                    ps.setString(2, currency.getCode());
                    ps.setString(3, currency.getSign());
                    return ps;
                },
                keyHolder);
        return keyHolder.getKey().intValue();
    }

    public boolean existByCode(String code) {
        return jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM currencies WHERE code = ?)", Boolean.class, code);
    }

    public Currency getByCode(String code) {
        return jdbcTemplate.queryForObject("SELECT * FROM currencies WHERE code = ?", new BeanPropertyRowMapper<>(Currency.class), code);
    }

    public Currency getById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM currencies WHERE id = ?"
                , new BeanPropertyRowMapper<>(Currency.class), id);
    }

    public int getCodeById(String сode) {
        Currency currency = jdbcTemplate.queryForObject("SELECT * FROM currencies WHERE code = ?"
                , new BeanPropertyRowMapper<>(Currency.class), сode);
        return currency.getId();
    }
}

