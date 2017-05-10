package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Constants {

	final static String url = "jdbc:mysql://localhost:3306/wslogin";
    final static String user = "root";
    final static String pass = "";
    static Connection con = null;
 
    public static Connection ConnectionOpen(){
    	try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pass);     
        }
        catch(Exception e){
           e.printStackTrace();
        }
		return con;   
    }
    
    public static void ConnectionClose(Connection con){
    	try{
            con.close();
        }
        catch(Exception e){
            e.printStackTrace();
        } 
    }
}
