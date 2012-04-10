//classe che serve per far comunicare logic con gui




//import logic.*;
//import gui.*;


import java.util.*;

public class Connector {

	private Hypervisor hyper=null;
	private MyFrame mf=null;
	private ReadURL reader=null;
	private ElaboratorData elaborator=null;
	private Client client=null;
	
	// leggo il file dalla URL url e se la lettura va a buon fine, setto i parametri letti nell'hypervisor
	public boolean readFile( String url ){
		Object[] values= reader.read( url );
		
		if ( values==null ) return false;
		
		Stats.setValues(values);
		hyper.setParamiters( values );
		
		elaborator.start();
		hyper.start();
		return true;
		
	}
	
	public void setParameters( Hypervisor h, MyFrame m, ReadURL r, ElaboratorData el, Client c ){
		hyper=h;
		mf=m;
		reader=r;
		elaborator=el;
		client=c;
	}
	
	// stampa s nella JTextArea in MyFrame
	public void print( String s){
		//System.out.println(s);
		mf.printDisplay(s);
	}
	
	// alla fine di ogni singola simulazione passo il vettore di nodi all' EladoratorData che ne legge i dati 
	public void pushData( Data d ){
		elaborator.push( d );
		
	}
	
	public void pushStats( Vector<Stats> stats ){
		client.sendStats(stats);		
	}
	
	public void closeAll(){
		hyper.interrupt();
		elaborator.interrupt();
		
	}
}