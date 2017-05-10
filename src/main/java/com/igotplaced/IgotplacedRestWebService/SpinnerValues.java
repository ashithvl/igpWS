package com.igotplaced.IgotplacedRestWebService;

import java.awt.List;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/spinner")
public class SpinnerValues {

	@GET
	@Path("/yearofpassout")
	@Produces(MediaType.APPLICATION_JSON)
	public String getYearOfPassOut() {
		String yearOfPassOutArray[] = { "'--Select the Passout Year--'", "'2025'", "'2024'", "'2023'", "'2022'", "'2021'", "'2020'",
				"'2019'", "'2018'", "'2017'", "'2016'", "'2015'", "'2014'" };
		ArrayList<String> yearOfPassOutArrayList = new ArrayList<String>();
		for (String arraylist : yearOfPassOutArray) {
			yearOfPassOutArrayList.add(arraylist);
		}
		return yearOfPassOutArrayList.toString();
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
		String yearOfPassOutArray[] = { "--Select the Passout Year--", "2025", "2024" };
		ArrayList<String> yearOfPassOutArrayList = new ArrayList<String>();
		for (String arraylist : yearOfPassOutArray) {
			yearOfPassOutArrayList.add(arraylist);
		}
		return yearOfPassOutArrayList.toString();
	}
	
}
