/** Data Security|Authentication Lab
 *
 * Created by:  Dimitris.Danampasis
 * Date:        Oct 31, 2014 12:36:15 PM
 * Project:     ProjectAuthentication 
 * Package:     ds.authentication
 * File:        RemoteInterface.java
 * Description: 
 */
package ds.authentication;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface RemoteInterface extends Remote{
	
	public String welcome() throws RemoteException;
	
	public Boolean createUser(String username, String password)throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException;
	
	public Boolean validateUser(String username, String password) throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException;
	
	public Boolean validateSession(String username)throws RemoteException;
	
	public Boolean logOut(String username)throws RemoteException;
	
	public String print(String filename, String printer) throws RemoteException; // prints file <filename> on the specified printer <printer>
	
	public String queue() throws RemoteException; // lists the print queue on the user's display in lines of the form <job number> <file name>
	
	public Boolean topQueue(int job) throws RemoteException; // moves job to the top of the queue
	
	public Boolean start() throws RemoteException; // starts the print server
	
	public Boolean stop() throws RemoteException; // stops the print server
	
	public Boolean restart() throws RemoteException; // stops the print server, clears the print queue and starts the print server again
	
	public String status() throws RemoteException; // prints status of printer on the user's display
	
	public String readConfig(String parameter) throws RemoteException; // prints the value of the parameter on the user's display
	
	public Boolean setConfig(String parameter, String value) throws RemoteException; // sets the parameter to value
	
}
