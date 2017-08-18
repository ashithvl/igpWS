package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Constants;

@Path("/autocompleteService")
public class AutoCompleteValue {

	JSONArray jsonArray = null;
	JSONArray jsonArrayInterview = null;
	JSONObject newObject = null;
	JSONObject mainObj = null;
	Connection con = null;
	Map<String, String> map = null;
	Map<String, String> mapInterview = null;
	List<String> colgList = new ArrayList<>();
	JSONArray colgJSONArray = null;

	List<String> deptList = new ArrayList<>();
	JSONArray deptJSONArray = null;
	
	List<String> searchList = new ArrayList<>();
	JSONArray searchJSONArray = null;
	
	List<String> searchResultList = new ArrayList<>();
	JSONArray searchResultJSONArray = null;

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
	
	
	
	
	@GET
	@Path("/searchAll/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String searchAll(@PathParam("id") String id) {

		try {

			con = Constants.ConnectionOpen();

			String sql = "SELECT ids,Caption FROM (SELECT 'company'Caption,companyname ids FROM company WHERE companyname LIKE  '%" + id + "%' UNION ALL SELECT 'industry'Caption,industry_type ids FROM industry WHERE industry_type LIKE  '%" + id + "%')subquery LIMIT 5";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				
				searchList.add(rs.getString("ids"));

			}
			String[] search = searchList.toArray(new String[searchList.size()]);

			searchJSONArray = new JSONArray(Arrays.asList(search));

			con.close();

		} catch (Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return searchJSONArray.toString();
	}

	
	
	@GET
	@Path("/searchResult/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String searchResult(@PathParam("id") String id) {

		try {
			jsonArray = new JSONArray(); 
			jsonArrayInterview = new JSONArray();
			map = new HashMap<String, String>();
			mapInterview = new HashMap<String, String>();
			con = Constants.ConnectionOpen();

			String sql = "SELECT Caption, ids, modified_by FROM (SELECT 'events'Caption, id ids, modified_by FROM events where companyname LIKE '%" + id + "%' or Industry LIKE '%" + id + "%' UNION ALL SELECT 'post'Caption, pid ids, modified_by FROM post  where companyname LIKE '%" + id + "%' or Industry LIKE '%" + id + "%' UNION ALL SELECT 'questions'Caption, id ids,modified_by FROM questions where companyname LIKE '%" + id + "%' or industryname LIKE '%" + id + "%' UNION ALL SELECT  'intexp'Caption, id ids, modified_by FROM interview_exp WHERE companyname LIKE  '%" + id + "%' OR industryname LIKE  '%" + id + "%')subquery ORDER BY modified_by DESC , FIELD( Caption,  'events',  'post',  'questions',  'intexp' ) LIMIT 10";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {


				String caption=rs.getString("Caption");				

				if(caption.equals("post")){
					String postid=rs.getString("ids");
					
					String postsql= "SELECT * from post where pid=?";
					
					PreparedStatement pspost = con.prepareStatement(postsql);
					
					pspost.setString(1, postid);
					
					ResultSet rspost = pspost.executeQuery();	
					
						while(rspost.next()){
							
						map.put("post", rspost.getString("post").replaceAll("\\<.*?\\>", ""));
						map.put("pid", rspost.getString("pid"));
						map.put("post_created_user", rspost.getString("created_user"));
						map.put("created_by", rspost.getString("created_by"));	
						
						
						jsonArray.put(map);
						
						
						}
						newObject = new JSONObject();
						newObject.append(caption, jsonArray);
					/*	JSONArray ja = new JSONArray();
						ja.put(newObject);
						 mainObj = new JSONObject();
						mainObj.put(caption, ja);*/
					}
				
				if(caption.equals("intexp")){
					String interviewids=rs.getString("ids");
					
					
					String interviewsql= "SELECT * from interview_exp where id=?";
					
					
					PreparedStatement psinterview = con.prepareStatement(interviewsql);
					
					psinterview.setString(1, interviewids);
					
					ResultSet rsinterview = psinterview.executeQuery();
					
					while(rsinterview.next()){
						
						mapInterview.put("feedback", rsinterview.getString("feedback").replaceAll("\\<.*?\\>", ""));
						mapInterview.put("industryname", rsinterview.getString("industryname"));
						mapInterview.put("interview_status", rsinterview.getString("interview_status"));
						mapInterview.put("industryname", rsinterview.getString("industryname"));
						mapInterview.put("companyname", rsinterview.getString("companyname"));
						mapInterview.put("id", rsinterview.getString("id"));
						mapInterview.put("user_id", rsinterview.getString("user_id"));
						mapInterview.put("username", rsinterview.getString("username"));
						mapInterview.put("created_by", rsinterview.getString("created_by"));	
						
						
						jsonArrayInterview.put(mapInterview);
						
						}
					newObject = new JSONObject();
					newObject.append(caption, jsonArrayInterview);
					
				}
				
			}
		

			con.close();

		} catch (Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return newObject.toString();
	}

	
	
	
	

}
