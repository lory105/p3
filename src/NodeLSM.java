

//import java.util.Vector;

public class NodeLSM extends Node {
	
	
	public NodeLSM( int id, Position pos, int en){
		super( id, pos, en );
	}

	
	
	public void receiveMessageClaim( MessageClaim msg ) throws ExcEndEnergy, ExcFindClone, SecurityException {
		// con probabilità 1-p non gestisco il messaggio e termino, mentre con prob p lo accetto
		// e eseguo il routing g volte ( locationDestination )
		
		if( (float)Math.random() <= (1-probAcceptLocation) )  return; // ignora il messaggio
		
		//if( isInterrupted() ) throw new SecurityException();
		synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
		
		energy-=energyToReceive;
		if(energy <0 ) throw new ExcEndEnergy();
		receivedMessages++;
		NodeLSM n = null;
		
		for(int i=0; i< locationDestination; i++){
			Position p = new Position( (float)Math.random(), (float)Math.random() );
			MessageControl mc = new MessageControl( (MessageClaim)msg, p);
			
			// neighbors è non vuoto
			n = (NodeLSM)nearestNeighbor(p);
			
			// se il + vicino alla destinazione del messagControll creato è il nodo stesso, faccio la detection
			if( n == this){
				if( findClone(mc) ){ 
					//if( isInterrupted() ) throw new SecurityException();
					synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
					Node.detection=1; throw new ExcFindClone();
				} 
				Node.detection=1;
				memoryMsg.add(mc);
			}
			else{
				// invio il messaggio di controllo al neighbor + vicino alla destinazione p del messaggio,
				//if( isInterrupted() ) throw new SecurityException();
				synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
				energy-=energyToSend;
				if(energy <0 ) throw new ExcEndEnergy();
				sentMessages++;
				sendMessageControl( n, mc );
			}
		}	
	}
	
	
	public void receiveMessageControl( MessageControl msg ) throws ExcEndEnergy, ExcFindClone, SecurityException{
		//if( isInterrupted() ) throw new SecurityException();
		synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
		energy-=energyToReceive;
		if(energy <0 ) throw new ExcEndEnergy();
		receivedMessages++;
		
		//hyper.connect.print("cerco se c'è clone2");
		
		// faccio la detection del clone
		if( findClone(msg) ){
			//if( isInterrupted() ) throw new SecurityException();
			synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
			Node.detection=1; throw new ExcFindClone();
		}
		Node.detection=1;
		
		memoryMsg.add(msg);
		
		
		Node n= nearestNeighbor( msg.getPosReceiver());
		// se il destinatario del messaggio è il nodo stesso non faccio nulla
		if( this == n ) return;
		
		//if( isInterrupted() ) throw new SecurityException();
		synchronized( lockEndSim ){ if( endSimulation ) throw new SecurityException(); }
		energy-=energyToSend;
		if(energy <0 ) throw new ExcEndEnergy();
		sentMessages++;
		sendMessageControl( n, msg);
	}
	

	
}
