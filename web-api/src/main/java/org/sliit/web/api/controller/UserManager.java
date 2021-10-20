package org.sliit.web.api.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.sliit.web.api.services.UserService;
import org.sliit.web.api.services.UserServiceImp;

@Path("/api/user")
public class UserManager {

	UserService us = new UserServiceImp();
/*
	@PostMapping(value = "/customerRegistration", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String registerCustomer(@RequestBody Customer customer) {
		return us.registerCustomer(customer.getFirstName(), customer.getLastName(), customer.getInitials(),
				customer.getDob(), customer.getPhoneNo(), customer.getGender(), customer.getAddress(), customer.getLogin().getEmail(), customer.getLogin().getPassword()).toString();
	}
	
	@PostMapping(value = "/customerLogin", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String customerLogin(@RequestBody LoginRequest login) {
		return us.customerLogin(login.getEmail(), login.getPassword()).toString();
	}
	
	@PutMapping(value = "/updateCustomer", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String updateCustomer(@RequestBody Customer customer) {
		return us.updateCustomerDetails(customer.getFirstName(), customer.getLastName(), customer.getInitials(),
				customer.getDob(), customer.getPhoneNo(), customer.getGender(), customer.getAddress(), customer.getId()).toString();
	}
	
	@PutMapping(value = "/resetPassword", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String resetPassword(@RequestBody ResetPassword resetPassword) {
		return us.resetPassword(resetPassword.getCurrentPassword(), resetPassword.getNewPassword(), resetPassword.getEmail()).toString();
	}
	
	@DeleteMapping(value = "/deleteCustomer/{id}")
	public String deleteCustomer(@PathVariable String id) {
		return us.deleteCustomer(id).toString();
	}

	@GetMapping(value = "/")
	public String getAllCustomers() {
		return us.getCustomerList().toString();
	}

	@GetMapping(value = "/{id}")
	public String getCustomerById(@PathVariable String id) {
		return us.getCustomerDetails(id).toString();
	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(UserManager.class, args);
	}
*/
	
	    @GET
	    @Produces(MediaType.TEXT_PLAIN)
	    public String hello() {
	        return "Hello RESTEasy";
	    }
}
