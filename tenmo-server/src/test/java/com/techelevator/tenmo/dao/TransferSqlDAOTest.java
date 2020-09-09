package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TransferSqlDAOTest {



    @Test
    void updateTransfer() throws SQLException {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        TransferSqlDAO dao = new TransferSqlDAO(jdbcTemplate);

        Transfer test = new Transfer(2L,2L,3L,4L, new BigDecimal(100));

        assertEquals(true,dao.updateTransferStatus(test));

        dataSource.getConnection().rollback();
        dataSource.destroy();
    }
}