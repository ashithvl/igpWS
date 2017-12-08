package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.jsoup.Jsoup;

import utils.Constants;

@Path("/autocompleteService")
public class AutoCompleteValue {

	JSONArray jsonArray = null;
	JSONArray jsonArrayInterview = null;
	JSONArray jsonArrayQuestions = null;
	JSONArray jsonArrayEvents = null;
	JSONObject newObject = null;
	JSONObject mainObj = null;
	Connection con = null;
	Map<String, String> map = null;
	Map<String, String> mapInterview = null;
	Map<String, String> mapQuestions = null;
	Map<String, String> mapEvents = null;
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

			String sql = "SELECT ids,Caption FROM (SELECT 'company'Caption,companyname ids FROM company WHERE companyname LIKE  '%"
					+ id
					+ "%' UNION ALL SELECT 'industry'Caption,industry_type ids FROM industry WHERE industry_type LIKE  '%"
					+ id + "%')subquery LIMIT 5";

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
			newObject = new JSONObject();

			con = Constants.ConnectionOpen();

			String sql = "SELECT Caption, ids, modified_by FROM (SELECT 'events'Caption, id ids, modified_by FROM events where companyname LIKE '%"
					+ id + "%' or Industry LIKE '%" + id
					+ "%' UNION ALL SELECT 'post'Caption, pid ids, modified_by FROM post  where companyname LIKE '%"
					+ id + "%' or Industry LIKE '%" + id
					+ "%' UNION ALL SELECT 'questions'Caption, id ids,modified_by FROM questions where companyname LIKE '%"
					+ id + "%' or industryname LIKE '%" + id
					+ "%' UNION ALL SELECT  'intexp'Caption, id ids, modified_by FROM interview_exp WHERE companyname LIKE  '%"
					+ id + "%' OR industryname LIKE  '%" + id
					+ "%')subquery ORDER BY modified_by DESC , FIELD( Caption,  'events',  'post',  'questions',  'intexp' ) LIMIT 10";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String caption = rs.getString("Caption");

				if (caption.equals("post")) {
					String postid = rs.getString("ids");

					String postsql = "SELECT * from post where pid=?";

					PreparedStatement pspost = con.prepareStatement(postsql);

					pspost.setString(1, postid);

					ResultSet rspost = pspost.executeQuery();

					while (rspost.next()) {

						map = new HashMap<String, String>();
						jsonArray = new JSONArray();
						
						SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						Date datePost = readFormat.parse(rspost.getString("created_by"));
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
						String postTime = dateFormat.format(datePost);

						map.put("post", Jsoup.parse(rspost.getString("post")).text());
						map.put("pid", rspost.getString("pid"));
						map.put("Industry", rspost.getString("Industry"));
						map.put("companyname", rspost.getString("companyname").replaceAll(",$", ""));
						map.put("created_user", rspost.getString("created_user"));
						map.put("created_uname", rspost.getString("created_uname"));
						map.put("created_by", postTime);
						map.put("created_uname", rspost.getString("created_uname"));

						String companyRequest = "SELECT * FROM `company` where companyname=?";

						PreparedStatement psCompany = con.prepareStatement(companyRequest);
						psCompany.setString(1, rspost.getString("companyname").replaceAll(",$", ""));
						ResultSet rsCompany = psCompany.executeQuery();
						while (rsCompany.next()) {
							map.put("company_id", rsCompany.getString("id"));
						}
						String sqlInnerDeep = "select * from `user_login` where id=?";

						PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
						psInnerDeep.setString(1, rspost.getString("created_user"));

						ResultSet rsInnerDeep = psInnerDeep.executeQuery();

						while (rsInnerDeep.next()) {

							if (rsInnerDeep.getString("imgname").equals("")) {
								map.put("postuserimgname", "/images/avatar.png");
							} else {
								map.put("postuserimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
							}
						}

						jsonArray.put(map);

						newObject.append(caption, map);
					}

				}

				if (caption.equals("intexp")) {
					String interviewids = rs.getString("ids");

					String interviewsql = "SELECT * from interview_exp where id=?";

					PreparedStatement psinterview = con.prepareStatement(interviewsql);

					psinterview.setString(1, interviewids);

					ResultSet rsinterview = psinterview.executeQuery();

					while (rsinterview.next()) {

						mapInterview = new HashMap<String, String>();
						jsonArrayInterview = new JSONArray();
						
						SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						Date datePost = readFormat.parse(rsinterview.getString("created_by"));
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
						String postTime = dateFormat.format(datePost);

						mapInterview.put("feedback", Jsoup.parse(rsinterview.getString("feedback")).text());
						mapInterview.put("industryname", rsinterview.getString("industryname"));
						mapInterview.put("interview_status", rsinterview.getString("interview_status"));
						mapInterview.put("industryname", rsinterview.getString("industryname"));
						mapInterview.put("companyname", rsinterview.getString("companyname").replaceAll(",$", ""));
						mapInterview.put("id", rsinterview.getString("id"));
						mapInterview.put("user_id", rsinterview.getString("user_id"));
						mapInterview.put("username", rsinterview.getString("username"));
						mapInterview.put("created_by",postTime);

						String companyRequest = "SELECT * FROM `company` where companyname=?";

						PreparedStatement psCompany = con.prepareStatement(companyRequest);
						psCompany.setString(1, rsinterview.getString("companyname").replaceAll(",$", ""));
						ResultSet rsCompany = psCompany.executeQuery();
						while (rsCompany.next()) {
							mapInterview.put("company_id", rsCompany.getString("id"));
						}
						String sqlInnerDeep = "select * from `user_login` where id=?";

						PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
						psInnerDeep.setString(1, rsinterview.getString("user_id"));

						ResultSet rsInnerDeep = psInnerDeep.executeQuery();

						while (rsInnerDeep.next()) {

							if (rsInnerDeep.getString("imgname").equals("")) {
								mapInterview.put("interviewUserImgName", "/images/avatar.png");
							} else {
								mapInterview.put("interviewUserImgName",
										"/uploads/" + rsInnerDeep.getString("imgname"));
							}
						}

						jsonArrayInterview.put(mapInterview);
						newObject.append(caption, mapInterview);

					}
				}

				if (caption.equals("questions")) {

					String questionids = rs.getString("ids");

					String questionssql = "SELECT * from questions where id=?";

					PreparedStatement psquestions = con.prepareStatement(questionssql);

					psquestions.setString(1, questionids);

					ResultSet rsquestions = psquestions.executeQuery();

					while (rsquestions.next()) {
						jsonArrayQuestions = new JSONArray();
						mapQuestions = new HashMap<String, String>();
						
						SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						Date datePost = readFormat.parse(rsquestions.getString("created_by"));
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
						String postTime = dateFormat.format(datePost);

						mapQuestions.put("question", Jsoup.parse(rsquestions.getString("question")).text());
						mapQuestions.put("category", rsquestions.getString("category"));
						mapQuestions.put("industryname", rsquestions.getString("industryname"));
						mapQuestions.put("companyname", rsquestions.getString("companyname").replaceAll(",$", ""));
						mapQuestions.put("subcategory", rsquestions.getString("subcategory"));
						mapQuestions.put("subcategory", rsquestions.getString("subcategory"));
						mapQuestions.put("created_uname", rsquestions.getString("created_uname"));
						mapQuestions.put("created_user", rsquestions.getString("created_user"));
						mapQuestions.put("id", rsquestions.getString("id"));
						mapQuestions.put("created_by", postTime);

						String companyRequest = "SELECT * FROM `company` where companyname=?";

						PreparedStatement psCompany = con.prepareStatement(companyRequest);
						psCompany.setString(1, rsquestions.getString("companyname").replaceAll(",$", ""));
						ResultSet rsCompany = psCompany.executeQuery();
						while (rsCompany.next()) {
							mapQuestions.put("company_id", rsCompany.getString("id"));
						}

						String sqlInnerDeep = "select * from `user_login` where id=?";

						PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
						psInnerDeep.setString(1, rsquestions.getString("created_user"));

						ResultSet rsInnerDeep = psInnerDeep.executeQuery();

						while (rsInnerDeep.next()) {

							if (rsInnerDeep.getString("imgname").equals("")) {
								mapQuestions.put("questionUserImgName", "/images/avatar.png");
							} else {
								mapQuestions.put("questionUserImgName", "/uploads/" + rsInnerDeep.getString("imgname"));
							}
						}

						jsonArrayQuestions.put(mapQuestions);
					}
					newObject.append(caption, mapQuestions);
				}

				if (caption.equals("events")) {

					String eventsids = rs.getString("ids");

					String eventssql = "SELECT * from events where id=?";

					PreparedStatement psevents = con.prepareStatement(eventssql);

					psevents.setString(1, eventsids);

					ResultSet rsevents = psevents.executeQuery();

					while (rsevents.next()) {

						jsonArrayEvents = new JSONArray();
						mapEvents = new HashMap<String, String>();

						SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						Date datePost = readFormat.parse(rsevents.getString("created_by"));
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
						String postTime = dateFormat.format(datePost);
						
						mapEvents.put("notes", Jsoup.parse(rsevents.getString("notes")).text());
						mapEvents.put("eventname", rsevents.getString("eventname"));
						mapEvents.put("datetime", rsevents.getString("datetime"));
						mapEvents.put("eventtype", rsevents.getString("eventtype"));
						mapEvents.put("location", rsevents.getString("location"));
						mapEvents.put("id", rsevents.getString("id"));
						mapEvents.put("Industry", rsevents.getString("Industry"));
						mapEvents.put("companyname", rsevents.getString("companyname").replaceAll(",$", ""));
						mapEvents.put("created_user", rsevents.getString("created_user"));
						mapEvents.put("created_by",postTime);
						mapEvents.put("created_uname", rsevents.getString("created_uname"));

						String companyRequest = "SELECT * FROM `company` where companyname=?";

						PreparedStatement psCompany = con.prepareStatement(companyRequest);
						psCompany.setString(1, rsevents.getString("companyname").replaceAll(",$", ""));
						ResultSet rsCompany = psCompany.executeQuery();
						while (rsCompany.next()) {
							mapEvents.put("company_id", rsCompany.getString("id"));
						}

						String sqlInnerDeep = "select * from `user_login` where id=?";

						PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
						psInnerDeep.setString(1, rsevents.getString("created_user"));

						ResultSet rsInnerDeep = psInnerDeep.executeQuery();

						while (rsInnerDeep.next()) {

							if (rsInnerDeep.getString("imgname").equals("")) {
								mapEvents.put("eventImgName", "/images/avatar.png");
							} else {
								mapEvents.put("eventImgName", "/uploads/" + rsInnerDeep.getString("imgname"));
							}
						}

						String eventDate = rsevents.getString("datetime");

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						Date todaysDate = new Date();

						if ((eventDate).compareTo(sdf.format(todaysDate)) < 0) {
							mapEvents.put("event", "Closed");
						} else {
							mapEvents.put("created_by", "I'm going");
						}

						String countSql = "SELECT * FROM `event_register` WHERE eventid=?";

						PreparedStatement psevent = con.prepareStatement(countSql);
						psevent.setString(1, rsevents.getString("id"));

						ResultSet eventRs = psevent.executeQuery();

						if (eventRs.next()) {

							if (eventRs.getInt(1) <= 0) {
								mapEvents.put("count", "Be First to Register");
							} else {
								mapEvents.put("count", eventRs.getRow() + " People going");
							}
						} else {
							mapEvents.put("count", "Be First to Register");
						}

						jsonArrayEvents.put(mapEvents);
					}
					newObject.append(caption, mapEvents);
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
