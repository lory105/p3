

import java.rmi.*;
import java.util.*;


public interface ServerInterface extends Remote {
	
	
	public void printStats( Object[] v,  Vector<Stats> s) throws RemoteException;
	
	public boolean testConnection() throws RemoteException;

}

