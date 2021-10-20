package org.sliit.web.api.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.sliit.web.api.model.LoginRequest;
import org.sliit.web.api.model.LoginResponse;
import org.sliit.web.api.model.Role;
import org.sliit.web.api.util.Crypt;
import org.sliit.web.api.util.DBConnection;
import org.sliit.web.api.util.RandomCode;
import org.sliit.web.api.util.SQLQueryManager;
import org.sliit.web.api.util.lambdaworks.crypto.SCryptUtil;

public class DBManager {

	private static SQLQueryManager sqlObject;
	private static DBManager dbObject;

	private DBManager() {
	}

	public static DBManager getInstance() {
		if (dbObject == null) {
			synchronized (DBManager.class) {
				if (dbObject == null) {
					dbObject = new DBManager();
					sqlObject = SQLQueryManager.getInstance();
				}
			}
		}
		return dbObject;
	}

	private static JSONObject checkEmailAvailability(String email) {
		JSONObject j = new JSONObject();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection con = null;

		try {
			con = DBConnection.connect();
			ps = con.prepareStatement(sqlObject.getUSER_CHECKEMAILAVAILABILITY());
			ps.setString(1, email);
			rs = ps.executeQuery();
			if (rs != null) {
				if (rs.first()) {
					j.put("status", "error");
					j.put("reason", "email already exist!");
				} else {
					j.put("status", "success");
				}
			}

		} catch (Exception e) {
			j.put("status", "error");
		}
		return j;
	}

	// Login Management Class
	static class LoginClass {

		public static JSONObject login(String email, String password) {

			JSONObject j = new JSONObject();

			PreparedStatement ps_verifyLogin = null;
			ResultSet rs_verifyLogin = null;
			Connection con = null;
			CallableStatement stm = null;

			try {
				con = DBConnection.connect();
				ps_verifyLogin = con.prepareStatement(sqlObject.getLOGIN_VERIFY());
				ps_verifyLogin.setString(1, email);

				rs_verifyLogin = ps_verifyLogin.executeQuery();
				if (rs_verifyLogin.first() != false && SCryptUtil.check(password, rs_verifyLogin.getString(5))) {
					if (rs_verifyLogin.getInt(6) == 1) {

						Crypt ed = new Crypt(rs_verifyLogin.getString(7), rs_verifyLogin.getLong(3),
								rs_verifyLogin.getLong(2), email);
						String authString = ed.encode();

						j.put("status", "success");
						j.put("loginId", rs_verifyLogin.getLong(1));
						j.put("loginRole", rs_verifyLogin.getLong(2));
						j.put("userId", rs_verifyLogin.getLong(3));
						j.put("firstName", rs_verifyLogin.getString(4));
						j.put("loginCount", rs_verifyLogin.getInt(8) + 1);
						j.put("authString", authString);

						stm = con.prepareCall(sqlObject.getINCREASE_LOGIN_COUNT());
						stm.setInt(1, rs_verifyLogin.getInt(1));
						stm.registerOutParameter(2, Types.INTEGER);
						stm.execute();

					} else {
						j.put("status", "error");
						j.put("reason", "Email Confirmation is not validated");
					}
				} else {
					j.put("status", "error");
					j.put("userId", 0);
					j.put("authString", "");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs_verifyLogin != null) {
					try {
						rs_verifyLogin.close();
					} catch (Exception e) {
						/* ignored */}
				}
				if (ps_verifyLogin != null) {
					try {
						ps_verifyLogin.close();
					} catch (Exception e) {
						/* ignored */}
				}
			}

			return j;
		}

		private static JSONObject verifyPassword(String email, String currentPassword) {

			JSONObject j = new JSONObject();

			Connection con = null;
			PreparedStatement ps_verifyPassword = null;

			try {

				con = DBConnection.connect();
				ps_verifyPassword = con.prepareStatement(sqlObject.getVERIFY_PASSWORD());
				ps_verifyPassword.setString(1, email);

				ResultSet rs_verifyPassword = ps_verifyPassword.executeQuery();

				if (rs_verifyPassword.first() != false
						&& SCryptUtil.check(currentPassword, rs_verifyPassword.getString(1))) {
					j.put("status", "success");
				} else {
					j.put("status", "error");
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (ps_verifyPassword != null) {
					try {
						ps_verifyPassword.close();
					} catch (Exception e) {
						/* ignored */}
				}
			}

			return j;
		}

		public static JSONObject resetPassword(String email, String currentPassword, String newPassword) {
			JSONObject j = new JSONObject();

			Connection con = null;
			CallableStatement stm = null;
			try {
				con = DBConnection.connect();
				JSONObject passwordVerification = verifyPassword(email, currentPassword);
				if (passwordVerification.getString("status").equalsIgnoreCase("success")) {
					System.out.println(newPassword);
					stm = con.prepareCall(sqlObject.getCHANGE_PASSWORD());
					stm.setString(1, SCryptUtil.scrypt(newPassword, 16, 16, 16));
					stm.setString(2, email);
					stm.registerOutParameter(3, Types.INTEGER);
					stm.execute();
					if (stm.getInt(3) == 1) {
						j.put("status", "success");
					} else {
						j.put("status", "error");
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				j.put("status", e.getMessage());
			} finally {
				if (stm != null) {
					try {
						stm.close();
					} catch (Exception e) {
					}
				}
			}

			return j;
		}
	}

	// Customer Management Class

	static class CustomerClass {

		public static JSONObject registerCustomer(String firstName, String lastName, String initials, String dob,
				String phoneNo, String gender, String address, String email, String password) {
			JSONObject j = new JSONObject();
			Connection con = null;
			CallableStatement stm;

			try {
				if (checkEmailAvailability(email).getString("status").equalsIgnoreCase("success")) {
					con = DBConnection.connect();
					stm = con.prepareCall(sqlObject.getUSER_REGISTRATION());
					String generatedSecuredPasswordHash = SCryptUtil.scrypt(password, 16, 16, 16);
					String code = RandomCode.GenCode(8);
					stm.setString(1, firstName);
					stm.setString(2, lastName);
					stm.setString(3, initials);
					stm.setString(4, dob);
					stm.setString(5, phoneNo);
					stm.setString(6, gender);
					stm.setString(7, address);
					stm.setString(8, email);
					stm.setString(9, generatedSecuredPasswordHash);
					stm.setLong(10, Long.parseLong("3"));
					stm.setString(11, code);
					stm.registerOutParameter(12, Types.INTEGER);
					stm.execute();
					if (stm.getInt(12) == 1) {
						j.put("status", "success");
					} else {
						j.put("status", "error");
					}
				} else {
					j.put("status", "error");
					j.put("reason", "email already exist");
				}

			} catch (Exception e) {
				return j.put("status", e.getMessage());
			}

			return j;
		}

		public static JSONObject updateUserDetails(String id, String firstName, String lastName, String initials,
				String dob, String phoneNo, String gender, String address) {
			JSONObject j = new JSONObject();

			Connection con = null;
			CallableStatement stm = null;

			try {

				con = DBConnection.connect();
				stm = con.prepareCall(sqlObject.getUPDATE_USER_DETAILS());
				stm.setString(1, firstName);
				stm.setString(2, lastName);
				stm.setString(3, initials);
				stm.setString(4, dob);
				stm.setString(5, phoneNo);
				stm.setString(6, gender);
				stm.setString(7, address);
				stm.setLong(8, Long.parseLong(id));
				stm.registerOutParameter(9, Types.INTEGER);
				stm.execute();
				if (stm.getInt(9) == 1) {
					j.put("status", "success");
				} else {
					j.put("status", "error");
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (stm != null) {
					try {
						stm.close();
					} catch (Exception e) {
					}
				}
			}

			return j;
		}

		/*public static JSONArray getAllUsers() {
			JSONArray j = null;

			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			ArrayList<Customer> customerList = new ArrayList<Customer>();

			try {
				con = DBConnection.connect();
				ps = con.prepareStatement(sqlObject.getALlUser);
				rs = ps.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						LoginResponse l = new LoginResponse();
						l.setEmail(rs.getString(13));
						l.setPassword("");
						l.setLoginRole(Long.parseLong(rs.getString(15)));
						l.setLoginId(Long.parseLong(rs.getString(1)));
						Customer c = new Customer(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
								rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), l);
						customerList.add(c);
					}
					j = new JSONArray(customerList);
				} else {
					j = null;
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
				if (ps != null) {
					try {
						ps.close();
					} catch (Exception e) {
					}
				}
			}
			return j;
		}*/

		/*public static JSONObject getUserById(String id) {
			JSONObject j = new JSONObject();

			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				con = DBConnection.connect();
				ps = con.prepareStatement(sqlObject.getCUSTOMER_GETCUSTOMERBYID());
				ps.setLong(1, Long.parseLong(id));
				rs = ps.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						j.put("firstName", rs.getString(2));
						j.put("lastName", rs.getString(3));
						j.put("initials", rs.getString(4));
						j.put("dob", rs.getString(5));
						j.put("phoneNo", rs.getString(6));
						j.put("gender", rs.getString(7));
						j.put("address", rs.getString(8));
						j.put("email", rs.getString(13));
						j.put("roleId", rs.getString(15));
					}
				} else {
					j.put("status", "error");
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
				if (ps != null) {
					try {
						ps.close();
					} catch (Exception e) {
					}
				}
			}
			return j;
		}*/

		public static JSONObject deleteUser(String id) {
			JSONObject j = new JSONObject();

			Connection con = null;
			CallableStatement stm = null;

			try {

				con = DBConnection.connect();
				stm = con.prepareCall(sqlObject.getUSER_DELETEUSER());
				stm.setLong(1, Long.parseLong(id));
				stm.registerOutParameter(2, Types.INTEGER);
				stm.execute();
				if (stm.getInt(2) == 1) {
					j.put("status", "success");
				} else {
					j.put("status", "error");
				}

			} catch (Exception e) {
				e.printStackTrace();
				j.put("status", e.getMessage());
			} finally {
				if (stm != null) {
					try {
						stm.close();
					} catch (Exception e) {
					}
				}
			}

			return j;
		}
	}
}
