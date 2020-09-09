package com.techelevator.tenmo.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Transfer {
	
    private long id;
    private long transfer_type_id;
    private long transfer_status_id;
    private long account_from_id;
    private long account_to_id;
    private BigDecimal amount;

    public Transfer(){

    }
    public Transfer(long transfer_type_id, long transfer_status_id, long account_from_id, long account_to_id, BigDecimal amount) {
        this.transfer_type_id = transfer_type_id;
        this.transfer_status_id = transfer_status_id;
        this.account_from_id = account_from_id;
        this.account_to_id = account_to_id;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTransfer_type_id() {
        return transfer_type_id;
    }

    public void setTransfer_type_id(long transfer_type_id) {
        this.transfer_type_id = transfer_type_id;
    }

    public long getTransfer_status_id() {
        return transfer_status_id;
    }

    public void setTransfer_status_id(long transfer_status_id) {
        this.transfer_status_id = transfer_status_id;
    }

    public long getAccount_from_id() {
        return account_from_id;
    }

    public void setAccount_from_id(long account_from_id) {
        this.account_from_id = account_from_id;
    }

    public long getAccount_to_id() {
        return account_to_id;
    }

    public void setAccount_to_id(long account_to_id) {
        this.account_to_id = account_to_id;
    }

    public BigDecimal getAmount() {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
	

}
