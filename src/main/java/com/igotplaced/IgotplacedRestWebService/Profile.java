package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Constants;

@Path("/profileService")
public class Profile {

	Connection con = null;
	JSONObject jsonObj = null;
	JSONArray  jsonArray = null;
	Map<String, String> map = null;

	@GET
	@Path("/profile/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String profile(@PathParam("id") String id) {

		try {
			
			jsonArray = new JSONArray();
			
			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `user_login` WHERE id=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);
			
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("imgname", rs.getString("imgname"));
				map.put("fname", rs.getString("fname"));
				map.put("department", rs.getString("department"));
				map.put("college", rs.getString("college"));
				map.put("industry1", rs.getString("industry1"));
				map.put("company1", rs.getString("company1"));
				map.put("industry2", rs.getString("industry2"));
				map.put("company2", rs.getString("company2"));
				map.put("industry3", rs.getString("industry3"));
				map.put("company3", rs.getString("company3"));

				jsonArray.put(map);

			}

			con.close();

		} catch ( 

		Exception e) {
 
			System.out.println(e);
			e.printStackTrace();
		}

		return jsonArray.toString();
	}

	

	@GET
	@Path("/profileInterviewExperience/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String profileInterviewExperience(@PathParam("id") String id) {

		try {
			
			jsonArray = new JSONArray();
			
			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from `interview_exp` where user_id=? order by modified_by desc";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);
			
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("feedback", rs.getString("feedback"));
				map.put("industryname", rs.getString("industryname"));
				map.put("interview_status", rs.getString("interview_status"));
				map.put("companyname", rs.getString("companyname"));
				map.put("user_id", rs.getString("user_id"));
				
				String sqlInner = "select * from `user_login` where id=?";

				PreparedStatement psInner = con.prepareStatement(sqlInner);
				psInner.setString(1, rs.getString("user_id"));

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {
					

					map.put("created_user", rs.getString("created_user"));
					map.put("username", rs.getString("username"));
					map.put("user_id", rs.getString("user_id"));
					map.put("created_by", rs.getString("created_by"));

					if (rsInner.getString("imgname").equals("")) {
						map.put("imgname","/images/avatar.png");
					} else {
						map.put("imgname", "/uploads/" + rsInner.getString("imgname"));
					}
					
					
					
					
					
					
					
					
				}
				

				jsonArray.put(map);

			}

			con.close();

		} catch ( 

		Exception e) {
 
			System.out.println(e);
			e.printStackTrace();
		}

		return jsonArray.toString();
	}
	
}
