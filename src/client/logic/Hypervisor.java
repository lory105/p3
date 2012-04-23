package client.logic;

import java.util.*; 


public class Hypervisor extends Thread {
	private Connector connect;
	
	// values of simulation
	private Object[] values=null;
	
	private String proto;
	private int nSim=0;
	
	private int numberNode=0;              // = n clone excluding!
	private float radius;  			       // = r
	private float probAcceptLocation;      // = p
	private int numLocationDestination;    // = g value
	   
	private int energyTot;
	private int energyToSend;
	private int energyToReceive;
	private int energyToSignature;
	
	
	private Vector<Node> listNode = null;				 // vector containing the n + 1 nodes, including clone!
	private Integer nodeActive = (numberNode+1);         // if ==0 => all nodes have finished => endSimulation = true
	

	private ActivatorNodes activatorNodes=null;
	private Object lockEndSimulation= new Object();
	private boolean endSimulation=false;			// if true => one nSimCont is finished
	private boolean findClone=false;				// if true => one nSinCont has find clone ha trovato un clone
	
	
	
	public Hypervisor( Connector c){ connect=c; }
	
	public void run(){
		try{
			
		Node.setParamiters( this, probAcceptLocation, numLocationDestination, energyTot, energyToSend, energyToReceive, energyToSignature );
		int nSimCont=1;

	    activatorNodes= ActivatorNodes.getInstance();
		activatorNodes.start();
		
		connect.print( proto + " simulation start..", 0);

		while( nSimCont <= nSim ){
			if( isInterrupted() ) throw new SecurityException();
			connect.print( "\nstart simulation n." + nSimCont, 0 );
			
			nodeActive=(numberNode+1);
			findClone=false;
			endSimulation=false;
			Node.setNewSim( 0 , false);
			listNode= new Vector<Node>();
			
			generateNode( numberNode+1 );
			findNeighbors();
		
			connect.print( "nodeActive: " + nodeActive, 0);

			if( isInterrupted() ){ throw new SecurityException(); }
						
			activatorNodes.pullVectorNodesToActive( listNode );
	
			synchronized (lockEndSimulation) {
				while( !endSimulation && nodeActive!=0 ){
					lockEndSimulation.wait();
				}
				connect.print("Hypervisor awakened..", 0);
			}
			
			
			if( findClone ){
				for( int x=0; x<listNode.size(); x++ ){
					synchronized( listNode.get(x).bufferMessage ){
						if( listNode.get(x).isInterrupted() == false ){
							connect.print( "interrompo nodo " + listNode.get(x), 0 );
							listNode.get(x).interrupt();
						}
					}
				}
			}

			connect.print( ( new Integer( listNode.size() ) ).toString(), 0 );
			connect.print("invio dati all'elaboratore", 0);
			connect.pullData( new Data( listNode, Node.getDetection() ) );
				
			connect.print( "endSimulation=" + endSimulation, 0 );
			connect.print( "findClone=" + findClone, 0 );
			connect.print( "nodeActive=" + nodeActive, 0 );
			connect.print( "Terminata simulazione n." + nSimCont, 0 );
			nSimCont++;
		}
		
		// send a null simulation that acts as a flag that the simulations were completed
		connect.print("simulation ended..", 0);
		connect.pullData( new Data(null, -1) );
		activatorNodes.interrupt();
		
		}
   	 	// if the simulation is interupted ( for example because STOP button was pressed )
		// during a wait o sleep state of Hypervisor ( so a simulation is active )
		catch( InterruptedException e){
			connect.print( getName() + "Hyper terminated with InterruptedExc", 0);
			activatorNodes.interrupt();
			if( listNode !=null ){
				Node.endSimulation=true;
				for( int x=0; x<listNode.size(); x++ )
					listNode.get(x).interrupt();

				listNode.clear();
			}
		}
		
   	 	// if simulation is interupted ( for example because STOP button was pressed ) 
		// during an active state of Hypervisior
		catch( SecurityException e){
			connect.print( getName() + "Hyper terminated with SecurityExc", 0);
			activatorNodes.interrupt();
			if( listNode !=null )
				listNode.clear();
		}

	}
	
	
	private void generateNode(int nodiTot){
		Position pos=null;
		boolean alreadyExistingPosition= false;
		int rand = (int)(Math.random() *10) ; // random value for RED Node [ 0<= rand <10 ]
		
		// creates all nodes including clone ( nodiTot )
		for(int nodeCreated=0; nodeCreated<nodiTot ; nodeCreated++){
			
			alreadyExistingPosition= false;  
			// create the position of the new node to create: Math.random() return a double [ 0<= double <1 ]
			pos= new Position( (float)Math.random(), (float)Math.random() );
			for(int k=0; k<nodeCreated && ! alreadyExistingPosition; k++){
				// check between nodes created, if the new position already exists
				if( listNode.get(k).getPosition().equals(pos)){ 
					alreadyExistingPosition=true; nodeCreated--; break; 
				} 
			}

			
			// if all node are created, create clone node checking if its chosen position is already used
			// choose a node to cloned and take its id
			if(nodeCreated == nodiTot-1 && ! alreadyExistingPosition ){
				int cloneRandomId = ( listNode.get( (int) ( Math.random()* (listNode.size()-1) ) ) ).getIdNode();
				connect.print( "Nodi creati" + listNode.size() + " Id nodo clonato: " + cloneRandomId, 0);
				
				if( proto.equals("LSM") ){
					listNode.add( new NodeLSM( cloneRandomId, pos, energyTot ) );
				}
				else{
					listNode.add( new NodeRED( cloneRandomId, pos, energyTot, rand ) );
				}
				connect.print( "created clone node", 0);
									
				break;
			}
			
			// create new node with id the number of nodes created up to now, and with position pos created
			if( ! alreadyExistingPosition )
				if( proto.equals("LSM") ){
					listNode.add( new NodeLSM( nodeCreated, pos, energyTot ) );
				}
				else{
					listNode.add( new NodeRED( nodeCreated, pos, energyTot, rand  ) );
				}
		}
	}
	
	
	// function used to find neighbors for each node
	public void findNeighbors(){
		Node node=null, nNeighbor;
		Position pos=null, posNeighbor=null;
		Vector<Node> neighbors = null;
		
		for(int i=0; i< listNode.size(); i++ ){
			// searching neighbors of Node node
			node = listNode.get(i);
			neighbors= node.getNeighbors();
			pos = node.getPosition();
			
			for(int k=0; k< listNode.size(); k++ ){
				nNeighbor = listNode.get(k);
				 
				if( node != nNeighbor ){
					posNeighbor = nNeighbor.getPosition();
				
					// if radius is >= distance between the two nodes => add nNeighbor to the list of neighbors
					if( radius >= pythagora( pos, posNeighbor ) ) neighbors.add( nNeighbor );
				}
			}
		}
		
	}

	// if a node found a clone, it stops all nodes and when nodeActive == 0 => the Hypervisor will awaken
	public synchronized void findClone(){
		Node.endSimulation=true;
		connect.print( "find clone!", 0);

		findClone=true;
		//endSimulation();
	}

	// function is called only when nodeActive==0, it wakes Hypervisor
	public void endSimulation(){
		
		synchronized( lockEndSimulation ){ 
			endSimulation=true;
			// Hypervisor awakened
			lockEndSimulation.notify();
		}
		
	}
	
	
	public synchronized void nodeActive(){
		synchronized (nodeActive) {
			System.out.println( "nodeActive++: " + ++nodeActive);
		}
	}

	public synchronized void nodeNotActive(){
		synchronized (nodeActive) {
			System.out.println( "nodeActive--: " + --nodeActive);
			if( nodeActive == 0 ) { Node.endSimulation=true; endSimulation(); }
		}
	}

	public boolean readEndSimulation(){
		synchronized(lockEndSimulation){
			if( ! endSimulation ) return true;
			else return false;
			
		}
	}
	
	
	public void setParamiters( Object[] v){
		values=v;
		
		proto= (String) v[0];
		nSim= (Integer) v[1];
		numberNode =(Integer) v[2];
		radius =(Float) v[3];
		probAcceptLocation=(Float) v[4];
		numLocationDestination= (Integer) v[5];
		energyTot =(Integer) v[6];
		energyToSend=(Integer) v[7];
		energyToReceive=(Integer) v[8];
		energyToSignature=(Integer) v[9];
	}
	
	public final Object[] getParameters(){ return values; }
	
	
	// function to calculate the distance between two nodes
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
	

	public void print(String text, int area){
		connect.print(text, area);
	}
}
