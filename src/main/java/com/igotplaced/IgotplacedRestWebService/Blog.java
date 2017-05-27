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

@Path("/blogService")
public class Blog {

	Connection con = null;
	JSONObject jsonObj = null;
	JSONArray jsonArray = null;
	Map<String, String> map = null;

	@GET
	@Path("/blog")
	@Produces(MediaType.TEXT_HTML)
	public String recentNotification() {

		try {

			jsonArray = new JSONArray();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `blog`";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("modified_by", rs.getString("modified_by"));
				if (rs.getString("image").equals("")) {
					map.put("image", "/admin/uploads/muffins-1600x700_1.jpg");
				} else {
					map.put("image", "/admin/uploads/" + rs.getString("image"));
				}

				map.put("author", rs.getString("author"));
				map.put("header", rs.getString("header"));

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
