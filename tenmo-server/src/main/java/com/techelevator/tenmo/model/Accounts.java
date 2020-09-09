package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Accounts {
	
	private Long accountId;
	private Long userId;
	private BigDecimal balance;
	
	
	public Accounts() {
		// TODO Auto-generated constructor stub
	}
	
	public Accounts(Long accountId, Long userId, BigDecimal balance) {
		super();
		this.accountId = accountId;
		this.userId = userId;
		this.balance = balance;
	}


	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public BigDecimal getBalance() {
		return balance.setScale(2, RoundingMode.HALF_UP);
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "Accounts "
				+ "[accountId=" + accountId + ", userId=" 
				+ userId + ", balance=" + balance + "]";
	}


	
	
	

}
