package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Accounts;
import com.techelevator.tenmo.model.User;
@Component
public class accountSqlDAO implements accountsDAO {
	
	private JdbcTemplate jdbcTemplate;
	
    public accountSqlDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	
	@Override
	public Accounts findAccountByAccountId(int accountId) { 
		String sql = "select * from accounts where account_id = ?";
		
		SqlRowSet results =jdbcTemplate.queryForRowSet(sql,accountId);
		 Accounts account = null;
		 while(results.next()) {
			account = mapRowToAccount(results);
	        }
		
		return account;
		
	}

	public List<Accounts> findAllAccountsByUserId(int userId) {
		 List<Accounts> accounts = new ArrayList<>();
		 String sql = "select * from accounts where user_id = ?";
		 SqlRowSet results = jdbcTemplate.queryForRowSet(sql,userId);
		  while(results.next()) {
	            Accounts account = mapRowToAccount(results);
	            accounts.add(account);
	        }
		
		return accounts;
	}
	
	
    private Accounts mapRowToAccount(SqlRowSet rs) {
        Accounts account = new Accounts();
        account.setAccountId(rs.getLong("account_id"));
        account.setUserId(rs.getLong("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));

        return account;
    }

	@Override
	public boolean update(Accounts account) {
		boolean tranferUpdated = false;
		
		String update = "update accounts set balance = ? where account_id = ?";

		int result = jdbcTemplate.update(update, account.getBalance() ,account.getAccountId());

		tranferUpdated = result == 1;
		
		return tranferUpdated;
	}

}
