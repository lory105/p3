


import java.util.*;
import java.rmi.*;
import java.net.*;
//import java.io.*;
//import manager.*;

public class Client {
	private static String HOST = "localhost";
	
	private Connector connect=null;
	private ReceiverStatInterface receiverStat=null;
	
	public Client( Connector c){
		connect=c;

	} 

	public boolean connetcToServer( String nameServer){
		HOST= nameServer;
		try{
			receiverStat= (ReceiverStatInterface) Naming.lookup( "rmi://" + HOST + "/ReceiverStat");
			
			boolean testResponse = receiverStat.testConnection();
			
			// da togliere !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			System.out.println("Client Pronto");
			if( testResponse )
				return true;
			else return false;
		}
		catch( NotBoundException e ){ 
			System.out.println("NoBound");
			connect.print( "NotBoundException: perhaps the server URL isn't correct..", 0 );
			return false; 
		}
		catch( RemoteException e ){
			System.out.println("RemoteExc"); 
			System.out.println( e.getMessage() + "\n" + e.getCause().getMessage() );
			connect.print( "RemoteException: some problem with server are occurred, perhaps the server URL isn't correct..", 0 );
			return false;
		}
		catch( MalformedURLException e ){ 
			System.out.println("MalformedURL");
			connect.print( "MalformedURLException: perhaps the server URL isn't correct..", 0 );
			return false;
			}

	}
	
	
	public void sendStats( Vector<Stat> stats ){
		System.out.println("Invio statistiche");
		
		try{ receiverStat.printStats( connect.getParameters(), stats ); }
		catch( RemoteException e ){}
		System.out.println("Invio e stampo statistiche");
	}
	
}
