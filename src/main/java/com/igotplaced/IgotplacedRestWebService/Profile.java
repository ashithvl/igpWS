package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.cj.api.jdbc.Statement;

import utils.Constants;

@Path("/profileService")
public class Profile {

	Connection con = null;
	JSONObject jsonObj = null;
	JSONObject newObject = null;
	JSONArray jsonArray = null;
	JSONArray jsonArrayCompany = null;
	Map<String, String> map = null;
	Map<String, String> mapCompany = null;

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

				if (rs.getString("imgname").equals("")) {
					map.put("imgname", "/images/avatar.png");
				} else {
					map.put("imgname", "/uploads/" + rs.getString("imgname"));
				}
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
	@Path("/profileEdit/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String profileEdit(@PathParam("id") String id) {

		List<String> Company1List = null, Company2List = null, Company3List = null;
		String company1 = "", company2 = "", company3 = "";

		try {

			jsonArray = new JSONArray();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `user_login` WHERE id=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				if (rs.getString("imgname").equals("")) {
					map.put("imgname", "/images/avatar.png");
				} else {
					map.put("imgname", "/uploads/" + rs.getString("imgname"));
				}

				if (!rs.getString("company1").equals("")) {
					Company1List = Arrays.asList(rs.getString("company1").split(","));
					if (!Company1List.isEmpty()) {
						company1 = Company1List.get(Company1List.size() - 1);
					}
				}

				if (!rs.getString("company1").equals("")) {
					Company2List = Arrays.asList(rs.getString("company2").split(","));

					if (!Company2List.isEmpty()) {
						company2 = Company2List.get(Company2List.size() - 1);
					}
				}

				if (!rs.getString("company1").equals("")) {
					Company3List = Arrays.asList(rs.getString("company2").split(","));

					if (!Company3List.isEmpty()) {
						company3 = Company3List.get(Company3List.size() - 1);
					}
				}

				map.put("fname", rs.getString("fname"));
				map.put("department", rs.getString("department"));
				map.put("college", rs.getString("college"));
				map.put("industry1", rs.getString("industry1"));
				map.put("company1", company1);
				map.put("industry2", rs.getString("industry2"));
				map.put("company2", company2);
				map.put("industry3", rs.getString("industry3"));
				map.put("company3", company3);
				map.put("interest", rs.getString("interest"));
				map.put("email", rs.getString("email"));
				map.put("passout", rs.getString("passout"));
				map.put("phone", rs.getString("phone"));
				map.put("location", rs.getString("location"));

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

	@POST
	@Path("/profileImageUpdate/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String profileImageUpdate(@FormParam("id") String id, @FormParam("url") String url) {

		int result = 0;
		int rsLastGeneratedAutoIncrementId = 0;
		String sqlInner = null;

		try {

			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "UPDATE `user_login` SET imgname=? WHERE id=?";

			PreparedStatement psInner = con.prepareStatement(sqlInner);

			psInner.setString(1, "http://www.igotplaced.com/uploads/" + url);
			psInner.setString(2, id);

			System.out.println(url);
			
			
			if (psInner.executeUpdate() > 0) {
				result = 1;
			}
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.valueOf(result);
	}

	@POST
	@Path("/profileUpdate/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String register(@FormParam("id") String id, @FormParam("industry1") String industry1,
			@FormParam("industry2") String industry2, @FormParam("industry3") String industry3,
			@FormParam("company1") String company1, @FormParam("company2") String company2,
			@FormParam("company3") String company3, @FormParam("phone") String phone,
			@FormParam("interest") String interest, @FormParam("location") String location,
			@FormParam("name") String name, @FormParam("email") String email, @FormParam("year") String year,
			@FormParam("colg") String colg, @FormParam("dept") String dept) {

		int result = 0;
		int rsLastGeneratedAutoIncrementId = 0;
		String sqlInner = null;

		try {

			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "UPDATE `user_login` SET industry1=?,industry2=?,industry3=?,company1=?,company2=?,"
					+ "company3=?,phone=?,location=?,interest=?,fname =?, email =?, passout=?,college=?,department=? WHERE id=?";

			PreparedStatement psInner = con.prepareStatement(sqlInner);

			psInner.setString(1, industry1);
			psInner.setString(2, industry2);
			psInner.setString(3, industry3);
			psInner.setString(4, company1);
			psInner.setString(5, company2);
			psInner.setString(6, company3);
			psInner.setString(7, phone);
			psInner.setString(8, location);
			psInner.setString(9, interest);
			psInner.setString(10, name);
			psInner.setString(11, email);
			psInner.setString(12, year);
			psInner.setString(13, colg.substring(0, Math.min(colg.length(), 44)));
			psInner.setString(14, dept.substring(0, Math.min(dept.length(), 28)));
			psInner.setString(15, id);

			if (psInner.executeUpdate() > 0) {
				result = 1;
			}
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.valueOf(result);
	}

	@GET
	@Path("/profilePost/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String profilePost(@PathParam("userId") String userId, @QueryParam("start") int start,
			@QueryParam("size") int size) {

		try {

			String sqlInner;
			PreparedStatement psInner;

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT a.pid pid,a.post post,a.status status,a.created_by created_by,a.created_user created_user,a.created_uname created_uname,b.imgname FROM post as a INNER JOIN user_login as b ON a.created_user=b.id and a.created_user=? order by created_by desc";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				map.put("post", rs.getString("post").replaceAll("\\<.*?\\>", ""));
				map.put("pid", rs.getString("pid"));
				map.put("post_created_user", rs.getString("created_user"));
				map.put("created_by", rs.getString("created_by"));
				map.put("pid", rs.getString("pid"));

				if (rs.getString("imgname").equals("")) {
					map.put("post_created_user_image", "/images/avatar.png");
				} else {
					map.put("post_created_user_image", "/uploads/" + rs.getString("imgname"));
				}

				sqlInner = "select * from `post` where pid=?";

				psInner = con.prepareStatement(sqlInner);
				psInner.setString(1, rs.getString("pid"));

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					map.put("Industry", rsInner.getString("Industry"));
					map.put("created_user", rsInner.getString("created_user"));
					map.put("companyname", rsInner.getString("companyname").replace(",$", ""));
					map.put("created_uname", rsInner.getString("created_uname"));

					String companyRequest = "SELECT * FROM `company` where companyname=?";

					PreparedStatement psCompany = con.prepareStatement(companyRequest);
					psCompany.setString(1, rsInner.getString("companyname").replaceAll(",$", ""));
					ResultSet rsCompany = psCompany.executeQuery();
					while (rsCompany.next()) {
						map.put("company_id", rsCompany.getString("id"));
					}

					jsonArray.put(map);

				}
			}

			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return jsonArray.toString();
	}

	@POST
	@Path("/profilePost")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String profilePost(@FormParam("post") String post, @FormParam("Industry") String Industry,
			@FormParam("created_user") String created_user, @FormParam("company1") String company1,
			@FormParam("created_uname") String created_uname) {

		int result = 0;
		String sqlInner = null;

		try {

			int defaultValueInt = 0;
			int typeValue = 1;
			int rsLastGeneratedAutoIncrementId = 0;
			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "INSERT INTO post (post,Industry,companyname,status,created_by,created_user,modified_by,modified_user,created_uname,type)"
					+ "VALUES('" + post + "','" + Industry + "','" + company1 + "','" + defaultValueInt + "','"
					+ dateTime + "','" + created_user + "','" + dateTime + "','" + created_user + "','" + created_uname
					+ "','" + typeValue + "')";

			PreparedStatement psInner = con.prepareStatement(sqlInner, Statement.RETURN_GENERATED_KEYS);

			psInner.executeUpdate();
			ResultSet rsInner = psInner.getGeneratedKeys();

			if (rsInner.next()) {
				rsLastGeneratedAutoIncrementId = rsInner.getInt(1);
			}

			result = rsLastGeneratedAutoIncrementId;

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(result);

	}

	@GET
	@Path("/profileInterviewExperience/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String profileInterviewExperience(@PathParam("userId") String userId, @QueryParam("start") int start,
			@QueryParam("size") int size) {

		try {

			String sqlInner;
			PreparedStatement psInner;

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from `interview_exp` where user_id=? order by modified_by desc";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("feedback", rs.getString("feedback").replaceAll("\\<.*?\\>", ""));
				map.put("interview_status", rs.getString("interview_status"));
				map.put("created_by", rs.getString("created_by"));

				map.put("username", rs.getString("username"));

				String sqlInnerDeep = "select * from `user_login` where id=?";

				PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
				psInnerDeep.setString(1, rs.getString("user_id"));

				ResultSet rsInnerDeep = psInnerDeep.executeQuery();

				while (rsInnerDeep.next()) {

					if (rsInnerDeep.getString("imgname").equals("")) {
						map.put("interviewExperienceimgname", "/images/avatar.png");
					} else {
						map.put("interviewExperienceimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
					}

				}

				map.put("industryname", rs.getString("industryname"));
				map.put("user_id", rs.getString("user_id"));
				map.put("companyname", rs.getString("companyname").replace(",$", ""));
				map.put("username", rs.getString("username"));
				map.put("created_by", rs.getString("created_by"));
				map.put("fid", rs.getString("id"));

				String companyRequest = "SELECT * FROM `company` where companyname=?";

				PreparedStatement psCompany = con.prepareStatement(companyRequest);
				psCompany.setString(1, rs.getString("companyname").replaceAll(",$", ""));
				ResultSet rsCompany = psCompany.executeQuery();
				while (rsCompany.next()) {
					map.put("company_id", rsCompany.getString("id"));
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

	@POST
	@Path("/profileInterview")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String profileInterview(@FormParam("feedback") String feedback, @FormParam("user_id") String user_id,
			@FormParam("industryname") String industryname, @FormParam("companyname") String companyname,
			@FormParam("username") String username, @FormParam("interview_status") String interview_status) {

		int result = 0;
		String sqlInner = null;
		int rsLastGeneratedAutoIncrementId = 0;

		try {

			int defaultValueInt = 0;
			int typeValue = 1;

			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "INSERT INTO interview_exp (user_id,username,industryname,companyname,feedback,interview_status,created_by,modified_by,modified_user)"
					+ "VALUES('" + user_id + "','" + username + "','" + industryname + "','" + companyname + "','"
					+ feedback + "','" + interview_status + "','" + dateTime + "','" + dateTime + "','" + user_id
					+ "')";

			PreparedStatement psInner = con.prepareStatement(sqlInner, Statement.RETURN_GENERATED_KEYS);

			psInner.executeUpdate();
			ResultSet rsInner = psInner.getGeneratedKeys();

			if (rsInner.next()) {
				rsLastGeneratedAutoIncrementId = rsInner.getInt(1);
			}

			result = rsLastGeneratedAutoIncrementId;

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(result);

	}

	@GET
	@Path("/profileQuestion/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String profileQuestion(@PathParam("userId") String userId, @QueryParam("start") int start,
			@QueryParam("size") int size) {

		try {

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT a.id id,a.companyname companyname,a.industryname industryname,a.question question,a.status status,a.created_by created_by,a.created_user created_user,a.created_uname created_uname,b.imgname FROM questions as a INNER JOIN user_login as b ON a.created_user=b.id AND a.created_user=?  order by created_by desc";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("question", rs.getString("question").replaceAll("\\<.*?\\>", ""));

				if (rs.getString("imgname").equals("")) {
					map.put("questionimgname", "/images/avatar.png");
				} else {
					map.put("questionimgname", "/uploads/" + rs.getString("imgname"));
				}

				map.put("industryname", rs.getString("industryname"));
				map.put("created_by", rs.getString("created_by"));
				map.put("created_uname", rs.getString("created_uname"));
				map.put("companyname", rs.getString("companyname").replace(",$", ""));
				map.put("qid", rs.getString("id"));
				map.put("created_user", rs.getString("created_user"));

				String companyRequest = "SELECT * FROM `company` where companyname=?";

				PreparedStatement psCompany = con.prepareStatement(companyRequest);
				psCompany.setString(1, rs.getString("companyname").replaceAll(",$", ""));
				ResultSet rsCompany = psCompany.executeQuery();
				while (rsCompany.next()) {
					map.put("company_id", rsCompany.getString("id"));
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

	@POST
	@Path("/profileQuestions")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String profileQuestions(@FormParam("question") String question,
			@FormParam("created_user") String created_user, @FormParam("industryname") String industryname,
			@FormParam("companyname") String companyname, @FormParam("created_uname") String created_uname) {

		int result = 0;
		String sqlInner = null;
		int rsLastGeneratedAutoIncrementId = 0;

		try {

			int defaultValueInt = 0;
			int typeValue = 1;
			String category = "";

			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "INSERT INTO questions (companyname,industryname,question,category,subcategory,status,created_by,created_user,modified_by,modified_user,created_uname)"
					+ "VALUES('" + companyname + "','" + industryname + "','" + question + "','" + category + "','"
					+ category + "','" + defaultValueInt + "','" + dateTime + "','" + created_user + "','" + dateTime
					+ "','" + created_user + "','" + created_uname + "')";

			PreparedStatement psInner = con.prepareStatement(sqlInner, Statement.RETURN_GENERATED_KEYS);

			psInner.executeUpdate();
			ResultSet rsInner = psInner.getGeneratedKeys();

			if (rsInner.next()) {
				rsLastGeneratedAutoIncrementId = rsInner.getInt(1);
			}

			result = rsLastGeneratedAutoIncrementId;

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(result);

	}

	@GET
	@Path("/profileEvent/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String profileEvent(@PathParam("userId") String userId, @QueryParam("start") int start,
			@QueryParam("size") int size) {

		try {

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT a.id id,a.eventname eventname,a.companyname companyname,a.notes notes,a.eventtype eventtype,a.status status,a.Industry Industry,a.location location,a.datetime datetime,a.created_by created_by,a.created_user created_user,a.created_uname created_uname,b.imgname FROM events as a INNER JOIN user_login as b ON a.created_user=b.id AND a.created_user=? order by created_by desc";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("eventname", rs.getString("eventname").replaceAll("\\<.*?\\>", ""));

				if (rs.getString("imgname").equals("")) {
					map.put("eventimgname", "/images/avatar.png");
				} else {
					map.put("eventimgname", "/uploads/" + rs.getString("imgname"));
				}

				map.put("datetime", rs.getString("datetime"));
				map.put("notes", rs.getString("notes"));
				map.put("location", rs.getString("location"));
				map.put("eid", rs.getString("id"));
				map.put("Industry", rs.getString("Industry"));
				map.put("eventtype", rs.getString("eventtype"));
				map.put("created_user", rs.getString("created_user"));
				map.put("created_uname", rs.getString("created_uname"));
				map.put("created_by", rs.getString("created_by"));
				map.put("companyname", rs.getString("companyname"));
				String countSql = "SELECT * FROM `events` WHERE id=?";

				PreparedStatement psevent = con.prepareStatement(countSql);
				psevent.setString(1, rs.getString("id"));

				String eventDate = rs.getString("datetime");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date todaysDate = new Date();

				if ((eventDate).compareTo(sdf.format(todaysDate)) < 0) {
					map.put("event", "Closed");
				} else {
					map.put("created_by", "I'm going");
				}

				ResultSet eventRs = psevent.executeQuery();

				if (eventRs.next()) {

					if (eventRs.getInt(1) <= 0) {
						map.put("count", "Be First to Register");
					} else {
						map.put("count", eventRs.getRow() + " People going");
					}
				} else {
					map.put("count", "Be First to Register");
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
	@Path("/companyDetails/{companyid}")
	@Produces(MediaType.TEXT_HTML)
	public String companyDetails(@PathParam("companyid") String companyid) {

		try {

			System.out.println(companyid);

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `company` WHERE id=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, companyid);

			ResultSet rs = ps.executeQuery();

			System.out.println(sql);
			while (rs.next()) {
				map.put("aboutus", rs.getString("aboutus").replaceAll("\\<.*?\\>", ""));
				map.put("id", rs.getString("id"));
				map.put("industryname", rs.getString("industryname"));
				map.put("companywebsite", rs.getString("companywebsite"));
				map.put("companyimage", rs.getString("companyimage"));
				map.put("companyname", rs.getString("companyname"));

				if (rs.getString("companyimage").equals("")) {
					map.put("companyImage", "/images/avatar.png");
				} else {
					map.put("companyImage", "/admin/uploads/" + rs.getString("companyimage"));
				}
				jsonArray.put(map);

				System.out.println(map);
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
	@Path("/companyName/{name}")
	@Produces(MediaType.TEXT_HTML)
	public String companyName(@PathParam("companyname") String companyname) {

		try {

			System.out.println(companyname);

			jsonArrayCompany = new JSONArray();

			mapCompany = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `company` WHERE companyname=?";
			System.out.println(sql);
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, companyname);

			ResultSet rs = ps.executeQuery();
			System.out.println(rs.getString("companywebsite"));

			while (rs.next()) {
				map.put("aboutus", rs.getString("aboutus").replaceAll("\\<.*?\\>", ""));
				map.put("id", rs.getString("id"));
				map.put("industryname", rs.getString("industryname"));
				map.put("companywebsite", rs.getString("companywebsite"));
				map.put("companyimage", rs.getString("companyimage"));
				map.put("companyname", rs.getString("companyname"));

				if (rs.getString("companyimage").equals("")) {
					map.put("companyImage", "/images/avatar.png");
				} else {
					map.put("companyImage", "/uploads/" + rs.getString("companyimage"));
				}
				jsonArray.put(map);

				System.out.println(map);
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
	@Path("/companyProfilePost/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String companyProfilePost(@PathParam("userId") String userId) {

		try {

			String sqlInner;
			PreparedStatement psInner;

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from `company` where id=" + userId;

			PreparedStatement ps = con.prepareStatement(sql);
			// ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();
			System.out.println(sql);
			while (rs.next()) {
				map.put("companyname", rs.getString("companyname").replace(",$", ""));
				map.put("company_id", rs.getString("id"));
				String companyNamePost = rs.getString("companyname").replace(",$", "");

				sqlInner = "select * from `post` where companyname=?";

				psInner = con.prepareStatement(sqlInner);

				psInner.setString(1, companyNamePost);

				ResultSet rsInner = psInner.executeQuery();

				System.out.println(companyNamePost);

				while (rsInner.next()) {

					map.put("pid", rsInner.getString("pid"));
					map.put("post", rsInner.getString("post").replaceAll("\\<.*?\\>", ""));
					map.put("industry", rsInner.getString("industry"));
					map.put("created_uname", rsInner.getString("created_uname"));
					map.put("created_by", rsInner.getString("created_by"));
					map.put("created_user", rsInner.getString("created_user"));

					String sqlInnerDeep = "select * from `user_login` where id=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, rsInner.getString("created_user"));

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						if (rsInnerDeep.getString("imgname").equals("")) {
							map.put("companyPostImage", "/images/avatar.png");
						} else {
							map.put("companyPostImage", "/uploads/" + rsInnerDeep.getString("imgname"));
						}

					}

					jsonArray.put(map);

				}
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
	@Path("/companyProfileInterview/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String companyProfileInterview(@PathParam("userId") String userId) {

		try {

			String sqlInner;
			PreparedStatement psInner;

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from `company` where id=" + userId;

			PreparedStatement ps = con.prepareStatement(sql);
			// ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();
			System.out.println(sql);
			while (rs.next()) {
				map.put("companyname", rs.getString("companyname").replace(",$", ""));
				map.put("company_id", rs.getString("id"));
				String companyNamePost = rs.getString("companyname").replace(",$", "");

				sqlInner = "select * from `interview_exp` where companyname=?";

				psInner = con.prepareStatement(sqlInner);

				psInner.setString(1, companyNamePost);

				ResultSet rsInner = psInner.executeQuery();

				System.out.println(companyNamePost);

				while (rsInner.next()) {

					map.put("id", rsInner.getString("id"));
					map.put("feedback", rsInner.getString("feedback").replaceAll("\\<.*?\\>", ""));
					map.put("industryname", rsInner.getString("industryname"));
					map.put("username", rsInner.getString("username"));
					map.put("created_by", rsInner.getString("created_by"));
					map.put("user_id", rsInner.getString("user_id"));

					String sqlInnerDeep = "select * from `user_login` where id=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, rsInner.getString("user_id"));

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						if (rsInnerDeep.getString("imgname").equals("")) {
							map.put("companyInterviewImage", "/images/avatar.png");
						} else {
							map.put("companyInterviewImage", "/uploads/" + rsInnerDeep.getString("imgname"));
						}

					}

					jsonArray.put(map);

				}
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
	@Path("/companyProfileQuestions/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String companyProfileQuestions(@PathParam("userId") String userId) {

		try {

			String sqlInner;
			PreparedStatement psInner;

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from `company` where id=" + userId;

			PreparedStatement ps = con.prepareStatement(sql);
			// ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();
			System.out.println(sql);
			while (rs.next()) {
				map.put("companyname", rs.getString("companyname").replace(",$", ""));
				map.put("company_id", rs.getString("id"));
				String companyNamePost = rs.getString("companyname").replace(",$", "");

				sqlInner = "select * from `questions` where companyname=?";

				psInner = con.prepareStatement(sqlInner);

				psInner.setString(1, companyNamePost);

				ResultSet rsInner = psInner.executeQuery();

				System.out.println(sqlInner);

				while (rsInner.next()) {

					map.put("id", rsInner.getString("id"));
					map.put("question", rsInner.getString("question").replaceAll("\\<.*?\\>", ""));
					map.put("industryname", rsInner.getString("industryname"));
					map.put("created_user", rs.getString("created_user"));
					map.put("created_uname", rsInner.getString("created_uname"));
					map.put("created_by", rsInner.getString("created_by"));

					System.out.println(rsInner.getString("created_user"));
					String sqlInnerDeep = "select * from `user_login` where id=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, rsInner.getString("created_user"));

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						if (rsInnerDeep.getString("imgname").equals("")) {
							map.put("companyQuestionsImage", "/images/avatar.png");
						} else {
							map.put("companyQuestionsImage", "/uploads/" + rsInnerDeep.getString("imgname"));
						}

					}

					jsonArray.put(map);

				}
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
	@Path("/companyProfileEvent/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String companyProfileEvent(@PathParam("userId") String userId) {

		try {

			String sqlInner;
			PreparedStatement psInner;

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from `company` where id=" + userId;

			PreparedStatement ps = con.prepareStatement(sql);
			// ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();
			System.out.println(sql);
			while (rs.next()) {
				map.put("companyname", rs.getString("companyname").replace(",$", ""));
				map.put("company_id", rs.getString("id"));
				String companyNamePost = rs.getString("companyname").replace(",$", "");

				sqlInner = "select * from `events` where companyname=?";

				psInner = con.prepareStatement(sqlInner);

				psInner.setString(1, companyNamePost);

				ResultSet rsInner = psInner.executeQuery();

				System.out.println(companyNamePost);

				while (rsInner.next()) {

					map.put("datetime", rsInner.getString("datetime"));
					map.put("notes", rsInner.getString("notes"));
					map.put("location", rsInner.getString("location"));
					map.put("eid", rsInner.getString("id"));
					map.put("Industry", rsInner.getString("Industry"));
					map.put("eventtype", rsInner.getString("eventtype"));
					map.put("eventname", rsInner.getString("eventname"));
					map.put("created_user", rsInner.getString("created_user"));
					map.put("created_uname", rsInner.getString("created_uname"));
					map.put("created_by", rsInner.getString("created_by"));

					String countSql = "SELECT * FROM `events` WHERE id=?";

					PreparedStatement psevent = con.prepareStatement(countSql);
					psevent.setString(1, rsInner.getString("id"));

					String eventDate = rsInner.getString("datetime");

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date todaysDate = new Date();

					if ((eventDate).compareTo(sdf.format(todaysDate)) < 0) {
						map.put("event", "Closed");
					} else {
						map.put("created_by", "I'm going");
					}

					ResultSet eventRs = psevent.executeQuery();

					if (eventRs.next()) {

						if (eventRs.getInt(1) <= 0) {
							map.put("count", "Be First to Register");
						} else {
							map.put("count", eventRs.getRow() + " People going");
						}
					} else {
						map.put("count", "Be First to Register");
					}

					String sqlInnerDeep = "select * from `user_login` where id=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, rsInner.getString("created_user"));

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						if (rsInnerDeep.getString("imgname").equals("")) {
							map.put("companyEventImage", "/images/avatar.png");
						} else {
							map.put("companyEventImage", "/uploads/" + rsInnerDeep.getString("imgname"));
						}

					}

					jsonArray.put(map);

				}
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
