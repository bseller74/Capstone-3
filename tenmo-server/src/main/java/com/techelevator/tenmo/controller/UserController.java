package com.techelevator.tenmo.controller;

import java.util.List;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.dao.accountsDAO;
import com.techelevator.tenmo.model.Accounts;
import com.techelevator.tenmo.model.User;

@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {
	
	@Autowired
	private accountsDAO accountDAO;
	@Autowired
	private UserDAO userDAO;


	@RequestMapping(path = "accounts/{id}", method = RequestMethod.GET)
	public Accounts findAccountByAccountId(@PathVariable(value = "id") int accountId) {
		return accountDAO.findAccountByAccountId(accountId);
	}


	@RequestMapping(path = "users/{id}", method = RequestMethod.GET)
	public List<Accounts> findAllAccountsByUserId(@PathVariable(value = "id") int userId) {

		return accountDAO.findAllAccountsByUserId(userId);

	}


	@RequestMapping(path = "users", method = RequestMethod.GET)
	public List<User> findAll() {
		return userDAO.findAll();

	}

	@RequestMapping(path = "users/find/{username}", method = RequestMethod.GET)
	public User findByUsername(@PathVariable String username) {

		return userDAO.findByUsername(username);

	}
	
	@RequestMapping(path = "users/{user_id}/find/", method = RequestMethod.GET)
	public String findByUsername(@PathVariable long user_id) {

		return userDAO.findNameByUserId(user_id);

	}

	@PreAuthorize("permitAll")
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(path = "", method = RequestMethod.POST)
	public boolean create(@Valid @RequestBody String username, String password) {

		return userDAO.create(username, password);
	}

}
