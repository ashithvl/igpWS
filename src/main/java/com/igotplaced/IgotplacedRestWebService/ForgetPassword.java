package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mysql.cj.api.jdbc.Statement;

import utils.Constants;

@Path("/ForgetPasswordService")
public class ForgetPassword {
	Connection con = null;

	@POST
	@Path("/forgotPassword")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String register(@FormParam("email") String email) {

		int result = 0;

		try {

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `user_login` WHERE email=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, email);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {

				result = 1;

			}

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(result);
	}
}
