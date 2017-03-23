package ds.authentication;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Demonstration {

	public static void prettyPrintQueue(String queue){
		List<String> myList = new ArrayList<String>();
		myList = Arrays.asList(queue.substring(1, queue.length()-1).split(","));
		System.out.println("Job\tFilename");
		for (int i = 0; i < myList.size(); i++) {
		    System.out.println(i + "\t" + myList.get(i));
		}
	}
	
	public static void doDemo(RemoteInterface rmt) throws RemoteException{
		if (rmt.start())
			System.out.println("-> Print Server started...\n");
		else
			System.out.println("!! Print Server failed to restart...");
		
		
		System.out.println(rmt.print("doc1.txt","Printer 1"));
		System.out.println(rmt.print("doc2.txt","Printer 2"));
		System.out.println(rmt.print("doc3.txt","Printer 3"));
		System.out.println(rmt.print("doc4.txt","Printer 1"));
		System.out.println(rmt.print("doc5.txt","Printer 3"));
		
		System.out.println("");
		
		System.out.println("-> Print queue...\n~Start of queue");
		prettyPrintQueue(rmt.queue());
		System.out.println("~End of queue...\n");
		
		System.out.println("-> Set an item at the top of the queue...\n~Start of the new queue");
		rmt.topQueue(2);
		prettyPrintQueue(rmt.queue());
		System.out.println("~End of queue...\n");
		
		// RESTART Print Service
		if (rmt.restart())
			System.out.println("-> Print Server restarted...\n");
		else
			System.out.println("!!Print Server failed to restart...\n");
		
		// STOP Print Service
		if (rmt.stop())
			System.out.println("-> Print Server stopped...\n");
		else
			System.out.println("!! Print Server failed to stop...\n");
	}
}
