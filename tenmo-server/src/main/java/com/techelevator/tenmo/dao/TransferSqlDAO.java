package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Accounts;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransferSqlDAO implements TransferDAO {

    private JdbcTemplate jdbcTemplate;
    private accountSqlDAO accntDao;

    public TransferSqlDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.accntDao = new accountSqlDAO(this.jdbcTemplate);
    }

    @Override
    public List<Transfer> allTransfersByAccountId(long accountId) {
        List<Transfer> allTransfer = new ArrayList<>();
        String select = "select * from transfers where (transfer_status_id = 2 or transfer_status_id = 3) " +
                        "and (account_from = ? or account_to = ?)";
        SqlRowSet result = jdbcTemplate.queryForRowSet(select, accountId, accountId);
        while(result.next()){
            Transfer transfer = createTransfer(result);
            allTransfer.add(transfer);
        }
        return allTransfer;
    }

    @Override
    public List<Transfer> allTransfersByStatus(long accountId, String status) {
        List<Transfer> transfersByStatus = new ArrayList<>();
        String select = "select * from transfers " +
                        "join transfer_statuses on transfers.transfer_status_id = transfer_statuses.transfer_status_id " +
                        "where transfer_statuses.transfer_status_desc = ? " +
                        "and transfers.account_from = ?";

        SqlRowSet result = jdbcTemplate.queryForRowSet(select, status, accountId);
        while(result.next()){
            Transfer transfer = createTransfer(result);
            transfersByStatus.add(transfer);
        }
        return transfersByStatus;
    }

    @Override
    public Transfer transferDetailByTransId(long transferId) {
        Transfer transfer= null;
        String select = "select * from transfers where transfer_id = ? ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(select, transferId);

        if(result.next()){
            transfer = createTransfer(result);
        }
        return transfer;
    }

    @Override
    public boolean createNewTransfer(Transfer transfer) {
       boolean newTransfer = false;

        String insert = "insert " +
                        "into transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                        "values(?,?,?,?,?)";
        newTransfer = jdbcTemplate.update(insert, transfer.getTransfer_type_id(), transfer.getTransfer_status_id(), transfer.getAccount_from_id(), transfer.getAccount_to_id(), transfer.getAmount()) == 1;

        return newTransfer;
    }

    @Override
    public boolean updateTransferStatus(Transfer transfer) {
        boolean transferUpdated = false;
        boolean toBalanceUpdated = false;
        boolean fromBalanceUpdate = false;

        String update = "update transfers " +
                        "set transfer_status_id = ? " +
                        "where transfer_id = ? ";
        int result = jdbcTemplate.update(update, transfer.getTransfer_status_id(), transfer.getId());

        transferUpdated = result == 1;

        if(transfer.getTransfer_status_id() == 2L) {
            int fromId = (int) transfer.getAccount_from_id();
            int toId = (int) transfer.getAccount_to_id();

            Accounts fromAccount = accntDao.findAccountByAccountId(fromId);
            Accounts toAccount = accntDao.findAccountByAccountId(toId);

            fromAccount.setBalance(fromAccount.getBalance().subtract(transfer.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(transfer.getAmount()));

            fromBalanceUpdate = accntDao.update(fromAccount);
            toBalanceUpdated = accntDao.update(toAccount);
        }

        return transferUpdated || fromBalanceUpdate && toBalanceUpdated;
    }

    private Transfer createTransfer(SqlRowSet result){
        Transfer transfer = new Transfer();
        transfer.setId(result.getLong("transfer_id"));
        transfer.setTransfer_type_id(result.getLong("transfer_type_id"));
        transfer.setTransfer_status_id(result.getLong("transfer_status_id"));
        transfer.setAccount_from_id(result.getLong("account_from"));
        transfer.setAccount_to_id(result.getLong("account_to"));
        transfer.setAmount(result.getBigDecimal("amount"));
        return transfer;
    }
}
