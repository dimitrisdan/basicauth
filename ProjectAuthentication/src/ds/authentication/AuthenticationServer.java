/** Data Security|Authentication Lab
 *
 * Created by:  Dimitris.Danampasis
 * Date:        Oct 31, 2014 1:00:47 PM
 * Project:     ProjectAuthentication 
 * Package:     ds.authentication
 * File:        AuthenticationServer.java
 * Description: 
 */
package ds.authentication;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class AuthenticationServer {

	public static void main(String[] args) throws RemoteException {
		Registry registry = null;
		try{
			registry = LocateRegistry.createRegistry(44444); //use any no. less than 55000
		}catch(RemoteException e){
			System.out.println("Error creating Registry...");
	    }
		System.out.println("** Authentication Server \n** All the Jedi members are valid to use the print server\n** Waiting for requests");
		
		registry.rebind("print", new AuthenticationService());
	}

}
