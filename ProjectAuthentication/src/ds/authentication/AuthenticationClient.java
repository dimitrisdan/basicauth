/** Data Security|Authentication Lab
 *
 * Created by:  Dimitris.Danampasis
 * Date:        Oct 31, 2014 12:46:15 PM
 * Project:     ProjectAuthentication 
 * Package:     ds.authentication
 * File:        AuthenticationClient.java
 * Description: 
 */
package ds.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;



public class AuthenticationClient {
	
	private static String readFromKeyboard() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s = br.readLine();
        return s;
	}
	private static String readUsername() throws IOException{
		String username = readFromKeyboard();
		return username;
	}
	
	private static String readPassword(String username) 
			throws IOException {
		String clientSideSalt = username+"datasecurity";
		try {
			String hashedPassword = PasswordHash.createHashFromClient(readFromKeyboard(),clientSideSalt);
			return hashedPassword;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	
	public static void main(String[] args) 
			throws NotBoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		try{
			RemoteInterface rmt = (RemoteInterface) Naming.lookup("rmi://localhost:44444/print");
			Boolean exit = false;
			String password;
			String username = "";
			String option;
			
			do{
				System.out.println(rmt.welcome());
				try {
					System.out.print("Option: ");
					option = readFromKeyboard();
				} catch (IOException e) {
					e.printStackTrace();
					option = "5";
				}
				
				// Create user
				if (option.equals("1")){
					System.out.print("\nUsername: ");
					username = readUsername();
					System.out.print("\nPassword: ");
					password = readPassword(username);
					System.out.println("");
					rmt.createUser(username,password);
					System.out.println("You are now a Master...Procceed to login \n\n\n");
				
				// Login	
				}else if (option.equals("2")) {
					for(int attempt = 0; attempt < 3; attempt = attempt+1) {
						if (attempt == 3){
							System.out.println("Reached the maximum number of attempts ! Your account has now been suspended.");
							exit = true;
						}
						System.out.print("\nUsername: ");
						username = readUsername();
						System.out.print("\nPassword: ");
						password = readPassword(username);
						if (rmt.validateUser(username,password)){
							System.out.println("\nWelcome Master...");
							break;
						}
						else
							System.out.println("Wrong username or password");
				    }
					
				
				// Demonstration
				}else if (option.equals("3")) {
					if(rmt.validateSession(username))
						Demonstration.doDemo(rmt);
					else
						System.out.println("\n\nTry to log in first!\n\n");
				
				// Log-out
				}else if (option.equals("4")) {
					if (username.equals(""))
						System.out.println("\n!!First you need to login...");
					else
						rmt.logOut(username);
					System.out.println("\nYou are not a Master anymore");
					
				// Exit
				}else if (option.equals("5")) {
					System.out.println("\nMay the Force be with you...");
					exit = true;
					
				// Wrong input
				}else
					System.out.println("Wrong input");
				
			}while(!exit);
			
		}catch(RemoteException e){
			System.out.println("RemoteException error...\n");
			e.printStackTrace();
		}

	}


}
