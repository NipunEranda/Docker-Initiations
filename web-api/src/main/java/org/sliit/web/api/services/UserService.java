package org.sliit.web.api.services;

import org.json.JSONArray;
import org.json.JSONObject;

public interface UserService {

	// POST
	public JSONObject registerUser(String firstName, String lastName, String initials, String dob, String phoneNo,
			String gender, String address, String email, String password);
	
	public JSONObject userLogin(String email, String password);

	// PUT
	public JSONObject updateUserDetails(String firstName, String lastName, String initials, String dob,
			String phoneNo, String gender, String address, String id);
	
	public JSONObject resetPassword(String currentPassword, String newPassowrd, String email);

	// DELETE
	public JSONObject deleteUser(String id);

	// GET
	public JSONObject getUserDetails(String id);

	
	public JSONArray getUserList();

}
