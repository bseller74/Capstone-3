package com.techelevator.tenmo;

import com.techelevator.tenmo.dao.TransferSqlDAO;
import com.techelevator.tenmo.model.Transfer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;

@SpringBootTest
class TenmoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test_transfers(){

    }
}
