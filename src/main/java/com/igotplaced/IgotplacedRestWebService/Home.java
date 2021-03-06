package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Constants;

@Path("/homeService")
public class Home {

	Connection con = null;
	JSONObject jsonObj = null;
	JSONArray  jsonArray = null;
	Map<String, String> map = null;

	@GET
	@Path("/recentFeeds")
	@Produces(MediaType.TEXT_HTML)
	public String recentFeeds() {

		try {
			
			jsonArray = new JSONArray();
			
			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT id ,created_user ,question ,modified_by ,companyname ,industryname, type FROM  questions UNION SELECT  pid, created_user, post, modified_by, companyname, Industry, type FROM post UNION SELECT id, created_user, eventname, modified_by, eventtype, datetime, type FROM events ORDER BY modified_by DESC LIMIT 6";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("type", rs.getString("type"));
				map.put("question", rs.getString("question").replaceAll("\\<.*?\\>", ""));
				map.put("industryname", rs.getString("industryname"));
				map.put("companyname", rs.getString("companyname"));
				map.put("modified_by", rs.getString("modified_by"));

				String sqlInner = "select imgname,department,fname from user_login where id=?";

				PreparedStatement psInner = con.prepareStatement(sqlInner);
				psInner.setString(1, rs.getString("created_user"));

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					map.put("name", rsInner.getString("fname"));
					if (rsInner.getString("imgname").equals("")) {
						map.put("imgname","/images/avatar.png");
					} else {
						map.put("imgname","/uploads/" + rsInner.getString("imgname"));
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

	@GET
	@Path("/recentlyGotPlaced")
	@Produces(MediaType.TEXT_HTML)
	public String recentlyGotPlaced() {

		try {
			
			jsonArray = new JSONArray();
			
			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `interview_exp` where interview_status=1 ORDER BY created_by DESC";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("username", rs.getString("username"));
				map.put("feedback", rs.getString("feedback").replaceAll("\\<.*?\\>", ""));
				map.put("companyname", rs.getString("companyname"));

				String sqlInner = "select imgname,department from user_login where id=?";

				PreparedStatement psInner = con.prepareStatement(sqlInner);
				psInner.setString(1, rs.getString("user_id"));

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

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

	@GET
	@Path("/mentors")
	@Produces(MediaType.TEXT_HTML)
	public String recentlyGotPlaqced() {

		try {
			
			jsonArray = new JSONArray();
			
			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM mentor";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("name", rs.getString("name"));
				map.put("designation", rs.getString("designation"));
				map.put("company", rs.getString("company"));
				map.put("linkedin", rs.getString("linkedin"));
				
				if (rs.getString("image").equals("")) {
					map.put("imgname","/uploads/gap-orange-blue-pocket-long-sleeve-t-shirt-mens.png");
				} else {
					map.put("imgname","/admin/uploads/" + rs.getString("image"));
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
	

	@GET
	@Path("/testimonials")
	@Produces(MediaType.TEXT_HTML)
	public String testimonials() {

		try {
			
			jsonArray = new JSONArray();
			
			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from igotintw_feeds order by created_by desc";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("user_name", rs.getString("user_name"));
				map.put("feedback", rs.getString("feedback").replaceAll("\\<.*?\\>", ""));

				String sqlInner = "select imgname,department,college from user_login where id=?";

				PreparedStatement psInner = con.prepareStatement(sqlInner);
				psInner.setString(1, rs.getString("user_id"));

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					if (rsInner.getString("imgname").equals("")) { 
						map.put("imgname","/images/avatar.png");
						map.put("college", rsInner.getString("college"));
					} else { 
						map.put("imgname","/uploads/" + rsInner.getString("imgname"));
						map.put("college", rsInner.getString("college"));
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
