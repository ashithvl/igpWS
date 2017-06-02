package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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

import utils.Constants;

@Path("/profileService")
public class Profile {

	Connection con = null;
	JSONObject jsonObj = null;
	JSONObject newObject = null;
	JSONArray jsonArray = null;
	Map<String, String> map = null;

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
	@Path("/profileUpdate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String register(@FormParam("id") String id, @FormParam("industry1") String industry1,
			@FormParam("industry2") String industry2, @FormParam("industry3") String industry3,
			@FormParam("company1") String company1, @FormParam("company2") String company2,
			@FormParam("company3") String company3, @FormParam("phone") String phone,
			@FormParam("interest") String interest, @FormParam("location") String location,
			@FormParam("name") String name, @FormParam("email") String email, @FormParam("year") String year,
			@FormParam("colg") String colg, @FormParam("dept") String dept, @FormParam("check") String check) {

		int result = 0;
		int rsLastGeneratedAutoIncrementId = 0;
		String sqlInner;

		try {

			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "UPDATE `user_login` SET check=?,industry1=?,industry2=?,industry3=?,company1=?,company2=?,"
					+ "company3=?,phone=?,location=?,interest=?,name =?, email = ?year=?,colg=?,dept=?,last_loggedin=? WHERE id=?";

			PreparedStatement psInner = con.prepareStatement(sqlInner);
			psInner.setString(1, check);
			psInner.setString(2, industry1);
			psInner.setString(3, industry2);
			psInner.setString(4, industry3);
			psInner.setString(5, company1);
			psInner.setString(6, company2);
			psInner.setString(7, company3);
			psInner.setString(8, phone);
			psInner.setString(9, location);
			psInner.setString(10, interest);
			psInner.setString(11, name);
			psInner.setString(12, email);
			psInner.setString(13, colg);
			psInner.setString(14, dept);
			psInner.setString(15, dateTime);
			psInner.setString(16, id);

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
					map.put("companyname", rsInner.getString("companyname"));
					map.put("created_uname", rsInner.getString("created_uname"));

					jsonArray.put(map);

				}

				for (int i = start; i <= size; i++) {
					if (!jsonArray.isNull(i)) {
						newObject.append("", jsonArray.getJSONObject(i));
					}
				}
			}

			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return newObject.toString();
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
				map.put("companyname", rs.getString("companyname"));
				map.put("username", rs.getString("username"));
				map.put("created_by", rs.getString("created_by"));
				map.put("fid", rs.getString("id"));

				jsonArray.put(map);

				for (int i = start; i <= size; i++) {
					if (!jsonArray.isNull(i)) {
						newObject.append("", jsonArray.getJSONObject(i));
					}
				}
			}

			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return newObject.toString();
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
				map.put("companyname", rs.getString("companyname"));
				map.put("qid", rs.getString("id"));
				map.put("created_user", rs.getString("created_user"));

				jsonArray.put(map);

				for (int i = start; i <= size; i++) {
					if (!jsonArray.isNull(i)) {
						newObject.append("", jsonArray.getJSONObject(i));
					}
				}
			}

			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return newObject.toString();
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

			String sql = "SELECT a.id id,a.eventname eventname,a.eventtype eventtype,a.status status,a.Industry Industry,a.location location,a.datetime datetime,a.created_by created_by,a.created_user created_user,a.created_uname created_uname,b.imgname FROM events as a INNER JOIN user_login as b ON a.created_user=b.id AND a.created_user=? order by created_by desc";
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
				map.put("location", rs.getString("location"));
				map.put("eid", rs.getString("id"));
				map.put("Industry", rs.getString("Industry"));
				map.put("created_user", rs.getString("created_user"));
				map.put("created_uname", rs.getString("created_uname"));
				map.put("created_by", rs.getString("created_by"));

				jsonArray.put(map);

				for (int i = start; i <= size; i++) {
					if (!jsonArray.isNull(i)) {
						newObject.append("", jsonArray.getJSONObject(i));
					}
				}
			}

			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return newObject.toString();
	}

}
