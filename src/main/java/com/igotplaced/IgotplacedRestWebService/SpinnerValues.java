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

@Path("/spinner")
public class SpinnerValues {

	Connection con = null;

	List<String> industryList = new ArrayList<>();
	JSONArray industryJSONArray = null;

	List<String> companyList = new ArrayList<>();
	JSONArray companyJSONArray = null;
	
	@GET
	@Path("/yearofpassout")
	@Produces(MediaType.APPLICATION_JSON)
	public String getYearOfPassOut() {
		String yearOfPassOutArray[] = { "--Select the Passout Year--", "2025", "2024", "2023", "2022", "2021", "2020",
				"2019", "2018", "2017", "2016", "2015", "2014" };

		JSONArray companyJSONArray = new JSONArray(Arrays.asList(yearOfPassOutArray));

		return companyJSONArray.toString();
	}

	@GET
	@Path("/company/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String getCompany(@PathParam("id") String id) {

		try {

			con = Constants.ConnectionOpen();

			String sql = "select companyname from company where industryname LIKE '%" + id + "%' LIMIT 5";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				
					companyList.add(rs.getString("companyname"));

			}
			String[] company = companyList.toArray(new String[companyList.size()]);

			companyJSONArray = new JSONArray(Arrays.asList(company));

			con.close();

		} catch (Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return companyJSONArray.toString();
	}

	@GET
	@Path("/industry")
	@Produces(MediaType.TEXT_HTML)
	public String getIndustry() {

		try {

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM industry WHERE status=0";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				industryList.add(rs.getString("industry_type"));

			}
			String[] industry = industryList.toArray(new String[industryList.size()]);

			industryJSONArray = new JSONArray(Arrays.asList(industry));

			con.close();

		} catch (Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return industryJSONArray.toString();

	}

}
