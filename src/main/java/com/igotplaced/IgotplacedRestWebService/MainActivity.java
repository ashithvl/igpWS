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

@Path("/home")
public class MainActivity {

	Connection con = null;
	JSONObject jsonObj = null;
	JSONArray jsonArray = null;
	JSONArray commentArray = null;
	JSONObject newObject = null;
	JSONObject commentJsonObject = null;
	Map<String, String> map = null;
	Map<String, String> commentMap = null;
	int pc;

	@GET
	@Path("/topPost/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String topPost(@PathParam("userId") String userId, @QueryParam("start") int start,
			@QueryParam("size") int size) {

		try {

			jsonArray = new JSONArray();
			commentArray = new JSONArray();
			newObject = new JSONObject();

			commentJsonObject = new JSONObject();

			map = new HashMap<String, String>();
			commentMap = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `user_login` WHERE id=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("fname", rs.getString("fname"));
				if (rs.getString("imgname").equals("")) {
					map.put("imgname", "/images/avatar.png");
				} else {
					map.put("imgname", "/uploads/" + rs.getString("imgname"));
				}

				String sqlInner;
				PreparedStatement psInner;

				if (rs.getString("industry1").equals("All Industries")
						|| rs.getString("industry2").equals("All Industries")
						|| rs.getString("industry3").equals("All Industries")) {

					sqlInner = "select * from `post` order by pid desc ";

					psInner = con.prepareStatement(sqlInner);

				} else {
					sqlInner = "select * from `post` where Industry=? or Industry=? or Industry=? or Industry=? order by modified_by desc ";

					psInner = con.prepareStatement(sqlInner);
					psInner.setString(1, rs.getString("industry1"));
					psInner.setString(2, rs.getString("industry2"));
					psInner.setString(3, rs.getString("industry3"));
					psInner.setString(4, "All Industries");

				}

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					map.put("post", rsInner.getString("post").replaceAll("\\<.*?\\>", ""));
					map.put("pid", rsInner.getString("pid"));
					map.put("Industry", rsInner.getString("Industry"));
					map.put("created_user", rsInner.getString("created_user"));
					map.put("companyname", rsInner.getString("companyname"));
					map.put("created_uname", rsInner.getString("created_uname"));
					map.put("created_by", rsInner.getString("created_by"));
					map.put("created_uname", rsInner.getString("created_uname"));

					String sqlInnerDeep = "select * from `user_login` where id=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, rsInner.getString("created_user"));

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						if (rsInnerDeep.getString("imgname").equals("")) {
							map.put("postuserimgname", "/images/avatar.png");
						} else {
							map.put("postuserimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
						}
					}

				/*	String sqlPc = "select imgname,department,college from user_login where id=?";

					PreparedStatement psPc = con.prepareStatement(sqlPc);
					psPc.setString(1, rsInner.getString("pid"));

					ResultSet rsPc = psPc.executeQuery();

					while (rsPc.next()) {

						if (rsPc.getRow() > 1) {
							pc = rsPc.getRow() - 2;
						} else if (rsPc.getRow() == 1 || rsPc.getRow() == 0) {
							pc = 0;
						}

					}

					String sqlComment = "SELECT a.id id,a.pid pid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM post_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.pid=? LIMIT ?,2";

					PreparedStatement psComment = con.prepareStatement(sqlComment);
					psComment.setString(1, rsInner.getString("pid"));
					psComment.setInt(2, pc);

					ResultSet rsComment = psComment.executeQuery();

					while (rsComment.next()) {

						commentMap.put("commentuser_id", rsComment.getString("user_id"));
						commentMap.put("commentcreated_uname", rsComment.getString("created_uname"));
						commentMap.put("commentcomments", rsComment.getString("comments"));
						commentMap.put("commentcreated_by", rsInner.getString("created_user"));
						commentMap.put("commentuser_id", rsComment.getString("user_id"));
						commentMap.put("commentid", rsComment.getString("id"));

						if (rsComment.getString("imgname").equals("")) {
							commentMap.put("commentuserimgname", "/images/avatar.png");
						} else {
							commentMap.put("commentuserimgname", "/uploads/" + rsComment.getString("imgname"));
						}

						if (rsInner.getString("created_user").equals(userId)
								|| rsComment.getString("user_id").equals(userId)) {
							commentMap.put("commentdelete", "1");
						} else {
							commentMap.put("commentdelete", "0");
						}
						
						 * commentArray.put(commentMap);
						 
						commentArray.put(commentMap);
					}*/

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
	@Path("/topPostComments/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String topPostComments(@PathParam("userId") String userId) {

		try {

			jsonArray = new JSONArray();
			commentArray = new JSONArray();

			commentJsonObject = new JSONObject();

			map = new HashMap<String, String>();
			commentMap = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `user_login` WHERE id=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String sqlInner;
				PreparedStatement psInner;

				if (rs.getString("industry1").equals("All Industries")
						|| rs.getString("industry2").equals("All Industries")
						|| rs.getString("industry3").equals("All Industries")) {

					sqlInner = "select * from `post` order by pid desc ";

					psInner = con.prepareStatement(sqlInner);

				} else {
					sqlInner = "select * from `post` where Industry=? or Industry=? or Industry=? or Industry=? order by modified_by desc ";

					psInner = con.prepareStatement(sqlInner);
					psInner.setString(1, rs.getString("industry1"));
					psInner.setString(2, rs.getString("industry2"));
					psInner.setString(3, rs.getString("industry3"));
					psInner.setString(4, "All Industries");

				}

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					map.put("post", rsInner.getString("post").replaceAll("\\<.*?\\>", ""));
					map.put("pid", rsInner.getString("pid"));
					map.put("created_user", rsInner.getString("created_user"));
					map.put("companyname", rsInner.getString("companyname"));
					map.put("created_uname", rsInner.getString("created_uname"));
					map.put("created_by", rsInner.getString("created_by"));
					map.put("created_uname", rsInner.getString("created_uname"));

					String sqlInnerDeep = "select * from `user_login` where id=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, rsInner.getString("created_user"));

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						if (rsInnerDeep.getString("imgname").equals("")) {
							map.put("postuserimgname", "/images/avatar.png");
						} else {
							map.put("postuserimgname", "/uploads/" + rsInnerDeep.getString("imgname"));
						}
					}

					String sqlPc = "SELECT * FROM `interview_comm` WHERE iid= ? order by desc";

					PreparedStatement psPc = con.prepareStatement(sqlPc);
					psPc.setString(1, rsInner.getString("pid"));

					ResultSet rsPc = psPc.executeQuery();

					while (rsPc.next()) {

						if (rsPc.getRow() > 1) {
							pc = rsPc.getRow() - 2;
						} else if (rsPc.getRow() == 1 || rsPc.getRow() == 0) {
							pc = 0;
						}

					}

					String sqlComment = "SELECT a.id id,a.pid pid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM post_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.pid=? LIMIT ?,2";

					PreparedStatement psComment = con.prepareStatement(sqlComment);
					psComment.setString(1, rsInner.getString("pid"));
					psComment.setInt(2, pc);

					ResultSet rsComment = psComment.executeQuery();

					while (rsComment.next()) {

						commentMap.put("commentuser_id", rsComment.getString("user_id"));
						commentMap.put("commentcreated_uname", rsComment.getString("created_uname"));
						commentMap.put("commentcomments", rsComment.getString("comments"));
						commentMap.put("commentcreated_by", rsInner.getString("created_user"));
						commentMap.put("commentuser_id", rsComment.getString("user_id"));
						commentMap.put("commentid", rsComment.getString("id"));

						if (rsComment.getString("imgname").equals("")) {
							commentMap.put("commentuserimgname", "/images/avatar.png");
						} else {
							commentMap.put("commentuserimgname", "/uploads/" + rsComment.getString("imgname"));
						}

						if (rsInner.getString("created_user").equals(userId)
								|| rsComment.getString("user_id").equals(userId)) {
							commentMap.put("commentdelete", "1");
						} else {
							commentMap.put("commentdelete", "0");
						}
						
						 commentArray.put(commentMap);
						 
						commentArray.put(commentMap);
					}

					jsonArray.put(map);

				}

				commentJsonObject.put("comment", commentArray);

			}

			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return commentJsonObject.toString();
	}

	@GET
	@Path("/topInterviewExperience/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String topInterviewExperience(@PathParam("userId") String userId, @QueryParam("start") int start,
			@QueryParam("size") int size) {

		try {

			jsonArray = new JSONArray();
			commentArray = new JSONArray();
			newObject = new JSONObject();

			commentJsonObject = new JSONObject();

			map = new HashMap<String, String>();
			commentMap = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `user_login` WHERE id=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("fname", rs.getString("fname"));
				if (rs.getString("imgname").equals("")) {
					map.put("imgname", "/images/avatar.png");
				} else {
					map.put("imgname", "/uploads/" + rs.getString("imgname"));
				}

				String sqlInner;
				PreparedStatement psInner;

				if (rs.getString("industry1").equals("All Industries")
						|| rs.getString("industry2").equals("All Industries")
						|| rs.getString("industry3").equals("All Industries")) {

					sqlInner = "select * from `interview_exp` order by id desc";

					psInner = con.prepareStatement(sqlInner);

				} else {
					sqlInner = "select * from `questions` where industryname=? or industryname=? or industryname=? or industryname=? order by id desc";

					psInner = con.prepareStatement(sqlInner);
					psInner.setString(1, rs.getString("industry1"));
					psInner.setString(2, rs.getString("industry2"));
					psInner.setString(3, rs.getString("industry3"));
					psInner.setString(4, "All Industries");

				}

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					map.put("question", rsInner.getString("question").replaceAll("\\<.*?\\>", ""));
					map.put("category", rsInner.getString("category"));
					map.put("subcategory", rsInner.getString("subcategory"));
					map.put("industryname", rsInner.getString("industryname"));
					map.put("companyname", rsInner.getString("companyname"));

					map.put("created_user", rsInner.getString("created_user"));
					map.put("created_by", rsInner.getString("created_by"));

					String sqlInnerDeep = "select * from `user_login` where id=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, rsInner.getString("created_user"));

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						if (rsInnerDeep.getString("imgname").equals("")) {
							map.put("questionUserImgName", "/images/avatar.png");
						} else {
							map.put("questionUserImgName", "/uploads/" + rsInnerDeep.getString("imgname"));
						}
					}

		/*			String sqlPc = "SELECT * FROM `questn_comm` WHERE qid=? order by desc";

					PreparedStatement psPc = con.prepareStatement(sqlPc);
					psPc.setInt(1, rsInner.getInt("id"));

					ResultSet rsPc = psPc.executeQuery();

					while (rsPc.next()) {

						if (rsPc.getRow() > 1) {
							pc = rsPc.getRow() - 2;
						} else if (rsPc.getRow() == 1 || rsPc.getRow() == 0) {
							pc = 0;
						}

					}

					String sqlquestion = "SELECT a.id id,a.qid qid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM questn_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.qid=? LIMIT ?,2";
					PreparedStatement psquestion = con.prepareStatement(sqlquestion);
					psquestion.setString(1, rsInner.getString("user_id"));
					psquestion.setInt(2, pc);

					ResultSet question = psquestion.executeQuery();

					while (question.next()) {

						commentMap.put("interviewExperienceUser_id", question.getString("user_id"));
						commentMap.put("interviewExperiencecreated_uname",
								question.getString("created_uname"));
						commentMap.put("interviewExperiencecomments", question.getString("comments"));
						commentMap.put("interviewExperiencecreated_by", rsInner.getString("created_user"));
						commentMap.put("interviewExperienceuser_id", question.getString("created_by"));
						commentMap.put("interviewExperienceid", question.getString("id"));

						if (question.getString("imgname").equals("")) {
							commentMap.put("commentuserimgname", "/images/avatar.png");
						} else {
							commentMap.put("commentuserimgname",
									"/uploads/" + question.getString("imgname"));
						}

						if (rsInner.getString("created_user").equals(userId)
								|| question.getString("user_id").equals(userId)) {
							commentMap.put("interviewExperiencedelete", "1");
						} else {
							commentMap.put("interviewExperiencedelete", "0");
						}
						
						 * commentArray.put(commentMap);
						 
						commentArray.put(commentMap);
					}
*/
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
	@Path("/topQuestion/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String topQuestion(@PathParam("userId") String userId, @QueryParam("start") int start,
			@QueryParam("size") int size) {

		try {

			jsonArray = new JSONArray();
			commentArray = new JSONArray();
			newObject = new JSONObject();

			commentJsonObject = new JSONObject();

			map = new HashMap<String, String>();
			commentMap = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `user_login` WHERE id=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("fname", rs.getString("fname"));
				if (rs.getString("imgname").equals("")) {
					map.put("imgname", "/images/avatar.png");
				} else {
					map.put("imgname", "/uploads/" + rs.getString("imgname"));
				}

				String sqlInner;
				PreparedStatement psInner;

				if (rs.getString("industry1").equals("All Industries")
						|| rs.getString("industry2").equals("All Industries")
						|| rs.getString("industry3").equals("All Industries")) {

					sqlInner = "select * from `interview_exp` order by id desc";

					psInner = con.prepareStatement(sqlInner);

				} else {
					sqlInner = "select * from `interview_exp` where industryname=? or industryname=? or industryname=? or industryname=? order by modified_by desc ";

					psInner = con.prepareStatement(sqlInner);
					psInner.setString(1, rs.getString("industry1"));
					psInner.setString(2, rs.getString("industry2"));
					psInner.setString(3, rs.getString("industry3"));
					psInner.setString(4, "All Industries");

				}

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					map.put("feedback", rsInner.getString("feedback").replaceAll("\\<.*?\\>", ""));
					map.put("interview_status", rsInner.getString("interview_status"));
					map.put("industryname", rsInner.getString("industryname"));
					map.put("companyname", rsInner.getString("companyname"));
					map.put("user_id", rsInner.getString("user_id"));

					map.put("username", rsInner.getString("username"));
					map.put("created_by", rsInner.getString("created_by"));

					String sqlInnerDeep = "select * from `user_login` where id=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, rsInner.getString("user_id"));

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						if (rsInnerDeep.getString("imgname").equals("")) {
							map.put("interviewUserImgName", "/images/avatar.png");
						} else {
							map.put("interviewUserImgName", "/uploads/" + rsInnerDeep.getString("imgname"));
						}
					}

				/*	String sqlPc = "SELECT * FROM `interview_comm` WHERE iid=? order by desc ";

					PreparedStatement psPc = con.prepareStatement(sqlPc);
					psPc.setString(1, rsInner.getString("user_id"));

					ResultSet rsPc = psPc.executeQuery();

					while (rsPc.next()) {

						if (rsPc.getRow() > 1) {
							pc = rsPc.getRow() - 2;
						} else if (rsPc.getRow() == 1 || rsPc.getRow() == 0) {
							pc = 0;
						}

					}

					String sqlinterviewExperiencet = "SELECT a.id id,a.iid iid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM interview_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.iid=? LIMIT ?,2";

					PreparedStatement psinterviewExperience = con.prepareStatement(sqlinterviewExperiencet);
					psinterviewExperience.setString(1, rsInner.getString("user_id"));
					psinterviewExperience.setInt(2, pc);

					ResultSet rsinterviewExperience = psinterviewExperience.executeQuery();

					while (rsinterviewExperience.next()) {

						commentMap.put("interviewExperienceUser_id", rsinterviewExperience.getString("user_id"));
						commentMap.put("interviewExperiencecreated_uname",
								rsinterviewExperience.getString("created_uname"));
						commentMap.put("interviewExperiencecomments", rsinterviewExperience.getString("comments"));
						commentMap.put("interviewExperiencecreated_by", rsInner.getString("created_user"));
						commentMap.put("interviewExperienceuser_id", rsinterviewExperience.getString("created_by"));
						commentMap.put("interviewExperienceid", rsinterviewExperience.getString("id"));

						if (rsinterviewExperience.getString("imgname").equals("")) {
							commentMap.put("commentuserimgname", "/images/avatar.png");
						} else {
							commentMap.put("commentuserimgname",
									"/uploads/" + rsinterviewExperience.getString("imgname"));
						}

						if (rsInner.getString("created_user").equals(userId)
								|| rsinterviewExperience.getString("user_id").equals(userId)) {
							commentMap.put("interviewExperiencedelete", "1");
						} else {
							commentMap.put("interviewExperiencedelete", "0");
						}
						
						 * commentArray.put(commentMap);
						 
						commentArray.put(commentMap);
					}
*/
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

}
