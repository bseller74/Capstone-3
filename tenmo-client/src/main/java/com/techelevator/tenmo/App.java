package com.techelevator.tenmo;

import com.techelevator.tenmo.models.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

	private static final String USER_LIST_OPTION = "Show list of Users";
	private static final String ENTER_USER_NAME = "Enter username";
	private static final String USER_OPTIONS_BACK = "Back"; 
	private static final String[] USER_OPTIONS = {USER_LIST_OPTION, ENTER_USER_NAME,USER_OPTIONS_BACK };



    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private RestTemplate rest = new RestTemplate();

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {

		try {
			Accounts[] account = rest.exchange(API_BASE_URL + "/users/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Accounts[].class).getBody();
			System.out.println("Your account balance is : $" + account[0].getBalance());
		}catch(Exception e){
			System.out.println("Could not locate your account");
		}
	}

	private void viewTransferHistory() {

		Accounts[] account = null;
		Transfer[] transfers = null;
		try {
			account = rest.exchange(API_BASE_URL + "/users/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Accounts[].class).getBody();

			transfers = rest.exchange(API_BASE_URL + "account/" + account[0].getAccountId() +
					"/transfers", HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
		}catch(Exception e){
			System.out.println("Could not get Transfer History");
			return;
		}
		System.out.println("-------------------------------------------");
		System.out.println("Transfers");
		String formatHeader = "%-5s %-16s %-10s %n";
		System.out.printf(formatHeader, "ID", "From / To", "Amount");
		System.out.println("-------------------------------------------");


	
		for (Transfer tra : transfers ) {
			long i = 0;
			String name;
			
			if (tra.getTransfer_type_id() == 1L && tra.getAccount_from_id() == account[0].getAccountId()) {
				i = tra.getAccount_to_id();
				name = "To: ";
				
			}else if(tra.getTransfer_type_id() == 2L && account[0].getAccountId() == tra.getAccount_to_id()){
				
				i = tra.getAccount_from_id();
				name = "From: ";
			}else if(tra.getTransfer_type_id() == 2L){
				i = tra.getAccount_to_id();
				name = "To: ";
			}
			else{
				i = tra.getAccount_from_id();
				name = "From: ";
			}


			String username = rest.exchange(API_BASE_URL + "/users/" + i + "/find/" , HttpMethod.GET, makeAuthEntity(), String.class).getBody();
			String money = "$" + tra.getAmount();
			String formatRowContent = "%-5s %-6s %-9s %-10s %n";
			System.out.printf(formatRowContent, tra.getId(),name, username, money);

		}

		System.out.println();

		String getId = "Enter Transaction ID for further details or 0 to cancel";
		int transId = 0;
		while(true){
			try{
				String input = (String) console.getUserInput(getId);
				if(input.toUpperCase().equals("0")){
					break;
				}else{
					transId = Integer.parseInt(input);
				}

				boolean found = false;
				long id = Long.parseLong(input);
				for(Transfer transfer : transfers){
					if(id == transfer.getId()){
						found = true;
					break;
					}
				}
				if(!found){
					System.out.println("Invalid Transfer ID");
					return;
				}

			}catch(Exception e){
				System.out.println("Invalid ID");
			}

			Transfer singleTrans = null;
			try{
				singleTrans = rest.exchange(API_BASE_URL + "/transfers/" + transId, HttpMethod.GET, makeAuthEntity(), Transfer.class).getBody();
				String fromUser = rest.exchange(API_BASE_URL + "/users/" + singleTrans.getAccount_from_id() + "/find/" , HttpMethod.GET, makeAuthEntity(), String.class).getBody();
				String toUser = rest.exchange(API_BASE_URL + "/users/" + singleTrans.getAccount_to_id() + "/find/" , HttpMethod.GET, makeAuthEntity(), String.class).getBody();
				String type = singleTrans.getTransfer_type_id() == 1L ? "Request" : "Send";
				String status = singleTrans.getTransfer_status_id() == 2L ? "Approved" : (singleTrans.getTransfer_status_id() == 3L ?  "Rejected" : "Pending");

				System.out.println("-------------------------------------------");
				System.out.println("Transfer Details");
				System.out.println("-------------------------------------------");
				System.out.println("ID: " + singleTrans.getId());
				System.out.println("From: " + fromUser);
				System.out.println("To: " + toUser);
				System.out.println("Type: " + type);
				System.out.println("Status: " + status);
				System.out.println("Amount: $" + singleTrans.getAmount());
				System.out.println();

			}catch(Exception e){
				System.out.println("Could not find transfer");
			}


		}

	}

	private void viewPendingRequests() {

		Accounts[] account = null;
		Transfer[] requests = null;
		try {
			account = rest.exchange(API_BASE_URL + "/users/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Accounts[].class).getBody();

			requests = rest.exchange(API_BASE_URL + "account/" + account[0].getAccountId() + "/transfers?status=Pending", HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();

			if(requests.length == 0){
				System.out.println("No Pending Requests!");
				return;
			}
		}catch(Exception e){
			System.out.println("Could not get Pending Requests");
			return;
		}

		System.out.println("-------------------------------------------");
		System.out.println("Pending Requests");
		String formatHeader = "%-10s %-15s %-10s %n";
		System.out.printf(formatHeader, "ID", "To", "Amount");
		System.out.println("-------------------------------------------");

		for (Transfer transReq : requests ) {

			String username = rest.exchange(API_BASE_URL + "/users/" + transReq.getAccount_to_id() + "/find/" , HttpMethod.GET, makeAuthEntity(), String.class).getBody();
			String money = "$" + transReq.getAmount() ;
			System.out.printf(formatHeader, transReq.getId(),username,money);

		}

		System.out.println();
		String input = (String) console.getUserInput("Please enter Transfer ID to Approve/Reject (0 to Cancel)");
		if(!input.equals("0")){
			Transfer pendingTrans = null;
			try{
				pendingTrans = rest.exchange(API_BASE_URL + "/transfers/" + input, HttpMethod.GET, makeAuthEntity(), Transfer.class).getBody();

				boolean found = false;
				long id = Long.parseLong(input);
				for(Transfer pending : requests){
					if(id == pending.getId()){
						found = true;
						break;
					}
				}
				if(!found){
					System.out.println("Invalid Transfer ID");
					return;
				}

				String[] options = new String[]{"Approve", "Reject", "Back"};
				String choice = (String)console.getChoiceFromOptions(options);

				if(choice.equals("Approve") && account[0].getBalance().compareTo(pendingTrans.getAmount()) > 0){
					pendingTrans.setTransfer_status_id(2L);
				}else if(choice.equals("Reject")){
					pendingTrans.setTransfer_status_id(3L);
				}else if(choice.equals("Approve") && account[0].getBalance().compareTo(pendingTrans.getAmount()) < 0){
					System.out.println("Insufficient Funds!");
					return;
				}else{
					return;
				}

				boolean transfered = rest.exchange(API_BASE_URL + "/transfers/" + input, HttpMethod.PUT, makeTransferEntity(pendingTrans), boolean.class).getBody();



				if(transfered){
					System.out.println("Status Confirmed");
				}else{
					System.out.println("Issue with transfer");
				}

			}catch(Exception e){
				System.out.println("Transfer could not be found");
				return;
			}


		}else{
			return;
		}

	}

	private void sendBucks() {

		Transfer newTransfer = null;
		boolean transferred = false;
		BigDecimal sendAmount;


		String choice = (String)console.getChoiceFromOptions(USER_OPTIONS);
		String username = "";
		Accounts[] currentUserAccount = null;
		User toUser = null;
		Accounts[] toUserAccount = null;
		if(choice.equals(ENTER_USER_NAME)) {

			String getUser = "Send to";

			username = (String) console.getUserInput(getUser);

		}else if(choice.equals(USER_LIST_OPTION)){
			try {
				User[] allUsers = rest.exchange(API_BASE_URL + "/users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
				Object input = console.getChoiceFromOptions(allUsers);
				username = input.toString();
				System.out.println(input);
			}catch(Exception e){
				System.out.println("Could not get user");
			}
		}else{
			return;
		}

		try {
			currentUserAccount = rest.exchange(API_BASE_URL + "/users/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Accounts[].class).getBody();
			toUser = rest.exchange(API_BASE_URL + "/users/find/" + username, HttpMethod.GET, makeAuthEntity(), User.class).getBody();
			toUserAccount = rest.exchange(API_BASE_URL + "/users/" + toUser.getId(), HttpMethod.GET, makeAuthEntity(), Accounts[].class).getBody();
		}catch (Exception e) {
			System.out.println("User could not be found");
		}

		String send = "Enter Amount to Send (0 to go back)";

		while(true) {
			try {
				String strAmt = (String) console.getUserInput(send);
				if (strAmt.equals("0")) {
					return;
				}
				sendAmount = new BigDecimal(strAmt);
				break;
			} catch (Exception e) {
				System.out.println("Invalid Amount");
			}
		}


		try{
			if (toUserAccount.length == 1 && sendAmount.compareTo(currentUserAccount[0].getBalance()) != 1) {
				newTransfer = new Transfer(2L, 2L, currentUserAccount[0].getAccountId(), toUserAccount[0].getAccountId(), sendAmount);
				transferred = rest.exchange(API_BASE_URL + "/transfers/send", HttpMethod.POST, makeTransferEntity(newTransfer), boolean.class).getBody();
			}

			if (transferred) {
				System.out.println("Transfer Success");
			} else {
				System.out.println("Transfer Failed - Insufficient Funds");;
			}
		}catch(Exception e){
			System.out.println("Issues with Transfer");
		}
	}

	private void requestBucks() {
		Transfer newTransfer = null;
		boolean transferred = false;
		BigDecimal requestAmount;

		//Prompt user for user list or enter user name
		String choice = (String)console.getChoiceFromOptions(USER_OPTIONS);
		String username = "";
		Accounts[] currentUserAccount = null;
		User toUser = null;
		Accounts[] toUserAccount = null;
		if(choice.equals(ENTER_USER_NAME)) {

			String getUser = "Request from";

			username = (String) console.getUserInput(getUser);

		}else if(choice.equals(USER_LIST_OPTION)){
			try {
				User[] allUsers = rest.exchange(API_BASE_URL + "/users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
				Object input = console.getChoiceFromOptions(allUsers);
				username = input.toString();
				System.out.println(input);
			}catch(Exception e){
				System.out.println("Could not get user");
			}
		} else {
			return;
		}

		try {
			currentUserAccount = rest.exchange(API_BASE_URL + "/users/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Accounts[].class).getBody();
			toUser = rest.exchange(API_BASE_URL + "/users/find/" + username, HttpMethod.GET, makeAuthEntity(), User.class).getBody();
			toUserAccount = rest.exchange(API_BASE_URL + "/users/" + toUser.getId(), HttpMethod.GET, makeAuthEntity(), Accounts[].class).getBody();
		}catch (Exception e) {
			System.out.println("User could not be found");
		}


		String request = "Enter Amount to Request (0 to go back)";


		while(true) {
			try {
				String strAmt = (String) console.getUserInput(request);
				if (strAmt.equals("0")) {
					return;
				}
				requestAmount = new BigDecimal(strAmt);
				break;
			} catch (Exception e) {
				System.out.println("Invalid Amount");
			}
		}

		try{
			if (toUserAccount.length == 1) {
				newTransfer = new Transfer(1L, 1L, toUserAccount[0].getAccountId(), currentUserAccount[0].getAccountId(), requestAmount);
				transferred = rest.exchange(API_BASE_URL + "/transfers/request", HttpMethod.POST, makeTransferEntity(newTransfer), boolean.class).getBody();
			}

			if (transferred) {
				System.out.println("Request Success");
			}
		}catch(Exception e){
			System.out.println("Issues with Request");
		}
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	private HttpEntity makeAuthEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}

	private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(currentUser.getToken());
		HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
		return entity;
	}
}
