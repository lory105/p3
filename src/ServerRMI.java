

import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;





public class ServerRMI extends UnicastRemoteObject implements ServerInterface{
	private FileWriter out;
	
	public ServerRMI() throws RemoteException{ System.out.println("Server pronto"); }
	
	
	public void printStats( Vector<Stats> s ) throws RemoteException{
		
//		synchronized( out ){
			System.out.println("Server Rmi sta stampando su file");
			try{
				out=new FileWriter(new File("output.txt"),true);
				out.append("Start simulation1:\n");
				out.append("Start simulation2:\n");
				
				
				Stats.getValue(out);
				
				//for(int i=0; i<s.size(); i++){
//					s.get(i).getInfo(out);
//				}
			}
			catch(FileNotFoundException e){ System.out.println("file not found"); }
			catch(IOException ioe){	System.out.println("I/O errore"); }
			finally{
				try{ out.close(); }
				catch(Exception e){ System.out.println("errore on close()"); }
			}
		
//		}
		
	}
	

}
