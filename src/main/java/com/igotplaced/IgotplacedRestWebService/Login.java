package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
	                result = "true";
	            } 
	              
	            con.close();
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }
	        
	        return result;
	    }
	    

}
