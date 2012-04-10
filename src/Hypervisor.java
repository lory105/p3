
import java.util.*; 

//import manager.*;


public class Hypervisor extends Thread {
	Connector connect;
	
	private String proto;
	private int nSim=0;
	
	private int numberNode=0;              // = n clone escluso!
	private float radius;  			       // = r
	private float probAcceptLocation;      // = p
	private int numLocationDestination;    // = g value
	   
	private int energyTot;
	private int energyToSend;
	private int energyToReceive;
	private int energyToSignature;
	
	
	Vector<Node> listNode = null;				 // vettore contenente gli n + 1 nodi, clone incluso!
	Integer nodeActive = (numberNode+1);         // if ==0 => tutti i nodi hanno terminato => endSimulation = true
	

	private Object lockEndSimulation= new Object();
	private boolean endSimulation=false;			// if true => una nSimCont è terminata
	private boolean findClone=false;				// if true => una nSinCont ha trovato un clone
	
	
	
	public Hypervisor( Connector c){ connect=c; } // devo togliere il public!!!!!!!!!!!!!!!!!!!!
	
	public void run(){
		try{  // forse da spostare dentro il while, così finisce tutte le simulazioni!!!!
			
		Node.setParamiters( this, probAcceptLocation, numLocationDestination, energyTot, energyToSend, energyToReceive, energyToSignature );
		int nSimCont=1;
		
		connect.print(proto);

		while( nSimCont <= nSim ){
			if( isInterrupted() ) throw new SecurityException();
			connect.print( "Inizio simulazione n." + nSimCont );
			
			nodeActive=(numberNode+1);
			findClone=false;
			endSimulation=false;
			Node.setNewSim( 0 , false);
			listNode= new Vector<Node>();
			
			generateNode( numberNode+1 );
			findNeighbors();
		
			connect.print( "nodeActive: " + nodeActive);
			
			if( isInterrupted() ){ throw new SecurityException(); }
			
			// creo e attivo il thread ActivateNode per attivare tutti i nodi
			ActivateNode an= new ActivateNode( listNode );
			an.start();
			
	
			synchronized (lockEndSimulation) {
				while( !endSimulation ){// attendo che la simulazione termini
					lockEndSimulation.wait();
				}
			}

			connect.print( ( new Integer( listNode.size() ) ).toString() );
			connect.print("invio dati all'elaboratore");
			// invoco il lettore dei valori dei nodi!!
			//connect.pushData( new Data( listNode, Node.getDetection() ) );
				
			
			connect.print( "endSimulation=" + endSimulation );
			connect.print( "findClone=" + findClone );
			connect.print( "nodeActive=" + nodeActive );
			connect.print( "Terminara simulazione n." + nSimCont + "\n" );
			nSimCont++;
		}
		
		// passo una simulazione vuota che fungerà da flag per indicare che le simulazioni sono terminate
		connect.print("invio dati sim vuota come flag all'elaboratore");
		//connect.pushData( new Data(null, -1) );
		
		}
   	 	// se il thread viene interrotto durante una wait o sleep ( quindi una simulazione è in atto )
		// quindi i nodi sono ancora attivi e la nSimCont è ancora in atto
		catch( InterruptedException e){
			connect.print( getName() + " terminated with InterruptedExc");
			if( listNode !=null ){  // se arrivo qui è sempre true!
				Node.endSimulation=true;
				for( int x=0; x<listNode.size(); x++ )
					listNode.get(x).interrupt();

				listNode.clear();
			}
		}
		
   	 	// se la simulazione termina durante uno stato attivo del Hypervisor in cui faccio il controllo: if( isInterrupted() )
		// il controllo viene fatto solo in situazioni in cui i nodi non sono ancora attivati!!
		catch( SecurityException e){
			connect.print( getName() + " terminated with SecurityExc");
			if( listNode !=null )
				listNode.clear();
		}

	}
	
	
	private void generateNode(int nodiTot){
		Position p=null;
		boolean alreadyExistingPosition= false;
		// creo tutti i nodi clone incluso ( nodiTot )
		for(int nodeCreated=0; nodeCreated<nodiTot ; nodeCreated++){
			
			alreadyExistingPosition= false;  
			// creo la posizione del nuovo nodo da creare: Math.random() ritorna un double compreso tra 0 e 1
			p= new Position( (float)Math.random(), (float)Math.random() );
			for(int k=0; k<nodeCreated && ! alreadyExistingPosition; k++){
				// verifico tra i nodi già creati se la posizione è già esistente
				// se la posizione esiste già decremento il contatore nodeCreated ed esco dal for interno
				if( listNode.get(k).getPosition().equals(p)){ 
					alreadyExistingPosition=true; nodeCreated--; break; 
				} 
			}

			
			// se ho creato tutti i nodi effettivi, creo il nodo clone se la sua posizione scelta non è già in uso
			// quindi scelgo un nodo da clonare ed estraggo il suo id
			if(nodeCreated == nodiTot-1 && ! alreadyExistingPosition ){
				int cloneRandomId = ( listNode.get( (int) ( Math.random()* (listNode.size()-1) ) ) ).getIdNode();
				connect.print( "Nodi creati" + listNode.size() + " Id nodo clonato: " + cloneRandomId);
				
				if( proto.equals("LSM") ){
					connect.print( "creato nodo LSM");
					listNode.add( new NodeLSM( cloneRandomId, p, energyTot ) );
				}
				else{
					connect.print( "creato nodo RED");
					listNode.add( new NodeRED( cloneRandomId, p, energyTot ) );
				}
									
				break;
			}
			
			// creo il nodo con id il numero di nodi fino ad ora creati, e con la posizione p creata
			if( ! alreadyExistingPosition )
				if( proto.equals("LSM") ){
					//connect.print( "creato nodo con id " + nodeCreated );
					listNode.add( new NodeLSM( nodeCreated, p, energyTot ) );
				}
				else{
					//connect.print( "creato nodo con id " + nodeCreated );
					listNode.add( new NodeRED( nodeCreated, p, energyTot ) );
				}
		}
	}
	
	
	public void findNeighbors(){
		Node n=null, nNeighbor;
		Position p=null, pNeighbor=null;
		Vector<Node> neighbors = null;
		
		for(int i=0; i< listNode.size(); i++ ){
			// salvo in n il nodo al quale devo calcolare i suoi neighbors
			n = listNode.get(i);
			neighbors= n.getNeighbors();
			p = n.getPosition();
			
			// calcolo i vicini di n, scorrendo il vettore dei nodi e calcolando le loro distanze
			for(int k=0; k< listNode.size(); k++ ){
				nNeighbor = listNode.get(k);
				
				// controllo che il nodo n al quale sto calcolando i suoi vicini sia diverso da sè stesso in nNeighbor 
				if( n != nNeighbor ){
					pNeighbor = nNeighbor.getPosition();
				
					// se il raggio prefissato è >= della distanza tra i due nodi => lo aggiungo nNeighbor ai neighbors di n
					if( radius >= pythagora( p, pNeighbor ) ) neighbors.add( nNeighbor );
				}
			}
			// stampo quanti vicini ha il nodo n
			//connect.print( "n: " + n + " - neighbors del nodo " + n.getIdNode() + ": "+ neighbors.size() );
		}
		
	}

	// se un nodo ha trovato un clone, interrompe tutti i nodi e quando nodeActive==0 => l'Hypervisor si risveglierà
	public synchronized void findClone(){
		Node.endSimulation=true;
		connect.print( "trovato clone");
		
		
		for( int x=0; x<listNode.size(); x++ ){
			synchronized( listNode.get(x).bufferMessage ){
				listNode.get(x).interrupt();
			}
		}
		
		synchronized(lockEndSimulation){ 
			endSimulation= true;
			findClone=true;
		//	lockEndSimulation.notify(); 
		}
		
	}

	// viene invocata solo quando nodeActive==0, setta endSimulation dell'Hypervisor a true e lo risveglia
	public void endSimulation(){
		
		synchronized( lockEndSimulation ){ 
			endSimulation=true;
			lockEndSimulation.notify();
			connect.print("END SIM SVEGLIO HYPER");
		}
		
	}
	
	public synchronized void nodeActive(){
		// quando un nodo si risveglia incrementa il contatore nodeActive dei nodi attivi in un determinato istante
		//synchronized (nodeActive) {
			//nodeActive++;
			//connect.print( "nodeActive++: " + nodeActive );
			System.out.println( "nodeActive++: " + ++nodeActive);
		//}
	}

	public synchronized void nodeNotActive(){
		// un nodo decrementa il contatore dei nodi attivi quando: va in wait, ha terminato l'energia o quando non ha più vicini
		//synchronized (nodeActive) {
			//nodeActive--;
			//connect.print( "nodeActive--: " + nodeActive );
			System.out.println( "nodeActive--: " + --nodeActive);
			if( nodeActive == 0 ) { Node.endSimulation=true; endSimulation(); }
		//}
	}

	public boolean readEndSimulation(){
		synchronized(lockEndSimulation){
			if( ! endSimulation ) return true;
			else return false;
			
		}
	}
	
	
	public void setParamiters( Object[] o){
		proto= (String) o[0];
		nSim= (Integer) o[1];
		numberNode =(Integer) o[2];
		radius =(Float) o[3];
		probAcceptLocation=(Float) o[4];
		numLocationDestination= (Integer) o[5];
		energyTot =(Integer) o[6];
		energyToSend=(Integer) o[7];
		energyToReceive=(Integer) o[8];
		energyToSignature=(Integer) o[9];
	}
	
	
	

	public static float pythagora( Position p1, Position p2 ){
		float a = p1.getX() - p2.getX();
		float b = p1.getY() - p2.getY();
		
		float r = 0.0F;
			    
			    if (Math.abs(a) > Math.abs(b)) {
			       r = b / a;
			       r = (float) ( Math.abs(a) * Math.sqrt(1 + r*r) );
			    }
			    else if (b != 0) {
			       r = a / b;
			       r = (float) ( Math.abs(b) * Math.sqrt(1 + r*r) );
			    }
		 
		return r;
	}
	

}