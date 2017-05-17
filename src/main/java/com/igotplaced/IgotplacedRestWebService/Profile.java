package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Constants;

@Path("/profile")
public class Profile {

	@GET
	@Path("/searchCompanyIndustry/{id}")
	@Produces(MediaType.TEXT_HTML)
	public String search(@PathParam("id") String id) {

		Connection con = null;

		List<String> companyList = new ArrayList<>();
		List<String> industryList = new ArrayList<>();

		JSONArray companyJSONArray = null;
		JSONArray industryJSONArray = null;

		JSONObject searchJSONObject = new JSONObject();

		try {

			con = Constants.ConnectionOpen();

			String sql = "SELECT ids,Caption FROM (SELECT 'company'Caption,companyname ids FROM company WHERE companyname LIKE  '%"
					+ id
					+ "%' UNION ALL SELECT 'industry'Caption,industry_type ids FROM industry WHERE industry_type LIKE  '%"
					+ id + "%')subquery LIMIT 5";

			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				if (rs.getString("Caption").equals("company"))
					companyList.add(rs.getString("ids"));
				else
					industryList.add(rs.getString("ids"));

			}

			String[] company = companyList.toArray(new String[companyList.size()]);

			companyJSONArray = new JSONArray(Arrays.asList(company));

			String[] industry = industryList.toArray(new String[industryList.size()]);

			industryJSONArray = new JSONArray(Arrays.asList(industry));

			searchJSONObject.put("company", companyJSONArray);
			searchJSONObject.put("industry", industryJSONArray);

			con.close();

		} catch (Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return searchJSONObject.toString(); 
	}

	@POST
	@Path("/searchButtonClick")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String searchButtonClick(@FormParam("id") String id, @FormParam("keyword") String keyword) {

		Connection con = null;

		String sqlCommand = null,sqlInnerJoin =null,sqlInnerEvenDeep=null,sqlInnerDeep = null,sqlInner=null;


		String caption = null, postId = null, post = null, pId = null;
		String companyName = null, industryName = null, image = null;
		String userId = null, userName = null, userCreatedTime = null;
		String commentUserId = null, commentUserName = null, commentUserImage = null, comment = null;
		String commentuserId = null, commentCreatedTime = null;
		int pc = 0, pc1rowCount = 0;
		boolean deleteComment = false;
		ResultSet rs=null,rsInner=null,rsInnerDeep=null,rsInnerJoin=null;
		
		try {

			con = Constants.ConnectionOpen();

			sqlCommand = "SELECT Caption, ids, modified_by FROM (SELECT 'events'Caption, id ids, modified_by FROM events where companyname LIKE '%"
					+ keyword + "%' or Industry LIKE '%" + keyword
					+ "%' UNION ALL SELECT 'post'Caption, pid ids, modified_by FROM post  where companyname LIKE '%"
					+ keyword + "%' or Industry LIKE '%" + keyword
					+ "%' UNION ALL SELECT 'questions'Caption, id ids,modified_by FROM questions where companyname LIKE '%"
					+ keyword + "%' or industryname LIKE '%" + keyword
					+ "%' UNION ALL SELECT  'intexp'Caption, id ids, modified_by FROM interview_exp WHERE companyname LIKE  '%"
					+ keyword + "%' OR industryname LIKE '%" + keyword
					+ "%')subquery ORDER BY modified_by DESC , FIELD( Caption,  'events',  'post',  'questions',  'intexp' ) LIMIT 10";

			PreparedStatement ps = con.prepareStatement(sqlCommand);

			rs = ps.executeQuery();

			while (rs.next()) {

				caption = rs.getString("Caption");

				if (caption.equals("post")) {

					postId = rs.getString("ids");

					sqlInner = "select * from post where pid=?";

					PreparedStatement psInner = con.prepareStatement(sqlInner);
					psInner.setString(1, postId);

					rsInner = psInner.executeQuery();

					while (rsInner.next()) {

						post = rsInner.getString("post");
						pId = rsInner.getString("pid");

						sqlInnerDeep = "select * from post where pid=?";

						PreparedStatement psInnerDeep = con.prepareStatement(sqlInnerDeep);
						psInnerDeep.setString(1, postId);

						rsInnerDeep = psInnerDeep.executeQuery();

						while (rsInnerDeep.next()) {

							companyName = rsInnerDeep.getString("companyname");

							industryName = rsInnerDeep.getString("Industry");

							if (!rsInner.getString("imgname").equals("")) {
								image = rsInner.getString("imgname");
							} else {
								image = "images/avatar.png";
							}

							userId = rsInner.getString("created_user");

							userName = rsInner.getString("created_uname");

							userCreatedTime = rsInner.getString("created_by");

							sqlInnerEvenDeep = "SELECT * FROM `post_comm` WHERE pid=? order by desc";

							PreparedStatement psInnerEvenDeep = con.prepareStatement(sqlInnerEvenDeep);
							psInnerEvenDeep.setString(1, postId);

							pc1rowCount = psInnerDeep.executeUpdate();

							if (pc1rowCount > 1) {
								pc = pc1rowCount - 2;
							} else if (pc1rowCount == 1 || pc1rowCount == 0) {
								pc = 0;
							}

							sqlInnerJoin = "SELECT a.id id,a.pid pid,a.comments comments,a.user_id user_id,a.created_by created_by,a.created_uname created_uname,b.imgname imgname FROM post_comm as a INNER JOIN user_login as b ON a.user_id=b.id AND a.pid=? LIMIT ?,2";

							PreparedStatement psInnerJoin = con.prepareStatement(sqlInnerJoin);
							psInnerJoin.setString(1, postId);
							psInnerJoin.setInt(2, pc);

							rsInnerJoin = psInnerJoin.executeQuery();

							while (rsInnerJoin.next()) {
								commentUserId = rsInnerJoin.getString("user_id");
								commentUserName = rsInnerJoin.getString("created_uname");

								if (!rsInnerJoin.getString("imgname").equals("")) {
									commentUserImage = rsInnerJoin.getString("imgname");
								} else {
									commentUserImage = "images/avatar.png";
								}

								comment = rsInnerJoin.getString("comments");
								commentCreatedTime = rsInnerJoin.getString("created_by");
								commentuserId = rsInnerJoin.getString("user_id");

								if (id.equals(userId) || id.equals(commentuserId)) {
									deleteComment = true;
								}
							}

						}

					}

				} else if (caption.equals("events")) {

				} else if (caption.equals("questions")) {

				} else if (caption.equals("intexp")) {

				}
			}

			con.close();

		} catch (Exception e) {

			System.out.println(e);
			e.printStackTrace();
		}

		return rsInnerDeep.toString();
	}

}
