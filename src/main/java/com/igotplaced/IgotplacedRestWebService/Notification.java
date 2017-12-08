package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import utils.Constants;

@Path("/notificationService")
public class Notification {

	Connection con = null;
	JSONObject jsonObj = null;
	JSONObject newObject = null;
	JSONArray jsonArray = null;
	Map<String, String> map = null;

	@GET
	@Path("/notification/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String recentNotification(@PathParam("id") String id, @QueryParam("start") int start,
			@QueryParam("size") int size) {

		try {

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT Caption, ids, created_by,user_id, id FROM (SELECT  'event'Caption, eid ids, created_by,user_id, id FROM event_comm WHERE evt_createrid =  ? UNION ALL SELECT  'post'Caption, pid ids, created_by,user_id, id FROM post_comm WHERE post_createrid =  ? UNION ALL SELECT  'newevent'Caption, id ids, created_by,created_user user_id, id	FROM events UNION ALL SELECT  'question'Caption, qid ids, created_by,user_id, id FROM questn_comm WHERE ques_createrid =  ? )subquery ORDER BY created_by DESC , FIELD( Caption,  'event',  'post',  'question' ) LIMIT 10";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, id);
			ps.setString(3, id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date datePost = readFormat.parse(rs.getString("created_by"));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
				String postTime = dateFormat.format(datePost);

				map.put("created_by", postTime);
				map.put("ids", rs.getString("ids"));
				map.put("Caption", rs.getString("Caption"));

				String sqlInner = "select imgname,department,fname from user_login where id=?";

				PreparedStatement psInner = con.prepareStatement(sqlInner);
				psInner.setString(1, rs.getString("user_id"));

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					if (!rs.getString("user_id").equals(id)) {

						if (rs.getString("Caption").equals("newevent")) {
							map.put("post", rsInner.getString("fname") + " Added new Event");
						} else {
							map.put("post",
									rsInner.getString("fname") + " Commented for your " + rs.getString("Caption"));
						}

						map.put("fname", rsInner.getString("fname"));
						if (rsInner.getString("imgname").equals("")) {
							map.put("imgname", "/images/avatar.png");
						} else {
							map.put("imgname", "/uploads/" + rsInner.getString("imgname"));
						}
					}
				}
				jsonArray.put(map);

			}

			for (int i = start; i <= size; i++) {
				if (!jsonArray.isNull(i)) {
					newObject.append("", jsonArray.getJSONObject(i));
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
	@Path("/postPopUp/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String postPopUp(@PathParam("id") String id) {

		Map<String, String> Cmap = new HashMap<String, String>();

		JSONArray CjsonArray = new JSONArray();

		try {

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT a.pid pid,a.post post,a.status status,a.created_by created_by,a.Industry Industry,a.companyname companyname,a.created_user created_user,a.created_uname created_uname,b.imgname FROM post as a INNER JOIN user_login as b ON a.created_user=b.id and a.pid=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date datePost = readFormat.parse(rs.getString("created_by"));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
				String postTime = dateFormat.format(datePost);

				map.put("post", Jsoup.parse(rs.getString("post")).text());
				map.put("Industry", rs.getString("Industry"));
				map.put("companyname", rs.getString("companyname").replaceAll(",$", ""));
				map.put("created_user", rs.getString("created_user"));
				map.put("created_uname", rs.getString("created_uname"));
				map.put("created_by", postTime);
				map.put("pid", id);

				if (rs.getString("imgname").equals("")) {
					map.put("imgname", "/images/avatar.png");
				} else {
					map.put("imgname", "/uploads/" + rs.getString("imgname"));
				}

				String companyRequest = "SELECT * FROM `company` where companyname=?";

				PreparedStatement psCompany = con.prepareStatement(companyRequest);
				psCompany.setString(1, rs.getString("companyname").replaceAll(",$", ""));
				ResultSet rsCompany = psCompany.executeQuery();
				while (rsCompany.next()) {
					map.put("company_id", rsCompany.getString("id"));
				}

				String sqlInnerDeep = "SELECT a.id id,a.pid pid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM post_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.pid=?";

				PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
				psInnerDeep.setString(1, id);

				ResultSet rsInnerDeep = psInnerDeep.executeQuery();

				while (rsInnerDeep.next()) {

					map.put("Cuser_id", rsInnerDeep.getString("user_id"));
					map.put("Ccreated_uname", rsInnerDeep.getString("created_uname"));
					map.put("Ccomments", rsInnerDeep.getString("comments"));
					map.put("Ccreated_by", rsInnerDeep.getString("created_by"));

					if (rsInnerDeep.getString("user_id").equals(id) || rs.getString("created_user").equals(id)) {
						map.put("delete", "1");
					}

					if (rsInnerDeep.getString("imgname").equals("")) {
						map.put("postCommentuserimgname", "/images/avatar.png");
					} else {
						map.put("postCommentuserimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
					}

					// CjsonArray.put(Cmap);
				}

				for (int i = 0; i < CjsonArray.length(); i++) {
					jsonArray.put(CjsonArray.get(i));

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
	@Path("/eventPopUp/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String eventPopUp(@PathParam("id") String id) {

		Map<String, String> Cmap = new HashMap<String, String>();

		JSONArray CjsonArray = new JSONArray();

		try {

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT a.id id,a.eventname eventname,a.eventtype eventtype,a.status status,a.Industry Industry,a.location location,a.datetime datetime,a.notes notes, a.created_by created_by,a.created_user created_user,a.created_uname created_uname,b.imgname FROM events as a INNER JOIN user_login as b ON a.created_user=b.id AND a.id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date datePost = readFormat.parse(rs.getString("created_by"));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
				String postTime = dateFormat.format(datePost);

				map.put("eventname", rs.getString("eventname"));
				map.put("datetime", rs.getString("datetime"));
				map.put("eventtype", rs.getString("eventtype"));
				map.put("location", rs.getString("location"));
				map.put("notes", Jsoup.parse(rs.getString("notes")).text());
				map.put("id", rs.getString("id"));
				map.put("Industry", rs.getString("Industry"));
				// map.put("companyname", rs.getString("companyname"));
				map.put("created_uname", rs.getString("created_uname"));
				map.put("created_by", postTime);

				if (rs.getString("imgname").equals("")) {
					map.put("imgname", "/images/avatar.png");
				} else {
					map.put("imgname", "/uploads/" + rs.getString("imgname"));
				}

				String cName = rs.getString("created_uname");

				String sqlUserDeep = "select * from `user_login` where fname=?";

				PreparedStatement psInnerUserDeep = con.prepareStatement(sqlUserDeep);
				psInnerUserDeep.setString(1, cName);

				ResultSet rsInnerUserDeep = psInnerUserDeep.executeQuery();

				while (rsInnerUserDeep.next()) {

					map.put("userid", rsInnerUserDeep.getString("id"));

				}

				String sqlInner = "SELECT * FROM `event_register` WHERE eventid=?";
				PreparedStatement psInner = con.prepareStatement(sqlInner);
				psInner.setString(1, rs.getString("id"));

				ResultSet rsInner = psInner.executeQuery();

				map.put("reg_count", "" + String.valueOf(rsInner.getRow()) + " People going");

				String eventDate = rs.getString("datetime");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date todaysDate = new Date();

				if ((eventDate).compareTo(sdf.format(todaysDate)) < 0) {
					map.put("event", "Closed");
				} else {
					map.put("event", "I'm going");
				}

				String sqlInnerDeep = "SELECT a.id id,a.eid eid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM event_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.eid=?";
				PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
				psInnerDeep.setString(1, id);

				ResultSet rsInnerDeep = psInnerDeep.executeQuery();

				while (rsInnerDeep.next()) {

					map.put("Cuser_id", rsInnerDeep.getString("user_id"));
					map.put("Ccreated_uname", rsInnerDeep.getString("created_uname"));
					map.put("Ccomments", rsInnerDeep.getString("comments"));
					map.put("Ccreated_by", rsInnerDeep.getString("created_by"));

					if (rsInnerDeep.getString("user_id").equals(id) || rs.getString("created_user").equals(id)) {
						map.put("delete", "1");
					}

					if (rsInnerDeep.getString("imgname").equals("")) {
						map.put("eventCommentuserimgname", "/images/avatar.png");
					} else {
						map.put("eventCommentuserimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
					}

					// CjsonArray.put(Cmap);
				}

				for (int i = 0; i < CjsonArray.length(); i++) {
					jsonArray.put(CjsonArray.get(i));

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
	@Path("/questionPopUp/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String questionPopUp(@PathParam("id") String id) {

		Map<String, String> Cmap = new HashMap<String, String>();

		JSONArray CjsonArray = new JSONArray();

		try {

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT a.id id,a.companyname companyname,a.industryname industryname,a.question question,a.status status,a.created_by created_by,a.created_user created_user,a.created_uname created_uname,b.imgname FROM questions as a INNER JOIN user_login as b ON a.created_user=b.id  AND a.id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date datePost = readFormat.parse(rs.getString("created_by"));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
				String postTime = dateFormat.format(datePost);

				map.put("question", Jsoup.parse(rs.getString("question")).text());
				map.put("industryname", rs.getString("industryname"));
				map.put("companyname", rs.getString("companyname").replaceAll(",$", ""));
				map.put("qid", id);
				map.put("created_uname", rs.getString("created_uname"));
				map.put("created_by", postTime);

				if (rs.getString("imgname").equals("")) {
					map.put("imgname", "/images/avatar.png");
				} else {
					map.put("imgname", "/uploads/" + rs.getString("imgname"));
				}
				String ComP = rs.getString("companyname").replaceAll(",$", "");

				if (!ComP.equals("Select Company")) {

					String companyRequest = "SELECT * FROM `company` where companyname=?";

					PreparedStatement psCompany = con.prepareStatement(companyRequest);
					psCompany.setString(1, rs.getString("companyname").replaceAll(",$", ""));
					ResultSet rsCompany = psCompany.executeQuery();
					while (rsCompany.next()) {
						map.put("company_id", rsCompany.getString("id"));
					}
				}else{
					map.put("company_id", "");
				}
				String sqlInnerDeep = "SELECT a.id id,a.qid qid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM questn_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.qid=?";
				PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
				psInnerDeep.setString(1, id);

				ResultSet rsInnerDeep = psInnerDeep.executeQuery();

				while (rsInnerDeep.next()) {

					map.put("Cuser_id", rsInnerDeep.getString("user_id"));
					map.put("Ccreated_uname", rsInnerDeep.getString("created_uname"));
					map.put("Ccomments", rsInnerDeep.getString("comments"));
					map.put("Ccreated_by", rsInnerDeep.getString("created_by"));

					if (rsInnerDeep.getString("user_id").equals(id) || rs.getString("created_user").equals(id)) {
						map.put("delete", "1");
					}

					if (rsInnerDeep.getString("imgname").equals("")) {
						map.put("questionCommentuserimgname", "/images/avatar.png");
					} else {
						map.put("questionCommentuserimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
					}

					// CjsonArray.put(map);
				}

				for (int i = 0; i < CjsonArray.length(); i++) {
					jsonArray.put(CjsonArray.get(i));

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
	@Path("/interviewExperiencePopUp/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String interviewExperiencePopUp(@PathParam("id") String id) {

		Map<String, String> Cmap = new HashMap<String, String>();

		JSONArray CjsonArray = new JSONArray();

		try {

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT a.id id,a.companyname companyname,a.industryname industryname,a.feedback feedback,a.interview_status interview_status,a.created_by created_by,a.user_id user_id,a.username username,b.imgname FROM interview_exp as a INNER JOIN user_login as b ON a.user_id=b.id  AND a.id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date datePost = readFormat.parse(rs.getString("created_by"));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
				String postTime = dateFormat.format(datePost);

				map.put("feedback", Jsoup.parse(rs.getString("feedback")).text());
				map.put("interview_status", rs.getString("interview_status"));
				map.put("companyname", rs.getString("companyname"));
				map.put("industryname", rs.getString("industryname"));
				map.put("created_by", postTime);
				map.put("username", rs.getString("username"));
				map.put("user_id", rs.getString("user_id"));

				if (rs.getString("imgname").equals("")) {
					map.put("imgname", "/images/avatar.png");
				} else {
					map.put("imgname", "/uploads/" + rs.getString("imgname"));
				}

				String companyRequest = "SELECT * FROM `company` where companyname=?";

				PreparedStatement psCompany = con.prepareStatement(companyRequest);
				psCompany.setString(1, rs.getString("companyname").replaceAll(",$", ""));
				ResultSet rsCompany = psCompany.executeQuery();
				while (rsCompany.next()) {
					map.put("company_id", rsCompany.getString("id"));
				}

				String sqlInnerDeep = "SELECT a.id id,a.iid iid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM interview_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.iid=?";
				PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
				psInnerDeep.setString(1, id);

				ResultSet rsInnerDeep = psInnerDeep.executeQuery();

				while (rsInnerDeep.next()) {

					Cmap.put("Cuser_id", rsInnerDeep.getString("user_id"));
					Cmap.put("Ccreated_uname", rsInnerDeep.getString("created_uname"));
					Cmap.put("Ccomments", rsInnerDeep.getString("comments"));
					Cmap.put("Ccreated_by", rsInnerDeep.getString("created_by"));

					if (rsInnerDeep.getString("user_id").equals(id) || rs.getString("user_id").equals(id)) {
						Cmap.put("delete", "1");
					}

					if (rsInnerDeep.getString("imgname").equals("")) {
						Cmap.put("interviewExperienceCommentuserimgname", "/images/avatar.png");
					} else {
						Cmap.put("interviewExperienceCommentuserimgname",
								"/uploads/" + rsInnerDeep.getString("imgname"));
					}

					CjsonArray.put(Cmap);
				}

				for (int i = 0; i < CjsonArray.length(); i++) {
					jsonArray.put(CjsonArray.get(i));

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

}
