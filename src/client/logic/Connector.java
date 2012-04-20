// class that allows communication between gui and logic
package client.logic;

import java.util.*;

import client.Client;
import client.gui.Gui;


public class Connector {

	private Hypervisor hyper=null;
	private Gui mf=null;
	private ReadURL reader=null;
	private ElaboratorData elaborator=null;
	private Client client=null;
	
	// function to read file cnfiguration fron URL   leggo il file dalla URL url e se la lettura va a buon fine, setto i parametri letti nell'hypervisor
	public boolean readFile( String url ){
		Object[] values= reader.read( url );
		
		// if the read is successful, send the parameters to hypervisor, else return false
		if ( values==null ) return false;
		hyper.setParamiters( values );
		return true;		
	}
	
	// the file was read correctly from the URL and the server connection is established, then the simulation can start
	public void start(){
		elaborator.start();
		hyper.start();
	}
	
	
	public void setParameters( Hypervisor h, Gui m, ReadURL r, ElaboratorData el, Client c ){
		hyper=h;
		mf=m;
		reader=r;
		elaborator=el;
		client=c;
	}
	
	
	// function to print some text in the Gui's text areas
	public void print( String s, int position){
		mf.printDisplay(s, position);
	}
	
	
	// at the end of each simulation, hypervisor sends the vector of the nodes to ElaboratorData which reads the data 
	public void pullData( Data d ){
		elaborator.push( d );
	}
	
	// at the end of all simulation, statistics are sent to server  
	public void pushStats( Vector<Stat> stats ){
		client.sendStats(stats);		
	}
	
	
	public void closeAll(){
		hyper.interrupt();
		elaborator.interrupt();
		
	}
	
	public final Object[] getParameters(){ return hyper.getParameters(); }
	
	public boolean connectToServer( String nameServer){ return client.connetcToServer(nameServer); }
	
}
