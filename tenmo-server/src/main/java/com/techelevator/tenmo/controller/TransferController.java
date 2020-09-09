package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDAO;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {

    @Autowired
    TransferDAO transferDao;

    @RequestMapping(path = "account/{id}/transfers", method = RequestMethod.GET)
    public List<Transfer> getAllTransById(@PathVariable long id, @RequestParam(defaultValue = "") String status){
        List<Transfer> transfers = null;
        if(status.equals("")) {
            transfers = transferDao.allTransfersByAccountId(id);
        }else{
            transfers = transferDao.allTransfersByStatus(id, status);
        }
        return transfers;
    }

    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
    public Transfer getTransferDetails(@PathVariable long id){
        return transferDao.transferDetailByTransId(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers/send", method = RequestMethod.POST)
    public boolean addTransfer(@RequestBody Transfer transfer){
        return transferDao.createNewTransfer(transfer) && transferDao.updateTransferStatus(transfer);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers/request", method = RequestMethod.POST)
    public boolean addRequestTransfer(@RequestBody Transfer transfer){
        return transferDao.createNewTransfer(transfer);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.PUT)
    public boolean updateTransfer(@RequestBody Transfer transfer, @PathVariable long id){
        return transferDao.updateTransferStatus(transfer);
    }

}
