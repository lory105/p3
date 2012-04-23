package client.logic;

import client.exception.ExcEndEnergy;
import client.exception.ExcFindClone;


public class NodeLSM extends Node {
	
	
	public NodeLSM( int id, Position pos, int en){
		super( id, pos, en );
	}

	
	
	public void receiveMessageClaim( MessageClaim msg ) throws ExcEndEnergy, ExcFindClone, SecurityException {
		// con probabilità 1-p non gestisco il messaggio e termino, mentre con prob p lo accetto
		// e eseguo il routing g volte ( locationDestination )
		
		if( (float)Math.random() <= (1-probAcceptLocation) )  return; // ignora il messaggio
		
		//if( isInterrupted() ) throw new SecurityException();
		checkEndSimulazion();
		//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
		
		checkEnergy(energyToReceive);
		
		receivedMessages++;
		NodeLSM receiver = null;
		
		// g time
		for(int i=0; i< locationDestination; i++){
			Position pos = new Position( (float)Math.random(), (float)Math.random() );
			MessageControl mc = new MessageControl( (MessageClaim)msg, pos);
			
			// buffer neighbors isn't empty
			receiver = (NodeLSM)nearestNeighbor(pos);
			
			// se il + vicino alla destinazione del messagControll creato è il nodo stesso, faccio la detection
			if( receiver == this){
				if( findClone(mc) ){ 
					//if( isInterrupted() ) throw new SecurityException();
					checkEndSimulazion();
					//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
					Node.detection=1; throw new ExcFindClone();
				} 
				Node.detection=1;
				memoryMsg.add(mc);
			}
			else{
				// invio il messaggio di controllo al neighbor + vicino alla destinazione p del messaggio,
				//if( isInterrupted() ) throw new SecurityException();
				checkEndSimulazion();
				//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
				checkEnergy(energyToSend);
				sentMessages++;
				sendMessageControl( receiver, mc );
			}
		}	
	}
	
	
	public void receiveMessageControl( MessageControl msg ) throws ExcEndEnergy, ExcFindClone, SecurityException{
		//if( isInterrupted() ) throw new SecurityException();
		checkEndSimulazion();
		//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
		checkEnergy(energyToReceive);
		
		receivedMessages++;
				
		// faccio la detection del clone
		if( findClone(msg) ){
			//if( isInterrupted() ) throw new SecurityException();
			checkEndSimulazion();
			//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
			Node.detection=1; throw new ExcFindClone();
		}
		Node.detection=1;
		
		memoryMsg.add(msg);
		
		
		Node n= nearestNeighbor( msg.getPosReceiver());
		// se il destinatario del messaggio è il nodo stesso non faccio nulla
		if( this == n ) return;
		
		//if( isInterrupted() ) throw new SecurityException();
		checkEndSimulazion();
		//synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
		checkEnergy(energyToSend);
		sentMessages++;
		sendMessageControl( n, msg);
	}
	

	
}
