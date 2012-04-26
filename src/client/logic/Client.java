// implementation of client
package client.logic;

import java.util.*;
import java.rmi.*;
import java.net.*;

import client.gui.Gui;
import server.ReceiverStatInterface;


class Client {
	private static String HOST = "localhost";
	
	private Connector connect=null;
	public ReceiverStatInterface receiverStat;
	
	public Client( Connector c){
		connect=c;
	} 

	// function for connection test
	public boolean connetcToServer( String nameServer){
		HOST= nameServer;
		try{
			receiverStat= (ReceiverStatInterface) Naming.lookup( "rmi://" + HOST + "/ReceiverStat");
			
			boolean testResponse = false;
			testResponse= receiverStat.testConnection();
			
			// if testResponse is true, connection with server is successfully established
			if( testResponse ){
				System.out.println("Client ready");
				return true;
			}
			else return false;
		}
		catch( NotBoundException e ){ 
			System.out.println("NoBound");
			connect.print( "NotBoundException: perhaps the server URL isn't correct..\n" + e.getMessage(), 0 );
			return false; 
		}
		catch( RemoteException e ){
			System.out.println("RemoteExc"); 
			connect.print( "RemoteException: some problem with server are occurred,\nperhaps the server URL isn't correct..\n" + e.getMessage(), 0 );
			return false;
		}
		catch( MalformedURLException e ){ 
			System.out.println("MalformedURL");
			connect.print( "MalformedURLException: perhaps the server URL isn't correct..\n" + e.getMessage(), 0 );
			return false;
			}

	}
	
	// function to send statistics to server
	public void sendStats( Vector<Stat> stat ){
		connect.print("Ready to send statistics to server..", 0);
		
		try{ receiverStat.printStats( connect.getParameters(), stat); }
		catch( RemoteException e ){ 
			connect.print( "RemoteException: some problem with server are occurred,\nperhaps the server URL isn't correct..\n" + e.getMessage(), 0 );
		}
		connect.print("Statistics sent correctly to the server\nEND", 0);
	}
	
	
	public static void main(String[] args){

		Connector connect= new Connector();
		Client client = new Client( connect );
		ReadURL reader = ReadURL.getInstance(connect);
		Hypervisor hyper = Hypervisor.getInstance( connect );
		ElaboratorData elaborator = ElaboratorData.getInstance(connect);
		Gui mf= new Gui(connect, "Simulation" );
		
		connect.setParameters( hyper, mf, reader, elaborator, client );
	}
	
}
