package com.igotplaced.IgotplacedRestWebService;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.IndustryCompany;

@Path("/spinner")
public class SpinnerValues {

	@GET
	@Path("/yearofpassout")
	@Produces(MediaType.APPLICATION_JSON)
	public String getYearOfPassOut() {
		String yearOfPassOutArray[] = { "--Select the Passout Year--", "2025", "2024", "2023", "2022", "2021", "2020",
				"2019", "2018", "2017", "2016", "2015", "2014" };
		
		/*ArrayList<String> yearOfPassOutArrayList = new ArrayList<String>();
		for (String arraylist : yearOfPassOutArray) {
			yearOfPassOutArrayList.add(arraylist);
		}
		
*/
		JSONArray companyJSONArray = new JSONArray(Arrays.asList(yearOfPassOutArray));
		
		return companyJSONArray.toString();
	}

	@GET
	@Path("/company")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCompany() {
		String yearOfPassOutArray[] = { "--Select the Passout Year--", "2025", "2024" };
		ArrayList<String> yearOfPassOutArrayList = new ArrayList<String>();
		for (String arraylist : yearOfPassOutArray) {
			yearOfPassOutArrayList.add(arraylist);
		}
		return yearOfPassOutArrayList.toString();
	}

	@GET
	@Path("/industry")
	@Produces(MediaType.APPLICATION_JSON)
	public String getIndustry() {

		IndustryCompany industryCompany = new IndustryCompany();

		String[] industry = { "All Industries", "ACCOUNTING", "APPAREL AND  FASHION", "KPMG IMPACT", "INTERNET",
				"AUTOMOTIVE", "BANKING", "CONSULTING", "E-COMMERCE", "EDUCATION", "E-LEARNING", "FINANCIAL SERVICES",
				"FMCG", "FOOD AND BEVERAGES", "FURNITURE", "HEALTHCARE", "HOSPITALITY AND TOURISM",
				"INVESTMENT BANKING", "IT INDUSTRY", "LOGISTICS AND SUPPLY CHAIN", "MARKETING AND ADVERTISING",
				"REAL ESTATE", "RETAIL", "TELECOMMUNICATIONS", "VENTURE CAPITAL AND PRIVATE EQUITY", "MECHANICAL" };

		ArrayList<String> industryDisplayArrayList = new ArrayList<String>();
		for (String arraylist : industry) {
			industryDisplayArrayList.add(arraylist);
		}

		String yearOfPassOutArray[] = { "--Select the Passout Year--", "2025", "2024", "2023", "2022", "2021", "2020",
				"2019", "2018", "2017", "2016", "2015", "2014" };
		ArrayList<String> yearOfPassOutArrayList = new ArrayList<String>();
		for (String arraylist : yearOfPassOutArray) {
			yearOfPassOutArrayList.add(arraylist);
		}

		JSONArray industryJSONArray = new JSONArray(Arrays.asList(industry));

		JSONArray companyJSONArray = new JSONArray(Arrays.asList(yearOfPassOutArray));

		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("industry", (Object) industryJSONArray);

		jsonObject.put("company", (Object) companyJSONArray);

		return jsonObject.toString();

	}

}
