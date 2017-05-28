package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import utils.Constants;

@Path("/loginService")
public class Login {

    Connection con = null;
	
	 @POST
	    @Path("/login")
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    @Produces(MediaType.TEXT_HTML)
	    public String login(@FormParam("email") String email, @FormParam("password") String password){
	        String result="false";
	        
	        try{

	        	con = Constants.ConnectionOpen();
	        	
	            PreparedStatement ps = con.prepareStatement("SELECT * FROM user_login WHERE email=? AND password=? AND status='0'");
	            ps.setString(1, email);
	            ps.setString(2, Constants.md5(password));
	            
	            ResultSet rs = ps.executeQuery();
	            
	            if(rs.next()){
	                
	                String userid = rs.getString("id");
	                
	                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	                LocalDateTime now = LocalDateTime.now();
	                
	                String dateTime = dtf.format(now);
	                PreparedStatement psInner = con.prepareStatement("UPDATE user_login SET last_loggedin = ? where id=?");
	                psInner.setString(1, dateTime);
	                psInner.setString(2, userid);
	                
	                psInner.executeUpdate();
	                
	                result = rs.getString("id")+","+rs.getString("fname");
 
	            }
	            con.close();
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }
	        
	        return result;
	    }
	    

}
