/** Data Security|Authentication Lab
 *
 * Created by:  Dimitris.Danampasis
 * Date:        Nov 4, 2014 10:42:56 AM
 * Project:     ProjectAuthentication 
 * Package:     ds.authentication
 * File:        DbConnect.java
 * Description: This class is used for the connection with MySQL
 */
package ds.authentication;
import java.sql.*;

/**
 * @author Dimitrios Danampasis
 *
 */
public class DbConnect {

	 // JDBC driver name and database URL
	 static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	 static final String DB_URL = "jdbc:mysql://localhost/authentication_lab";
	
	 //  Database credentials
	 static final String USER = "root";
	 static final String PASS = "";
	 
	 public Connection connectToMysql(){
		 Connection conn = null;
		 try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			return conn;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return conn;
		}catch (SQLException e) {
			e.printStackTrace();
			return conn;
		}
	 }	 
}