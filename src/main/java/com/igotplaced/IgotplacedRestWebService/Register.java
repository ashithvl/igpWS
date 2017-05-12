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
				

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now(); 
				String dateTime = dtf.format(now);
				
				String sqlInner = "INSERT INTO user_login (fname, email, passout, college, department, interest, created_by, modified_by) VALUES("
						+name+",'"+email+"', "+year+","+colg+", "+dept+","+Integer.parseInt(check)+", '"+dateTime+"','"+dateTime+"')";


				PreparedStatement psInner = con.prepareStatement(sqlInner);
				/*psInner.setString(1, name);
				psInner.setString(2, email);
				psInner.setString(3, year);
				psInner.setString(4, colg);
				psInner.setString(5, dept);
				psInner.setInt(6, Integer.parseInt(check));
				psInner.setString(7, dateTime);
				psInner.setString(8, dateTime);*/
				
				psInner.executeUpdate();
				ResultSet rsInner = psInner.getGeneratedKeys();
				
				if(rsInner.next()){
					rsLastGeneratedAutoIncrementId = rsInner.getInt(1);
				}
				

				
				/*int rsLastGeneratedAutoIncrementId = psInner.executeUpdate(sqlInner, Statement.RETURN_GENERATED_KEYS);*/

				result = rsLastGeneratedAutoIncrementId;

			}

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(result);
	}

}
