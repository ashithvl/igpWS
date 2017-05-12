package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;

import utils.Constants;

@Path("/autocompleteService")
public class AutoComplete {
	Connection con = null;

	List<String> colgList = new ArrayList<>();
	JSONArray companyJSONArray = null;
	
	@GET
	@Path("/searchCollege")
	@Produces(MediaType.TEXT_HTML)
	public String searchCollege() {

		try {

			con = Constants.ConnectionOpen();

			String sql = "select collegename from college";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				
				colgList.add(rs.getString("collegename"));
				
			}
			String[] colg = colgList.toArray(new String[colgList.size()]);
			
			companyJSONArray = new JSONArray(Arrays.asList(colg));
		
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return companyJSONArray.toString();
	}
}
