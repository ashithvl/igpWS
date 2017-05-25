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
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Constants;

@Path("/notificationService")
public class Notification {

	Connection con = null;
	JSONObject jsonObj = null;
	JSONArray jsonArray = null;
	Map<String, String> map = null;

	@GET
	@Path("/notification/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String recentNotification(@PathParam("id") String id) {

		try {

			jsonArray = new JSONArray();

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
				map.put("postid", rs.getString("ids"));
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

			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return jsonArray.toString();
	}

	@GET
	@Path("/notificationDetails/{postid}/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String recentNotificationDetails(@PathParam("postid") String postid, @PathParam("userId") String userId) {

		try {

			jsonArray = new JSONArray();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `user_login` WHERE id=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String sqlInner = "SELECT a.pid pid,a.post post,a.status status,a.created_by created_by,a.Industry Industry,a.companyname companyname,a.created_user created_user,a.created_uname created_uname,b.imgname FROM post as a INNER JOIN user_login as b ON a.created_user=b.id and a.pid=?";

				PreparedStatement psInner = con.prepareStatement(sqlInner);
				psInner.setString(1, postid);

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					map.put("post", rsInner.getString("post").replaceAll("\\<.*?\\>", ""));

					map.put("Industry", rsInner.getString("Industry"));

					if (!rsInner.getString("companyname").isEmpty()) {
						map.put("company", rsInner.getString("companyname"));
					} else {
						map.put("company", "");
					}

					if (rsInner.getString("imgname").equals("")) {
						map.put("imgname", "/images/avatar.png");
					} else {
						map.put("imgname", "/uploads/" + rsInner.getString("imgname"));
					}

					map.put("created_by", rsInner.getString("created_by"));

					String sqlInnerDeep = "SELECT a.id id,a.pid pid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM post_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.pid=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, postid);

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						map.put("user_id", rsInnerDeep.getString("user_id"));
						map.put("created_uname", rsInnerDeep.getString("created_uname"));

						if (rsInnerDeep.getString("imgname").equals("")) {
							map.put("createdimgname", "/images/avatar.png");
						} else {
							map.put("comments", "/uploads/" + rsInnerDeep.getString("imgname"));
						}

						map.put("created_user", rsInnerDeep.getString("comments").replaceAll("\\<.*?\\>", ""));

						map.put("created_by", rsInnerDeep.getString("created_by"));
						map.put("created_user", rsInner.getString("created_user"));

						if (rsInnerDeep.getString("user_id").equals(userId)
								|| rsInner.getString("created_user").equals(userId)) {
							map.put("delete", "1");
							map.put("edit", "0");
						}else{

							map.put("delete", "0");
							map.put("edit", "1");
						}
						
						if (rs.getString("imgname").equals("")) {
							map.put("Currentimgname","/images/avatar.png");
						} else {
							map.put("Currentimgname", "/uploads/" + rsInner.getString("imgname"));
						}
						

					}

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
