// class represents generic nodes
package client.logic;

import java.util.*;

import client.exception.ExcEndEnergy;
import client.exception.ExcFindClone;
import client.exception.ExcNoNeighbors;


abstract class Node extends Thread {
	static Hypervisor hyper=null;
	static int detection = 0; 	   // detection flag. if almost one node makes detection detection==1
	static Object lockEndSim = new Object();
	static boolean endSimulation = false;
	
	static float probAcceptLocation;       // p
	static int locationDestination;        // g
	static int energyTot;
	static int energyToSend;
	static int energyToReceive;
	static int energyToSignature;
	
	
	int id;
	Position pos=null;
	int energy=0;
	
    Vector<Node> neighbors= new Vector<Node>();
    Vector<MessageControl> memoryMsg= new Vector<MessageControl>();
	Vector<Message> bufferMessage = new Vector<Message>();
    
	int sentMessages=0;
    int receivedMessages=0;
	int signatureVerified=0;

	
	protected Node( int i, Position p, int en ){
		super( "Node" + i);
		id=i; pos=p; energy= en;
	}
	
	// function to set simulation parameters
	public static void setParamiters( Hypervisor h, float p, int ld, int et, int ets, int etr, int etsg){
		hyper=h;
		probAcceptLocation=p;
		locationDestination = ld;
		energyTot= et;
		energyToSend=ets;
		energyToReceive=etr;
		energyToSignature=etsg;	
	}
		
	
	public void run(){
		try{
			checkEndSimulazion();
			checkEndNeighbors();
			sendLocationClaim(); 
			Message msg=null;
			
			while(true){
				checkEndSimulazion();
				checkEndNeighbors();
				synchronized( bufferMessage ){
					// if a node hasn't message to manage, it waits over bufferMessage
					while( bufferMessage.isEmpty() ){
						checkEndSimulazion();
						//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
						hyper.nodeNotActive();
						bufferMessage.wait();	
						//if( isInterrupted() ) throw new SecurityException();
						checkEndSimulazion();
						//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
						// if a node wakes up => it has received a message
						hyper.nodeActive(); 
					}
					
					msg = bufferMessage.remove(0);
				}
				checkEndSimulazion();
				if( msg instanceof MessageControl){
					receiveMessageControl( (MessageControl)msg); }
				checkEndSimulazion();
				if( msg instanceof MessageClaim  ){ 
					receiveMessageClaim( ((MessageClaim)msg) ); }
				checkEndSimulazion();
				if( msg instanceof MessageDeath  ){  
					receiveMessageDeath( (MessageDeath)msg); }				
			}

		}
		// if node is interrupted during an active state
		catch( SecurityException e){
   	 		hyper.print( getName() + "securExc terminato " + this.getName(), 0 );

   	 		// ottimizzare la memoria: if( closeAll ) faccio delle clear sui vettori
   	 		hyper.nodeNotActive();
   	 	}

		// if node is interrupted during an wait state 
		catch( InterruptedException e){
   	 		hyper.print( getName() + "interrExc terminato " + this.getName(), 0 );
   	 	}

		catch( ExcNoNeighbors e){ hyper.nodeNotActive(); }
		catch( ExcFindClone e ){ 
			hyper.print("CLONE EXIT FIND", 0);
			hyper.findClone(); hyper.nodeNotActive();}

		catch( ExcEndEnergy e){
			sendMessageDeath();
			hyper.nodeNotActive();
		}
		
	}
	
	
	// function to check if simulation are ended
	public void checkEndSimulazion() throws SecurityException{
		//if( isInterrupted() ) throw new SecurityException();
		if( endSimulation ) throw new SecurityException();
		//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
	}
	
	// function to check if the node has no more neighbors
	public void checkEndNeighbors() throws ExcNoNeighbors{
		synchronized( neighbors ){ if( neighbors.isEmpty() ) throw new ExcNoNeighbors(); }
	} 
	
	// function that subtract the current energy of a node and checks if this energy is terminated
	protected void checkEnergy( int energyToSubtract) throws ExcEndEnergy{
		energy-=energyToReceive;
		if(energy <=0 ) throw new ExcEndEnergy();
	}
	
	// creates the Location Claim and sends it in broadcast to all neighbors [ LSM, RED ]
	public void sendLocationClaim() throws ExcEndEnergy, SecurityException {
		checkEnergy(energyToSignature);
		
		MessageClaim mc = new MessageClaim( id, pos);
		
		// sends message to all neighbors
		synchronized( neighbors ){
			for( int x=0; x< neighbors.size(); x++){
				checkEndSimulazion();
				checkEnergy(energyToSend);
				sentMessages++;
				neighbors.get(x).pushMessage(mc);
			}
		}
	}
	
	// function to send message control
	public void sendMessageControl( Node receiver, MessageControl msg){
		receiver.pushMessage(msg);
	}

	// function to send message death ( sent when the node finishes its energy )
	public void sendMessageDeath(){
		MessageDeath msg = new MessageDeath( id );
		synchronized( neighbors ){
			for( int x=0; x< neighbors.size(); x++ ){
				neighbors.get(x).pushMessage( msg );
			}
		}
	}
	
	// abstract implementation of function to receive a message claim
	abstract public void receiveMessageClaim( MessageClaim msg ) throws ExcEndEnergy, ExcFindClone, SecurityException;

	// abstract implementation of function to receive a message control
	abstract public void receiveMessageControl( MessageControl msg ) throws ExcEndEnergy, ExcFindClone, SecurityException;
	
	// function called when a node receives a messageDeath 
	public void receiveMessageDeath( MessageDeath msg ){
		int idNodeDied= msg.getIdSender();
		synchronized( neighbors ){
			for(int x=0; x<neighbors.size(); x++){
				if( neighbors.get(x).getId() == idNodeDied){
					neighbors.remove(x);
					return;
				}
			}
		}
	}
	
	// function for detection of clone: it searches for clone in memoryMsg
	public boolean findClone( MessageControl msg ) throws ExcEndEnergy, SecurityException{
		checkEndSimulazion();
		checkEnergy(energyToSignature);
		
		signatureVerified++;
		
		int idSender = msg.getIdSender();
		Position posSender = msg.getPosSender();
		
		// detection about me
		if(idSender == id && ! posSender.equals(pos) )
			return true;
		
		// detection about of my messages stored in buffer memoryMsg
		synchronized (memoryMsg) {
			for(int i=0; i < memoryMsg.size(); i++ ){
				checkEndSimulazion();
				if( idSender == memoryMsg.get(i).getIdSender() &&  posSender != memoryMsg.get(i).getPosSender() )
					return true;
			}
		}
		return false;

	}
	

	// pushes the message to sent in buffer messages of receiver node 
	public void pushMessage( Message msg ){
	
		synchronized( bufferMessage ){
		// if received a death message from a neighbor, puts it in first position of bufferMessage
			if( msg instanceof MessageDeath ) bufferMessage.add(0, msg);
			else bufferMessage.add(msg);
			
			bufferMessage.notify();
		}
	}
	
	
	
	// returns the nearest neighbor to the position p, also monitoring own position
	public Node nearestNeighbor(Position p){ 
		Node nearestNode = this;
		float minDistance = Hypervisor.pythagora( pos, p);
		float distance=0;
		
		synchronized( neighbors ){
			for( int i=0; i < neighbors.size(); i++ ){
				distance = Hypervisor.pythagora( neighbors.get(i).getPosition(), p);
				if(  distance < minDistance ){
					nearestNode = neighbors.get(i);
					minDistance = distance;
				}
			}
		}
		return nearestNode;
	}
	
	// function to update values for a new simulation
	static public void setNewSim( int d, boolean e){ detection = d; endSimulation=e; };
	
	// function to return identification number of node
	public int getIdNode(){ return id; }

	// function to return node's position
	public final Position getPosition(){ return pos; }
	
	// function to return node's neighbors
	public Vector<Node> getNeighbors(){ synchronized(neighbors){ return neighbors;}  }
	static public int getDetection(){ return detection; }
	
	// function to return node's information at end of simulation to do statistics 
	public int[] getDatas(){
		int en;
		if( energy <= 0 )
			en=energyTot;
		else
			en= (energyTot-energy);
			
		int[] datas= { sentMessages, receivedMessages, signatureVerified, en, memoryMsg.size() };

		return datas;
		
	}

}

