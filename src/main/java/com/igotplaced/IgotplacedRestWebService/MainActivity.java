package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
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
import org.jsoup.Jsoup;

import com.mysql.cj.api.jdbc.Statement;

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

					map.put("post", Jsoup.parse(rsInner.getString("post")).text());

					map.put("pid", rsInner.getString("pid"));
					map.put("Industry", rsInner.getString("Industry"));
					map.put("created_user", rsInner.getString("created_user"));
					map.put("companyname", rsInner.getString("companyname").replaceAll(",$", ""));
					map.put("created_uname", rsInner.getString("created_uname"));
					map.put("created_by", rsInner.getString("created_by"));
					map.put("created_uname", rsInner.getString("created_uname"));

					String companyName = rsInner.getString("companyname").replaceAll(",$", "");
					

					if (companyName.equals("")) {
						map.put("company_id", "");
					} else {
						String companyRequest = "SELECT * FROM `company` where companyname=?";

						PreparedStatement psCompany = con.prepareStatement(companyRequest);
						psCompany.setString(1, companyName);

						ResultSet rsCompany = psCompany.executeQuery();

					
						while (rsCompany.next()) {
							map.put("company_id", rsCompany.getString("id"));

						}

					}

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

					/*
					 * String sqlPc =
					 * "select imgname,department,college from user_login where id=?"
					 * ;
					 * 
					 * PreparedStatement psPc = con.prepareStatement(sqlPc);
					 * psPc.setString(1, rsInner.getString("pid"));
					 * 
					 * ResultSet rsPc = psPc.executeQuery();
					 * 
					 * while (rsPc.next()) {
					 * 
					 * if (rsPc.getRow() > 1) { pc = rsPc.getRow() - 2; } else
					 * if (rsPc.getRow() == 1 || rsPc.getRow() == 0) { pc = 0; }
					 * 
					 * }
					 * 
					 * String sqlComment =
					 * "SELECT a.id id,a.pid pid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM post_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.pid=? LIMIT ?,2"
					 * ;
					 * 
					 * PreparedStatement psComment =
					 * con.prepareStatement(sqlComment); psComment.setString(1,
					 * rsInner.getString("pid")); psComment.setInt(2, pc);
					 * 
					 * ResultSet rsComment = psComment.executeQuery();
					 * 
					 * while (rsComment.next()) {
					 * 
					 * commentMap.put("commentuser_id",
					 * rsComment.getString("user_id"));
					 * commentMap.put("commentcreated_uname",
					 * rsComment.getString("created_uname"));
					 * commentMap.put("commentcomments",
					 * rsComment.getString("comments"));
					 * commentMap.put("commentcreated_by",
					 * rsInner.getString("created_user"));
					 * commentMap.put("commentuser_id",
					 * rsComment.getString("user_id"));
					 * commentMap.put("commentid", rsComment.getString("id"));
					 * 
					 * if (rsComment.getString("imgname").equals("")) {
					 * commentMap.put("commentuserimgname",
					 * "/images/avatar.png"); } else {
					 * commentMap.put("commentuserimgname", "/uploads/" +
					 * rsComment.getString("imgname")); }
					 * 
					 * if (rsInner.getString("created_user").equals(userId) ||
					 * rsComment.getString("user_id").equals(userId)) {
					 * commentMap.put("commentdelete", "1"); } else {
					 * commentMap.put("commentdelete", "0"); }
					 * 
					 * commentArray.put(commentMap);
					 * 
					 * commentArray.put(commentMap); }
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
	@Path("/postCommentList/{user_id}")
	@Produces(MediaType.TEXT_HTML)
	public String postCommentList(@PathParam("user_id") String user_id) {

		try {

			String sqlInner;
			PreparedStatement psInner;

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from `post_comm` where pid=? ";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user_id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("comments", rs.getString("comments").replaceAll("\\<.*?\\>", ""));
				map.put("created_by", rs.getString("created_by"));
				map.put("created_uname", rs.getString("created_uname"));

				String sqlInnerDeep = "select * from `user_login` where id=?";

				PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
				psInnerDeep.setString(1, rs.getString("user_id"));

				ResultSet rsInnerDeep = psInnerDeep.executeQuery();

				while (rsInnerDeep.next()) {

					if (rsInnerDeep.getString("imgname").equals("")) {
						map.put("commentedUserImage", "/images/avatar.png");
					} else {
						map.put("commentedUserImage", "/uploads/" + rsInnerDeep.getString("imgname"));
					}
				}

				map.put("user_id", rs.getString("user_id"));
				map.put("post_createrid", rs.getString("post_createrid"));
				map.put("id", rs.getString("id"));

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
	@Path("/deletePostComment/{user_id}")
	@Produces(MediaType.TEXT_HTML)
	public String deletePostComment(@PathParam("user_id") String user_id) {

		try {

		

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "delete  from `post_comm` where id=? ";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user_id);
			ps.executeUpdate();
			
			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return jsonArray.toString();
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

					String sqlPc = "SELECT * FROM `post_comm` WHERE iid= ? order by desc";

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

	@POST
	@Path("/postComments")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String postComments(@FormParam("pid") String pid, @FormParam("post_createdid") String post_createdid,
			@FormParam("user_id") String user_id, @FormParam("comments") String comments,
			@FormParam("created_uname") String created_uname) {

		int result = 0;
		String sqlInner = null;

		try {

			int rsLastGeneratedAutoIncrementId = 0;
			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "INSERT INTO post_comm (pid,post_createrid,comments,created_by,user_id,created_uname)"
					+ "VALUES('" + pid + "','" + post_createdid + "','" + comments + "','" + dateTime + "','" + user_id
					+ "','" + created_uname + "')";

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
	
	
	@POST
	@Path("/deleteInterviewComment/{user_id}")
	@Produces(MediaType.TEXT_HTML)
	public String deleteInterviewComment(@PathParam("user_id") String user_id) {

		try {

		

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "delete  from `interview_comm` where id=? ";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user_id);
			ps.executeUpdate();
			
			
			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return jsonArray.toString();
	}

	@POST
	@Path("/interviewComments")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String interviewComments(@FormParam("iid") String iid, @FormParam("intex_createrid") String intex_createrid,
			@FormParam("user_id") String user_id, @FormParam("comments") String comments,
			@FormParam("created_uname") String created_uname) {

		int result = 0;
		String sqlInner = null;

		try {

			int rsLastGeneratedAutoIncrementId = 0;
			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "INSERT INTO interview_comm (iid,intex_createrid,comments,created_by,user_id,created_uname)"
					+ "VALUES('" + iid + "','" + intex_createrid + "','" + comments + "','" + dateTime + "','" + user_id
					+ "','" + created_uname + "')";

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
	@Path("/interviewCommentList/{user_id}")
	@Produces(MediaType.TEXT_HTML)
	public String interviewCommentList(@PathParam("user_id") String user_id) {

		try {

			String sqlInner;
			PreparedStatement psInner;

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from `interview_comm` where iid=? ";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user_id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("comments", rs.getString("comments").replaceAll("\\<.*?\\>", ""));
				map.put("created_by", rs.getString("created_by"));
				map.put("created_uname", rs.getString("created_uname"));

				String sqlInnerDeep = "select * from `user_login` where id=?";

				PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
				psInnerDeep.setString(1, rs.getString("user_id"));

				ResultSet rsInnerDeep = psInnerDeep.executeQuery();

				while (rsInnerDeep.next()) {

					if (rsInnerDeep.getString("imgname").equals("")) {
						map.put("commentedUserImage", "/images/avatar.png");
					} else {
						map.put("commentedUserImage", "/uploads/" + rsInnerDeep.getString("imgname"));
					}
				}

				map.put("user_id", rs.getString("user_id"));
				map.put("intex_createrid", rs.getString("intex_createrid"));
				map.put("id", rs.getString("id"));

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
					sqlInner = "select * from `interview_exp` where industryname=? or industryname=? or industryname=? or industryname=? order by id desc";

					psInner = con.prepareStatement(sqlInner);
					psInner.setString(1, rs.getString("industry1"));
					psInner.setString(2, rs.getString("industry2"));
					psInner.setString(3, rs.getString("industry3"));
					psInner.setString(4, "All Industries");

				}

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					map.put("feedback", Jsoup.parse(rsInner.getString("feedback")).text());

					map.put("industryname", rsInner.getString("industryname"));
					map.put("interview_status", rsInner.getString("interview_status"));
					map.put("industryname", rsInner.getString("industryname"));
					map.put("companyname", rsInner.getString("companyname").replaceAll(",$", ""));
					map.put("id", rsInner.getString("id"));
					map.put("user_id", rsInner.getString("user_id"));
					map.put("username", rsInner.getString("username"));
					map.put("created_by", rsInner.getString("created_by"));

					String companyName = rsInner.getString("companyname").replaceAll(",$", "");
					System.out.println(companyName);

					if (companyName.equals("")) {
						map.put("company_id", "");
					} else {
						String companyRequest = "SELECT * FROM `company` where companyname=?";

						PreparedStatement psCompany = con.prepareStatement(companyRequest);
						psCompany.setString(1, companyName);

						ResultSet rsCompany = psCompany.executeQuery();

						System.out.println(companyRequest);
						while (rsCompany.next()) {
							map.put("company_id", rsCompany.getString("id"));

						}
					}

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

					/*
					 * String sqlPc =
					 * "SELECT * FROM `questn_comm` WHERE qid=? order by desc";
					 * 
					 * PreparedStatement psPc = con.prepareStatement(sqlPc);
					 * psPc.setInt(1, rsInner.getInt("id"));
					 * 
					 * ResultSet rsPc = psPc.executeQuery();
					 * 
					 * while (rsPc.next()) {
					 * 
					 * if (rsPc.getRow() > 1) { pc = rsPc.getRow() - 2; } else
					 * if (rsPc.getRow() == 1 || rsPc.getRow() == 0) { pc = 0; }
					 * 
					 * }
					 * 
					 * String sqlquestion =
					 * "SELECT a.id id,a.qid qid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM questn_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.qid=? LIMIT ?,2"
					 * ; PreparedStatement psquestion =
					 * con.prepareStatement(sqlquestion);
					 * psquestion.setString(1, rsInner.getString("user_id"));
					 * psquestion.setInt(2, pc);
					 * 
					 * ResultSet question = psquestion.executeQuery();
					 * 
					 * while (question.next()) {
					 * 
					 * commentMap.put("interviewExperienceUser_id",
					 * question.getString("user_id"));
					 * commentMap.put("interviewExperiencecreated_uname",
					 * question.getString("created_uname"));
					 * commentMap.put("interviewExperiencecomments",
					 * question.getString("comments"));
					 * commentMap.put("interviewExperiencecreated_by",
					 * rsInner.getString("created_user"));
					 * commentMap.put("interviewExperienceuser_id",
					 * question.getString("created_by"));
					 * commentMap.put("interviewExperienceid",
					 * question.getString("id"));
					 * 
					 * if (question.getString("imgname").equals("")) {
					 * commentMap.put("commentuserimgname",
					 * "/images/avatar.png"); } else {
					 * commentMap.put("commentuserimgname", "/uploads/" +
					 * question.getString("imgname")); }
					 * 
					 * if (rsInner.getString("created_user").equals(userId) ||
					 * question.getString("user_id").equals(userId)) {
					 * commentMap.put("interviewExperiencedelete", "1"); } else
					 * { commentMap.put("interviewExperiencedelete", "0"); }
					 * 
					 * commentArray.put(commentMap);
					 * 
					 * commentArray.put(commentMap); }
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

	@POST
	@Path("/questionsComments")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String questionsComments(@FormParam("qid") String qid, @FormParam("ques_createrid") String ques_createrid,
			@FormParam("user_id") String user_id, @FormParam("comments") String comments,
			@FormParam("created_uname") String created_uname) {

		int result = 0;
		String sqlInner = null;

		try {

			int rsLastGeneratedAutoIncrementId = 0;
			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "INSERT INTO questn_comm (qid,ques_createrid,comments,created_by,user_id,created_uname)"
					+ "VALUES('" + qid + "','" + ques_createrid + "','" + comments + "','" + dateTime + "','" + user_id
					+ "','" + created_uname + "')";

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
	
	@POST
	@Path("/deleteQuestionComment/{user_id}")
	@Produces(MediaType.TEXT_HTML)
	public String deleteQuestionComment(@PathParam("user_id") String user_id) {

		try {

			

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "delete  from `questn_comm` where id=? ";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user_id);
			ps.executeUpdate();
		
			
			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return jsonArray.toString();
	}


	@GET
	@Path("/questionsCommentList/{user_id}")
	@Produces(MediaType.TEXT_HTML)
	public String questionsCommentList(@PathParam("user_id") String user_id) {

		try {

			String sqlInner;
			PreparedStatement psInner;

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from `questn_comm` where qid=? ";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user_id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("comments", rs.getString("comments").replaceAll("\\<.*?\\>", ""));
				map.put("created_by", rs.getString("created_by"));
				map.put("created_uname", rs.getString("created_uname"));

				String sqlInnerDeep = "select * from `user_login` where id=?";

				PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
				psInnerDeep.setString(1, rs.getString("user_id"));

				ResultSet rsInnerDeep = psInnerDeep.executeQuery();

				while (rsInnerDeep.next()) {

					if (rsInnerDeep.getString("imgname").equals("")) {
						map.put("commentedUserImage", "/images/avatar.png");
					} else {
						map.put("commentedUserImage", "/uploads/" + rsInnerDeep.getString("imgname"));
					}
				}

				map.put("user_id", rs.getString("user_id"));
				map.put("ques_createrid", rs.getString("ques_createrid"));
				map.put("id", rs.getString("id"));

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

					sqlInner = "select * from `questions` order by id desc";

					psInner = con.prepareStatement(sqlInner);

				} else {
					sqlInner = "select * from `questions` where industryname=? or industryname=? or industryname=? or industryname=? order by modified_by desc ";

					psInner = con.prepareStatement(sqlInner);
					psInner.setString(1, rs.getString("industry1"));
					psInner.setString(2, rs.getString("industry2"));
					psInner.setString(3, rs.getString("industry3"));
					psInner.setString(4, "All Industries");

				}

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					map.put("question", Jsoup.parse(rsInner.getString("question")).text());
					map.put("category", rsInner.getString("category"));
					map.put("industryname", rsInner.getString("industryname"));
					map.put("companyname", rsInner.getString("companyname").replaceAll(",$", ""));
					map.put("subcategory", rsInner.getString("subcategory"));
					map.put("subcategory", rsInner.getString("subcategory"));
					map.put("created_uname", rsInner.getString("created_uname"));
					map.put("created_user", rsInner.getString("created_user"));
					map.put("id", rsInner.getString("id"));
					map.put("created_by", rsInner.getString("created_by"));

					String companyName = rsInner.getString("companyname").replaceAll(",$", "");
					System.out.println(companyName);

					if (companyName.equals("")) {
						map.put("company_id", "");
					} else {
						String companyRequest = "SELECT * FROM `company` where companyname=?";

						PreparedStatement psCompany = con.prepareStatement(companyRequest);
						psCompany.setString(1, companyName);

						ResultSet rsCompany = psCompany.executeQuery();

						System.out.println(companyRequest);
						while (rsCompany.next()) {
							map.put("company_id", rsCompany.getString("id"));

						}
					}

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

					/*
					 * String sqlPc =
					 * "SELECT * FROM `interview_comm` WHERE iid=? order by desc "
					 * ;
					 * 
					 * PreparedStatement psPc = con.prepareStatement(sqlPc);
					 * psPc.setString(1, rsInner.getString("user_id"));
					 * 
					 * ResultSet rsPc = psPc.executeQuery();
					 * 
					 * while (rsPc.next()) {
					 * 
					 * if (rsPc.getRow() > 1) { pc = rsPc.getRow() - 2; } else
					 * if (rsPc.getRow() == 1 || rsPc.getRow() == 0) { pc = 0; }
					 * 
					 * }
					 * 
					 * String sqlinterviewExperiencet =
					 * "SELECT a.id id,a.iid iid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM interview_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.iid=? LIMIT ?,2"
					 * ;
					 * 
					 * PreparedStatement psinterviewExperience =
					 * con.prepareStatement(sqlinterviewExperiencet);
					 * psinterviewExperience.setString(1,
					 * rsInner.getString("user_id"));
					 * psinterviewExperience.setInt(2, pc);
					 * 
					 * ResultSet rsinterviewExperience =
					 * psinterviewExperience.executeQuery();
					 * 
					 * while (rsinterviewExperience.next()) {
					 * 
					 * commentMap.put("interviewExperienceUser_id",
					 * rsinterviewExperience.getString("user_id"));
					 * commentMap.put("interviewExperiencecreated_uname",
					 * rsinterviewExperience.getString("created_uname"));
					 * commentMap.put("interviewExperiencecomments",
					 * rsinterviewExperience.getString("comments"));
					 * commentMap.put("interviewExperiencecreated_by",
					 * rsInner.getString("created_user"));
					 * commentMap.put("interviewExperienceuser_id",
					 * rsinterviewExperience.getString("created_by"));
					 * commentMap.put("interviewExperienceid",
					 * rsinterviewExperience.getString("id"));
					 * 
					 * if
					 * (rsinterviewExperience.getString("imgname").equals("")) {
					 * commentMap.put("commentuserimgname",
					 * "/images/avatar.png"); } else {
					 * commentMap.put("commentuserimgname", "/uploads/" +
					 * rsinterviewExperience.getString("imgname")); }
					 * 
					 * if (rsInner.getString("created_user").equals(userId) ||
					 * rsinterviewExperience.getString("user_id").equals(userId)
					 * ) { commentMap.put("interviewExperiencedelete", "1"); }
					 * else { commentMap.put("interviewExperiencedelete", "0");
					 * }
					 * 
					 * commentArray.put(commentMap);
					 * 
					 * commentArray.put(commentMap); }
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

	@POST
	@Path("/eventsComments")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String eventsComments(@FormParam("eid") String eid, @FormParam("evt_createrid") String evt_createrid,
			@FormParam("user_id") String user_id, @FormParam("comments") String comments,
			@FormParam("created_uname") String created_uname) {

		int result = 0;
		String sqlInner = null;

		try {

			int rsLastGeneratedAutoIncrementId = 0;
			con = Constants.ConnectionOpen();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String dateTime = dtf.format(now);

			sqlInner = "INSERT INTO event_comm (eid,evt_createrid,comments,created_by,user_id,created_uname)"
					+ "VALUES('" + eid + "','" + evt_createrid + "','" + comments + "','" + dateTime + "','" + user_id
					+ "','" + created_uname + "')";

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

	
	@POST
	@Path("/deleteEventComment/{user_id}")
	@Produces(MediaType.TEXT_HTML)
	public String deleteEventComment(@PathParam("user_id") String user_id) {

		try {

			
			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "delete  from `event_comm` where id=? ";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user_id);
			ps.executeUpdate();
		
			
			con.close();

		} catch (

		Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return jsonArray.toString();
	}
	
	
	@GET
	@Path("/eventCommentList/{user_id}")
	@Produces(MediaType.TEXT_HTML)
	public String eventCommentList(@PathParam("user_id") String user_id) {

		try {

			String sqlInner;
			PreparedStatement psInner;

			jsonArray = new JSONArray();
			newObject = new JSONObject();

			map = new HashMap<String, String>();

			con = Constants.ConnectionOpen();

			String sql = "select * from `event_comm` where eid=? ";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user_id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				map.put("comments", rs.getString("comments").replaceAll("\\<.*?\\>", ""));
				map.put("created_by", rs.getString("created_by"));
				map.put("created_uname", rs.getString("created_uname"));

				String sqlInnerDeep = "select * from `user_login` where id=?";

				PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
				psInnerDeep.setString(1, rs.getString("user_id"));

				ResultSet rsInnerDeep = psInnerDeep.executeQuery();

				while (rsInnerDeep.next()) {

					if (rsInnerDeep.getString("imgname").equals("")) {
						map.put("commentedUserImage", "/images/avatar.png");
					} else {
						map.put("commentedUserImage", "/uploads/" + rsInnerDeep.getString("imgname"));
					}
				}

				map.put("user_id", rs.getString("user_id"));
				map.put("evt_createrid", rs.getString("evt_createrid"));
				map.put("id", rs.getString("id"));

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
	@Path("/topEvent/{userId}")
	@Produces(MediaType.TEXT_HTML)
	public String topEvent(@PathParam("userId") String userId, @QueryParam("start") int start,
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

					sqlInner = "select * from `events` order by id desc";

					psInner = con.prepareStatement(sqlInner);

				} else {
					sqlInner = "select * from `events` where Industry=? or Industry=? or Industry=? or Industry=? order by id desc ";

					psInner = con.prepareStatement(sqlInner);
					psInner.setString(1, rs.getString("industry1"));
					psInner.setString(2, rs.getString("industry2"));
					psInner.setString(3, rs.getString("industry3"));
					psInner.setString(4, "All Industries");

				}

				ResultSet rsInner = psInner.executeQuery();

				while (rsInner.next()) {

					map.put("notes", Jsoup.parse(rsInner.getString("notes")).text());
					map.put("eventname", rsInner.getString("eventname"));
					map.put("datetime", rsInner.getString("datetime"));
					map.put("eventtype", rsInner.getString("eventtype"));
					map.put("location", rsInner.getString("location"));
					map.put("id", rsInner.getString("id"));
					map.put("Industry", rsInner.getString("Industry"));
					map.put("companyname", rsInner.getString("companyname").replaceAll(",$", ""));

					map.put("created_user", rsInner.getString("created_user"));
					map.put("created_by", rsInner.getString("created_by"));
					map.put("created_uname", rsInner.getString("created_uname"));

					String companyRequest = "SELECT * FROM `company` where companyname=?";

					PreparedStatement psCompany = con.prepareStatement(companyRequest);
					psCompany.setString(1, rsInner.getString("companyname").replaceAll(",$", ""));
					ResultSet rsCompany = psCompany.executeQuery();
					while (rsCompany.next()) {
						map.put("company_id", rsCompany.getString("id"));
					}

					String sqlInnerDeep = "select * from `user_login` where id=?";

					PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
					psInnerDeep.setString(1, rsInner.getString("created_user"));

					ResultSet rsInnerDeep = psInnerDeep.executeQuery();

					while (rsInnerDeep.next()) {

						if (rsInnerDeep.getString("imgname").equals("")) {
							map.put("eventImgName", "/images/avatar.png");
						} else {
							map.put("eventImgName", "/uploads/" + rsInnerDeep.getString("imgname"));
						}
					}

					String eventDate = rsInner.getString("datetime");

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date todaysDate = new Date();

					if ((eventDate).compareTo(sdf.format(todaysDate)) < 0) {
						map.put("event", "Closed");
					} else {
						map.put("created_by", "I'm going");
					}

					String countSql = "SELECT * FROM `event_register` WHERE eventid=?";

					PreparedStatement psevent = con.prepareStatement(countSql);
					psevent.setString(1, rsInner.getString("id"));

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

					/*
					 * String sqlPc =
					 * "SELECT * FROM `interview_comm` WHERE iid=? order by desc "
					 * ;
					 * 
					 * PreparedStatement psPc = con.prepareStatement(sqlPc);
					 * psPc.setString(1, rsInner.getString("user_id"));
					 * 
					 * ResultSet rsPc = psPc.executeQuery();
					 * 
					 * while (rsPc.next()) {
					 * 
					 * if (rsPc.getRow() > 1) { pc = rsPc.getRow() - 2; } else
					 * if (rsPc.getRow() == 1 || rsPc.getRow() == 0) { pc = 0; }
					 * 
					 * }
					 * 
					 * String sqlinterviewExperiencet =
					 * "SELECT a.id id,a.iid iid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM interview_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.iid=? LIMIT ?,2"
					 * ;
					 * 
					 * PreparedStatement psinterviewExperience =
					 * con.prepareStatement(sqlinterviewExperiencet);
					 * psinterviewExperience.setString(1,
					 * rsInner.getString("user_id"));
					 * psinterviewExperience.setInt(2, pc);
					 * 
					 * ResultSet rsinterviewExperience =
					 * psinterviewExperience.executeQuery();
					 * 
					 * while (rsinterviewExperience.next()) {
					 * 
					 * commentMap.put("interviewExperienceUser_id",
					 * rsinterviewExperience.getString("user_id"));
					 * commentMap.put("interviewExperiencecreated_uname",
					 * rsinterviewExperience.getString("created_uname"));
					 * commentMap.put("interviewExperiencecomments",
					 * rsinterviewExperience.getString("comments"));
					 * commentMap.put("interviewExperiencecreated_by",
					 * rsInner.getString("created_user"));
					 * commentMap.put("interviewExperienceuser_id",
					 * rsinterviewExperience.getString("created_by"));
					 * commentMap.put("interviewExperienceid",
					 * rsinterviewExperience.getString("id"));
					 * 
					 * if
					 * (rsinterviewExperience.getString("imgname").equals("")) {
					 * commentMap.put("commentuserimgname",
					 * "/images/avatar.png"); } else {
					 * commentMap.put("commentuserimgname", "/uploads/" +
					 * rsinterviewExperience.getString("imgname")); }
					 * 
					 * if (rsInner.getString("created_user").equals(userId) ||
					 * rsinterviewExperience.getString("user_id").equals(userId)
					 * ) { commentMap.put("interviewExperiencedelete", "1"); }
					 * else { commentMap.put("interviewExperiencedelete", "0");
					 * }
					 * 
					 * commentArray.put(commentMap);
					 * 
					 * commentArray.put(commentMap); }
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
