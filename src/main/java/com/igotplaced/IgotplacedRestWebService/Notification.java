package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

				map.put("created_by", rs.getString("created_by"));
				map.put("ids", rs.getString("ids"));
				map.put("Caption", rs.getString("Caption"));

				String sqlInner = "select imgname,department,fname from user_login where id=?";

				PreparedStatement psInner = con.prepareStatement(sqlInner);
				psInner.setString(1, rs.getString("user_id"));

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					if (rs.getString("Caption").equals("newevent")) {
						map.put("post", rsInner.getString("fname") + " Added newEvent");
					} else {
						map.put("post", rsInner.getString("fname") + " Commented for your " + rs.getString("Caption"));
					}

					map.put("fname", rsInner.getString("fname"));
					if (rsInner.getString("imgname").equals("")) {
						map.put("imgname", "/images/avatar.png");
					} else {
						map.put("imgname", "/uploads/" + rsInner.getString("imgname"));
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

					map.put("post", rs.getString("post").replaceAll("\\<.*?\\>", ""));
					map.put("Industry", rs.getString("Industry"));
					map.put("companyname", rs.getString("companyname"));
					map.put("created_user", rs.getString("created_user"));
					map.put("created_uname", rs.getString("created_uname"));
					map.put("created_by", rs.getString("created_by"));
					map.put("companyname", rs.getString("companyname"));

					if (rs.getString("imgname").equals("")) {
						map.put("imgname", "/images/avatar.png");
					} else {
						map.put("imgname", "/uploads/" + rs.getString("imgname"));
					}

					String sqlInnerDeep = "SELECT a.id id,a.pid pid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM post_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.pid=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, id);

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						Cmap.put("Cuser_id", rsInnerDeep.getString("user_id"));
						Cmap.put("Ccreated_uname", rsInnerDeep.getString("created_uname"));
						Cmap.put("Ccomments", rsInnerDeep.getString("comments"));
						Cmap.put("Ccreated_by", rsInnerDeep.getString("created_by"));

						if (rsInnerDeep.getString("user_id").equals(id) || rs.getString("created_user").equals(id)) {
							Cmap.put("delete", "1");
						}

						if (rsInnerDeep.getString("imgname").equals("")) {
							Cmap.put("postCommentuserimgname", "/images/avatar.png");
						} else {
							Cmap.put("postCommentuserimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
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

					map.put("eventname", rs.getString("eventname"));
					map.put("datetime", rs.getString("datetime"));
					map.put("eventtype", rs.getString("eventtype"));
					map.put("location", rs.getString("location"));
					map.put("notes", rs.getString("notes").replaceAll("\\<.*?\\>", ""));
					map.put("id", rs.getString("id"));
					map.put("Industry", rs.getString("Industry"));
					//map.put("companyname", rs.getString("companyname"));
					map.put("created_uname", rs.getString("created_uname"));
					map.put("created_by", rs.getString("created_by"));

					if (rs.getString("imgname").equals("")) {
						map.put("imgname", "/images/avatar.png");
					} else {
						map.put("imgname", "/uploads/" + rs.getString("imgname"));
					}
					
					String sqlInner = "SELECT * FROM `event_register` WHERE eventid=?";
					PreparedStatement psInner = con.prepareStatement(sqlInner);
					psInner.setString(1, rs.getString("id"));

					ResultSet rsInner = psInner.executeQuery();

					while (rsInner.next()) {

						map.put("reg_count", String.valueOf(rsInner.getRow()));

					}

					String sqlInnerDeep = "SELECT a.id id,a.eid eid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM event_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.eid=?";
					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, id);

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						Cmap.put("Cuser_id", rsInnerDeep.getString("user_id"));
						Cmap.put("Ccreated_uname", rsInnerDeep.getString("created_uname"));
						Cmap.put("Ccomments", rsInnerDeep.getString("comments"));
						Cmap.put("Ccreated_by", rsInnerDeep.getString("created_by"));

						if (rsInnerDeep.getString("user_id").equals(id) || rs.getString("created_user").equals(id)) {
							Cmap.put("delete", "1");
						}

						if (rsInnerDeep.getString("imgname").equals("")) {
							Cmap.put("eventCommentuserimgname", "/images/avatar.png");
						} else {
							Cmap.put("eventCommentuserimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
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

					map.put("question", rs.getString("question").replaceAll("\\<.*?\\>", ""));
					map.put("industryname", rs.getString("industryname"));
					map.put("companyname", rs.getString("companyname"));
					//map.put("companyname", rs.getString("companyname"));
					map.put("created_uname", rs.getString("created_uname"));
					map.put("created_by", rs.getString("created_by"));

					if (rs.getString("imgname").equals("")) {
						map.put("imgname", "/images/avatar.png");
					} else {
						map.put("imgname", "/uploads/" + rs.getString("imgname"));
					}
					
					String sqlInnerDeep = "SELECT a.id id,a.qid qid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM questn_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.qid=?";
					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, id);

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						Cmap.put("Cuser_id", rsInnerDeep.getString("user_id"));
						Cmap.put("Ccreated_uname", rsInnerDeep.getString("created_uname"));
						Cmap.put("Ccomments", rsInnerDeep.getString("comments"));
						Cmap.put("Ccreated_by", rsInnerDeep.getString("created_by"));

						if (rsInnerDeep.getString("user_id").equals(id) || rs.getString("created_user").equals(id)) {
							Cmap.put("delete", "1");
						}

						if (rsInnerDeep.getString("imgname").equals("")) {
							Cmap.put("questionCommentuserimgname", "/images/avatar.png");
						} else {
							Cmap.put("questionCommentuserimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
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

					map.put("feedback", rs.getString("feedback").replaceAll("\\<.*?\\>", ""));
					map.put("interview_status", rs.getString("interview_status"));
					map.put("companyname", rs.getString("companyname"));
					map.put("industryname", rs.getString("industryname"));
					map.put("created_by", rs.getString("created_by"));
					map.put("username", rs.getString("username"));
					map.put("user_id", rs.getString("user_id"));

					if (rs.getString("imgname").equals("")) {
						map.put("imgname", "/images/avatar.png");
					} else {
						map.put("imgname", "/uploads/" + rs.getString("imgname"));
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
							Cmap.put("interviewExperienceCommentuserimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
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
