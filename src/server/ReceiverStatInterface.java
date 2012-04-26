// common interface of remote object that receives and prints statistics
package server;

import java.rmi.*;
import java.util.*;


public interface ReceiverStatInterface extends Remote {
	
	// function for connection test
	public boolean testConnection() throws RemoteException;
	
	// function to print statistics to a file
	public void printStats( Object[] v,  Vector<client.logic.Stat> stat) throws RemoteException;
	
}