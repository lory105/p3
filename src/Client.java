


import java.util.*;
import java.rmi.*;
import java.net.*;
//import java.io.*;
//import manager.*;

public class Client {
	private static final String HOSTServer = "localhost";
	
	private Connector connect=null;
	private ServerInterface server=null;
	
	public Client( Connector c){
		connect=c;
		try{  server= (ServerInterface) Naming.lookup("rmi://localhost/server");  }
		catch( NotBoundException e ){ System.out.println("NoBound");}
		catch( RemoteException e ){
			System.out.println("RemoteExc"); System.out.println( e.getMessage() + "\n" + e.getCause().getMessage() );
		}
		catch( MalformedURLException e ){ System.out.println("MalformedURL");}
		System.out.println("Client Pronto");
	} 

	
	
	public void sendStats( Vector<Stats> stats ){
		System.out.println("Invio statistiche");
		try{ server.printStats( stats ); }
		catch( RemoteException e ){}
	}
	
}
