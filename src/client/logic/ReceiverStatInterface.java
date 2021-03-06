// common interface of remote object that receives and prints statistics
package client.logic;

import java.rmi.*;
import java.util.*;

import common.Stat;


public interface ReceiverStatInterface extends Remote {
	
	// function for connection test
	public boolean testConnection() throws RemoteException;
	
	// function to print statistics to a file
	public void printStats( Object[] v,  Vector<Stat> s) throws RemoteException;
	
}

