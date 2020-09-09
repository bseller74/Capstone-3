package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Accounts;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class accountSqlDAOTest {

    @Test
    void test_update() throws SQLException {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        accountSqlDAO accntDao = new accountSqlDAO(jdbcTemplate);
        Accounts test = new Accounts(3L, 3L, new BigDecimal(900));
        assertEquals(true, accntDao.update(test));

        dataSource.getConnection().rollback();
        dataSource.destroy();

    }

}
