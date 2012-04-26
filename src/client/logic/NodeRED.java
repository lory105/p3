// Class represents the nodes for RED protocol
package client.logic;

import client.exception.ExcEndEnergy;
import client.exception.ExcFindClone;


class NodeRED extends Node {
	int rand; // random value generated by Hypervisor
	
	
	NodeRED( int id, Position pos, int en, int r){
		super( id, pos, en );
		rand=r;
	}
	
	// function called to manage a claim message received
	public void receiveMessageClaim( MessageClaim msg ) throws ExcEndEnergy, ExcFindClone {
		// with probability 1-p node does not manage the message and terms,
		// while with probability p it accepts message and it run the routing g times
		if( (float)Math.random() <= 1-probAcceptLocation )  return; // message not accepted
		
		checkEndSimulazion();
		checkEnergy(energyToReceive);
		receivedMessages++;
		
		NodeRED receiver =  null;
		
		// g time
		for(int i=0; i< locationDestination; i++){
			
			Position pos = hashPosition( msg.getIdSender(), rand, i );
			MessageControl mc = new MessageControl( (MessageClaim)msg, pos);
			
			// buffer neighbors isn't empty
			receiver = (NodeRED)nearestNeighbor(pos);
			
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

	// function called to manage a control message received
	public void receiveMessageControl( MessageControl msg ) throws ExcEndEnergy, ExcFindClone, SecurityException{
		checkEndSimulazion();
		checkEnergy(energyToReceive);
		
		receivedMessages++;
				
		Node receiver= nearestNeighbor( msg.getPosReceiver());

		// if the receiver of the message is the node itself
		if( this == receiver ){
			if( findClone(msg) ){
				checkEndSimulazion();
				Node.detection=1; throw new ExcFindClone();
			}
			Node.detection=1;
		
			memoryMsg.add(msg);
			return;
		}
		
		checkEndSimulazion();
		checkEnergy(energyToSend);
		sentMessages++;
		sendMessageControl( receiver, msg);
	}
	
	
	// hash function to generate the position destination of message control 
	static public Position hashPosition( int id, int rand, int counter){
		String stringToHashX=new String( Integer.toString(id+rand+counter));
		String stringToHashY=new String( Integer.toString(counter+rand+id));
		
		float doubleForX=stringToHashX.hashCode();
		float doubleForY=stringToHashY.hashCode();
		
		Float normalizedDoubleForX=normalize(doubleForX);
		Float normalizedDoubleForY=normalize(doubleForY);

		return new Position(normalizedDoubleForX,normalizedDoubleForY);

	}

	// function to normalize number ( e.g. 34566 => 0.34566 )
	private static float normalize(float toNormalize){
		toNormalize=Math.abs(toNormalize);
		float index=1;
		while (index<toNormalize){
			index=index*10;
		}
		return toNormalize/index;
	}

	
}