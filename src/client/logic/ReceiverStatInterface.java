// common interface of remote object that receives and prints statistics
package client.logic;

import java.rmi.*;
import java.util.*;


public interface ReceiverStatInterface extends Remote {
	
	// function for connection test
	public boolean testConnection() throws RemoteException;
	
	public void printStats( Object[] v,  Vector<Stat> s) throws RemoteException;
	
}

