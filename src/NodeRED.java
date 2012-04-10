

public class NodeRED extends Node {
	// static int random per la funzione hash per generare la posizione, passato dall'hypervisor a tutti i nodi RED
	
	
	NodeRED( int id, Position pos, int en){
		super( id, pos, en );
	}
	
	
	public void receiveMessageClaim( MessageClaim msg ) throws ExcEndEnergy {
		// con probabilità 1-p non gestisco il messaggio e termino, mentre con prob p lo accetto
		
		if( (float)Math.random() <= 1-probAcceptLocation )  return; // ignora il messaggio
		
		energy-=energyToReceive;
		if(energy <=0 ) throw new ExcEndEnergy();
		
		int i = msg.getIdSender();
		
		
	}

	
	public void receiveMessageControl( MessageControl msg ) throws ExcEndEnergy{
		
	}
	
	
	public boolean findClone( MessageControl msg ){
		if( msg.getIdSender() == id && msg.getPosSender()!= pos )
			return true;
		
		return false;
	}
}
