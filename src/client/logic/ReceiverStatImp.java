// implementation of remote object that receives and prints statistics
package client.logic;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;


public class ReceiverStatImp extends UnicastRemoteObject implements ReceiverStatInterface{
	private FileWriter out;
	
	public ReceiverStatImp() throws RemoteException{}
	
	// function that prints the statistics provided by the client
	public void printStats( Object[] v, Vector<Stat> s ) throws RemoteException{
		
		synchronized( out ){
			System.out.println("Server Rmi sta stampando su file");
			try{
				out=new FileWriter(new File("output.txt"),true);
				
				// da togliere !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				out.append( "Simulation:\n");
				
				for( int x=0; x<s.size(); x++){
					
					// prints the value of simulation
					for(int y=0; y<v.length; y++){
						out.append( v[y].toString() + " ");
					}
					
					// da togliere !!!!!!!!!!!!!!!
					out.append("	");
									
					out.append( (s.get(x).printValues()) );
				
					out.append("\n");
				}
				
			}
			catch(FileNotFoundException e){ System.out.println( e.getMessage() + "\nFile not found"); }
			catch(IOException e){	System.out.println( e.getMessage() + "\nI/O error"); }
			finally{
				try{ out.close(); }
				catch(Exception e){ System.out.println(e.getMessage() + "\nError on close()"); }
			}
		
		}
		
	}
	
	// function for test response
	public boolean testConnection() throws RemoteException { return true;}

}
