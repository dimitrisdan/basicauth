/** Data Security|Authentication Lab
 *
 * Created by:  Dimitris.Danampasis
 * Date:        Oct 31, 2014 12:58:22 PM
 * Project:     ProjectAuthentication 
 * Package:     ds.authentication
 * File:        AuthenticationService.java
 * Description: 
 */

package ds.authentication;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;




public class AuthenticationService extends UnicastRemoteObject implements RemoteInterface {
	
	private static final long serialVersionUID = 1L;
	private List<String> printerQueue = new ArrayList<String>();
	
	
	public AuthenticationService() throws RemoteException {
		super();
	}
	@Override
	public String welcome() throws RemoteException {
		String welcomeMsg = "*********************************************************\n"
						  + "** Welcome to Authentication Server!\n"
						  + "** 1.Create User\n** 2.Log-in\n** 3.Demonstration\n** 4.Log-out\n** 5.Exit\n"
						  + "*********************************************************\n"
						  + "For demonstration username and password is : admin";
		return welcomeMsg;  
	}
	
	public int findLastId(Connection conn) throws SQLException{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT id FROM passwords ORDER BY ID");
		int key;
		if (rs.last())
			key = rs.getInt("id");
		else
			key = 1;
		return key;
	}
	
	/**
	 * Get a diff between two dates
	 * @param date2 the oldest date
	 * @param date1 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date1.getTime() - date2.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
    /**
     * Creates a new user
     * First hashes the password and then stores the credentials in the database
     * @param   username     
     * @param   password     
     * @return               true if the credentials are stored correct, false if not
     */
	@Override
	public Boolean createUser(String username, String password)
			throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException {
		System.out.println("REQUEST: Create a user:"+username);
		DbConnect db = new DbConnect();
		Connection conn = null;
		Statement stmt = null;

		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		conn = db.connectToMysql();
		String hash = PasswordHash.createHash(password);
		String[] params = hash.split(":");
		String hashedPassword = params[2];
		String salt = params[1];
		try {
			stmt = conn.createStatement();
			String sql;
			int id = findLastId(conn)+1;
		    sql = "INSERT INTO passwords VALUES("+ id +",'"+ username +"','"+hashedPassword+"','"+salt+"','"+sdf.format(dt)+"', false)";
		    stmt.executeUpdate(sql);
		    conn.close();
		    System.out.println("SUCCESS\n");
		    return true;
		} catch (SQLException e) {
			System.out.println("!!SQL exception while creating a user...");
			return false;
		}
	}
	
	/**
     * Validates a user by comparing the input credentials with the correct from the database
     * 
     * @param   username     
     * @param   passwordToValidate     
     * @return               		true if the credentials are matched, false if not
     */
	@Override
	public Boolean validateUser(String username, String passwordToValidate) 
			throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException {
		System.out.println("\nREQUEST: Validate user(try to log in):"+username);
		DbConnect db = new DbConnect();
		Connection conn = db.connectToMysql();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql;
		    sql = "SELECT * FROM passwords WHERE username = '"+username+"'";
		    //System.out.println(sql);
		    rs = stmt.executeQuery(sql);
		    rs.first();
		    int id = rs.getInt("id"); 
		    String correctHashedPassword = rs.getString("password");
		    String salt = rs.getString("salt");
		    //System.out.println(correctHashedPassword +" : "+ salt + "\n");
		    if (PasswordHash.validatePasswordFromServer(correctHashedPassword,passwordToValidate,salt)){
		    	java.util.Date dt = new java.util.Date();
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    	sql = "UPDATE passwords SET lastlogin = '" + sdf.format(dt) + "', session = true WHERE id = " + id;
		    	System.out.println("SQL STATEMENT: "+sql);
		    	stmt.executeUpdate(sql);
		    	conn.close();
		    	System.out.println("SUCCESS\n");
		    	return true;
		    }else
		    	conn.close();
		    	return false;
		} catch (SQLException e) {
			System.out.println("!!SQL exception while validating a user...");
			return false;
		}
		
	}
	
	/**
     * Validates a user session. Each session of a user has duration of 30 minutes
     * If the time passes, session is expired
     * @param   username         
     * @return         		true if the user has a valid session, false if not
     */
	@Override
	public Boolean validateSession(String username)
			throws RemoteException {
		System.out.println("REQUEST: Validate session for user:"+username);
		//if (username.equals(null)) return false;
		DbConnect db = new DbConnect();
		Connection conn = db.connectToMysql();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql;
		    sql = "SELECT lastlogin,session FROM passwords WHERE username = '"+username+"'";
		    System.out.println("SQL STATEMENT: "+sql);
		    ResultSet rs = stmt.executeQuery(sql);
		    rs.next();
		    Boolean session = rs.getBoolean("session");
		    		    
		    java.util.Date lastLogin = null;
			java.text.SimpleDateFormat lastLoginFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			lastLogin = rs.getDate("lastlogin");
			lastLoginFormat.format(lastLogin);
			
			java.util.Date currentDate = new java.util.Date();;
			java.text.SimpleDateFormat currentDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			currentDateFormat.format(currentDate);
			
			long milliseconds = getDateDiff(currentDate,lastLogin,TimeUnit.MINUTES);
			conn.close();
			//System.out.println("MILLISECONDS: "+milliseconds);
			//System.out.println("SESSION: "+session);
			if ( (milliseconds<30000) && (session) ){
				System.out.println("SESSION VALID FOR USER:"+username);
				return true;
			}else
				return false;		    
		} catch (SQLException e) {
			System.out.println("!! SQL exception while validating a session...");
			return false;
		}
	}
	
	/**
     * Set session to False for a user to logout
     * 
     * @param   username         
     * @return         		true if the user logged out, false if not
     */
	public Boolean logOut(String username){
		System.out.println("REQUEST: Logout for user:"+username);
		//if (username.equals(null)) return false;
		DbConnect db = new DbConnect();
		Connection conn = db.connectToMysql();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql;
			java.util.Date currentDate = new java.util.Date();;
			java.text.SimpleDateFormat currentDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			currentDateFormat.format(currentDate);
			sql = "UPDATE passwords SET session = false, date = '" + currentDate + "' WHERE username = '" + username + "'";
		    System.out.println("SQL STATEMENT: "+sql);
		    stmt.executeUpdate(sql);
		    conn.close();
		    System.out.println("User "+username+" disconnected");
		} catch (SQLException e) {
			System.out.println("!! SQL exception while logging out...");
			return false;
		}
		return true;
	}
	
	
	@Override
	public String print(String filename, String printer) throws RemoteException {
		System.out.println("\nREQUEST: print ");
		String output = "\tUser is printing file <" + filename + "> in printer <" + printer + ">...";
		StringBuilder jobToList = new StringBuilder();
		jobToList.append(filename);
		 
		System.out.print(output);
		printerQueue.add(jobToList.toString());
		System.out.print("SUCCESS\n");
		return "-> Print file "+filename+"...";
	}

	@Override
	public String queue() throws RemoteException {
		return printerQueue.toString();
	}

	@Override
	public Boolean topQueue(int job) throws RemoteException {
		String serverMessage = "REQUEST topQueue()...";
		try{
			String item = printerQueue.get(job);
			printerQueue.remove(job);
			printerQueue.add(0,item);
			System.out.println(serverMessage.concat("DONE"));
		    return true;
		}catch(Exception e){
			System.out.println(serverMessage.concat("FAILED"));
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Boolean start() throws RemoteException {
		String serverMessage = "REQUEST start()...";
		try{
			//printerQueue = new String[20];
			System.out.println(serverMessage.concat("DONE"));
			return true;
		}catch(Exception e){
			System.out.println(serverMessage.concat("FAILED"));
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Boolean stop() throws RemoteException {
		String serverMessage = "REQUEST stop() FROM...";
		try{
			printerQueue = null;
			System.out.println(serverMessage.concat("DONE"));
			return true;
		}catch(Exception e){
			System.out.println(serverMessage.concat("FAILED"));
			e.printStackTrace();
			return false;
		}
		
	}

	@Override
	public Boolean restart() throws RemoteException {
		String serverMessage = "REQUEST restart() FROM...";
		try{
			printerQueue = null;
			printerQueue = new ArrayList<String>();
			System.out.println(serverMessage.concat("DONE"));
			return true;
		}catch(Exception e){
			System.out.println(serverMessage.concat("FAILED"));
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String status() throws RemoteException {
		String clientMessage;
		try{
			if ( printerQueue == null) 
				clientMessage = "Printer queue is null...Please start the service";
			else if ( ( printerQueue != null ) && (printerQueue.size() < 30)) 
				clientMessage = "Printer is available...";
			else 
				clientMessage = "Printer is offline";
			return clientMessage;
		}catch(Exception e){
			clientMessage = "Disconnected...Try again";
			e.printStackTrace();
			return clientMessage;
		}
	}

	@Override
	public String readConfig(String parameter) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setConfig(String parameter, String value) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}

