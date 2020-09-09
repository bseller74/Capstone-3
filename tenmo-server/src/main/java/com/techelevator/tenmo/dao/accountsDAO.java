package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.util.List;

import com.techelevator.tenmo.model.Accounts;
import com.techelevator.tenmo.model.User;

public interface accountsDAO {
	
	 Accounts findAccountByAccountId(int accountId);

	List<Accounts> findAllAccountsByUserId(int userId);
	
	boolean update(Accounts account);
	 

}
