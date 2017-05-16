package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;

import utils.Constants;

@Path("/autocompleteService")
public class AutoCompleteValue {

	Connection con = null;

	List<String> colgList = new ArrayList<>();
	JSONArray colgJSONArray = null;

	List<String> deptList = new ArrayList<>();
	JSONArray deptJSONArray = null;

	@GET
	@Path("/searchCollege/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String searchCollege(@PathParam("id") String id) {

		try {

			con = Constants.ConnectionOpen();

			String sql = "select collegename from college where collegename LIKE '%" + id + "%' LIMIT 5";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				colgList.add(rs.getString("collegename"));

			}
			String[] colg = colgList.toArray(new String[colgList.size()]);

			colgJSONArray = new JSONArray(Arrays.asList(colg));

			con.close();

		} catch (Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return colgJSONArray.toString();
	}

	@GET
	@Path("/searchDepartment/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String searchDepartment(@PathParam("id") String id) {

		try {

			con = Constants.ConnectionOpen();

			String sql = "select DISTINCT courses from courses where courses LIKE '%" + id + "%' LIMIT 5";

			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				deptList.add(rs.getString("courses"));

			}
			String[] colg = deptList.toArray(new String[deptList.size()]);

			deptJSONArray = new JSONArray(Arrays.asList(colg));

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return deptJSONArray.toString();

	}

}
