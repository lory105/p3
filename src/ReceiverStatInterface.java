// interface of remote object that receives and prints statistics

import java.rmi.*;
import java.util.*;


public interface ReceiverStatInterface extends Remote {
	
	
	public void printStats( Object[] v,  Vector<Stat> s) throws RemoteException;
	
	public boolean testConnection() throws RemoteException;

}

