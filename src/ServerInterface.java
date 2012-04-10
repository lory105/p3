

import java.rmi.*;
import java.util.*;


public interface ServerInterface extends Remote {
	
	
	public void printStats( Vector<Stats> s) throws RemoteException;

}

