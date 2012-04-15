

import java.rmi.*;
import java.net.*;


public class MainRMI {
	private static final String HOST = "localhost";
	
	
	public static void main(String[] args) throws Exception{

		try{
			ServerRMI server= new ServerRMI();
			String serverName = "localhost";
			Naming.rebind( serverName, server);
		}
		catch( RemoteException e){ System.out.println("RemoteExc server"); }
		catch( MalformedURLException e){ System.out.println("MalformedExc server"); }
		
		//System.out.println("Server pronto");
		
	}
	
	
	

}
