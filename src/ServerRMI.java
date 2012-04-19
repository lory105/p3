

import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;





public class ServerRMI extends UnicastRemoteObject implements ServerInterface{
	private FileWriter out;
	
	public ServerRMI() throws RemoteException{ System.out.println("Server pronto"); }
	
	
	public void printStats( Object[] v, Vector<Stats> s ) throws RemoteException{
		
//		synchronized( out ){
			System.out.println("Server Rmi sta stampando su file");
			try{
				out=new FileWriter(new File("output.txt"),true);
				
				// da togliere !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				out.append( "Simulation:\n");
				
				for( int x=0; x<s.size(); x++){
					
					// stampo i 10 valori riguardanti la simulazione
					for(int y=0; y<v.length; y++){
						out.append( v[y].toString() + " ");
					}
					
					// da togliere !!!!!!!!!!!!!!!
					out.append("	");
					
					s.get(x).printValues(out);
					
					out.append("\n");
				}
				

				
				
				
				
			}
			catch(FileNotFoundException e){ System.out.println("file not found"); }
			catch(IOException ioe){	System.out.println("I/O errore"); }
			finally{
				try{ out.close(); }
				catch(Exception e){ System.out.println("errore on close()"); }
			}
		
//		}
		
	}
	
	public boolean testConnection() throws RemoteException { return true;}

}
