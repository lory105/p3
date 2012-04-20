package server;

import java.rmi.*;


public class ServerRMI {
	private static final String HOST = "localhost";
	
	
	public static void main(String[] args) throws Exception{

		// instance of remote object
		ReceiverStatImp receiverStat = new ReceiverStatImp();
		
		String receiverStatName = "rmi://" + HOST + "/ReceiverStat";
		Naming.rebind( receiverStatName, receiverStat );
		
		System.out.println("Server ready");
		
	}
}
