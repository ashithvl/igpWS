package com.igotplaced.IgotplacedRestWebService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mysql.cj.api.jdbc.Statement;

import utils.Constants;

@Path("/ForgetPasswordService")
public class ForgetPassword {
	Connection con = null;

	@POST
	@Path("/forgotPassword")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String register(@FormParam("email") String email) {

		int result = 0;

		try {
			String userid = null;
			con = Constants.ConnectionOpen();

			String sql = "SELECT * FROM `user_login` WHERE email=?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, email);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {

				result = 1;
				userid = rs.getString("id");

			}
			if (userid != null) {
				String clickHere = "http://tritonitsolutions.in/demo/igot/changepwd.php?userid=" + userid;

				// Recipient's email ID needs to be mentioned.
				String to = email;

				// Sender's email ID needs to be mentioned
				final String from = "shrirambaabu0902@gmail.com";
				final String password = "shreyaji12";
				// Assuming you are sending email from localhost
				String host = "smtp.gmail.com";

				// Get system properties
				Properties properties = System.getProperties();
				properties.put("mail.smtp.host", host); // SMTP Host
				properties.put("mail.smtp.port", "587");
				properties.put("mail.smtp.auth", "true"); // enable
															// authentication
				properties.put("mail.smtp.starttls.enable", "true"); // enable
																		// STARTTLS

				// Setup mail server
				// properties.setProperty("mail.smtp.host", host);

				// Get the default Session object.
				Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(from, password);
					}
				});

				try {
					// Create a default MimeMessage object.
					MimeMessage message = new MimeMessage(session);

					// Set From: header field of the header.
					message.setFrom(new InternetAddress(from));

					message.setReplyTo(InternetAddress.parse("shrirambaabu0902@gmail.com"));

					// Set To: header field of the header.
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

					// Set Subject: header field
					message.setSubject("I got Password Link.");

					// Send the actual HTML message, as big as you like
					message.setContent(
							"<html><body><div style='width:550px; padding:15px; font-weight:bold;'>Hey Sir</div><br/><br/><div style='font-family: Arial;'>Check your e-mail. You will receive an email from us with instructions for resetting your password. If you dont receive this email, please check your junk mail folder<br/>'Change Your Password URL Link : <a href='"
									+ clickHere
									+ "'>Click Here</a><br/><br/></div>Wishing you the best for your placement preparation,<br/><bold>The iGotPlaced Team</bold></body></html>",
							"text/html");

					Transport transport = session.getTransport("smtp");
					transport.connect(host, from, password);
					transport.send(message);
					transport.close();
					// Send message
					// Transport.send(message);

					System.out.println("Sent message successfully....");
				} catch (MessagingException mex) {
					mex.printStackTrace();
				}

				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.valueOf(result);
	}
}
