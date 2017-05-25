package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mysql.cj.api.jdbc.Statement;

import utils.Constants;

@Path("/registrationService")
public class Register {

	Connection con = null;

	@GET
	@Path("/mail")
	@Produces(MediaType.TEXT_HTML)
	public void mailingCheck() {
		Constants.sendMail();
	}

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
						+ year + "','" + colg.substring(0, Math.min(colg.length(), 44)) + "','"
						+ dept.substring(0, Math.min(dept.length(), 28)) + "','" + defaultValue + "','" + defaultValue
						+ "','" + defaultValue + "','" + defaultValue + "','" + defaultValue + "','" + defaultValue
						+ "','" + defaultValue + "','" + defaultValue + "'," + defaultValueInt + "," + defaultValueInt
						+ ",'" + defaultValue + "','" + Integer.parseInt(check) + "'," + defaultValueInt + ","
						+ defaultValueInt + ", '" + dateTime + "','" + dateInString + "','" + defaultValue + "','"
						+ dateTime + "','" + defaultValue + "')";

				PreparedStatement psInner = con.prepareStatement(sqlInner, Statement.RETURN_GENERATED_KEYS);

				psInner.executeUpdate();
				ResultSet rsInner = psInner.getGeneratedKeys();

				if (rsInner.next()) {
					rsLastGeneratedAutoIncrementId = rsInner.getInt(1);
				}

				result = rsLastGeneratedAutoIncrementId;

			}

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(result);
	}

	@POST
	@Path("/registerPassword")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String registerPassword(@FormParam("id") String id, @FormParam("password") String password,
			@FormParam("industry1") String industry1, @FormParam("industry2") String industry2,
			@FormParam("industry3") String industry3, @FormParam("company1") String company1,
			@FormParam("company2") String company2, @FormParam("company3") String company3,
			@FormParam("phone") String phone, @FormParam("interest") String interest,
			@FormParam("location") String location) {

		int result = 0;
		String sqlInner = null;

		try {

			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "UPDATE `user_login` SET password=?,industry1=?,industry2=?,industry3=?,company1=?,company2=?,company3=?,phone=?,location=?,interest=?,last_loggedin=? WHERE id=?";
 
			PreparedStatement psInner = con.prepareStatement(sqlInner);
			psInner.setString(1, Constants.md5(password));
			psInner.setString(2, industry1);
			psInner.setString(3, industry2);
			psInner.setString(4, industry3);
			psInner.setString(5, company1);
			psInner.setString(6, company2);
			psInner.setString(7, company3);
			psInner.setString(8, phone);
			psInner.setString(9, location);
			psInner.setString(10, interest);
			psInner.setString(11, dateTime);
			psInner.setString(12, id);

			if (psInner.executeUpdate() > 0) {
				result = 1;
			}

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(result);
	}

}
