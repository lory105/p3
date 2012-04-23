package client.logic;

import client.exception.ExcEndEnergy;
import client.exception.ExcFindClone;


public class NodeLSM extends Node {
	
	
	public NodeLSM( int id, Position pos, int en){
		super( id, pos, en );
	}

	
	public void receiveMessageClaim( MessageClaim msg ) throws ExcEndEnergy, ExcFindClone, SecurityException {
		// with probability 1-p node does not manage the message and terms,
		// while with probability p it accepts message and it run the routing g times
		if( (float)Math.random() <= (1-probAcceptLocation) )  return; // message not accepted
		
		checkEndSimulazion();
		checkEnergy(energyToReceive);
		
		receivedMessages++;
		NodeLSM receiver = null;
		
		// g time
		for(int i=0; i< locationDestination; i++){
			Position pos = new Position( (float)Math.random(), (float)Math.random() );
			MessageControl mc = new MessageControl( (MessageClaim)msg, pos);
			
			// buffer neighbors isn't empty
			receiver = (NodeLSM)nearestNeighbor(pos);
			
			// if node nearest to the destination of messagControl created is the node itself, it makes the detection
			if( receiver == this){
				if( findClone(mc) ){ 
					checkEndSimulazion();
					Node.detection=1; throw new ExcFindClone();
				} 
				Node.detection=1;
				memoryMsg.add(mc);
			}
			else{
				// sending the control message to the nearest neighbor to the destination pos of the message
				checkEndSimulazion();
				checkEnergy(energyToSend);
				sentMessages++;
				sendMessageControl( receiver, mc );
			}
		}	
	}
	
	
	public void receiveMessageControl( MessageControl msg ) throws ExcEndEnergy, ExcFindClone, SecurityException{
		checkEndSimulazion();
		checkEnergy(energyToReceive);
		
		receivedMessages++;
				
		// detection of clone
		if( findClone(msg) ){
			checkEndSimulazion();
			Node.detection=1; throw new ExcFindClone();
		}
		Node.detection=1;
		
		memoryMsg.add(msg);
		
		Node n= nearestNeighbor( msg.getPosReceiver());
		// if the receiver of the message is the node itself does not do anything
		if( this == n ) return;
		
		checkEndSimulazion();
		checkEnergy(energyToSend);
		sentMessages++;
		sendMessageControl( n, msg);
	}
		
}
