


import java.util.*;
import java.rmi.*;
import java.net.*;
//import java.io.*;
//import manager.*;

public class Client {
	private static final String HOSTServer = "localhos";
	
	private Connector connect=null;
	private ServerInterface server=null;
	
	public Client( Connector c){
		connect=c;

	} 

	public boolean connetcToServer( String nameServer){
		try{
			server= (ServerInterface) Naming.lookup( nameServer );
			
			// da togliere !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			System.out.println("Client Pronto");
			
			return true;
		}
		catch( NotBoundException e ){ System.out.println("NoBound"); return false; }
		catch( RemoteException e ){
			System.out.println("RemoteExc"); System.out.println( e.getMessage() + "\n" + e.getCause().getMessage() );
			return false;
		}
		catch( MalformedURLException e ){ System.out.println("MalformedURL"); return false;}

	}
	
	
	public void sendStats( Vector<Stats> stats ){
		System.out.println("Invio statistiche");
		
		try{ server.printStats( connect.getParameters(), stats ); }
		catch( RemoteException e ){}
		System.out.println("Invio e stampo statistiche");
	}
	
}
