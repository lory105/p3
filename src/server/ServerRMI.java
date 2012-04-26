// Class represents server
package server;

import java.rmi.*;


class ServerRMI {
	private static final String HOST = "localhost";
	
	// main function server
	public static void main(String[] args) throws Exception{

		// instance of remote object
		ReceiverStatImp receiverStat = new ReceiverStatImp();
		
		String receiverStatName = "rmi://" + HOST + "/ReceiverStat";
		Naming.rebind( receiverStatName, receiverStat );
		
		System.out.println("Server ready");
		
	}
}
