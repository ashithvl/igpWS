package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
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

@Path("/registrationService")
public class Register {

	Connection con = null;

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String register(@FormParam("name") String name, @FormParam("email") String email,
			@FormParam("year") String year, @FormParam("colg") String colg, @FormParam("dept") String dept,
			@FormParam("check") String check) {

		int result = 0;
		int rsLastGeneratedAutoIncrementId = 0;

		try {

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `user_login` WHERE email=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, email);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {

				return String.valueOf(result);

			} else {

				String defaultValue = "";
				int defaultValueInt = 0;

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				String dateTime = dtf.format(now);

				String dateInString = "0000-00-00 00:00:00";

				String sqlInner = "INSERT INTO user_login (fname, event_admin, email, password, passout, college, department, imgname, industry1, industry2, industry3, company1, company2, company3, phone, status, interview_status, location, interest, intw_schedule, assessment, created_by, last_loggedin, created_user, modified_by, modified_user) "
						+ "VALUES('" + name + "'," + defaultValueInt + ",'" + email + "','" + defaultValue + "','"
						+ year + "','" + colg + "','" + dept + "','" + defaultValue + "','" + defaultValue + "','"
						+ defaultValue + "','" + defaultValue + "','" + defaultValue + "','" + defaultValue + "','"
						+ defaultValue + "','" + defaultValue + "'," + defaultValueInt + "," + defaultValueInt + ",'"
						+ defaultValue + "','" + Integer.parseInt(check) + "'," + defaultValueInt + ","
						+ defaultValueInt + ", '" + dateTime + "','" + dateInString + "','" + defaultValue + "','"
						+ dateTime + "','" + defaultValue + "')";

				PreparedStatement psInner = con.prepareStatement(sqlInner, Statement.RETURN_GENERATED_KEYS);

				psInner.executeUpdate();
				ResultSet rsInner = psInner.getGeneratedKeys();

				if (rsInner.next()) {
					rsLastGeneratedAutoIncrementId = rsInner.getInt(1);
				}

				/*
				 * int rsLastGeneratedAutoIncrementId =
				 * psInner.executeUpdate(sqlInner,
				 * Statement.RETURN_GENERATED_KEYS);
				 */

				result = rsLastGeneratedAutoIncrementId;

			}

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(result);
	}

}
