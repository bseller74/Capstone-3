package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDAO {
    List<Transfer> allTransfersByAccountId(long accountId);
    List<Transfer> allTransfersByStatus(long accountId, String status);
    Transfer transferDetailByTransId(long transferId);
    boolean createNewTransfer(Transfer transfer);
    boolean updateTransferStatus(Transfer transfer);

}
