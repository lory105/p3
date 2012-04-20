package Server;
// interface of remote object that receives and prints statistics

//import Stat;

import java.rmi.*;
import java.util.*;


public interface ReceiverStatInterface extends Remote {
	
	
	public void printStats( Object[] v,  Vector<Client.Stat> stat) throws RemoteException;
	
	public boolean testConnection() throws RemoteException;

}

